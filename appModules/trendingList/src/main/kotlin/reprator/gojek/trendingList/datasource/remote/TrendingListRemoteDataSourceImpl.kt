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

package reprator.gojek.trendingList.datasource.remote

import app.template.base.useCases.AppError
import app.template.base.useCases.AppResult
import app.template.base.useCases.AppSuccess
import app.template.base.util.network.safeApiCall
import app.template.base.util.network.toResult
import app.template.base.util.toListMapper
import reprator.gojek.trendingList.data.dataSource.TrendingListRemoteDataSource
import reprator.gojek.trendingList.datasource.remote.remotemapper.TrendingListMapper
import reprator.gojek.trendingList.modals.TrendingModal
import javax.inject.Inject

class TrendingListRemoteDataSourceImpl @Inject constructor(
    private val trendingListApiService: TrendingListApiService,
    private val trendingListMapper: TrendingListMapper
) : TrendingListRemoteDataSource {

    private suspend fun getTrendingListRemoteDataSourceApi():
        AppResult<List<TrendingModal>> {

            return when (val data = trendingListApiService.fetchTrendingRepo().toResult()) {
                is AppSuccess -> {
                    AppSuccess(trendingListMapper.toListMapper().invoke(data.data))
                }
                is AppError -> {
                    AppError(message = data.message, throwable = data.throwable)
                }
                else -> throw IllegalArgumentException("Illegal State")
            }
        }

    override suspend fun getTrendingListRemoteDataSource(): AppResult<List<TrendingModal>> =
        safeApiCall(call = { getTrendingListRemoteDataSourceApi() })
}
