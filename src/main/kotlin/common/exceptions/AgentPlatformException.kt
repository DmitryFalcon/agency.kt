package common.exceptions

import java.lang.Exception

sealed class AgentPlatformException : Exception()

open class UsupportedException: AgentPlatformException()

open class UnrecogniedException: AgentPlatformException()

open class UnexpectedException: AgentPlatformException()

open class MissingException: AgentPlatformException()