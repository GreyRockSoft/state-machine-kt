package com.greyrock.util.statemachine.core

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.Test

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
    fun stateMachineBuilder() {
        val sM = stateMachine(TestStates.OK) {
            stateFactory(TestStates.OK) {
                validTransitions = setOf(TestStates.BAD, TestStates.OTHER)
                evaluateState { _ ->

                }
                onTransitionTo {

                }
            }
        }

        assertThat(sM.currentState()).isEqualTo(TestStates.OK)
    }
}

enum class TestStates {
    OK, BAD, OTHER
}