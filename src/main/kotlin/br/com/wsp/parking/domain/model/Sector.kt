package br.com.wsp.parking.domain.model

import java.math.BigDecimal

/**
 * Modelo de domínio representando um setor do estacionamento.
 */
data class Sector(
    val name: String,
    val basePrice: BigDecimal,
    val maxCapacity: Int,
    val openHour: String,
    val closeHour: String,
    val durationLimitMinutes: Int,
    val isOpen: Boolean = true
) {

    /**
     * Cria uma cópia do setor com o status de abertura alterado.
     */
    fun withOpen(open: Boolean) = copy(isOpen = open)
}