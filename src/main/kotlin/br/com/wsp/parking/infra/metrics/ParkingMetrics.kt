package br.com.wsp.parking.infra.metrics

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import org.springframework.stereotype.Component
import java.util.concurrent.atomic.AtomicInteger

@Component
class ParkingMetrics(private val meterRegistry: MeterRegistry) {

    private val occupancyGauge = AtomicInteger(0)
    private val sectorOccupancy = mutableMapOf<String, AtomicInteger>()
    private val sectorOccupancyRate = mutableMapOf<String, AtomicInteger>()
    private val sectorOpen = mutableMapOf<String, AtomicInteger>()

    init {
        meterRegistry.gauge("parking_occupancy_current", occupancyGauge)
    }

    fun updateOccupancy(total: Int) {
        occupancyGauge.set(total)
    }

    fun updateSectorOccupancy(sector: String, occupied: Int, capacity: Int) {
        val gauge = sectorOccupancy.getOrPut(sector) {
            val newGauge = AtomicInteger(0)
            meterRegistry.gauge("parking_sector_occupancy", listOf(Tag.of("sector", sector)), newGauge)
            newGauge
        }
        gauge.set(occupied)

        val rateGauge = sectorOccupancyRate.getOrPut(sector) {
            val newGauge = AtomicInteger(0)
            meterRegistry.gauge("parking_sector_occupancy_rate", listOf(Tag.of("sector", sector)), newGauge)
            newGauge
        }
        val rate = if (capacity > 0) ((occupied.toDouble() / capacity * 100).toInt()) else 0
        rateGauge.set(rate)
    }

    fun recordVehicleEntry(sector: String) {
        meterRegistry.counter("parking_vehicle_entry_total", listOf(Tag.of("sector", sector))).increment()
    }

    fun recordVehicleParked(sector: String) {
        meterRegistry.counter("parking_vehicle_parked_total", listOf(Tag.of("sector", sector))).increment()
    }

    fun recordVehicleExit(sector: String) {
        meterRegistry.counter("parking_vehicle_exit_total", listOf(Tag.of("sector", sector))).increment()
    }

    fun recordRevenue(sector: String, amount: Double) {
        meterRegistry.counter("parking_revenue_total", listOf(Tag.of("sector", sector))).increment(amount)
    }

    fun recordDuration(sector: String, minutes: Long) {
        meterRegistry.timer("parking_duration_minutes", listOf(Tag.of("sector", sector)))
            .record(minutes, java.util.concurrent.TimeUnit.MINUTES)
    }

    fun recordEventError(eventType: String, errorType: String) {
        meterRegistry.counter(
            "parking_event_error_total",
            listOf(Tag.of("eventType", eventType), Tag.of("errorType", errorType))
        ).increment()
    }

    fun updateSectorOpen(sector: String, isOpen: Boolean) {
        val gauge = sectorOpen.getOrPut(sector) {
            val newGauge = AtomicInteger(0)
            meterRegistry.gauge("parking_sector_open", listOf(Tag.of("sector", sector)), newGauge)
            newGauge
        }
        gauge.set(if (isOpen) 1 else 0)
    }
}
