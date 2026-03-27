package br.com.wsp.parking.infra.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.time.OffsetDateTime
import java.time.ZoneOffset

@Schema(description = "Resposta de erro padronizada da API")
data class ErrorResponse(
    @Schema(description = "Código HTTP de status", example = "400")
    val status: Int,

    @Schema(description = "Tipo de erro", example = "INVALID_REQUEST")
    val type: String,

    @Schema(description = "Mensagem de erro amigável", example = "Placa de veículo inválida")
    val message: String,

    @Schema(description = "Detalhes adicionais do erro", example = "Placa não pode estar vazia")
    val details: String? = null,

    @Schema(description = "Timestamp da ocorrência em UTC", example = "2026-03-27T10:30:00Z")
    val timestamp: String = OffsetDateTime.now(ZoneOffset.UTC).toString(),

    @Schema(description = "Caminho da requisição", example = "/v1/webhook")
    val path: String? = null,

    @Schema(description = "Erros de validação de campos")
    val fieldErrors: Map<String, String>? = null
)

