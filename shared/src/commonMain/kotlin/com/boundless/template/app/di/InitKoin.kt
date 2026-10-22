package com.boundless.template.app.di

import com.boundless.template.core.common.AppBuildInfo
import com.boundless.template.core.common.di.coreCommonModule
import com.boundless.template.core.database.di.coreDatabaseModule
import com.boundless.template.core.datastore.di.coreDatastoreModule
import com.boundless.template.core.designsystem.installAppImageLoader
import com.boundless.template.core.network.ApiConfig
import com.boundless.template.core.network.di.coreNetworkModule
import com.boundless.template.feature.devtools.di.devToolsModule
import com.boundless.template.feature.home.di.homeModule
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.module

/** Single DI entry point for every platform; `androidApp` additionally supplies `androidContext`. */
fun initKoin(
    apiConfig: ApiConfig = ApiConfig(),
    buildInfo: AppBuildInfo = AppBuildInfo(),
    declaration: KoinAppDeclaration? = null,
): KoinApplication {
    installAppImageLoader(enableLogging = apiConfig.enableLogging)
    return startKoin {
        declaration?.invoke(this)
        modules(
            module { single { buildInfo } },
            coreCommonModule,
            coreDatastoreModule,
            coreDatabaseModule,
            coreNetworkModule(apiConfig),
            homeModule,
            devToolsModule,
            appModule,
        )
    }
}
