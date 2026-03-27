package br.com.wsp.parking.infra.web.dto.request

import java.time.LocalDate

data class RevenueRequest(
    val date: LocalDate,
    val sector: String
)
