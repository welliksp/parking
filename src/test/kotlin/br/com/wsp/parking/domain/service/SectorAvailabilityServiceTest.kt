package br.com.wsp.parking.domain.service

import br.com.wsp.parking.domain.exception.UnavailableResourceException
import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.domain.port.out.SpotRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import java.math.BigDecimal

@DisplayName("SectorAvailabilityService - Testes Unitários")
class SectorAvailabilityServiceTest {

    private lateinit var sectorRepository: SectorRepository
    private lateinit var spotRepository: SpotRepository
    private lateinit var service: SectorAvailabilityService

    @BeforeEach
    fun setup() {
        sectorRepository = mock()
        spotRepository = mock()
        service = SectorAvailabilityService(sectorRepository, spotRepository)
    }

    @Test
    @DisplayName("Deve retornar setor disponível quando há vagas")
    fun `should return available sector when spots are available`() {
        val sector = createSector("A", 100, true)
        whenever(sectorRepository.findAll()).thenReturn(listOf(sector))
        whenever(spotRepository.countOccupiedBySector("A")).thenReturn(50)

        val (foundSector, occupied) = service.findAvailableSector()

        assertEquals(sector, foundSector)
        assertEquals(50, occupied)
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum setor está aberto")
    fun `should throw exception when no sector is open`() {
        val sector = createSector("A", 100, false)
        whenever(sectorRepository.findAll()).thenReturn(listOf(sector))

        val exception = assertThrows<UnavailableResourceException> {
            service.findAvailableSector()
        }

        assertEquals("Nenhum setor está aberto no momento", exception.message)
    }

    @Test
    @DisplayName("Deve lançar exceção quando todos os setores estão lotados")
    fun `should throw exception when all sectors are full`() {
        val sector = createSector("A", 100, true)
        whenever(sectorRepository.findAll()).thenReturn(listOf(sector))
        whenever(spotRepository.countOccupiedBySector("A")).thenReturn(100)

        val exception = assertThrows<UnavailableResourceException> {
            service.findAvailableSector()
        }

        assertTrue(exception.message!!.contains("Nenhuma vaga disponível"))
    }

    @Test
    @DisplayName("Deve retornar primeiro setor disponível quando há múltiplos setores")
    fun `should return first available sector when multiple sectors exist`() {
        val sectorA = createSector("A", 100, true)
        val sectorB = createSector("B", 50, true)
        whenever(sectorRepository.findAll()).thenReturn(listOf(sectorA, sectorB))
        whenever(spotRepository.countOccupiedBySector("A")).thenReturn(100)
        whenever(spotRepository.countOccupiedBySector("B")).thenReturn(30)

        val (foundSector, occupied) = service.findAvailableSector()

        assertEquals(sectorB, foundSector)
        assertEquals(30, occupied)
    }

    private fun createSector(name: String, maxCapacity: Int, isOpen: Boolean) = Sector(
        name = name,
        basePrice = BigDecimal("10.00"),
        maxCapacity = maxCapacity,
        openHour = "08:00",
        closeHour = "18:00",
        durationLimitMinutes = 480,
        isOpen = isOpen
    )
}
