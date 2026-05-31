package com.mvi.core.datastore

import dagger.Module
import dagger.Provides

@Module
interface DatastoreModule {

    val keyedValueDatastore: KeyedValueDatastore
    val serializer: Serializer

    companion object {

        @Provides
        internal fun provideSerializer(impl: KotlinSerializer): Serializer = impl

        @Provides
        internal fun provideKeyedValueDatastore(impl: KeyedValueDatastoreImpl): KeyedValueDatastore = impl
    }
}
