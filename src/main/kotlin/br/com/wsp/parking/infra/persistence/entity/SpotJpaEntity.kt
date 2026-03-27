package br.com.wsp.parking.infra.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * Entidade JPA representando uma vaga de estacionamento.
 */
@Entity
@Table(name = "spots")
class SpotJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "sector_name", nullable = false)
    val sectorName: String,

    @Column(nullable = false)
    val lat: Double,

    @Column(nullable = false)
    val lng: Double,

    @Column(nullable = false)
    var occupied: Boolean = false,
)