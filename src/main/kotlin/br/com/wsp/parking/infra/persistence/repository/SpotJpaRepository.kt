package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.infra.persistence.entity.SpotJpaEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpotJpaRepository: JpaRepository<SpotJpaEntity, Long> {

}