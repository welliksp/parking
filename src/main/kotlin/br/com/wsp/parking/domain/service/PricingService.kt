package br.com.wsp.parking.domain.service

import br.com.wsp.parking.domain.model.Sector
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.ceil


@Service
class PricingService {

    companion object {
        private const val OCCUPANCY_RATE_LOW = 0.25
        private const val OCCUPANCY_RATE_MEDIUM = 0.50
        private const val OCCUPANCY_RATE_HIGH = 0.75
        
        private val MULTIPLIER_LOW = BigDecimal("0.90")
        private val MULTIPLIER_NORMAL = BigDecimal("1.00")
        private val MULTIPLIER_MEDIUM = BigDecimal("1.10")
        private val MULTIPLIER_HIGH = BigDecimal("1.25")
        
        private const val FREE_MINUTES = 30L
        private const val MINUTES_PER_HOUR = 60.0
    }

    fun calculateEntryPrice(sector: Sector, currentOccupied: Int): BigDecimal {
        val rate = currentOccupied.toDouble() / sector.maxCapacity.toDouble()
        val multiplier = when {
            rate < OCCUPANCY_RATE_LOW -> MULTIPLIER_LOW
            rate < OCCUPANCY_RATE_MEDIUM -> MULTIPLIER_NORMAL
            rate < OCCUPANCY_RATE_HIGH -> MULTIPLIER_MEDIUM
            else -> MULTIPLIER_HIGH
        }
        return sector.basePrice.multiply(multiplier).setScale(2, RoundingMode.HALF_UP)
    }


    fun calculateParkingFee(appliedPrice: BigDecimal, durationMinutes: Long): BigDecimal {
        if (durationMinutes <= FREE_MINUTES) return BigDecimal.ZERO
        val hours = ceil((durationMinutes - FREE_MINUTES) / MINUTES_PER_HOUR).toLong().coerceAtLeast(1)
        return appliedPrice.multiply(BigDecimal(hours)).setScale(2, RoundingMode.HALF_UP)
    }
}