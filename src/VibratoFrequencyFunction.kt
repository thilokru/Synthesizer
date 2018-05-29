import SpecialKeys.isVibrato
import java.lang.Math.exp
import java.util.function.DoubleFunction
import kotlin.math.PI
import kotlin.math.sin

class VibratoFrequencyFunction(private val frequencyVariance: Double, private val riseTime: Double, private val vibratoFrequency: Double, private val baseFrequency: Double) : DoubleFunction<Double> {
    override fun apply(timeStamp: Double): Double {
        if (isVibrato) {
            return baseFrequency * (1 + (1 - exp(-timeStamp / riseTime)) * frequencyVariance * sin(timeStamp * vibratoFrequency * 2 * PI))
        } else {
            return baseFrequency
        }
    }
}