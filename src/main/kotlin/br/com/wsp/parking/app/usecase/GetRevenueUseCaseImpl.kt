package br.com.wsp.parking.app.usecase

import br.com.wsp.parking.domain.port.`in`.GetRevenueUseCase
import br.com.wsp.parking.domain.port.out.ParkingRecordRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class GetRevenueUseCaseImpl(
    private val parkingRecordRepository: ParkingRecordRepository
) : GetRevenueUseCase {

    override fun execute(sector: String, date: LocalDate): BigDecimal =
        parkingRecordRepository.sumRevenueBySectorAndDateRange(
            sectorName = sector,
            from = date.atStartOfDay(),
            to = date.atTime(LocalTime.MAX)
        )

}