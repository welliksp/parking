package br.com.wsp.parking.infra.web.controller.impl

import br.com.wsp.parking.domain.port.`in`.GetRevenueUseCase
import br.com.wsp.parking.infra.web.controller.IRevenueController
import br.com.wsp.parking.infra.web.dto.request.RevenueRequest
import br.com.wsp.parking.infra.web.dto.response.RevenueResponse
import org.springframework.http.ResponseEntity
import java.time.OffsetDateTime
import java.time.ZoneOffset

class RevenueController(

    private val getRevenueUseCase: GetRevenueUseCase

) : IRevenueController {


    override fun getRevenue(request: RevenueRequest): ResponseEntity<RevenueResponse> {
        val amount = getRevenueUseCase.execute(request.sector, request.date)

        return ResponseEntity.ok(
            RevenueResponse(
                amount = amount,
                currency = "BRL",
                timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString()
            )
        )
    }

}