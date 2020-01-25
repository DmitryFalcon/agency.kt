package common.interfaces

import common.message.Message

interface Behaviour {

    suspend fun onReceive(message: Message, agent: Agent,  mts: DirectoryFacilitator)
}