package com.greyrock.util.statemachine.api

interface MachineState <T: Enum<*>>: Runnable {
    fun transition()
    val validTransitions: ImmutableSet<T>
    val state: T
    val stateMachine: StateMachine<T>
}