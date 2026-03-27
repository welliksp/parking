package br.com.wsp.parking.infra.persistence.repository

import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.infra.persistence.entity.ParkingRecordJpaEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.time.LocalDateTime

interface ParkingRecordJpaRepository : JpaRepository<ParkingRecordJpaEntity, Long> {

    @Query("""
        SELECT r FROM ParkingRecordJpaEntity r
        WHERE r.licensePlate = :licensePlate
          AND r.status IN ('ENTERED', 'PARKED')
        ORDER BY r.entryTime DESC
        LIMIT 1
    """)
    fun findActiveByLicensePlate(licensePlate: String): ParkingRecordJpaEntity?
    @Query("""
        SELECT COALESCE(SUM(r.totalAmount), 0)
        FROM ParkingRecordJpaEntity r
        WHERE r.sectorName = :sectorName
          AND r.status = 'EXITED'
          AND r.exitTime >= :from
          AND r.exitTime <= :to
    """)
    fun sumRevenueBySectorAndDateRange(
        sectorName: String,
        from: LocalDateTime,
        to: LocalDateTime
    ): BigDecimal

}