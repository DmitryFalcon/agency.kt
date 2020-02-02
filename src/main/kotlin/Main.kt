import common.implementation.AgentPlatform
import kotlinx.coroutines.delay
import task.ConsumerAgentBehaviour
import task.ProducerAgentBehavior

fun main() {

    AgentPlatform.initialize("PC") {

        df("producers") {
            for (i in 0..20) {
                agent(name = "${System.nanoTime()}",behaviour = ProducerAgentBehavior(-10f + 20f * Math.random().toFloat()))
            }
        }

        df("consumers") {
            for (i in 0..10) {
                agent(name = "${System.nanoTime()}", behaviour = ConsumerAgentBehaviour(-10f + 20f * Math.random().toFloat()))
            }
        }
    }
}


