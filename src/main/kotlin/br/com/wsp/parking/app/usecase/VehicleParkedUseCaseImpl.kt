package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.InvalidInputException
import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.port.`in`.VehicleParkedUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import br.com.wsp.parking.infra.metrics.ParkingMetrics
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class VehicleParkedUseCaseImpl(
    private val spotRepository: SpotRepository,
    private val parkingRecordRepository: ParkingRecordRepository,
    private val parkingMetrics: ParkingMetrics
) : VehicleParkedUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(licensePlate: String, lat: Double, lng: Double) {
        log.info("Processando veículo estacionado: placa=$licensePlate, lat=$lat, lng=$lng")

        val record = parkingRecordRepository.findActiveByLicensePlate(licensePlate)
            ?: throw ResourceNotFoundException("Nenhuma entrada ativa encontrada para a placa $licensePlate")

        if (record.spotId != null) {
            log.warn("Veículo já estacionado: placa=$licensePlate, vagaId=${record.spotId}")
            throw InvalidInputException("Veículo com placa $licensePlate já está estacionado na vaga ${record.spotId}")
        }

        val spot = spotRepository.findByLatAndLng(lat, lng)
            ?: throw ResourceNotFoundException("Nenhuma vaga encontrada nas coordenadas lat=$lat lng=$lng")

        if (spot.occupied) {
            log.warn("Vaga já ocupada: vagaId=${spot.id}, lat=$lat, lng=$lng")
            throw InvalidInputException("Vaga ${spot.id} já está ocupada")
        }

        spotRepository.save(spot.occupy())

        parkingRecordRepository.save(record.park(spot.id))

        parkingMetrics.recordVehicleParked(spot.sectorName)

        log.info("Veículo estacionado: placa=$licensePlate, vagaId=${spot.id}, setor=${spot.sectorName}")
    }
}