/*
 * Copyright 2021 Vikram LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package reprator.gojek.trendingList.di

import app.template.base.util.interent.ConnectionDetector
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import reprator.gojek.trendingList.data.dataSource.TrendingListRemoteDataSource
import reprator.gojek.trendingList.data.repository.TrendingListDataRepositoryImpl
import reprator.gojek.trendingList.datasource.remote.TrendingListApiService
import reprator.gojek.trendingList.datasource.remote.TrendingListRemoteDataSourceImpl
import reprator.gojek.trendingList.datasource.remote.remotemapper.TrendingListMapper
import reprator.gojek.trendingList.domain.repository.TrendingListRepository
import reprator.gojek.trendingList.domain.useCase.TrendingListUseCase
import retrofit2.Retrofit

@InstallIn(ViewModelComponent::class)
@Module
class TrendingListModule {

    @Provides
    fun provideTrendingListRemoteDataSource(
        trendingListApiService: TrendingListApiService,
        trendingListMapper: TrendingListMapper
    ): TrendingListRemoteDataSource {
        return TrendingListRemoteDataSourceImpl(
            trendingListApiService,
            trendingListMapper
        )
    }

    @Provides
    fun provideTrendingListRepository(
        trendingListRemoteDataSource: TrendingListRemoteDataSource,
        connectionDetector: ConnectionDetector,
    ): TrendingListRepository {
        return TrendingListDataRepositoryImpl(
            connectionDetector,
            trendingListRemoteDataSource
        )
    }

    @Provides
    fun provideTrendingListUseCase(
        TrendingListRepository: TrendingListRepository
    ): TrendingListUseCase {
        return TrendingListUseCase(TrendingListRepository)
    }

    @Provides
    fun provideTrendingListApiService(
        retrofit: Retrofit
    ): TrendingListApiService {
        return retrofit
            .create(TrendingListApiService::class.java)
    }
}
