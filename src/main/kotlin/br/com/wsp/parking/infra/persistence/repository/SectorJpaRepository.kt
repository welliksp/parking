package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.infra.persistence.entity.SectorJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SectorJpaRepository : JpaRepository<SectorJpaEntity, Long> {

    fun findByName(name: String): SectorJpaEntity?
    fun existsByName(name: String): Boolean

}