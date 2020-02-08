package common.implementation

import common.interfaces.*
import common.message.Message
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.SelectClause2
import java.time.Duration
import kotlin.coroutines.CoroutineContext

class AgentManagementSystemImpl(
    override val messageTransportService: MessageTransportService,
    override val name: String,
    override val coroutineContext: CoroutineContext,
    override val capacity: Int = Channel.CONFLATED
) : AgentManagementSystem {

    private val subscription = messageTransportService.openSubscription()
    private val innerDFAgent = BaseDFAgent.DFAgentImpl("MAIN", coroutineContext, capacity, this)

    init {
        launch(Dispatchers.IO) { subscription.consumeEach { innerDFAgent.send(it) } }
    }

    override fun get(agentId: String): Agent = innerDFAgent[agentId]

    override suspend fun onPathUnresolved(message: Message) {
        println("Try send request to network")
    }

    override suspend fun df(
        name: String,
        mts: MessageTransportService?,
        capicity: Int,
        init: suspend DirectoryFacilitator.() -> Unit
    ) {
        innerDFAgent.df(name, mts, capicity, init)
    }

    override suspend fun agent(agent: Agent) {
        innerDFAgent.agent(agent)
    }

    override suspend fun agent(name: String, capicity: Int, lifecycle: Duration, behaviour: Behaviour) {
        innerDFAgent.agent(name, capicity, lifecycle, behaviour)
    }

    override val identifier: String
        get() = name


    override val parent: DirectoryFacilitator
        get() = this

    @ExperimentalCoroutinesApi
    override val isClosedForSend: Boolean
        get() = innerDFAgent.isClosedForSend

    @ExperimentalCoroutinesApi
    override val isFull: Boolean
        get() = true

    override val onSend: SelectClause2<Message, SendChannel<Message>>
        get() = innerDFAgent.onSend

    override fun close(cause: Throwable?): Boolean = innerDFAgent.close()

    @ExperimentalCoroutinesApi
    override fun invokeOnClose(handler: (cause: Throwable?) -> Unit) = innerDFAgent.invokeOnClose(handler)

    override fun offer(element: Message): Boolean = innerDFAgent.offer(element)

    override suspend fun send(element: Message) = innerDFAgent.send(element)

    suspend fun register() {
        send(Message(innerDFAgent.identifier, innerDFAgent.identifier, code = AgentPlatform.CODE_REGISTRATION))
    }

}