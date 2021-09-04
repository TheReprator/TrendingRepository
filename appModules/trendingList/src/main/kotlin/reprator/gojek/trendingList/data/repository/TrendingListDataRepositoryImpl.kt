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

package reprator.gojek.trendingList.data.repository

import app.template.base.useCases.AppResult
import app.template.base.util.interent.ConnectionDetector
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import reprator.gojek.trendingList.data.dataSource.TrendingListRemoteDataSource
import reprator.gojek.trendingList.domain.repository.TrendingListRepository
import reprator.gojek.trendingList.modals.TrendingModal
import javax.inject.Inject

class TrendingListDataRepositoryImpl @Inject constructor(
    private val connectionDetector: ConnectionDetector,
    private val trendingRemoteDataSource: TrendingListRemoteDataSource
) : TrendingListRepository {

    companion object {
        private const val NO_INTERNET = "No internet connection detected."
    }

    override suspend fun getTrendingListRepository(): Flow<AppResult<List<TrendingModal>>> {
        // return if (connectionDetector.isInternetAvailable)
        return flowOf(trendingRemoteDataSource.getTrendingListRemoteDataSource())
       /* else {
            flowOf(AppError(message = NO_INTERNET))
        }*/
    }
}
