package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.infra.persistence.entity.SpotEntity
import org.springframework.data.jpa.repository.JpaRepository

interface SpotJpaRepository: JpaRepository<SpotEntity, Long> {

}