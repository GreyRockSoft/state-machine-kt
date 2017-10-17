package com.greyrock.util.statemachine.core

import com.google.common.collect.ImmutableMap
import com.greyrock.util.statemachine.api.StateFactory
import com.greyrock.util.statemachine.api.StateMachine

class StateMachineImpl<T: Enum<*>> constructor(private val startState: T, private val stateFactories: ImmutableMap<T, StateFactory<T>>): StateMachine<T> {

    private var state = startState

    override fun currentState() = startState


    override fun transition(state: T) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}