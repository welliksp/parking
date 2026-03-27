package br.com.wsp.parking.domain.port.`in`

import java.math.BigDecimal
import java.time.LocalDate

interface GetRevenueUseCase {

    fun execute(sector: String, date: LocalDate): BigDecimal
}