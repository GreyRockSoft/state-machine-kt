package com.greyrock.util.statemachine.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test
import java.util.concurrent.atomic.AtomicBoolean

class StateMachineBuilder_Test {

    @Test
    fun noStartState() {
        val builder = StateMachineBuilder<TestStates>()
        assertThatThrownBy {
            builder.build()
        }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("The startState must be set and cannot be null")
    }

    @Test
    fun noStates() {
        val builder = StateMachineBuilder<TestStates>()
        assertThatThrownBy {
            builder.startState(TestStates.OK).build()
        }
                .isInstanceOf(IllegalArgumentException::class.java)
                .hasMessage("The StateMachine must have at least one StateFactory registered with it")
    }

    @Test
    fun badStateTransition() {
        val sM = stateMachine(TestStates.OK) {
            stateFactory(TestStates.OK) {
                validTransitions = setOf(TestStates.OTHER)
                evaluateState { stateMachine ->
                    stateMachine.transition(TestStates.BAD)
                }
                onTransitionTo {

                }
            }
            stateFactory(TestStates.BAD) {
                validTransitions = emptySet()
                evaluateState {  }
                onTransitionTo {  }
            }
        }

        assertThat(sM.currentState()).isEqualTo(TestStates.OK)
        assertThatThrownBy { sM.run() }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("The State Machine can not transition to state BAD from OK")
    }

    @Test
    fun validStateTransition() {

        val transitioned = AtomicBoolean(false)

        val sM = stateMachine(TestStates.START) {
            stateFactory(TestStates.START) {
                validTransitions = setOf(TestStates.OK, TestStates.OTHER)
                evaluateState { stateMachine ->
                    stateMachine.transition(TestStates.OK)
                }
                onTransitionTo {  }
            }
            stateFactory(TestStates.OK) {
                validTransitions = emptySet()
                evaluateState {  }
                onTransitionTo {
                    transitioned.set(true)
                }
            }
        }

        assertThat(sM.currentState()).isEqualTo(TestStates.START)
        sM.run()
        assertThat(sM.currentState()).isEqualTo(TestStates.OK)
        assertThat(transitioned).isTrue
    }

    @Test
    fun callTransitionOutsideThread() {
        val sM = stateMachine(TestStates.START) {
            stateFactory(TestStates.START) {
                validTransitions = emptySet()
                evaluateState {  }
                onTransitionTo {  }
            }
        }

        assertThatThrownBy { sM.transition(TestStates.BAD) }
                .isInstanceOf(IllegalStateException::class.java)
                .hasMessage("Another thread holds the lock for the machineState machine.  The transition method can only be called by the thread that currently holds the lock.")
    }
}

enum class TestStates {
    START, OK, BAD, OTHER
}