package br.com.wsp.parking.domain.model

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDateTime

@DisplayName("Domain Models - Testes Unitários")
class DomainModelsTest {

    @Test
    @DisplayName("Spot deve marcar como ocupado")
    fun `spot should mark as occupied`() {
        val spot = Spot(1L, "A", -23.561684, -46.655981, false)
        val occupiedSpot = spot.occupy()
        assertTrue(occupiedSpot.occupied)
        assertFalse(spot.occupied)
    }

    @Test
    @DisplayName("Spot deve marcar como livre")
    fun `spot should mark as free`() {
        val spot = Spot(1L, "A", -23.561684, -46.655981, true)
        val freeSpot = spot.free()
        assertFalse(freeSpot.occupied)
        assertTrue(spot.occupied)
    }

    @Test
    @DisplayName("Sector deve alterar status de abertura")
    fun `sector should change open status`() {
        val sector = Sector(
            name = "A",
            basePrice = BigDecimal("10.00"),
            maxCapacity = 100,
            openHour = "08:00",
            closeHour = "18:00",
            durationLimitMinutes = 480,
            isOpen = true
        )
        val closedSector = sector.withOpen(false)
        assertFalse(closedSector.isOpen)
        assertTrue(sector.isOpen)
    }

    @Test
    @DisplayName("ParkingRecord deve registrar estacionamento")
    fun `parking record should register parking`() {
        val record = ParkingRecord(
            id = 1L,
            licensePlate = "ABC1234",
            sectorName = "A",
            entryTime = LocalDateTime.now(),
            appliedPrice = BigDecimal("10.00")
        )
        val parkedRecord = record.park(5L)
        assertEquals(5L, parkedRecord.spotId)
        assertEquals(ParkingStatus.PARKED, parkedRecord.status)
        assertNull(record.spotId)
    }

    @Test
    @DisplayName("ParkingRecord deve registrar saída")
    fun `parking record should register exit`() {
        val entryTime = LocalDateTime.now().minusHours(2)
        val exitTime = LocalDateTime.now()
        val record = ParkingRecord(
            id = 1L,
            licensePlate = "ABC1234",
            sectorName = "A",
            spotId = 5L,
            entryTime = entryTime,
            appliedPrice = BigDecimal("10.00"),
            status = ParkingStatus.PARKED
        )
        val exitedRecord = record.exit(exitTime, BigDecimal("20.00"))
        assertEquals(exitTime, exitedRecord.exitTime)
        assertEquals(BigDecimal("20.00"), exitedRecord.totalAmount)
        assertEquals(ParkingStatus.EXITED, exitedRecord.status)
        assertNull(record.exitTime)
    }
}
