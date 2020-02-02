package common.implementation

import common.interfaces.Agent
import common.interfaces.Behaviour
import common.interfaces.DirectoryFacilitator
import common.message.Message
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.selects.SelectClause2
import kotlin.coroutines.CoroutineContext

abstract class BaseAgent internal constructor(
    override val name: String,
    val behaviour: Behaviour,
    override val coroutineContext: CoroutineContext,
    override val capacity: Int = Channel.CONFLATED,
    override val parent: DirectoryFacilitator
) : Agent, SendChannel<Message>, Behaviour by behaviour {

    private val agent = actor<Message>(
        context = coroutineContext,
        capacity = capacity
    ) {
        for (message in channel) with(this@BaseAgent) {
            behaviour.onReceive(message, this@BaseAgent, parent)
        }
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

    override val identifier: String
        get() = "${parent.identifier}@$name"

    internal class AgentImpl(
        name: String,
        behaviour: Behaviour,
        coroutineContext: CoroutineContext,
        capacity: Int = Channel.CONFLATED,
        parent: DirectoryFacilitator
    ) : BaseAgent(name, behaviour, coroutineContext, capacity, parent)
}