package common.test

import common.implementation.AgentPlatform
import common.interfaces.Agent
import common.interfaces.DirectoryFacilitator
import common.message.Message

class ConsumerAgentBehaviour(x: Float) : AbstractAgentBehavior(x) {

    private val inequalities = inequalitySystemOf {
        moreThan(3f + Math.random().toFloat() * 3f)
        lessThan(-3f - Math.random().toFloat() * 3f)
    }

    override suspend fun onReceive(message: Message, agent: Agent, mts: DirectoryFacilitator) {
        if (message.code == AgentPlatform.CODE_REGISTRATION) {
            mts.send(Message(agent.identifier, endpoint(mts), code = CODE_CHECK, data = x))
        } else {
            super.onReceive(message, agent, mts)
        }
    }

    override fun check(x: Float): Boolean = inequalities.test(x)

    override fun endpoint(mts: DirectoryFacilitator): String {
        return "${mts.parent.identifier}@producers"
    }
}