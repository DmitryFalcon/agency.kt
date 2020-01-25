package common.interfaces

import common.message.Message
import common.utils.DURATION_INFINITE
import kotlinx.coroutines.channels.Channel
import java.time.Duration

interface DirectoryFacilitator : Agent {

    val children: Map<String, Agent>

    suspend fun onPathUnresolved(message: Message)

    fun df(
        name: String,
        mts: MessageTransportService? = null,
        capicity: Int = Channel.CONFLATED,
        init: suspend DirectoryFacilitator.() -> Unit
    )

    fun agent(agent: Agent)

    fun agent(
        name: String = "${System.currentTimeMillis()}",
        capicity: Int = Channel.CONFLATED,
        lifecycle: Duration = DURATION_INFINITE,
        behaviour: Behaviour
    )

    fun agent(
        name: String = "${System.currentTimeMillis()}",
        capicity: Int = Channel.CONFLATED,
        lifecycle: Duration = DURATION_INFINITE,
        block: suspend Agent.(Message, DirectoryFacilitator) -> Unit
    ) {
        val behaviour = object : Behaviour {
            override suspend fun onReceive(message: Message, agent: Agent, mts: DirectoryFacilitator) {
                agent.block(message, mts)
            }
        }
        agent(name, capicity, lifecycle, behaviour)
    }
}