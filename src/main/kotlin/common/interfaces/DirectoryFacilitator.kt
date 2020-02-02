package common.interfaces

import common.message.Message
import common.utils.DURATION_INFINITE
import kotlinx.coroutines.channels.Channel
import java.time.Duration

interface DirectoryFacilitator : Agent {

    suspend fun onPathUnresolved(message: Message)

    suspend fun df(
        name: String,
        mts: MessageTransportService? = null,
        capicity: Int = Channel.CONFLATED,
        init: suspend DirectoryFacilitator.() -> Unit
    )

    suspend fun agent(agent: Agent)

    suspend fun agent(
        name: String = "${System.currentTimeMillis()}",
        capicity: Int = Channel.CONFLATED,
        lifecycle: Duration = DURATION_INFINITE,
        behaviour: Behaviour
    )

    suspend fun agent(
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

    operator fun get(agentId: String): Agent
}