package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.infra.persistence.entity.SectorJpaEntity
import br.com.wsp.parking.infra.persistence.repository.SectorJpaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.util.*

@DisplayName("SectorPersistenceAdapter - Testes Unitários")
class SectorPersistenceAdapterTest {

    private lateinit var jpaRepository: SectorJpaRepository
    private lateinit var adapter: SectorPersistenceAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mock()
        adapter = SectorPersistenceAdapter(jpaRepository)
    }

    @Test
    @DisplayName("Deve salvar setor com sucesso")
    fun `should save sector successfully`() {
        val sector = createSector("A")
        val entity = createEntity("A")

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        val saved = adapter.save(sector)

        assertEquals("A", saved.name)
        assertEquals(BigDecimal("10.00"), saved.basePrice)
        verify(jpaRepository).save(org.mockito.kotlin.any())
    }

    @Test
    @DisplayName("Deve buscar setor por nome")
    fun `should find sector by name`() {
        val entity = createEntity("A")

        whenever(jpaRepository.findByName("A")).thenReturn(entity)

        val found = adapter.findByName("A")

        assertNotNull(found)
        assertEquals("A", found?.name)
    }

    @Test
    @DisplayName("Deve retornar null quando setor não existe")
    fun `should return null when sector does not exist`() {
        whenever(jpaRepository.findByName("Z")).thenReturn(null)

        val found = adapter.findByName("Z")

        assertNull(found)
    }

    @Test
    @DisplayName("Deve buscar todos os setores")
    fun `should find all sectors`() {
        val entities = listOf(
            createEntity("A"),
            createEntity("B")
        )

        whenever(jpaRepository.findAll()).thenReturn(entities)

        val sectors = adapter.findAll()

        assertEquals(2, sectors.size)
        assertEquals("A", sectors[0].name)
        assertEquals("B", sectors[1].name)
    }

    @Test
    @DisplayName("Deve verificar se setor existe por nome")
    fun `should check if sector exists by name`() {
        whenever(jpaRepository.existsByName("A")).thenReturn(true)
        whenever(jpaRepository.existsByName("Z")).thenReturn(false)

        assertTrue(adapter.existsByName("A"))
        assertFalse(adapter.existsByName("Z"))
    }

    @Test
    @DisplayName("Deve converter corretamente de domínio para entidade")
    fun `should convert correctly from domain to entity`() {
        val sector = createSector("A")
        val entity = createEntity("A")

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        adapter.save(sector)

        verify(jpaRepository).save(org.mockito.kotlin.argThat {
            this.name == "A" &&
            this.basePrice == BigDecimal("10.00") &&
            this.maxCapacity == 100
        })
    }

    private fun createSector(name: String) = Sector(
        name = name,
        basePrice = BigDecimal("10.00"),
        maxCapacity = 100,
        openHour = "08:00",
        closeHour = "18:00",
        durationLimitMinutes = 480,
        isOpen = true
    )

    private fun createEntity(name: String) = SectorJpaEntity(
        id = 1L,
        name = name,
        basePrice = BigDecimal("10.00"),
        maxCapacity = 100,
        openHour = "08:00",
        closeHour = "18:00",
        durationLimitMinutes = 480,
        isOpen = true
    )
}
