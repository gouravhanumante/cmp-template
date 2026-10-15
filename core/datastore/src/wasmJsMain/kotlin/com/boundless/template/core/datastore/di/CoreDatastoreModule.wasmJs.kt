package com.boundless.template.core.datastore.di

import com.boundless.template.core.datastore.BrowserKeyValueStore
import com.boundless.template.core.datastore.BrowserTokenStorage
import com.boundless.template.core.datastore.KeyValueStore
import com.boundless.template.core.datastore.TokenStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual val coreDatastorePlatformModule: Module =
    module {
        single<TokenStorage> { BrowserTokenStorage() }
        single<KeyValueStore> { BrowserKeyValueStore() }
    }
