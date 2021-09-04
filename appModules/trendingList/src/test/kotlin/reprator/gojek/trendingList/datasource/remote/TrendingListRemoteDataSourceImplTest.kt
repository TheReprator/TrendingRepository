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
import app.template.base.useCases.AppSuccess
import io.mockk.coEvery
import io.mockk.coVerifySequence
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.MatcherAssert.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reprator.gojek.trendingList.TestFakeData.getFakeManipulatedRemoteDataList
import reprator.gojek.trendingList.TestFakeData.getFakeRemoteDataList
import reprator.gojek.trendingList.data.dataSource.TrendingListRemoteDataSource
import reprator.gojek.trendingList.datasource.remote.remotemapper.TrendingListMapper
import retrofit2.HttpException
import retrofit2.Response

@ExtendWith(value = [MockKExtension::class])
class TrendingListRemoteDataSourceImplTest {

    @MockK
    lateinit var trendingListApiService: TrendingListApiService

    @MockK
    lateinit var trendingListMapper: TrendingListMapper

    private lateinit var trendingListRemoteDataSource: TrendingListRemoteDataSource

    @BeforeEach
    fun setUp() {
        trendingListRemoteDataSource = TrendingListRemoteDataSourceImpl(
            trendingListApiService,
            trendingListMapper
        )
    }

    @Test
    fun `fetch list successfully from server and map it to UI pojo`() = runBlockingTest {

        val output = getFakeManipulatedRemoteDataList()
        coEvery {
            trendingListApiService.fetchTrendingRepo()
        } returns Response.success(getFakeRemoteDataList())

        coEvery {
            trendingListMapper.map(any())
        } returns output[0]

        val result = trendingListRemoteDataSource.getTrendingListRemoteDataSource()

        assertTrue(result is AppSuccess<*>)
        assertEquals(output, result.get())

        coVerifySequence {
            trendingListApiService.fetchTrendingRepo()
            trendingListMapper.map(any())
        }
    }

    @Test
    fun `fetch list failed with errorBody`() = runBlockingTest {

        coEvery {
            trendingListApiService.fetchTrendingRepo()
        } returns Response.error(404, mockk(relaxed = true))

        val resp = trendingListRemoteDataSource.getTrendingListRemoteDataSource()

        assertThat((resp as AppError).throwable, `is`(instanceOf(HttpException::class.java)))
        assertEquals(AppError::class.java, resp.javaClass)
    }
}
