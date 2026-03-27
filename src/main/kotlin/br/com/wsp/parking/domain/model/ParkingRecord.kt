package br.com.wsp.parking.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Status do registro de estacionamento.
 */
enum class ParkingStatus { ENTERED, PARKED, EXITED }

/**
 * Modelo de domínio representando um registro de estacionamento.
 */
data class ParkingRecord(
    val id: Long = 0,
    val licensePlate: String,
    val sectorName: String,
    val spotId: Long? = null,
    val entryTime: LocalDateTime,
    val exitTime: LocalDateTime? = null,
    val appliedPrice: BigDecimal,
    val totalAmount: BigDecimal? = null,
    val status: ParkingStatus = ParkingStatus.ENTERED
) {

    /**
     * Marca o veículo como estacionado em uma vaga específica.
     */
    fun park(spotId: Long) = copy(spotId = spotId, status = ParkingStatus.PARKED)

    /**
     * Registra a saída do veículo com o valor total cobrado.
     */
    fun exit(exitTime: LocalDateTime, totalAmount: BigDecimal) =
        copy(exitTime = exitTime, totalAmount = totalAmount, status = ParkingStatus.EXITED)
}
