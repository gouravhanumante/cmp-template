package com.boundless.template.core.datastore.di

import com.boundless.template.core.datastore.TokenStorage
import org.koin.core.module.Module
import org.koin.dsl.module

/** Binds [TokenStorage] to the platform secure store. */
expect val coreDatastorePlatformModule: Module

val coreDatastoreModule: Module = module {
    includes(coreDatastorePlatformModule)
}
