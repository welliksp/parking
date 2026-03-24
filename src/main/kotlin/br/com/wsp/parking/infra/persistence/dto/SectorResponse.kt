package br.com.wsp.parking.infra.persistence.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class SectorResponse(
    val sector: String,
    @JsonProperty("base_price") val basePrice: BigDecimal,
    @JsonProperty("max_capacity") val maxCapacity: Int,
    @JsonProperty("open_hour") val openHour: String = "00:00",
    @JsonProperty("close_hour") val closeHour: String = "23:59",
    @JsonProperty("duration_limit_minutes") val durationLimitMinutes: Int = 1440
)
