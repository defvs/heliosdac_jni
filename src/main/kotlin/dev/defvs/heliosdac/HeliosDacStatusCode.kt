package dev.defvs.heliosdac

/**
 * Enum class representing the various return codes from the Helios DAC.
 * Mirrors the definitions in the `HeliosDac.h` file.
 */
enum class HeliosDacStatusCode(val code: Int) {
    SUCCESS(1),                                    // HELIOS_SUCCESS: Successful operation
    ERROR_NOT_INITIALIZED(-1),                      // HELIOS_ERROR_NOT_INITIALIZED: Not initialized
    ERROR_INVALID_DEVNUM(-2),                       // HELIOS_ERROR_INVALID_DEVNUM: Invalid device number
    ERROR_NULL_POINTS(-3),                          // HELIOS_ERROR_NULL_POINTS: Null points pointer
    ERROR_TOO_MANY_POINTS(-4),                      // HELIOS_ERROR_TOO_MANY_POINTS: Too many points in frame
    ERROR_PPS_TOO_HIGH(-5),                         // HELIOS_ERROR_PPS_TOO_HIGH: PPS higher than maximum allowed
    ERROR_PPS_TOO_LOW(-6),                          // HELIOS_ERROR_PPS_TOO_LOW: PPS lower than minimum allowed

    // Errors from HeliosDacDevice class
    ERROR_DEVICE_CLOSED(-1000),                     // HELIOS_ERROR_DEVICE_CLOSED: Device closed
    ERROR_DEVICE_FRAME_READY(-1001),                // HELIOS_ERROR_DEVICE_FRAME_READY: Frame ready before previous operation completed
    ERROR_DEVICE_SEND_CONTROL(-1002),               // HELIOS_ERROR_DEVICE_SEND_CONTROL: SendControl() failed
    ERROR_DEVICE_RESULT(-1003),                     // HELIOS_ERROR_DEVICE_RESULT: Unexpected result from SendControl()
    ERROR_DEVICE_NULL_BUFFER(-1004),                // HELIOS_ERROR_DEVICE_NULL_BUFFER: Null buffer pointer
    ERROR_DEVICE_SIGNAL_TOO_LONG(-1005),            // HELIOS_ERROR_DEVICE_SIGNAL_TOO_LONG: Control signal too long

    // Errors from libusb (libusb error codes will be added to this base value)
    ERROR_LIBUSB_BASE(-5000);                       // HELIOS_ERROR_LIBUSB_BASE: Base value for libusb errors

    companion object {
        /**
         * Get the corresponding [HeliosDacStatusCode] for a given integer value.
         */
        fun fromCode(code: Int): HeliosDacStatusCode? {
            return entries.find { it.code == code }
        }
    }
}
