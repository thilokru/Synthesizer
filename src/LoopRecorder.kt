import java.awt.event.KeyEvent
import java.util.*

class LoopRecorder {

    private val eventBuffer: MutableMap<Int, MutableList<KeyEvent>> = TreeMap()
    var loopLength: Int = 0
        private set
    var currentTime: Int = 0
        private set

    private var extendingEnabled = true
    var recording = false
        set(value) {
            if (!value)
                extendingEnabled = false
            else (field && !value)
            trim()
            field = value
        }

    var waitingForTrigger = false
    var playback = false

    public fun registerEvent(event: KeyEvent) {
        if (!recording)
            if (waitingForTrigger) {
                waitingForTrigger = false
                recording = true
            } else
                return
        var list = eventBuffer[currentTime]
        if (list == null) {
            list = LinkedList()
            eventBuffer[currentTime] = list
        }
        list.add(event)
    }

    public fun update() {
        currentTime++
        if (currentTime >= loopLength) {
            if (extendingEnabled && recording) {
                loopLength = currentTime
            } else {
                currentTime = 0
            }
        }
    }

    public fun trigger() {
        if (!playback) return
        val oldRecordingState = recording
        recording = false
        eventBuffer[currentTime]?.forEach {
            if (it.id == KeyEvent.KEY_PRESSED) {
                Main.listener.keyPressed(it)
            } else if (it.id == KeyEvent.KEY_RELEASED) {
                Main.listener.keyReleased(it)
            }
        }
        recording = oldRecordingState
    }

    private fun trim() {
        loopLength = 1 + (eventBuffer.keys.max() ?: -1)
        if (loopLength == 0)
            extendingEnabled = true
    }
}