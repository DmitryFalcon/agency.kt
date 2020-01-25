package common.interfaces

import common.message.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel

interface Agent : SendChannel<Message>, CoroutineScope {

    val name: String

    val identifier: String

    val capacity: Int

    val parent: DirectoryFacilitator
}