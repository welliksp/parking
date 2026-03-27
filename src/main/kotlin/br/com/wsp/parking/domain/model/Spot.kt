package br.com.wsp.parking.domain.model

/**
 * Modelo de domínio representando uma vaga de estacionamento.
 */
data class Spot(
    val id: Long,
    val sectorName: String,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean = false
) {

    /**
     * Marca a vaga como ocupada.
     */
    fun occupy() = copy(occupied = true)
    
    /**
     * Marca a vaga como livre.
     */
    fun free() = copy(occupied = false)
}