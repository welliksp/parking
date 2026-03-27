package br.com.wsp.parking.infra.web.dto.request

import com.fasterxml.jackson.annotation.JsonProperty
import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime

@Schema(description = "Evento de webhook para operações de estacionamento")
data class WebhookEventRequest(
    @Schema(description = "Placa do veículo", example = "ABC1234")
    @JsonProperty("license_plate")
    val licensePlate: String,
    
    @Schema(description = "Horário de entrada do veículo", example = "2026-03-27T10:30:00Z")
    @JsonProperty("entry_time")
    val entryTime: OffsetDateTime? = null,
    
    @Schema(description = "Horário de saída do veículo", example = "2026-03-27T14:30:00Z")
    @JsonProperty("exit_time")
    val exitTime: OffsetDateTime? = null,
    
    @Schema(description = "Latitude da localização", example = "-23.5505")
    val lat: Double? = null,
    
    @Schema(description = "Longitude da localização", example = "-46.6333")
    val lng: Double? = null,
    
    @Schema(description = "Tipo do evento (ENTRY ou EXIT)", example = "ENTRY")
    @JsonProperty("event_type")
    val eventType: String

)
