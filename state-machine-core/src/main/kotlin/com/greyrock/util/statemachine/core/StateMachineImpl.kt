package com.greyrock.util.statemachine.core

import com.google.common.collect.ImmutableMap
import com.greyrock.util.statemachine.api.MachineState
import com.greyrock.util.statemachine.api.StateFactory
import com.greyrock.util.statemachine.api.StateMachine
import org.slf4j.LoggerFactory
import java.util.concurrent.locks.ReentrantLock

class StateMachineImpl<T: Enum<*>> constructor(startState: T, private val stateFactories: ImmutableMap<T, StateFactory<T>>): StateMachine<T> {

    private companion object {
        private val LOG = LoggerFactory.getLogger(StateMachineImpl::class.java)
    }

    private val lock = ReentrantLock()

    private var machineState: MachineState<T>

    init {
        machineState = createMachineState(startState)
    }

    override fun currentState() = machineState.state

    private fun createMachineState(state: T): MachineState<T> {
        val stateFactory = stateFactories[state]
        if (stateFactory == null) {
            throw IllegalStateException("The StateMachine must have a factory for the start machineState")
        } else {
            return stateFactory.createState(this)
        }
    }

    /**
     * This method must be called by one of the MachineStates, but only after being invoked inside of the State Machine's run method.
     */
    override fun transition(state: T) {
        if (lock.isHeldByCurrentThread) {

            if (machineState.validTransitions.contain(state)) {
                try {
                    val newMachineState = createMachineState(state)
                    newMachineState.transition()
                    LOG.info("Transitioning from ${machineState.state} to $state")
                    machineState = newMachineState
                } catch (t: Throwable) {
                    LOG.error("Failed to transition to state $state from ${machineState.state}", t)
                }
            } else {
                throw IllegalStateException("The State Machine can not transition to state $state from ${machineState.state}")
            }

        } else {
            throw IllegalStateException("Another thread holds the lock for the machineState machine.  The transition method can only be called by the thread that currently holds the lock.")
        }
    }

    override fun run() {
        if (lock.tryLock()) {
            try {
                machineState.run()
            } finally {
                lock.unlock()
            }
        } else {
            LOG.warn("Lock was already acquired, returning...")
        }
    }
}