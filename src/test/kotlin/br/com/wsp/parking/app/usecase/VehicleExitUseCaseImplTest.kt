package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.ResourceNotFoundException
import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.domain.model.ParkingStatus
import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.model.Spot
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import br.com.wsp.parking.domain.service.PricingService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDateTime

@DisplayName("VehicleExitUseCaseImpl - Testes Unitários")
class VehicleExitUseCaseImplTest {

    private lateinit var sectorRepository: SectorRepository
    private lateinit var spotRepository: SpotRepository
    private lateinit var parkingRecordRepository: ParkingRecordRepository
    private lateinit var pricingService: PricingService
    private lateinit var useCase: VehicleExitUseCaseImpl

    @BeforeEach
    fun setup() {
        sectorRepository = mock()
        spotRepository = mock()
        parkingRecordRepository = mock()
        pricingService = mock()
        useCase = VehicleExitUseCaseImpl(
            sectorRepository,
            spotRepository,
            parkingRecordRepository,
            pricingService
        )
    }

    @Test
    @DisplayName("Deve processar saída de veículo com sucesso")
    fun `should process vehicle exit successfully`() {
        val licensePlate = "ABC1234"
        val entryTime = LocalDateTime.now().minusHours(2)
        val exitTime = LocalDateTime.now()
        val record = createParkingRecord(1L, licensePlate, "A", 1L, entryTime)
        val spot = createSpot(1L, "A", true)
        val totalAmount = BigDecimal("20.00")

        whenever(parkingRecordRepository.findActiveByLicensePlate(licensePlate)).thenReturn(record)
        whenever(spotRepository.findById(1L)).thenReturn(spot)
        whenever(pricingService.calculateParkingFee(any(), any())).thenReturn(totalAmount)
        whenever(parkingRecordRepository.save(any())).thenReturn(record)

        useCase.execute(licensePlate, exitTime)

        verify(parkingRecordRepository).findActiveByLicensePlate(licensePlate)
        verify(spotRepository).save(any())
        verify(parkingRecordRepository).save(any())
    }

    @Test
    @DisplayName("Deve lançar exceção quando não há registro ativo")
    fun `should throw exception when no active record found`() {
        val licensePlate = "ABC1234"
        val exitTime = LocalDateTime.now()

        whenever(parkingRecordRepository.findActiveByLicensePlate(licensePlate)).thenReturn(null)

        val exception = assertThrows<ResourceNotFoundException> {
            useCase.execute(licensePlate, exitTime)
        }

        assertEquals("Nenhum registro de estacionamento ativo encontrado para a placa: $licensePlate", exception.message)
    }

    @Test
    @DisplayName("Deve liberar vaga ao processar saída")
    fun `should free spot when processing exit`() {
        val licensePlate = "ABC1234"
        val entryTime = LocalDateTime.now().minusHours(1)
        val exitTime = LocalDateTime.now()
        val record = createParkingRecord(1L, licensePlate, "A", 1L, entryTime)
        val spot = createSpot(1L, "A", true)

        whenever(parkingRecordRepository.findActiveByLicensePlate(licensePlate)).thenReturn(record)
        whenever(spotRepository.findById(1L)).thenReturn(spot)
        whenever(pricingService.calculateParkingFee(any(), any())).thenReturn(BigDecimal("10.00"))
        whenever(parkingRecordRepository.save(any())).thenReturn(record)

        useCase.execute(licensePlate, exitTime)

        verify(spotRepository).save(argThat { !this.occupied })
    }

    @Test
    @DisplayName("Deve reabrir setor quando estava fechado")
    fun `should reopen sector when it was closed`() {
        val licensePlate = "ABC1234"
        val entryTime = LocalDateTime.now().minusHours(1)
        val exitTime = LocalDateTime.now()
        val record = createParkingRecord(1L, licensePlate, "A", 1L, entryTime)
        val spot = createSpot(1L, "A", true)
        val sector = createSector("A", false)

        whenever(parkingRecordRepository.findActiveByLicensePlate(licensePlate)).thenReturn(record)
        whenever(spotRepository.findById(1L)).thenReturn(spot)
        whenever(sectorRepository.findByName("A")).thenReturn(sector)
        whenever(pricingService.calculateParkingFee(any(), any())).thenReturn(BigDecimal("10.00"))
        whenever(parkingRecordRepository.save(any())).thenReturn(record)

        useCase.execute(licensePlate, exitTime)

        verify(sectorRepository).save(argThat { this.isOpen })
    }

    private fun createParkingRecord(
        id: Long,
        licensePlate: String,
        sectorName: String,
        spotId: Long?,
        entryTime: LocalDateTime
    ) = ParkingRecord(
        id = id,
        licensePlate = licensePlate,
        sectorName = sectorName,
        spotId = spotId,
        entryTime = entryTime,
        appliedPrice = BigDecimal("10.00"),
        status = ParkingStatus.PARKED
    )

    private fun createSpot(id: Long, sectorName: String, occupied: Boolean) = Spot(
        id = id,
        sectorName = sectorName,
        lat = -23.561684,
        lng = -46.655981,
        occupied = occupied
    )

    private fun createSector(name: String, isOpen: Boolean) = Sector(
        name = name,
        basePrice = BigDecimal("10.00"),
        maxCapacity = 100,
        openHour = "08:00",
        closeHour = "18:00",
        durationLimitMinutes = 480,
        isOpen = isOpen
    )
}
