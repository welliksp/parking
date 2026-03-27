package br.com.wsp.parking.infra.client.adapter

import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.model.GarageConfig
import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.model.Spot
import br.com.wsp.parking.domain.port.out.GarageSimulatorClient
import br.com.wsp.parking.infra.persistence.dto.GarageConfigResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient

@Component
class GarageSimulatorClientAdapter(

    @Value("\${garage.simulator.url}")
    simulatorUrl: String
) : GarageSimulatorClient {

    private val webClient = WebClient.create(simulatorUrl)

    override fun fetchGarageConfig(): GarageConfig {

        val response = webClient.get()
            .uri("/garage")
            .retrieve()
            .bodyToMono(GarageConfigResponse::class.java)
            .block() ?: throw ResourceNotFoundException("Resposta vazia do simulador de garagem")
        return GarageConfig(
            sector = response.garage.map { s ->
                Sector(
                    name = s.sector,
                    basePrice = s.basePrice,
                    maxCapacity = s.maxCapacity,
                    openHour = s.openHour,
                    closeHour = s.closeHour,
                    durationLimitMinutes = s.durationLimitMinutes
                )
            },
            spot = response.spots.map { sp ->
                Spot(
                    id = sp.id,
                    sectorName = sp.sector,
                    lat = sp.lat,
                    lng = sp.lng,
                    occupied = sp.occupied
                )
            }
        )
    }
}