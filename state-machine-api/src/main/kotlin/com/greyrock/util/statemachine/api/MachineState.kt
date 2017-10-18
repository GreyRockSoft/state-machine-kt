package com.greyrock.util.statemachine.api

interface MachineState <T: Enum<*>>: Runnable {
    fun transition()
    fun validTransitions(): ImmutableSet<T>
    fun getRotationState(): T
}