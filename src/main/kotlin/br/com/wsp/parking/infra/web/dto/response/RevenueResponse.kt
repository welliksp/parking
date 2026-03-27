package br.com.wsp.parking.infra.web.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal

@Schema(description = "Resposta contendo o cálculo de receita")
data class RevenueResponse(
    @Schema(description = "Valor da receita calculada", example = "1500.00")
    val amount: BigDecimal,
    
    @Schema(description = "Moeda da receita", example = "BRL")
    val currency: String = "BRL",
    
    @Schema(description = "Timestamp da resposta em UTC", example = "2026-03-27T10:30:00.000Z")
    val timestamp: String
)
