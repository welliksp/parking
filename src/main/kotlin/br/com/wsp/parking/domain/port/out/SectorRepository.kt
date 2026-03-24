package br.com.wsp.parking.domain.port.out

import br.com.wsp.parking.domain.model.Sector

interface SectorRepository {

    fun save(sector: Sector): Sector
    fun findByName(name: String): Sector?
    fun findAll(): List<Sector>
    fun existsByName(name: String): Boolean
}