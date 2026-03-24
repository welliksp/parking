package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.infra.persistence.entity.SectorEntity
import br.com.wsp.parking.infra.persistence.repository.SectorJpaRepository
import org.springframework.stereotype.Component

@Component
class SectorPersistenceAdapter(
    private val jpaRepository: SectorJpaRepository
) : SectorRepository {

    override fun save(sector: Sector): Sector =
        jpaRepository.save(sector.toEntity()).toDomain()

    override fun findByName(name: String): Sector? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<Sector> {
        TODO("Not yet implemented")
    }

    override fun existsByName(name: String): Boolean =
        jpaRepository.existsByName(name)

    private fun Sector.toEntity() = SectorEntity(
        name = name,
        basePrice = basePrice,
        maxCapacity = maxCapacity,
        openHour = openHour,
        closeHour = closeHour,
        durationLimitMinutes = durationLimitMinutes,
        isOpen = isOpen
    )

    private fun SectorEntity.toDomain() = Sector(
        name = name,
        basePrice = basePrice,
        maxCapacity = maxCapacity,
        openHour = openHour,
        closeHour = closeHour,
        durationLimitMinutes = durationLimitMinutes,
        isOpen = isOpen
    )
}