package br.com.wsp.parking.domain.port.out

import br.com.wsp.parking.domain.model.GarageConfig

interface GarageSimulatorClient {

    fun fetchGarageConfig(): GarageConfig
}