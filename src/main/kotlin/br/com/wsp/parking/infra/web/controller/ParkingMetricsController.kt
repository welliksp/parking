package br.com.wsp.parking.infra.web.controller

import br.com.wsp.parking.infra.metrics.ParkingMetrics
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/actuator/parking")
class ParkingMetricsController(
    private val parkingMetrics: ParkingMetrics
) {

    @PostMapping("/init-metrics")
    fun initMetrics(): Map<String, String> {
        parkingMetrics.updateOccupancy(0)
        return mapOf("status" to "Metrics initialized")
    }
}
