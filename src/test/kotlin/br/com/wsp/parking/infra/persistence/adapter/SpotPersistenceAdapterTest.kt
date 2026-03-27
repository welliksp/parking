package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.Spot
import br.com.wsp.parking.infra.persistence.entity.SpotJpaEntity
import br.com.wsp.parking.infra.persistence.repository.SpotJpaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.util.*

@DisplayName("SpotPersistenceAdapter - Testes Unitários")
class SpotPersistenceAdapterTest {

    private lateinit var jpaRepository: SpotJpaRepository
    private lateinit var adapter: SpotPersistenceAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mock()
        adapter = SpotPersistenceAdapter(jpaRepository)
    }

    @Test
    @DisplayName("Deve salvar vaga com sucesso")
    fun `should save spot successfully`() {
        val spot = createSpot(1L, "A", false)
        val entity = createEntity(1L, "A", false)

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        val saved = adapter.save(spot)

        assertEquals(1L, saved.id)
        assertEquals("A", saved.sectorName)
        assertFalse(saved.occupied)
        verify(jpaRepository).save(org.mockito.kotlin.any())
    }

    @Test
    @DisplayName("Deve buscar vaga por ID")
    fun `should find spot by id`() {
        val entity = createEntity(1L, "A", false)

        whenever(jpaRepository.findById(1L)).thenReturn(Optional.of(entity))

        val found = adapter.findById(1L)

        assertNotNull(found)
        assertEquals(1L, found?.id)
        assertEquals("A", found?.sectorName)
    }

    @Test
    @DisplayName("Deve retornar null quando vaga não existe")
    fun `should return null when spot does not exist`() {
        whenever(jpaRepository.findById(999L)).thenReturn(Optional.empty())

        val found = adapter.findById(999L)

        assertNull(found)
    }

    @Test
    @DisplayName("Deve buscar vaga por coordenadas")
    fun `should find spot by coordinates`() {
        val entity = createEntity(1L, "A", false)
        val lat = -23.561684
        val lng = -46.655981

        whenever(jpaRepository.findByLatAndLng(lat, lng)).thenReturn(entity)

        val found = adapter.findByLatAndLng(lat, lng)

        assertNotNull(found)
        assertEquals(lat, found?.lat)
        assertEquals(lng, found?.lng)
    }

    @Test
    @DisplayName("Deve contar vagas ocupadas por setor")
    fun `should count occupied spots by sector`() {
        whenever(jpaRepository.countOccupiedBySector("A")).thenReturn(45)

        val count = adapter.countOccupiedBySector("A")

        assertEquals(45, count)
    }

    @Test
    @DisplayName("Deve verificar se vaga existe por ID")
    fun `should check if spot exists by id`() {
        whenever(jpaRepository.existsById(1L)).thenReturn(true)
        whenever(jpaRepository.existsById(999L)).thenReturn(false)

        assertTrue(adapter.existsById(1L))
        assertFalse(adapter.existsById(999L))
    }

    @Test
    @DisplayName("Deve marcar vaga como ocupada")
    fun `should mark spot as occupied`() {
        val spot = createSpot(1L, "A", false)
        val occupiedSpot = spot.occupy()
        val entity = createEntity(1L, "A", true)

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        val saved = adapter.save(occupiedSpot)

        assertTrue(saved.occupied)
    }

    @Test
    @DisplayName("Deve marcar vaga como livre")
    fun `should mark spot as free`() {
        val spot = createSpot(1L, "A", true)
        val freeSpot = spot.free()
        val entity = createEntity(1L, "A", false)

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        val saved = adapter.save(freeSpot)

        assertFalse(saved.occupied)
    }

    private fun createSpot(id: Long, sectorName: String, occupied: Boolean) = Spot(
        id = id,
        sectorName = sectorName,
        lat = -23.561684,
        lng = -46.655981,
        occupied = occupied
    )

    private fun createEntity(id: Long, sectorName: String, occupied: Boolean) = SpotJpaEntity(
        id = id,
        sectorName = sectorName,
        lat = -23.561684,
        lng = -46.655981,
        occupied = occupied
    )
}
