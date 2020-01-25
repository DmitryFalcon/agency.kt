package common.implementation

import common.exceptions.UnrecogniedException
import common.interfaces.*
import common.message.Message
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlinx.coroutines.selects.SelectClause2
import java.time.Duration
import kotlin.coroutines.CoroutineContext

abstract class BaseDFAgent internal constructor(
    override val name: String,
    override val coroutineContext: CoroutineContext,
    override val capacity: Int = Channel.CONFLATED,
    override val parent: DirectoryFacilitator
) : DirectoryFacilitator, Behaviour {

    protected val agent = actor<Message>(
        context = coroutineContext,
        capacity = capacity
    ) {
        for (message in channel) with(this@BaseDFAgent) {
            onReceive(message, this, parent)
        }
    }

    private val _children = mutableMapOf<String, Agent>()

    override val identifier: String
        get() = if (parent is AgentManagementSystem) {
            name
        } else {
            "${parent.name}@$name"
        }

    override val children: Map<String, Agent> = _children

    override fun agent(agent: Agent) {
        _children[agent.name] = agent
    }

    override fun agent(name: String, capicity: Int, lifecycle: Duration, behaviour: Behaviour) {
        val newAgent = BaseAgent.AgentImpl(name, behaviour, coroutineContext + Job(), capacity, this)
        agent(newAgent)
    }

    @ExperimentalCoroutinesApi
    override val isClosedForSend: Boolean
        get() = agent.isClosedForSend

    @ExperimentalCoroutinesApi
    override val isFull: Boolean = false

    override val onSend: SelectClause2<Message, SendChannel<Message>>
        get() = agent.onSend

    override fun close(cause: Throwable?): Boolean = agent.close(cause)

    @ExperimentalCoroutinesApi
    override fun invokeOnClose(handler: (cause: Throwable?) -> Unit) = agent.invokeOnClose(handler)

    override fun offer(element: Message): Boolean = agent.offer(element)

    override suspend fun send(element: Message) = agent.send(element)

    override fun df(
        name: String,
        mts: MessageTransportService?,
        capicity: Int,
        init: suspend DirectoryFacilitator.() -> Unit
    ) {
        val newDf = BaseDFAgent.DFAgentImpl(name, coroutineContext, capacity, this)
        agent(newDf)
    }

    override suspend fun onReceive(message: Message, agent: Agent, mts: DirectoryFacilitator) =
        withContext(coroutineContext) {
            val receiverPath = message.receiverId.split("@")
            val currentDFIndex = receiverPath.indexOfFirst { it == name }
            when (currentDFIndex) {
                receiverPath.lastIndex -> with(message) {
                    children.values.forEach { it.send(Message(senderId, receiverId, data, code, timestamp)) }
                }
                receiverPath.lastIndex - 1 -> {
                    val agent = children[receiverPath.last()]
                    agent?.send(message) ?: throw UnrecogniedException()
                }
                -1 -> {
                    onPathUnresolved(message)
                }
                else -> children[receiverPath[currentDFIndex + 1]]?.send(message) ?: throw UnrecogniedException()
            }
        }

    override suspend fun onPathUnresolved(message: Message) = parent.send(message)

    internal class DFAgentImpl(
        name: String,
        coroutineContext: CoroutineContext,
        capacity: Int = Channel.CONFLATED,
        parent: DirectoryFacilitator
    ) : BaseDFAgent(name, coroutineContext, capacity, parent)
}

