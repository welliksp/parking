package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.port.`in`.VehicleParkedUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


@Service
@Transactional
class VehicleParkedUseCaseImpl(
    private val spotRepository: SpotRepository,
    private val parkingRecordRepository: ParkingRecordRepository
) : VehicleParkedUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(licensePlate: String, lat: Double, lng: Double) {
        log.info("Processing vehicle parked: licensePlate=$licensePlate, lat=$lat, lng=$lng")

        val record = parkingRecordRepository.findActiveByLicensePlate(licensePlate)
            ?: throw ResourceNotFoundException("Nenhuma entrada ativa encontrada para a placa $licensePlate")

        val spot = spotRepository.findByLatAndLng(lat, lng)
            ?: throw ResourceNotFoundException("Nenhuma vaga encontrada nas coordenadas lat=$lat lng=$lng")

        spotRepository.save(spot.occupy())

        parkingRecordRepository.save(record.park(spot.id))

        log.info("Vehicle parked: licensePlate=$licensePlate, spotId=${spot.id}, sector=${spot.sectorName}")
    }
}