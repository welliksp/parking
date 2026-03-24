package br.com.wsp.parking.domain.port.out

import br.com.wsp.parking.domain.model.Spot

interface SpotRepository {

    fun save(spot: Spot): Spot
    fun findById(id: Long): Spot?
    fun findByLatAndLng(lat: Double, lng: Double): Spot?
    fun countOccupiedBySector(sectorName: String): Int
    fun existsById(id: Long): Boolean
}