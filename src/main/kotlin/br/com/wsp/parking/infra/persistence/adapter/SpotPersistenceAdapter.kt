package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.Spot
import br.com.wsp.parking.domain.port.out.SpotRepository
import br.com.wsp.parking.infra.persistence.entity.SpotJpaEntity
import br.com.wsp.parking.infra.persistence.repository.SpotJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component


@Component
class SpotPersistenceAdapter(
    private val jpaRepository: SpotJpaRepository
) : SpotRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(spot: Spot): Spot {
        log.debug("Salvando vaga: id=${spot.id}, setor=${spot.sectorName}, ocupada=${spot.occupied}")
        val saved = jpaRepository.save(spot.toEntity()).toDomain()
        log.debug("Vaga salva: id=${saved.id}")
        return saved
    }

    override fun findById(id: Long): Spot? {
        log.debug("Buscando vaga por id: id=$id")
        val spot = jpaRepository.findById(id).map { it.toDomain() }.orElse(null)
        
        if (spot != null) {
            log.debug("Vaga encontrada: id=$id, setor=${spot.sectorName}")
        } else {
            log.debug("Vaga não encontrada: id=$id")
        }
        
        return spot
    }

    override fun findByLatAndLng(lat: Double, lng: Double): Spot? {
        log.debug("Buscando vaga por coordenadas: lat=$lat, lng=$lng")
        val spot = jpaRepository.findByLatAndLng(lat, lng)?.toDomain()
        
        if (spot != null) {
            log.debug("Vaga encontrada nas coordenadas: id=${spot.id}, lat=$lat, lng=$lng")
        } else {
            log.debug("Nenhuma vaga encontrada nas coordenadas: lat=$lat, lng=$lng")
        }
        
        return spot
    }

    override fun countOccupiedBySector(sectorName: String): Int {
        log.debug("Contando vagas ocupadas: setor=$sectorName")
        val count = jpaRepository.countOccupiedBySector(sectorName)
        log.debug("Vagas ocupadas contadas: setor=$sectorName, quantidade=$count")
        return count
    }

    override fun countOccupied(): Int {
        log.debug("Contando total de vagas ocupadas")
        val count = jpaRepository.countOccupied()
        log.debug("Total de vagas ocupadas contadas: quantidade=$count")
        return count
    }

    override fun existsById(id: Long): Boolean {
        val exists = jpaRepository.existsById(id)
        log.debug("Verificação de existência da vaga: id=$id, existe=$exists")
        return exists
    }

    private fun Spot.toEntity() = SpotJpaEntity(
        sectorName = sectorName,
        lat = lat,
        lng = lng,
        occupied = occupied
    )

    private fun SpotJpaEntity.toDomain() = Spot(
        id = id,
        sectorName = sectorName,
        lat = lat,
        lng = lng,
        occupied = occupied
    )
}