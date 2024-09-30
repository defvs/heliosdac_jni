import dev.defvs.heliosdac.HeliosDac
import kotlin.system.exitProcess

fun main() {
    with (HeliosDac()) {
        val totalDevices = openDevices()

        println("Total devices found: $totalDevices")

        if (totalDevices < 1) {
            println("No devices to open.")
            exitProcess(1)
        }
    }
}