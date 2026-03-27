package br.com.wsp.parking.infra.web.controller

import br.com.wsp.parking.domain.port.`in`.LoadGarageUseCase
import br.com.wsp.parking.domain.port.out.GarageSimulatorClient
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

@Component
class GarageInitializerAdapter(
    private val simulatorClient: GarageSimulatorClient,
    private val loadGarageUseCase: LoadGarageUseCase
) {
    private val log = LoggerFactory.getLogger(javaClass)

    @EventListener(ApplicationReadyEvent::class)
    fun applicationReady() {
        log.info("Application ready event received, initializing garage configuration")

        try {
            log.info("Fetching garage configuration from simulator")
            val config = simulatorClient.fetchGarageConfig()
            
            log.debug("Garage configuration fetched: sectors=${config.sector.size}, spots=${config.spot.size}")
            loadGarageUseCase.execute(config)
            
            log.info("Garage configuration loaded successfully")
        } catch (ex: Exception) {
            log.error(
                "Failed to load garage configuration on startup: ${ex.message}. " +
                "Application started but may not function correctly.", ex
            )
        }
    }


}