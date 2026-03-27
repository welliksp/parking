package br.com.wsp.parking.infra.web.controller

import br.com.wsp.parking.domain.exception.InvalidInputException
import br.com.wsp.parking.domain.port.`in`.VehicleEntryUseCase
import br.com.wsp.parking.domain.port.`in`.VehicleExitUseCase
import br.com.wsp.parking.domain.port.`in`.VehicleParkedUseCase
import br.com.wsp.parking.infra.web.dto.request.WebhookEventRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.ZoneOffset

@RestController
@RequestMapping("/webhook")
@Tag(name = "Webhook", description = "API para receber eventos de estacionamento")
class WebhookController(

    private val vehicleEntryUseCase: VehicleEntryUseCase,
    private val vehicleParkedUseCase: VehicleParkedUseCase,
    private val vehicleExitUseCase: VehicleExitUseCase

) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @Operation(
        summary = "Receber evento de webhook",
        description = "Processa eventos de entrada e saída de veículos do estacionamento"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "204",
                description = "Evento processado com sucesso",
                content = [Content()]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Dados de entrada inválidos"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor"
            )
        ]
    )
    fun handleEvent(@RequestBody event: WebhookEventRequest): ResponseEntity<Void> {

        log.debug("Webhook recebido: tipo=${event.eventType}, placa=${event.licensePlate}")

        when (event.eventType.uppercase()) {
            "ENTRY" -> {
                val entryTime = event.entryTime?.withOffsetSameInstant(ZoneOffset.UTC)?.toLocalDateTime()
                    ?: throw InvalidInputException("entry_time é obrigatório para eventos de tipo ENTRY")
                vehicleEntryUseCase.execute(event.licensePlate, entryTime)
            }

            "PARKED" -> {
                val lat = event.lat ?: throw InvalidInputException("lat é obrigatório para eventos de tipo PARKED")
                val lng = event.lng ?: throw InvalidInputException("lng é obrigatório para eventos de tipo PARKED")
                vehicleParkedUseCase.execute(event.licensePlate, lat, lng)
            }

            "EXIT" -> {
                val exitTime = event.exitTime?.withOffsetSameInstant(ZoneOffset.UTC)?.toLocalDateTime()
                    ?: throw InvalidInputException("exit_time é obrigatório para eventos de tipo EXIT")
                vehicleExitUseCase.execute(event.licensePlate, exitTime)
            }

            else -> throw InvalidInputException("Tipo de evento desconhecido: ${event.eventType}")
        }
        return ResponseEntity.noContent().build()

    }


}