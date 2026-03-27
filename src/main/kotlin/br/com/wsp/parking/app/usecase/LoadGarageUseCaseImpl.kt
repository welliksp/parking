package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.model.GarageConfig
import br.com.wsp.parking.domain.port.`in`.LoadGarageUseCase
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional



@Service
@Transactional
class LoadGarageUseCaseImpl(

    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository


) : LoadGarageUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun execute(config: GarageConfig) {
        log.info("Loading garage configuration: ${config.sector.size} sectors, ${config.spot.size} spots")

        config.sector.forEach { sector ->

            if (!sectorRepository.existsByName(sector.name)) {
                sectorRepository.save(sector)
                log.info("Sector saved: ${sector.name}, capacity=${sector.maxCapacity}, basePrice=${sector.basePrice}")
            }

        }

        config.spot.forEach { spot ->

            if (!spotRepository.existsById(spot.id)) {
                spotRepository.save(spot)
            }

        }
        log.info("Garage loaded successfully: ${config.sector.size} sectors, ${config.spot.size} spots")
    }

}