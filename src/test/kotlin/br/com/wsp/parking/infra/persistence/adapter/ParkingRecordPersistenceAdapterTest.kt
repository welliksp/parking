package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.domain.model.ParkingStatus
import br.com.wsp.parking.infra.persistence.entity.ParkingRecordJpaEntity
import br.com.wsp.parking.infra.persistence.repository.ParkingRecordJpaRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@DisplayName("ParkingRecordPersistenceAdapter - Testes Unitários")
class ParkingRecordPersistenceAdapterTest {

    private lateinit var jpaRepository: ParkingRecordJpaRepository
    private lateinit var adapter: ParkingRecordPersistenceAdapter

    @BeforeEach
    fun setup() {
        jpaRepository = mock()
        adapter = ParkingRecordPersistenceAdapter(jpaRepository)
    }

    @Test
    @DisplayName("Deve salvar novo registro com sucesso")
    fun `should save new record successfully`() {
        val record = createRecord(0L, "ABC1234", "A")
        val entity = createEntity(1L, "ABC1234", "A")

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        val saved = adapter.save(record)

        assertEquals(1L, saved.id)
        assertEquals("ABC1234", saved.licensePlate)
        verify(jpaRepository).save(org.mockito.kotlin.any())
    }

    @Test
    @DisplayName("Deve atualizar registro existente")
    fun `should update existing record`() {
        val existingEntity = createEntity(1L, "ABC1234", "A")
        val record = createRecord(1L, "ABC1234", "A").copy(
            spotId = 5L,
            status = ParkingStatus.PARKED
        )

        whenever(jpaRepository.findById(1L)).thenReturn(Optional.of(existingEntity))
        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(existingEntity)

        val updated = adapter.save(record)

        assertEquals(1L, updated.id)
        verify(jpaRepository).findById(1L)
        verify(jpaRepository).save(org.mockito.kotlin.any())
    }

    @Test
    @DisplayName("Deve buscar registro ativo por placa")
    fun `should find active record by license plate`() {
        val entity = createEntity(1L, "ABC1234", "A")

        whenever(jpaRepository.findActiveByLicensePlate("ABC1234")).thenReturn(entity)

        val found = adapter.findActiveByLicensePlate("ABC1234")

        assertNotNull(found)
        assertEquals("ABC1234", found?.licensePlate)
        assertEquals(ParkingStatus.ENTERED, found?.status)
    }

    @Test
    @DisplayName("Deve retornar null quando não há registro ativo")
    fun `should return null when no active record`() {
        whenever(jpaRepository.findActiveByLicensePlate("XYZ9999")).thenReturn(null)

        val found = adapter.findActiveByLicensePlate("XYZ9999")

        assertNull(found)
    }

    @Test
    @DisplayName("Deve somar receita por setor e intervalo de datas")
    fun `should sum revenue by sector and date range`() {
        val from = LocalDateTime.of(2025, 1, 1, 0, 0)
        val to = LocalDateTime.of(2025, 1, 1, 23, 59)
        val expectedRevenue = BigDecimal("150.00")

        whenever(jpaRepository.sumRevenueBySectorAndDateRange("A", from, to))
            .thenReturn(expectedRevenue)

        val revenue = adapter.sumRevenueBySectorAndDateRange("A", from, to)

        assertEquals(expectedRevenue, revenue)
    }

    @Test
    @DisplayName("Deve converter status corretamente de domínio para entidade")
    fun `should convert status correctly from domain to entity`() {
        val record = createRecord(0L, "ABC1234", "A").copy(
            status = ParkingStatus.PARKED
        )
        val entity = createEntity(1L, "ABC1234", "A")

        whenever(jpaRepository.save(org.mockito.kotlin.any())).thenReturn(entity)

        adapter.save(record)

        verify(jpaRepository).save(org.mockito.kotlin.argThat {
            this.status == br.com.wsp.parking.infra.persistence.entity.enums.ParkingStatus.ENTERED
        })
    }

    @Test
    @DisplayName("Deve lançar exceção quando tentar atualizar registro inexistente")
    fun `should throw exception when updating non-existent record`() {
        val record = createRecord(999L, "ABC1234", "A")

        whenever(jpaRepository.findById(999L)).thenReturn(Optional.empty())

        assertThrows(IllegalStateException::class.java) {
            adapter.save(record)
        }
    }

    private fun createRecord(id: Long, licensePlate: String, sectorName: String) = ParkingRecord(
        id = id,
        licensePlate = licensePlate,
        sectorName = sectorName,
        entryTime = LocalDateTime.now(),
        appliedPrice = BigDecimal("10.00"),
        status = ParkingStatus.ENTERED
    )

    private fun createEntity(id: Long, licensePlate: String, sectorName: String) = ParkingRecordJpaEntity(
        id = id,
        licensePlate = licensePlate,
        sectorName = sectorName,
        entryTime = LocalDateTime.now(),
        appliedPrice = BigDecimal("10.00"),
        status = br.com.wsp.parking.infra.persistence.entity.enums.ParkingStatus.ENTERED
    )
}
