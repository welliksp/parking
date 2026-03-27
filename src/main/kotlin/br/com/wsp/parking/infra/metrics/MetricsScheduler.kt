package br.com.wsp.parking.infra.metrics

import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class MetricsScheduler(
    private val parkingMetrics: ParkingMetrics,
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(fixedRate = 15000)
    fun updateOccupancyMetrics() {
        try {
            val totalOccupied = spotRepository.countOccupied()
            parkingMetrics.updateOccupancy(totalOccupied)

            val sectors = sectorRepository.findAll()
            sectors.forEach { sector ->
                val occupied = spotRepository.countOccupiedBySector(sector.name)
                parkingMetrics.updateSectorOccupancy(sector.name, occupied, sector.maxCapacity)
                parkingMetrics.updateSectorOpen(sector.name, sector.isOpen)
            }
        } catch (ex: Exception) {
            log.error("Erro ao atualizar métricas de ocupação: ${ex.message}", ex)
        }
    }
}
