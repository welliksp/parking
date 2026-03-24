package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.Spot
import br.com.wsp.parking.domain.port.out.SpotRepository
import br.com.wsp.parking.infra.persistence.entity.SpotEntity
import br.com.wsp.parking.infra.persistence.repository.SpotJpaRepository
import org.springframework.stereotype.Component

@Component
class SpotPersistenceAdapter(
    private val jpaRepository: SpotJpaRepository
) : SpotRepository {
    override fun save(spot: Spot): Spot =
        jpaRepository.save(spot.toEntity()).toDomain()

    override fun findById(id: Long): Spot? {
        TODO("Not yet implemented")
    }

    override fun findByLatAndLng(lat: Double, lng: Double): Spot? {
        TODO("Not yet implemented")
    }

    override fun countOccupiedBySector(sectorName: String): Int {
        TODO("Not yet implemented")
    }

    override fun existsById(id: Long): Boolean = jpaRepository.existsById(id)


    private fun Spot.toEntity() = SpotEntity(
        sectorName = sectorName,
        lat = lat,
        lng = lng,
        occupied = occupied
    )

    private fun SpotEntity.toDomain() = Spot(
        id = id,
        sectorName = sectorName,
        lat = lat,
        lng = lng,
        occupied = occupied
    )
}