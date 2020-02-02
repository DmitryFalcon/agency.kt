package task.inequality

import java.util.function.Predicate

fun inequalitySystemOf(block: InequalitySystem.() -> Unit): Predicate<Float> {
    return InequalitySystem().apply(block)
}