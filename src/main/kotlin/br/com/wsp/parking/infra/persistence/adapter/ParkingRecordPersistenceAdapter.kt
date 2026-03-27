package br.com.wsp.parking.infra.persistence.adapter

import br.com.wsp.parking.domain.model.ParkingRecord
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import br.com.wsp.parking.infra.persistence.entity.ParkingRecordJpaEntity
import br.com.wsp.parking.infra.persistence.entity.enums.ParkingStatus
import br.com.wsp.parking.infra.persistence.repository.ParkingRecordJpaRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDateTime


@Component
class ParkingRecordPersistenceAdapter(
    private val jpaRepository: ParkingRecordJpaRepository
) : ParkingRecordRepository {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun save(record: ParkingRecord): ParkingRecord {
        log.debug("Salvando registro de estacionamento: id=${record.id}, placa=${record.licensePlate}, status=${record.status}")
        
        val entity = if (record.id == 0L) {
            jpaRepository.save(record.toNewEntity())
        } else {
            jpaRepository.findById(record.id)
                .map { existing -> existing.applyDomain(record) }
                .map { jpaRepository.save(it) }
                .orElseThrow { IllegalStateException("Registro ${record.id} não encontrado") }
        }
        
        log.debug("Registro de estacionamento salvo: id=${entity.id}, placa=${entity.licensePlate}")
        return entity.toDomain()
    }

    override fun findActiveByLicensePlate(licensePlate: String): ParkingRecord? {
        log.debug("Buscando registro ativo de estacionamento: placa=$licensePlate")
        val record = jpaRepository.findActiveByLicensePlate(licensePlate)?.toDomain()
        
        if (record != null) {
            log.debug("Registro ativo encontrado: id=${record.id}, placa=$licensePlate")
        } else {
            log.debug("Nenhum registro ativo encontrado: placa=$licensePlate")
        }
        
        return record
    }

    override fun sumRevenueBySectorAndDateRange(
        sectorName: String,
        from: LocalDateTime,
        to: LocalDateTime
    ): BigDecimal {
        log.debug("Calculando receita: setor=$sectorName, de=$from, ate=$to")
        val revenue = jpaRepository.sumRevenueBySectorAndDateRange(sectorName, from, to)
        log.debug("Receita calculada: setor=$sectorName, valor=$revenue")
        return revenue
    }


    private fun ParkingRecord.toNewEntity() = ParkingRecordJpaEntity(
        licensePlate = licensePlate,
        sectorName = sectorName,
        spotId = spotId,
        entryTime = entryTime,
        exitTime = exitTime,
        appliedPrice = appliedPrice,
        totalAmount = totalAmount,
        status = ParkingStatus.ENTERED,
        activeKey = "${licensePlate}_ACTIVE"
    )

    private fun ParkingRecordJpaEntity.applyDomain(domain: ParkingRecord): ParkingRecordJpaEntity {
        this.spotId = domain.spotId
        this.exitTime = domain.exitTime
        this.totalAmount = domain.totalAmount
        this.status = when (domain.status) {
            br.com.wsp.parking.domain.model.ParkingStatus.ENTERED -> ParkingStatus.ENTERED
            br.com.wsp.parking.domain.model.ParkingStatus.PARKED -> ParkingStatus.PARKED
            br.com.wsp.parking.domain.model.ParkingStatus.EXITED -> ParkingStatus.EXITED
        }
        if (domain.status == br.com.wsp.parking.domain.model.ParkingStatus.EXITED) {
            this.activeKey = null
        }
        return this
    }

    private fun ParkingRecordJpaEntity.toDomain() = ParkingRecord(
        id = id,
        licensePlate = licensePlate,
        sectorName = sectorName,
        spotId = spotId,
        entryTime = entryTime,
        exitTime = exitTime,
        appliedPrice = appliedPrice,
        totalAmount = totalAmount,
        status = when (status) {
            ParkingStatus.ENTERED -> br.com.wsp.parking.domain.model.ParkingStatus.ENTERED
            ParkingStatus.PARKED -> br.com.wsp.parking.domain.model.ParkingStatus.PARKED
            ParkingStatus.EXITED -> br.com.wsp.parking.domain.model.ParkingStatus.EXITED
        }
    )


}