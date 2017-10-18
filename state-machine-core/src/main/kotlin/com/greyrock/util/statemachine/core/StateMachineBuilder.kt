package com.greyrock.util.statemachine.core

import com.google.common.collect.ImmutableMap
import com.greyrock.util.statemachine.api.ImmutableSet
import com.greyrock.util.statemachine.api.MachineState
import com.greyrock.util.statemachine.api.StateFactory
import com.greyrock.util.statemachine.api.StateMachine

class StateMachineBuilder<T: Enum<*>> {

    private val stateFactoryBuilder = ImmutableMap.builder<T, StateFactory<T>>()
    private var startState: T? = null

    fun addStateFactory(state: T, stateFactory: StateFactory<T>): StateMachineBuilder<T> {
        stateFactoryBuilder.put(state, stateFactory)
        return this
    }

    fun startState(state: T): StateMachineBuilder<T> {
        startState = state
        return this
    }

    fun build(): StateMachine<T> {
        val startStateCopy = startState ?: throw IllegalArgumentException("The startState must be set and cannot be null")

        val stateFactories = stateFactoryBuilder.build()

        if (stateFactories.isEmpty()) {
            throw IllegalArgumentException("The StateMachine must have at least one StateFactory registered with it")
        }

        return StateMachineImpl<T>(startStateCopy, stateFactories)
    }
}

fun <T: Enum<*>>stateMachine(startState: T, states: StateMachineBuilder<T>.() -> Unit): StateMachine<T> {
    val builder = StateMachineBuilder<T>()

    builder.startState(startState)
    builder.states()

    return builder.build()
}

fun <T: Enum<*>> StateMachineBuilder<T>.stateFactory(state: T, factory: StateBuilder<T>.() -> Unit) {

    val builder = StateBuilder(state)
    builder.factory()

    val stateFactory = builder.build()
    this.addStateFactory(state, stateFactory)
}

class StateBuilder<T: Enum<*>>(val state: T) {

    var validTransitions: Set<T> = emptySet()

    private var evaluateFunction: ((stateMachine: StateMachine<T>) -> Unit)? = null

    private var transitionFunction: (() -> Unit)? = null

    fun evaluateState(evaluate: (stateMachine: StateMachine<T>) -> Unit) {
        this.evaluateFunction = evaluate
    }

    fun onTransitionTo(transition: () -> Unit) {
        this.transitionFunction = transition
    }

    internal fun build(): StateFactory<T> {
        val evalFuncCpy = evaluateFunction ?: throw IllegalArgumentException("The 'evaluateState' method must be defined")
        val transitionFuncCopy = transitionFunction ?: throw IllegalArgumentException("The 'onTransitionTo' method must be defined")

        val builder = com.google.common.collect.ImmutableSet.builder<T>()
        builder.addAll(validTransitions)

        return StateFactoryImpl(state, ImmutableSetWrapper(builder.build()),evalFuncCpy, transitionFuncCopy)
    }
}

internal class StateFactoryImpl<T: Enum<*>> constructor(
        private val state: T,
        private val validTransitions: ImmutableSet<T>,
        private val evalFunc: (stateMachine: StateMachine<T>) -> Unit,
        private val transitionFunc: () -> Unit
): StateFactory<T> {
    override fun createState(stateMachine: StateMachine<T>): MachineState<T> = StateImpl(state, stateMachine, validTransitions, evalFunc, transitionFunc)
}

internal class StateImpl<T: Enum<*>>
constructor(
        override val state: T,
        override val stateMachine: StateMachine<T>,
        override val validTransitions: ImmutableSet<T>,
        private val evalFunc: (stateMachine: StateMachine<T>) -> Unit,
        private val transitionFunc: () -> Unit
) : MachineState<T> {
    override fun run() = evalFunc(stateMachine)
    override fun transition() = transitionFunc()
}