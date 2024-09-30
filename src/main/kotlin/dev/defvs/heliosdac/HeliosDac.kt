package dev.defvs.heliosdac

internal class HeliosDacNative {
    companion object {
        init {
            HeliosJNILoader
        }
    }

    external fun openDevices(): Int
    external fun closeDevices(): Int
    external fun writeFrame(devNum: Int, pps: Int, flags: Byte, points: Array<HeliosPoint>, numOfPoints: Int): Int
    external fun getStatus(devNum: Int): Int
    external fun stop(devNum: Int): Int
    external fun setShutter(devNum: Int, level: Boolean): Int
}

/**
 * The front-facing API for the JNI bindings of helios_dac
 *
 * BASIC USAGE:
 * 1.	Call [openDevices], which returns the number of available devices.
 * 2.	To send a frame to the DAC, first call [getStatus]. If the function returns [HeliosDacStatusCode.SUCCESS],
 * 	then you can call [writeFrame]. The status should be polled until it returns ready.
 * 	It can and sometimes will fail to return ready on the first try.
 * 3.  To stop output, use [stop]. To restart output you must send a new frame as described above.
 *
 * The DAC is double-buffered. When it receives its first frame, it starts outputting it. When a second frame is sent to
 * the DAC while the first frame is being played, the second frame is stored in the DACs memory until the first frame
 * finishes playback, at which point the second, buffered frame will start playing. If the DAC finished playback of a frame
 * without having received and buffered a second frame, it will by default loop the first frame until a new frame is
 * received (but the flag HELIOS_FLAG_SINGLE_MODE will make it stop playback instead).
 * The GetStatus() function actually checks whether the buffer on the DAC is empty or full. If it is full, the DAC
 * cannot receive a new frame until the currently playing frame finishes, freeing up the buffer.
 *
 */
class HeliosDac {
    private val native = HeliosDacNative()
    private fun Int.toHeliosCode() = HeliosDacStatusCode.fromCode(this) ?: throw Exception("Unknown HeliosDac status code returned: $this")

    /**
     * Initializes drivers and opens the connection to all devices.
     * If the function has already been called previously, and
     * a re-scan is wanted, [closeDevices] must be called first.
     *
     * @return the number of available devices.
     */
    fun openDevices() = native.openDevices()

    /**
     * Closes and frees all devices.
     *
     * @return the status code for this operation
     */
    fun closeDevices() = native.closeDevices().toHeliosCode()

    /**
     * Writes a frame to one of the frame buffer of the given device (index [devNum]).
     *
     * @param devNum The device index, must be inferior to the value returned by [openDevices]
     * @param pps Points per second of the frame
     * @param points An array of points to be displayed
     * @param startImmediately Whether to switch to the newly copied buffer when the copy is finished, or
     *  to wait for the previous buffer to be entirely displayed.
     * @param repeatFrame Whether to repeat this frame once it finishes
     * @param blocking Whether to make this call blocking during the copy operation
     *
     * @return the status code for this operation
     */
    fun writeFrame(
        devNum: Int,
        pps: Int,
        points: Array<HeliosPoint>,
        startImmediately: Boolean = false,
        repeatFrame: Boolean = true,
        blocking: Boolean = true,
    ) = native.writeFrame(
        devNum, pps,
        (if (startImmediately) 1 else 0 +
                if (!repeatFrame) 2 else 0 +
                if (!blocking) 4 else 0).toByte(),
        points, points.size
    ).toHeliosCode()

    /**
     * Gets the status of the given device ([devNum])
     *
     * @param devNum The device index, must be inferior to the value returned by [openDevices]
     *
     * @return true if the device is ready to receive a new frame
     */
    fun getStatus(devNum: Int) = (native.getStatus(devNum) >= 1)

    /**
     * Stops output of the given device ([devNum]) until a new frame is written
     * Also blocks execution for 100ms.
     *
     * @param devNum The device index, must be inferior to the value returned by [openDevices]
     *
     * @return the status code for this operation
     */
    fun stop(devNum: Int) = native.stop(devNum)

    /**
     * Sets the shutter level of the given device ([devNum])
     *
     * @param devNum The device index, must be inferior to the value returned by [openDevices]
     * @param level whether the shutter should be opened (true) or closed (false)
     *
     * @return the status code for this operation
     */
    fun setShutter(devNum: Int, level: Boolean) = native.setShutter(devNum, level)

}