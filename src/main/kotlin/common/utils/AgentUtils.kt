package common.utils

import common.interfaces.Agent
import common.interfaces.Behaviour
import common.interfaces.DirectoryFacilitator
import common.interfaces.MessageTransportService
import common.message.Message

fun behaviour(block: Agent.(Message, DirectoryFacilitator) -> Unit) = object : Behaviour {
    override suspend fun onReceive(message: Message, agent: Agent, mts: DirectoryFacilitator) {
        agent.block(message, mts)
    }
}