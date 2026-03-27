package br.com.wsp.parking.domain.port.out

import br.com.wsp.parking.domain.model.ParkingRecord
import java.math.BigDecimal
import java.time.LocalDateTime

interface ParkingRecordRepository {
    fun save(record: ParkingRecord): ParkingRecord
    fun findActiveByLicensePlate(licensePlate: String): ParkingRecord?
    fun sumRevenueBySectorAndDateRange(
        sectorName: String,
        from: LocalDateTime,
        to: LocalDateTime
    ): BigDecimal
}
