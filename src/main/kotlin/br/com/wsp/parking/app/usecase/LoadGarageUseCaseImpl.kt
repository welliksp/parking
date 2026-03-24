package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.model.GarageConfig
import br.com.wsp.parking.domain.port.`in`.LoadGarageUseCase
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import jakarta.transaction.Transactional
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service


@Service
@Transactional
class LoadGarageUseCaseImpl(

    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository


) : LoadGarageUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(config: GarageConfig) {

        config.sector.forEach { sector ->

            if (!sectorRepository.existsByName(sector.name)) {
                sectorRepository.save(sector)
                log.info("Sector salvo: ${sector.name} capacidade=${sector.maxCapacity} preço=${sector.basePrice}")
            }

        }

        config.spot.forEach { spot ->

            if (!spotRepository.existsById(spot.id)) {
                spotRepository.save(spot)
            }

        }
        log.info("Garagem carregada: ${config.sector.size} setores, ${config.spot.size} vagas")
    }

}