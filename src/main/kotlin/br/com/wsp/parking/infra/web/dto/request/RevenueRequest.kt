package br.com.wsp.parking.infra.web.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

@Schema(description = "Requisição para cálculo de receita")
data class RevenueRequest(
    @Schema(description = "Data para consulta de receita", example = "2026-03-27")
    @NotNull(message = "Data não pode ser nula")
    val date: LocalDate,
    
    @Schema(description = "Nome do setor de estacionamento", example = "A")
    @NotNull(message = "Setor não pode ser nulo")
    @NotEmpty(message = "Setor não pode estar vazio")
    val sector: String
)
