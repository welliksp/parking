package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.port.`in`.VehicleExitUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import br.com.wsp.parking.domain.service.PricingService
import br.com.wsp.parking.infra.metrics.ParkingMetrics
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Duration
import java.time.LocalDateTime


@Service
@Transactional
class VehicleExitUseCaseImpl(

    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
    private val parkingRecordRepository: ParkingRecordRepository,
    private val pricingService: PricingService,
    private val parkingMetrics: ParkingMetrics

) : VehicleExitUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(licensePlate: String, exitTime: LocalDateTime) {
        log.info("Processando saída de veículo: placa=$licensePlate, horarioSaida=$exitTime")

        val record = parkingRecordRepository.findActiveByLicensePlate(licensePlate)
            ?: throw ResourceNotFoundException("Nenhum registro de estacionamento ativo encontrado para a placa: $licensePlate")

        val durationMinutes = Duration.between(record.entryTime, exitTime).toMinutes()
        val totalAmount = pricingService.calculateParkingFee(record.appliedPrice, durationMinutes)

        record.spotId?.let { spotId ->
            spotRepository.findById(spotId)?.let { spot ->
                spotRepository.save(spot.free())
                log.debug("Vaga liberada: vagaId=$spotId")
            }
        }

        val sector = sectorRepository.findByName(record.sectorName)

        if (sector != null && !sector.isOpen) {
            sectorRepository.save(sector.withOpen(true))
            log.info("Setor ${sector.name} reaberto após liberação de vaga")
        }

        parkingRecordRepository.save(record.exit(exitTime, totalAmount))
        
        parkingMetrics.recordVehicleExit(record.sectorName)
        parkingMetrics.recordRevenue(record.sectorName, totalAmount.toDouble())
        parkingMetrics.recordDuration(record.sectorName, durationMinutes)

        log.info("Saída de veículo processada: placa=$licensePlate, duracao=${durationMinutes}min, total=$totalAmount")
    }

}