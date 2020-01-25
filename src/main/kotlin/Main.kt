import common.implementation.AgentPlatform
import common.message.Message
import kotlinx.coroutines.delay

fun main() {
    AgentPlatform.initialize("PC") {

        agent(name = "logger") { message, mts ->
            println(message)
        }

        agent(name = "ping-1") { message, mts ->
            if (message.senderId == "${mts.identifier}@ping-2") {
                mts.send(Message(identifier, "${mts.identifier}@logger", data = "pong"))
            }
            delay(500)
            mts.send(Message(identifier, "${mts.identifier}@logger", data = "ping"))
            mts.send(Message(identifier, "${mts.identifier}@ping-2"))
        }

        agent(name = "ping-2") { message, mts ->
            if (message.senderId == "${mts.identifier}@ping-1") {
                mts.send(Message(identifier, "${mts.identifier}@logger", data = "pong"))
                delay(500)
                mts.send(Message(identifier, "${mts.identifier}@logger", data = "ping"))
                mts.send(Message(identifier, "${mts.identifier}@ping-1", data = "ping"))
            }
        }
    }
}