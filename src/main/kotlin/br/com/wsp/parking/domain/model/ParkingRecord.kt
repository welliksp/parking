package br.com.wsp.parking.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

enum class ParkingStatus { ENTERED, PARKED, EXITED }

data class ParkingRecord(
    val id: Long,
    val licensePlate: String,
    val sectorName: String,
    val spotId: Long? = null,
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime? = null,
    val appliedPrice: BigDecimal,
    val totalAmount: BigDecimal? = null,
    val status: ParkingStatus = ParkingStatus.ENTERED
){


}
