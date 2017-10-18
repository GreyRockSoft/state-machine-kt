package com.greyrock.util.statemachine.core

import com.greyrock.util.statemachine.api.ImmutableSet

class ImmutableSetWrapper<T> constructor(private val immutableSet: com.google.common.collect.ImmutableSet<T>): ImmutableSet<T>, Iterable<T> by immutableSet {
    override fun contain(o: T) = immutableSet.contains(o)
}