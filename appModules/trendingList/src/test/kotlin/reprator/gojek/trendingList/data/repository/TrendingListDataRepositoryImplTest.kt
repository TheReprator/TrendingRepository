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

import app.template.base.useCases.AppError
import app.template.base.useCases.AppSuccess
import app.template.base.util.interent.ConnectionDetector
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reprator.gojek.trendingList.TestFakeData.getFakeManipulatedRemoteDataList
import reprator.gojek.trendingList.data.dataSource.TrendingListRemoteDataSource
import reprator.gojek.trendingList.domain.repository.TrendingListRepository

@ExtendWith(value = [MockKExtension::class])
class TrendingListDataRepositoryImplTest {

    @MockK
    lateinit var trendingListRemoteDataSource: TrendingListRemoteDataSource

    @MockK
    lateinit var connectionDetector: ConnectionDetector

    lateinit var trendingListRepository: TrendingListRepository

    @BeforeEach
    fun setup() {
        trendingListRepository = TrendingListDataRepositoryImpl(
            connectionDetector, trendingListRemoteDataSource
        )
    }

    @Test
    fun `get trending list from data repository`() = runBlockingTest {

        val output = getFakeManipulatedRemoteDataList()

        coEvery {
            trendingListRemoteDataSource.getTrendingListRemoteDataSource()
        } returns AppSuccess(output)

        val result = trendingListRepository.getTrendingListRepository().single()

        Assertions.assertTrue(result is AppSuccess<*>)
        Assertions.assertEquals(output.size, result.get()!!.size)

        coVerify(atMost = 1) {
            trendingListRemoteDataSource.getTrendingListRemoteDataSource()
        }
    }

    @Test
    fun `get trending list from data repository, on fail`() = runBlockingTest {

        val output = "An Error Occurred"

        coEvery {
            trendingListRemoteDataSource.getTrendingListRemoteDataSource()
        } returns AppError(message = output)

        val result = trendingListRepository.getTrendingListRepository().single()

        Assertions.assertTrue(result is AppError)
        Assertions.assertEquals(output, (result as AppError).message)

        coVerify(atMost = 1) {
            trendingListRemoteDataSource.getTrendingListRemoteDataSource()
        }
    }
}
