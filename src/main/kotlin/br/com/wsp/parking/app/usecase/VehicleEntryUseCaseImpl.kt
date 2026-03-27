package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.InvalidInputException
import br.com.wsp.parking.domain.service.PricingService
import br.com.wsp.parking.domain.service.SectorAvailabilityService
import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.domain.port.`in`.VehicleEntryUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.infra.metrics.ParkingMetrics
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
@Transactional
class VehicleEntryUseCaseImpl(
    private val sectorAvailabilityService: SectorAvailabilityService,
    private val pricingService: PricingService,
    private val parkingRecordRepository: ParkingRecordRepository,
    private val parkingMetrics: ParkingMetrics
) : VehicleEntryUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(licensePlate: String, entryTime: LocalDateTime) {
        log.info("Processando entrada de veículo: placa=$licensePlate, horarioEntrada=$entryTime")
        
        validateInput(licensePlate, entryTime)
        
        val existingRecord = parkingRecordRepository.findActiveByLicensePlate(licensePlate)
        if (existingRecord != null) {
            log.warn("Veículo já possui registro ativo: placa=$licensePlate, registroId=${existingRecord.id}")
            throw InvalidInputException("Veículo com placa $licensePlate já possui um registro ativo no estacionamento")
        }

        val (availableSector, currentOccupied) = sectorAvailabilityService.findAvailableSector()

        val appliedPrice = pricingService.calculateEntryPrice(availableSector, currentOccupied)

        val record = parkingRecordRepository.save(
            ParkingRecord(
                licensePlate = licensePlate,
                sectorName = availableSector.name,
                entryTime = entryTime,
                appliedPrice = appliedPrice
            )
        )
        
        parkingMetrics.recordVehicleEntry(availableSector.name)

        log.info(
            "Entrada de veículo processada: id=${record.id}, placa=$licensePlate, " +
            "setor=${availableSector.name}, precoAplicado=$appliedPrice"
        )
    }

    private fun validateInput(licensePlate: String, entryTime: LocalDateTime) {
        if (licensePlate.isBlank()) {
            throw InvalidInputException("Placa do veículo não pode estar vazia")
        }
        
        if (entryTime.isAfter(LocalDateTime.now().plusMinutes(5))) {
            throw InvalidInputException("Horário de entrada não pode estar no futuro")
        }
    }
}