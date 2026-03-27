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
        log.debug("Saving spot: id=${spot.id}, sector=${spot.sectorName}, occupied=${spot.occupied}")
        val saved = jpaRepository.save(spot.toEntity()).toDomain()
        log.debug("Spot saved: id=${saved.id}")
        return saved
    }

    override fun findById(id: Long): Spot? {
        log.debug("Finding spot by id: id=$id")
        val spot = jpaRepository.findById(id).map { it.toDomain() }.orElse(null)
        
        if (spot != null) {
            log.debug("Spot found: id=$id, sector=${spot.sectorName}")
        } else {
            log.debug("Spot not found: id=$id")
        }
        
        return spot
    }

    override fun findByLatAndLng(lat: Double, lng: Double): Spot? {
        log.debug("Finding spot by coordinates: lat=$lat, lng=$lng")
        val spot = jpaRepository.findByLatAndLng(lat, lng)?.toDomain()
        
        if (spot != null) {
            log.debug("Spot found at coordinates: id=${spot.id}, lat=$lat, lng=$lng")
        } else {
            log.debug("No spot found at coordinates: lat=$lat, lng=$lng")
        }
        
        return spot
    }

    override fun countOccupiedBySector(sectorName: String): Int {
        log.debug("Counting occupied spots: sector=$sectorName")
        val count = jpaRepository.countOccupiedBySector(sectorName)
        log.debug("Occupied spots counted: sector=$sectorName, count=$count")
        return count
    }

    override fun existsById(id: Long): Boolean {
        val exists = jpaRepository.existsById(id)
        log.debug("Spot existence check: id=$id, exists=$exists")
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