package common.interfaces

import common.message.Message
import kotlinx.coroutines.channels.BroadcastChannel


interface MessageTransportService : BroadcastChannel<Message>