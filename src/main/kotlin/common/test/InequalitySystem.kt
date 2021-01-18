package common.test

import java.util.function.Predicate

class InequalitySystem : Predicate<Float> {

    private var predicate: Predicate<Float>? = null

    fun lessThan(x1: Float) {
        predicate = predicate?.and { x -> x < x1 } ?: Predicate { x -> x < x1 }
    }

    fun moreThan(x1: Float) {
        predicate = predicate?.and { x -> x > x1 } ?: Predicate { x -> x > x1 }
    }

    fun equalTo(x1: Float) {
        predicate = predicate?.and(Predicate.isEqual(x1)) ?: Predicate.isEqual(x1)
    }

    fun notEqualTo(x1: Float) {
        predicate = predicate?.and { x -> !x1.equals(x) } ?: Predicate { x -> !x1.equals(x) }
    }

    override fun test(t: Float) = predicate?.test(t) ?: true
}

fun inequalitySystemOf(block: InequalitySystem.() -> Unit): Predicate<Float> {
    return InequalitySystem().apply(block)
}