package task

import common.interfaces.DirectoryFacilitator
import task.inequality.inequalitySystemOf
import java.lang.Math.random

class ProducerAgentBehavior(x: Float) : AbstractAgentBehavior(x) {

    private val inequalities = inequalitySystemOf {
        lessThan(10f + random().toFloat() * 10f)
        moreThan(-10f - random().toFloat() * 10f)
    }

    override fun check(x: Float): Boolean = inequalities.test(x)

    override fun endpoint(mts: DirectoryFacilitator): String {
        return "${mts.parent.identifier}@consumers"
    }
}