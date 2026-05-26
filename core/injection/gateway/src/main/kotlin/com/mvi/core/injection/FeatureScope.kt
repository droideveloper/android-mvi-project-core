package com.mvi.core.injection

import javax.inject.Scope

/**
 * Scope annotation for feature-level dependencies in the dependency injection graph.
 *
 * This scope ensures that dependencies are created once per feature instance and reused
 * throughout the feature's lifecycle.
 */
@Scope
@MustBeDocumented
@Retention
annotation class FeatureScope()
