package com.greyrock.util.statemachine.core

import com.google.common.collect.ImmutableMap
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


fun <T: Enum<*>>stateMachine(states: StateMachineBuilder<T>.() -> Unit): StateMachine<T> {
    val builder = StateMachineBuilder<T>()

    builder.states()

    return builder.build()
}

fun <T: Enum<*>> stateFactory(state: T, factory: StateMachineBuilder<T>.() -> MachineState<T>) {

}