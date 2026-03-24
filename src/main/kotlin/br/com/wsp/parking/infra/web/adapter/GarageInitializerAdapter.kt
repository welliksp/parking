package br.com.wsp.parking.infra.web.adapter

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

        try {

            log.info("Buscando configuração da garagem no simulador...")
            val config = simulatorClient.fetchGarageConfig()
            loadGarageUseCase.execute(config)

        } catch (ex: Exception) {

            log.error(
                "Falha ao carregar configuração da garagem na inicialização: ${ex.message}. " +
                        "Aplicação iniciada, mas pode não funcionar corretamente.", ex
            )
        }


    }


}