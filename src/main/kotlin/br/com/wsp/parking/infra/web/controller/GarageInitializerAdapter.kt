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
        log.info("Evento de aplicação pronta recebido, inicializando configuração da garagem")

        try {
            log.info("Buscando configuração da garagem do simulador")
            val config = simulatorClient.fetchGarageConfig()
            
            log.debug("Configuração da garagem obtida: setores=${config.sector.size}, vagas=${config.spot.size}")
            loadGarageUseCase.execute(config)
            
            log.info("Configuração da garagem carregada com sucesso")
        } catch (ex: Exception) {
            log.error(
                "Falha ao carregar configuração da garagem na inicialização: ${ex.message}. " +
                "Aplicação iniciada mas pode não funcionar corretamente.", ex
            )
        }
    }


}