package common.implementation

import common.interfaces.AgentManagementSystem
import common.interfaces.MessageTransportService
import common.message.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlin.coroutines.CoroutineContext

object AgentPlatform : CoroutineScope {

    const val CODE_REGISTRATION = 101

    val messageTransportService: MessageTransportService = MessageTransportServiceImpl(
        BroadcastChannel(Channel.CONFLATED)
    )

    private val superVisorJob = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO + superVisorJob

    private lateinit var agentManagementSystem: AgentManagementSystem

    fun initialize(
        name: String,
        init: suspend AgentManagementSystem.() -> Unit
    ) = runBlocking(coroutineContext) {
        agentManagementSystem = AgentManagementSystemImpl(messageTransportService, name, coroutineContext).also {
            it.init()
            it.register()
        }
    }

}