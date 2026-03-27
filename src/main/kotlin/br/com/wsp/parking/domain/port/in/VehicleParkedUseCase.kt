package br.com.wsp.parking.domain.port.`in`

interface VehicleParkedUseCase {

    fun execute(licensePlate: String, lat: Double, lng: Double)
}