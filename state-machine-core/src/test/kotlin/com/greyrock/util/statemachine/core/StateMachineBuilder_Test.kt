package com.greyrock.util.statemachine.core

import com.greyrock.util.statemachine.api.ImmutableSet
import com.greyrock.util.statemachine.api.MachineState
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

    /*
    @Test
    fun stateMachineBuilder() {
        val stateMachine = stateMachine<TestStates> {
            stateFactory(TestStates.OK) {
                validTransitions = setOf(TestStates.BAD, TestStates.OTHER)
                run {

                }
                transition {

                }
            }
        }


    }
    */
}


class OKMachineState: MachineState<TestStates> {
    override fun transition() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun validTransitions(): ImmutableSet<TestStates> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getRotationState(): TestStates {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun run() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}

enum class TestStates {
    OK, BAD, OTHER
}