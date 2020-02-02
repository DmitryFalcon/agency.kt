package task

import common.interfaces.Agent
import common.interfaces.Behaviour
import common.interfaces.DirectoryFacilitator
import common.message.Message
import kotlinx.coroutines.delay
import task.inequality.inequalitySystemOf

abstract class AbstractAgentBehavior(protected val x: Float) : Behaviour {

    private val appropriateIds = mutableListOf<String>()
    private val inappropriateIds = mutableListOf<String>()

    override suspend fun onReceive(message: Message, agent: Agent, mts: DirectoryFacilitator) {
        if (appropriateIds.contains(message.senderId)) {
            return
        }
        if (inappropriateIds.contains(message.senderId)) {
            return
        }
        delay(1000)
        when (message.code) {
            CODE_CHECK -> if (check(message.data as Float)) {
                println("Accepted by  ${message.receiverId}")
                mts.send(Message(agent.identifier, message.senderId, x, CODE_HAND_SHAKE))
            } else {
                mts.send(Message(agent.identifier, message.senderId, code = CODE_ERROR))
                mts.send(Message(agent.identifier, endpoint(mts), code = CODE_CHECK, data = x))
            }
            CODE_HAND_SHAKE -> if (check(message.data as Float)) {
                mts.send(Message(agent.identifier, message.senderId, code = CODE_SUBMIT_HAND_SHAKE))
            } else {
                mts.send(Message(agent.identifier, message.senderId, code = CODE_ERROR))
                mts.send(Message(agent.identifier, endpoint(mts), code = CODE_CHECK, data = x))
            }
            CODE_SUBMIT_HAND_SHAKE -> {
                println("Submitted by ${message.senderId}")
                appropriateIds.add(message.senderId)
                mts.send(Message(agent.identifier, message.senderId, code = CODE_SUBMIT_HAND_SHAKE, data = x))
                mts.send(Message(agent.identifier, endpoint(mts), code = CODE_CHECK, data = x))
            }
            CODE_ERROR -> {
                println("Denied by ${message.senderId}")
                inappropriateIds.add(message.senderId)
                mts.send(Message(agent.identifier, message.senderId, code = CODE_ERROR))
                mts.send(Message(agent.identifier, endpoint(mts), code = CODE_CHECK, data = x))
            }
        }
    }

    protected abstract fun endpoint(mts: DirectoryFacilitator): String

    protected abstract fun check(x: Float): Boolean

}