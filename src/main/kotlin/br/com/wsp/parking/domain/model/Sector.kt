package br.com.wsp.parking.domain.model

import java.math.BigDecimal

data class Sector(
    val name: String,
    val basePrice: BigDecimal,
    val maxCapacity: Int,
    val openHour: String,
    val closeHour: String,
    val durationLimitMinutes: Int,
    val isOpen: Boolean = true
) {

    fun withOpen(open: Boolean) = copy(isOpen = open)
}