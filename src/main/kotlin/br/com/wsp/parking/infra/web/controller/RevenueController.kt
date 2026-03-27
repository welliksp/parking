package br.com.wsp.parking.infra.web.controller

import br.com.wsp.parking.domain.port.`in`.GetRevenueUseCase
import br.com.wsp.parking.infra.web.dto.request.RevenueRequest
import br.com.wsp.parking.infra.web.dto.response.RevenueResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RestController
@RequestMapping("/revenue")
@Tag(name = "Revenue", description = "API para cálculo de receita do estacionamento")
class RevenueController(
    private val getRevenueUseCase: GetRevenueUseCase
)  {

    private val log = LoggerFactory.getLogger(javaClass)

    @PostMapping
    @Operation(
        summary = "Calcular receita do setor",
        description = "Calcula a receita total de um setor em um período específico"
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Receita calculada com sucesso",
                content = [Content(
                    mediaType = "application/json",
                    schema = Schema(implementation = RevenueResponse::class)
                )]
            ),
            ApiResponse(
                responseCode = "400",
                description = "Dados de entrada inválidos"
            ),
            ApiResponse(
                responseCode = "500",
                description = "Erro interno do servidor"
            )
        ]
    )
    fun getRevenue(@Valid @RequestBody request: RevenueRequest): ResponseEntity<RevenueResponse> {
        log.info("Revenue calculation requested: sector=${request.sector}, date=${request.date}")
        
        val amount = getRevenueUseCase.execute(request.sector, request.date)

        log.info("Revenue calculated: sector=${request.sector}, date=${request.date}, amount=$amount")
        
        return ResponseEntity.ok(
            RevenueResponse(
                amount = amount,
                currency = "BRL",
                timestamp = OffsetDateTime.now(ZoneOffset.UTC).toString()
            )
        )
    }

}