package br.com.wsp.parking.infra.actuator

import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.springframework.boot.actuate.endpoint.annotation.Endpoint
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
@Endpoint(id = "parking")
class ParkingStatsEndpoint(
    private val sectorRepository: SectorRepository,
    private val spotRepository: SpotRepository
) {

    @ReadOperation
    fun parkingStats(): Map<String, Any> {
        val sectors = sectorRepository.findAll()
        
        val sectorStats = sectors.map { sector ->
            val occupied = spotRepository.countOccupiedBySector(sector.name)
            val available = sector.maxCapacity - occupied
            val occupancyRate = (occupied.toDouble() / sector.maxCapacity * 100)
            
            mapOf(
                "sector" to sector.name,
                "capacity" to sector.maxCapacity,
                "occupied" to occupied,
                "available" to available,
                "occupancyRate" to String.format("%.2f%%", occupancyRate),
                "isOpen" to sector.isOpen,
                "basePrice" to sector.basePrice
            )
        }
        
        val totalCapacity = sectors.sumOf { it.maxCapacity }
        val totalOccupied = sectors.sumOf { spotRepository.countOccupiedBySector(it.name) }
        val totalAvailable = totalCapacity - totalOccupied
        val overallOccupancy = if (totalCapacity > 0) {
            (totalOccupied.toDouble() / totalCapacity * 100)
        } else 0.0
        
        return mapOf(
            "timestamp" to LocalDateTime.now().toString(),
            "summary" to mapOf(
                "totalCapacity" to totalCapacity,
                "totalOccupied" to totalOccupied,
                "totalAvailable" to totalAvailable,
                "overallOccupancy" to String.format("%.2f%%", overallOccupancy)
            ),
            "sectors" to sectorStats
        )
    }
}
