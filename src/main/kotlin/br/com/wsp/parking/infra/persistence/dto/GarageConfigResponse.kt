package br.com.wsp.parking.infra.persistence.dto

data class GarageConfigResponse(
    val garage: List<SectorResponse>,
    val spots: List<SpotResponse>

)
