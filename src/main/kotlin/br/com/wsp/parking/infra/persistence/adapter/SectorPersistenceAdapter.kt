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
        log.debug("Salvando setor: nome=${sector.name}, capacidade=${sector.maxCapacity}, aberto=${sector.isOpen}")
        val saved = jpaRepository.save(sector.toEntity()).toDomain()
        log.debug("Setor salvo: nome=${saved.name}")
        return saved
    }

    @Cacheable(value = ["sectors"], key = "#name")
    override fun findByName(name: String): Sector? {
        log.debug("Buscando setor por nome: nome=$name")
        val sector = jpaRepository.findByName(name)?.toDomain()
        
        if (sector != null) {
            log.debug("Setor encontrado: nome=$name")
        } else {
            log.debug("Setor não encontrado: nome=$name")
        }
        
        return sector
    }

    @Cacheable(value = ["sectors"], key = "'all'")
    override fun findAll(): List<Sector> {
        log.debug("Buscando todos os setores")
        val sectors = jpaRepository.findAll().map { it.toDomain() }
        log.debug("Setores encontrados: quantidade=${sectors.size}")
        return sectors
    }

    override fun existsByName(name: String): Boolean {
        val exists = jpaRepository.existsByName(name)
        log.debug("Verificação de existência do setor: nome=$name, existe=$exists")
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