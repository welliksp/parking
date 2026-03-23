package br.com.wsp.parking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class ParkingApplication

fun main(args: Array<String>) {
    runApplication<ParkingApplication>(*args)
}
