package br.com.wsp.parking.domain.port.`in`

import java.time.LocalDateTime

interface VehicleEntryUseCase {

    fun execute(licensePlate: String, entryTime: LocalDateTime)
}