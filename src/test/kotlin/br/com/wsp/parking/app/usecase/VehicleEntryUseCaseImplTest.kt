package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.exception.InvalidInputException
import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.domain.service.PricingService
import br.com.wsp.parking.domain.service.SectorAvailabilityService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.mock
import org.mockito.kotlin.*
import java.math.BigDecimal
import java.time.LocalDateTime

@DisplayName("VehicleEntryUseCaseImpl - Testes Unitários")
class VehicleEntryUseCaseImplTest {

    private lateinit var sectorAvailabilityService: SectorAvailabilityService
    private lateinit var pricingService: PricingService
    private lateinit var parkingRecordRepository: ParkingRecordRepository
    private lateinit var useCase: VehicleEntryUseCaseImpl

    @BeforeEach
    fun setup() {
        sectorAvailabilityService = mock()
        pricingService = mock()
        parkingRecordRepository = mock()
        useCase = VehicleEntryUseCaseImpl(
            sectorAvailabilityService,
            pricingService,
            parkingRecordRepository
        )
    }

    @Test
    @DisplayName("Deve processar entrada de veículo com sucesso")
    fun `should process vehicle entry successfully`() {
        val licensePlate = "ABC1234"
        val entryTime = LocalDateTime.now()
        val sector = createSector("A")
        val appliedPrice = BigDecimal("10.00")
        val savedRecord = createParkingRecord(1L, licensePlate, "A", appliedPrice)

        whenever(sectorAvailabilityService.findAvailableSector()).thenReturn(Pair(sector, 50))
        whenever(pricingService.calculateEntryPrice(sector, 50)).thenReturn(appliedPrice)
        whenever(parkingRecordRepository.save(any())).thenReturn(savedRecord)

        useCase.execute(licensePlate, entryTime)

        verify(sectorAvailabilityService).findAvailableSector()
        verify(pricingService).calculateEntryPrice(sector, 50)
        verify(parkingRecordRepository).save(any())
    }

    @Test
    @DisplayName("Deve lançar exceção quando placa está vazia")
    fun `should throw exception when license plate is empty`() {
        val licensePlate = ""
        val entryTime = LocalDateTime.now()

        val exception = assertThrows<InvalidInputException> {
            useCase.execute(licensePlate, entryTime)
        }

        assertEquals("Placa do veículo não pode estar vazia", exception.message)
    }

    @Test
    @DisplayName("Deve lançar exceção quando horário de entrada está no futuro")
    fun `should throw exception when entry time is in future`() {
        val licensePlate = "ABC1234"
        val entryTime = LocalDateTime.now().plusHours(1)

        val exception = assertThrows<InvalidInputException> {
            useCase.execute(licensePlate, entryTime)
        }

        assertEquals("Horário de entrada não pode estar no futuro", exception.message)
    }

    @Test
    @DisplayName("Deve calcular preço correto baseado na ocupação")
    fun `should calculate correct price based on occupancy`() {
        val licensePlate = "ABC1234"
        val entryTime = LocalDateTime.now()
        val sector = createSector("A")
        val appliedPrice = BigDecimal("12.50")
        val savedRecord = createParkingRecord(1L, licensePlate, "A", appliedPrice)

        whenever(sectorAvailabilityService.findAvailableSector()).thenReturn(Pair(sector, 90))
        whenever(pricingService.calculateEntryPrice(sector, 90)).thenReturn(appliedPrice)
        whenever(parkingRecordRepository.save(any())).thenReturn(savedRecord)

        useCase.execute(licensePlate, entryTime)

        verify(pricingService).calculateEntryPrice(sector, 90)
    }

    private fun createSector(name: String) = Sector(
        name = name,
        basePrice = BigDecimal("10.00"),
        maxCapacity = 100,
        openHour = "08:00",
        closeHour = "18:00",
        durationLimitMinutes = 480
    )

    private fun createParkingRecord(
        id: Long,
        licensePlate: String,
        sectorName: String,
        appliedPrice: BigDecimal
    ) = ParkingRecord(
        id = id,
        licensePlate = licensePlate,
        sectorName = sectorName,
        entryTime = LocalDateTime.now(),
        appliedPrice = appliedPrice
    )
}
