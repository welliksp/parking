package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.port.`in`.VehicleExitUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import br.com.wsp.parking.domain.service.PricingService
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
    private val pricingService: PricingService

) : VehicleExitUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(licensePlate: String, exitTime: LocalDateTime) {
        log.info("Processing vehicle exit: licensePlate=$licensePlate, exitTime=$exitTime")

        val record = parkingRecordRepository.findActiveByLicensePlate(licensePlate)
            ?: throw ResourceNotFoundException("Nenhum registro de estacionamento ativo encontrado para a placa: $licensePlate")

        val durationMinutes = Duration.between(record.entryTime, exitTime).toMinutes()
        val totalAmount = pricingService.calculateParkingFee(record.appliedPrice, durationMinutes)

        record.spotId?.let { spotId ->
            spotRepository.findById(spotId)?.let { spot ->
                spotRepository.save(spot.free())
                log.debug("Spot freed: spotId=$spotId")
            }
        }

        val sector = sectorRepository.findByName(record.sectorName)

        if (sector != null && !sector.isOpen) {
            sectorRepository.save(sector.withOpen(true))
            log.info("Sector ${sector.name} re-opened after spot freed")
        }

        parkingRecordRepository.save(record.exit(exitTime, totalAmount))

        log.info("Vehicle exit processed: licensePlate=$licensePlate, duration=${durationMinutes}min, total=$totalAmount")
    }

}