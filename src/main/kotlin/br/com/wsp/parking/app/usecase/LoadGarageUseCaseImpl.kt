package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.model.GarageConfig
import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.domain.port.`in`.LoadGarageUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime


@Service
@Transactional
class LoadGarageUseCaseImpl(

    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository,
    private val parkingRecordRepository: ParkingRecordRepository


) : LoadGarageUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(config: GarageConfig) {
        log.info("Carregando configuração da garagem: ${config.sector.size} setores, ${config.spot.size} vagas")

        config.sector.forEach { sector ->

            if (!sectorRepository.existsByName(sector.name)) {
                sectorRepository.save(sector)
                log.info("Setor salvo: ${sector.name}, capacidade=${sector.maxCapacity}, precoBase=${sector.basePrice}")
            }

        }

        var occupiedCount = 0
        config.spot.forEach { spot ->

            if (!spotRepository.existsById(spot.id)) {
                spotRepository.save(spot)

                if (spot.occupied) {
                    val sector = sectorRepository.findByName(spot.sectorName)
                    if (sector != null) {
                        val licensePlate = "SIM${spot.id.toString().padStart(4, '0')}"
                        val entryTime = LocalDateTime.now().minusHours(2)

                        parkingRecordRepository.save(
                            ParkingRecord(
                                licensePlate = licensePlate,
                                sectorName = spot.sectorName,
                                spotId = spot.id,
                                entryTime = entryTime,
                                appliedPrice = sector.basePrice
                            ).park(spot.id)
                        )
                        occupiedCount++
                        log.debug("Registro criado para vaga ocupada: vagaId=${spot.id}, placa=$licensePlate")
                    }
                }
            }

        }
        log.info("Garagem carregada com sucesso: ${config.sector.size} setores, ${config.spot.size} vagas, $occupiedCount ocupadas")
    }

}