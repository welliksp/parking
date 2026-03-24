package br.com.wsp.parking.domain.port.`in`

import br.com.wsp.parking.domain.model.GarageConfig

interface LoadGarageUseCase {

    fun execute(config: GarageConfig)
}