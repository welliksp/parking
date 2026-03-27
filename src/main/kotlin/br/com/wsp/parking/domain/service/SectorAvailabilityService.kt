package br.com.wsp.parking.domain.service

import br.com.wsp.parking.domain.exception.UnavailableResourceException
import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Serviço de domínio responsável por encontrar setores disponíveis.
 */
@Service
class SectorAvailabilityService(
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    fun findAvailableSector(): Pair<Sector, Int> {
        log.debug("Searching for available sector")
        
        val openSectors = sectorRepository.findAll().filter { it.isOpen }
        
        if (openSectors.isEmpty()) {
            log.warn("No open sectors found")
            throw UnavailableResourceException("Nenhum setor está aberto no momento")
        }

        log.debug("Open sectors found: count=${openSectors.size}")

        for (sector in openSectors) {
            val currentOccupied = spotRepository.countOccupiedBySector(sector.name)
            log.debug("Checking sector availability: sector=${sector.name}, occupied=$currentOccupied, capacity=${sector.maxCapacity}")
            
            if (currentOccupied < sector.maxCapacity) {
                log.info("Available sector found: sector=${sector.name}, occupied=$currentOccupied, capacity=${sector.maxCapacity}")
                return Pair(sector, currentOccupied)
            }
        }

        log.warn("All open sectors are at maximum capacity")
        throw UnavailableResourceException(
            "Nenhuma vaga disponível em nenhum setor aberto. Todos os setores estão no máximo de capacidade."
        )
    }
}
