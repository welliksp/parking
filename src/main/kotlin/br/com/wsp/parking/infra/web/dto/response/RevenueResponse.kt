package br.com.wsp.parking.infra.web.dto.response

import java.math.BigDecimal

data class RevenueResponse(
    val amount: BigDecimal,
    val currency: String = "BRL",
    val timestamp: String
)
