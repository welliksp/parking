package br.com.wsp.parking.infra.persistence.dto

data class SpotResponse(
    val id: Long,
    val sector: String,
    val lat: Double,
    val lng: Double,
    val occupied: Boolean = false
)
