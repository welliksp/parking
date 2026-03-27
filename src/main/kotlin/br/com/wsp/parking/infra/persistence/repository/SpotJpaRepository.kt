package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.infra.persistence.entity.SpotJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface SpotJpaRepository : JpaRepository<SpotJpaEntity, Long> {

    fun findByLatAndLng(lat: Double, lng: Double): SpotJpaEntity?

    @Query("SELECT COUNT(s) FROM SpotJpaEntity s WHERE s.sectorName = :sectorName AND s.occupied = true")
    fun countOccupiedBySector(sectorName: String): Int

}