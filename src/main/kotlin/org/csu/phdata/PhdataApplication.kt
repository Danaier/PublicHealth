package org.csu.phdata

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PhdataApplication

fun main(args: Array<String>) {
	runApplication<PhdataApplication>(*args)
}
