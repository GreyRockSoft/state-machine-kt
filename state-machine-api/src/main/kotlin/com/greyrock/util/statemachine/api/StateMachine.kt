package com.greyrock.util.statemachine.api

interface StateMachine<T: Enum<*>>: Runnable {
    fun currentState(): T
    fun transition(state: T)
}

interface StateFactory<T: Enum<*>> {
    fun createState(stateMachine: StateMachine<T>): MachineState<T>
}