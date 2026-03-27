package br.com.wsp.parking.infra.web.controller

import br.com.wsp.parking.infra.web.dto.request.RevenueRequest
import br.com.wsp.parking.infra.web.dto.response.RevenueResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/revenue")
interface IRevenueController {

    @GetMapping
    fun getRevenue(@RequestBody request: RevenueRequest): ResponseEntity<RevenueResponse>
}