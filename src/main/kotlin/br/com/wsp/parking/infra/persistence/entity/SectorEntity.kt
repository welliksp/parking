package br.com.wsp.parking.infra.persistence.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal

@Entity
@Table(name = "sectors")
class SectorEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(unique = true, nullable = false)
    val name: String,

    @Column(name = "base_price", nullable = false, precision = 10, scale = 2)
    val basePrice: BigDecimal,

    @Column(name = "max_capacity", nullable = false)
    val maxCapacity: Int,

    @Column(name = "open_hour", nullable = false)
    val openHour: String,

    @Column(name = "close_hour", nullable = false)
    val closeHour: String,

    @Column(name = "duration_limit_minutes", nullable = false)
    val durationLimitMinutes: Int,

    @Column(name = "is_open", nullable = false)
    var isOpen: Boolean = true

)