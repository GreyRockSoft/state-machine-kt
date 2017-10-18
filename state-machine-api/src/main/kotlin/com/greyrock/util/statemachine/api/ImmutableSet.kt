package com.greyrock.util.statemachine.api

interface ImmutableSet<T>: Iterable<T> {
    fun contain(o: T): Boolean
}