package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.infra.persistence.entity.ParkingRecordEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ParkingRecordJpaRepository : JpaRepository<ParkingRecordEntity, Long> {
}