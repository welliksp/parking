package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@DisplayName("GetRevenueUseCaseImpl - Testes Unitários")
class GetRevenueUseCaseImplTest {

    private lateinit var parkingRecordRepository: ParkingRecordRepository
    private lateinit var useCase: GetRevenueUseCaseImpl

    @BeforeEach
    fun setup() {
        parkingRecordRepository = mock()
        useCase = GetRevenueUseCaseImpl(parkingRecordRepository)
    }

    @Test
    @DisplayName("Deve calcular receita corretamente")
    fun `should calculate revenue correctly`() {
        val sector = "A"
        val date = LocalDate.of(2025, 1, 1)
        val expectedRevenue = BigDecimal("150.00")
        val from = date.atStartOfDay()
        val to = date.atTime(23, 59, 59, 999999999)

        whenever(parkingRecordRepository.sumRevenueBySectorAndDateRange(sector, from, to))
            .thenReturn(expectedRevenue)

        val revenue = useCase.execute(sector, date)

        assertEquals(expectedRevenue, revenue)
    }

    @Test
    @DisplayName("Deve retornar zero quando não há receita")
    fun `should return zero when no revenue`() {
        val sector = "A"
        val date = LocalDate.of(2025, 1, 1)
        val from = date.atStartOfDay()
        val to = date.atTime(23, 59, 59, 999999999)

        whenever(parkingRecordRepository.sumRevenueBySectorAndDateRange(sector, from, to))
            .thenReturn(BigDecimal.ZERO)

        val revenue = useCase.execute(sector, date)

        assertEquals(BigDecimal.ZERO, revenue)
    }

    @Test
    @DisplayName("Deve usar intervalo de data correto")
    fun `should use correct date range`() {
        val sector = "A"
        val date = LocalDate.of(2025, 1, 15)
        val expectedFrom = date.atStartOfDay()
        val expectedTo = date.atTime(23, 59, 59, 999999999)

        whenever(parkingRecordRepository.sumRevenueBySectorAndDateRange(sector, expectedFrom, expectedTo))
            .thenReturn(BigDecimal("100.00"))

        useCase.execute(sector, date)

        val sectorCaptor = argumentCaptor<String>()
        val fromCaptor = argumentCaptor<LocalDateTime>()
        val toCaptor = argumentCaptor<LocalDateTime>()
        
        verify(parkingRecordRepository).sumRevenueBySectorAndDateRange(
            sectorCaptor.capture(),
            fromCaptor.capture(),
            toCaptor.capture()
        )

        assertEquals(sector, sectorCaptor.firstValue)
        assertEquals(expectedFrom, fromCaptor.firstValue)
        assertEquals(expectedTo, toCaptor.firstValue)
    }
}
