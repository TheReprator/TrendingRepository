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

package reprator.gojek.trendingList.domain.useCase

import app.template.base.useCases.AppSuccess
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import reprator.gojek.trendingList.TestFakeData.getFakeManipulatedRemoteDataList
import reprator.gojek.trendingList.domain.repository.TrendingListRepository

@ExtendWith(value = [MockKExtension::class])
class TrendingListUseCaseTest {

    @MockK
    lateinit var trendingListRepository: TrendingListRepository

    lateinit var trendingListUseCase: TrendingListUseCase

    @BeforeEach
    fun setup() {
        trendingListUseCase = TrendingListUseCase(trendingListRepository)
    }

    @Test
    fun `fetch trending from remote data source`() = runBlockingTest {
        val output = getFakeManipulatedRemoteDataList()

        coEvery {
            trendingListRepository.getTrendingListRepository()
        } returns flowOf(AppSuccess(output))

        val result = trendingListUseCase().single()

        assertTrue(result is AppSuccess<*>)
        assertEquals(output, result.get())
    }
}
