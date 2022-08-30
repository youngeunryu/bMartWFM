package wfm

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WfmApplication

fun main(args: Array<String>) {
    runApplication<WfmApplication>(*args)
}
