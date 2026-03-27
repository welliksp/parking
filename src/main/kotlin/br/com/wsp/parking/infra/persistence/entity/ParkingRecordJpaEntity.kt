package br.com.wsp.parking.infra.persistence.entity

import br.com.wsp.parking.infra.persistence.entity.enums.ParkingStatus
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Enumerated
import jakarta.persistence.EnumType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Entidade JPA representando um registro de estacionamento.
 */
@Entity
@Table(name = "parking_records")
class ParkingRecordJpaEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "license_plate", nullable = false)
    val licensePlate: String,

    @Column(name = "sector_name", nullable = false)
    val sectorName: String,

    @Column(name = "spot_id")
    var spotId: Long? = null,

    @Column(name = "entry_time", nullable = false)
    val entryTime: LocalDateTime,

    @Column(name = "exit_time")
    var exitTime: LocalDateTime? = null,

    @Column(name = "applied_price", nullable = false, precision = 10, scale = 2)
    val appliedPrice: BigDecimal,

    @Column(name = "total_amount", precision = 10, scale = 2)
    var totalAmount: BigDecimal? = null,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ParkingStatus

)