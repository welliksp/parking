package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.Sector
import br.com.wsp.parking.domain.port.out.SectorRepository
import br.com.wsp.parking.infra.persistence.entity.SectorJpaEntity
import br.com.wsp.parking.infra.persistence.repository.SectorJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component


@Component
class SectorPersistenceAdapter(
    private val jpaRepository: SectorJpaRepository
) : SectorRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    @CacheEvict(value = ["sectors"], allEntries = true)
    override fun save(sector: Sector): Sector {
        log.debug("Saving sector: name=${sector.name}, capacity=${sector.maxCapacity}, isOpen=${sector.isOpen}")
        val saved = jpaRepository.save(sector.toEntity()).toDomain()
        log.debug("Sector saved: name=${saved.name}")
        return saved
    }

    @Cacheable(value = ["sectors"], key = "#name")
    override fun findByName(name: String): Sector? {
        log.debug("Finding sector by name: name=$name")
        val sector = jpaRepository.findByName(name)?.toDomain()
        
        if (sector != null) {
            log.debug("Sector found: name=$name")
        } else {
            log.debug("Sector not found: name=$name")
        }
        
        return sector
    }

    @Cacheable(value = ["sectors"], key = "'all'")
    override fun findAll(): List<Sector> {
        log.debug("Finding all sectors")
        val sectors = jpaRepository.findAll().map { it.toDomain() }
        log.debug("Sectors found: count=${sectors.size}")
        return sectors
    }

    override fun existsByName(name: String): Boolean {
        val exists = jpaRepository.existsByName(name)
        log.debug("Sector existence check: name=$name, exists=$exists")
        return exists
    }

    private fun Sector.toEntity() = SectorJpaEntity(
        name = name,
        basePrice = basePrice,
        maxCapacity = maxCapacity,
        openHour = openHour,
        closeHour = closeHour,
        durationLimitMinutes = durationLimitMinutes,
        isOpen = isOpen
    )

    private fun SectorJpaEntity.toDomain() = Sector(
        name = name,
        basePrice = basePrice,
        maxCapacity = maxCapacity,
        openHour = openHour,
        closeHour = closeHour,
        durationLimitMinutes = durationLimitMinutes,
        isOpen = isOpen
    )
}