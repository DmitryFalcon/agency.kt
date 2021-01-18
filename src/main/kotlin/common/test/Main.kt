package common.test

import common.implementation.AgentPlatform

fun main() {

    AgentPlatform.initialize("PC") {

        df("producers") {
            for (i in 0..1000000) {
                agent(
                    name = "${System.nanoTime()}",
                    behaviour = ProducerAgentBehavior(-10f + 20f * Math.random().toFloat())
                )
            }
        }

        df("consumers") {
            for (i in 0..1000000) {
                agent(
                    name = "${System.nanoTime()}",
                    behaviour = ConsumerAgentBehaviour(-10f + 20f * Math.random().toFloat())
                )
            }
        }
    }
}