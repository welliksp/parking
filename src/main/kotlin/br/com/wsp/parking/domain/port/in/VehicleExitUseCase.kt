package br.com.wsp.parking.domain.port.`in`

import java.time.LocalDateTime

interface VehicleExitUseCase {

    fun execute(licensePlate: String, exitTime: LocalDateTime)
}