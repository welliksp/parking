package br.com.wsp.parking.domain.service

import br.com.wsp.parking.domain.model.Sector
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("PricingService - Testes Unitários")
class PricingServiceTest {

    private lateinit var pricingService: PricingService
    private lateinit var sector: Sector

    @BeforeEach
    fun setup() {
        pricingService = PricingService()
        sector = Sector(
            name = "A",
            basePrice = BigDecimal("10.00"),
            maxCapacity = 100,
            openHour = "08:00",
            closeHour = "18:00",
            durationLimitMinutes = 480
        )
    }

    @Test
    @DisplayName("Deve aplicar desconto de 10% quando lotação menor que 25%")
    fun `should apply 10 percent discount when occupancy less than 25 percent`() {
        val currentOccupied = 20
        val price = pricingService.calculateEntryPrice(sector, currentOccupied)
        assertEquals(BigDecimal("9.00"), price)
    }

    @Test
    @DisplayName("Deve manter preço normal quando lotação entre 25% e 50%")
    fun `should keep normal price when occupancy between 25 and 50 percent`() {
        val currentOccupied = 40
        val price = pricingService.calculateEntryPrice(sector, currentOccupied)
        assertEquals(BigDecimal("10.00"), price)
    }

    @Test
    @DisplayName("Deve aumentar 10% quando lotação entre 50% e 75%")
    fun `should increase 10 percent when occupancy between 50 and 75 percent`() {
        val currentOccupied = 60
        val price = pricingService.calculateEntryPrice(sector, currentOccupied)
        assertEquals(BigDecimal("11.00"), price)
    }

    @Test
    @DisplayName("Deve aumentar 25% quando lotação entre 75% e 100%")
    fun `should increase 25 percent when occupancy between 75 and 100 percent`() {
        val currentOccupied = 90
        val price = pricingService.calculateEntryPrice(sector, currentOccupied)
        assertEquals(BigDecimal("12.50"), price)
    }

    @Test
    @DisplayName("Deve retornar zero quando duração menor ou igual a 30 minutos")
    fun `should return zero when duration less than or equal to 30 minutes`() {
        val appliedPrice = BigDecimal("10.00")
        val durationMinutes = 30L
        val fee = pricingService.calculateParkingFee(appliedPrice, durationMinutes)
        assertEquals(BigDecimal.ZERO, fee)
    }

    @Test
    @DisplayName("Deve cobrar 1 hora quando duração entre 31 e 90 minutos")
    fun `should charge 1 hour when duration between 31 and 90 minutes`() {
        val appliedPrice = BigDecimal("10.00")
        val durationMinutes = 45L
        val fee = pricingService.calculateParkingFee(appliedPrice, durationMinutes)
        assertEquals(BigDecimal("10.00"), fee)
    }

    @Test
    @DisplayName("Deve cobrar 2 horas quando duração entre 91 e 150 minutos")
    fun `should charge 2 hours when duration between 91 and 150 minutes`() {
        val appliedPrice = BigDecimal("10.00")
        val durationMinutes = 120L
        val fee = pricingService.calculateParkingFee(appliedPrice, durationMinutes)
        assertEquals(BigDecimal("20.00"), fee)
    }

    @Test
    @DisplayName("Deve arredondar para cima quando duração não é múltiplo de hora")
    fun `should round up when duration is not multiple of hour`() {
        val appliedPrice = BigDecimal("10.00")
        val durationMinutes = 91L
        val fee = pricingService.calculateParkingFee(appliedPrice, durationMinutes)
        assertEquals(BigDecimal("20.00"), fee)
    }
}
