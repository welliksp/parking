package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.infra.persistence.entity.SectorEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorJpaRepository : JpaRepository<SectorEntity, Long> {

    fun findByName(name: String): SectorEntity?
    fun existsByName(name: String): Boolean

}