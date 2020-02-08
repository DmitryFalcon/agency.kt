package common.implementation

import common.interfaces.MessageTransportService
import common.message.Message
import kotlinx.coroutines.channels.BroadcastChannel

class MessageTransportServiceImpl(
    broadcastChannel: BroadcastChannel<Message>
) : MessageTransportService, BroadcastChannel<Message> by broadcastChannel