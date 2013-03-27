package org.jetbrains.kannotator.controlFlow.builder.analysis

import java.util.HashMap
import kotlinlib.mapMerge
import kotlinlib.mapValues

public trait Qualifier

public trait QualifierSet<Q: Qualifier> {
    public val id: Any
    public val initial: Q

    public fun merge(q1: Q, q2: Q): Q

    public fun contains(q: Qualifier): Boolean
}

public trait QualifierEvaluator<out Q: Qualifier> {
    fun evaluateQualifier(baseValue: TypedValue): Q
}

public class MultiQualifier<K: AnalysisType>(val qualifiers: Map<K, Qualifier>): Qualifier {
    public fun copy(key: K, qualifier: Qualifier): MultiQualifier<K> {
        val map = HashMap<K, Qualifier>(qualifiers)
        map.put(key, qualifier)
        return MultiQualifier(map)
    }

    public fun toString(): String = qualifiers.values().toString()

    public fun equals(obj: Any?): Boolean {
        if (this identityEquals obj) return true
        if (obj == null) return false

        if (obj !is MultiQualifier<*>) return false

        return qualifiers == obj.qualifiers
    }

    public fun hashCode(): Int = qualifiers.hashCode()
}

public class MultiQualifierSet<K: AnalysisType>(val qualifierSets: Map<K, QualifierSet<Qualifier>>): QualifierSet<MultiQualifier<K>> {
    class object {
        private object MULTI_QUALIFIER_KEY
    }

    public override val id: Any = MULTI_QUALIFIER_KEY

    public override val initial: MultiQualifier<K> =
            MultiQualifier(qualifierSets.mapValues { (key, qualifierSet) -> qualifierSet.initial })

    public override fun merge(q1: MultiQualifier<K>, q2: MultiQualifier<K>): MultiQualifier<K> {
        val map = mapMerge(q1.qualifiers, q2.qualifiers, qualifierSets.keySet()) {(key, v1, v2) ->
            qualifierSets[key]!!.merge(v1, v2)
        }
        return MultiQualifier(map)
    }

    public override fun contains(q: Qualifier): Boolean = q is MultiQualifier<*>
}

public class MultiQualifierEvaluator<K: AnalysisType>(
        val evaluators: Map<K, QualifierEvaluator<*>>
): QualifierEvaluator<MultiQualifier<K>> {
    override fun evaluateQualifier(baseValue: TypedValue): MultiQualifier<K> {
        return MultiQualifier(evaluators.mapValues { (key, eval) -> eval.evaluateQualifier(baseValue) })
    }
}