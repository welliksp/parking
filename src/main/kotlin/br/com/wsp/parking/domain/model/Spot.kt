package br.com.wsp.parking.domain.model

data class Spot(
    val id: Long,
    val sectorName: String,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean = false
) {

    fun occupy() = copy(occupied = true)
    fun free() = copy(occupied = false)
}