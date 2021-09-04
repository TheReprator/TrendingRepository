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

package reprator.gojek.trendingList.datasource.remote.remotemapper

import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.spyk
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import reprator.gojek.trendingList.TestFakeData.getFakeManipulatedRemoteData
import reprator.gojek.trendingList.TestFakeData.getFakeRemoteData

class TrendingListMapperTest {

    @Test
    @DisplayName("Mapping from json Modal to UI Modal")
    fun `create the parsed json Trending entity modal into TrendingModal`() = runBlockingTest {
        val input = getFakeRemoteData()
        val output = getFakeManipulatedRemoteData()

        val mapper = spyk(TrendingListMapper())

        val result = mapper.map(input)

        assertEquals(output, result, "mapping result should be same")

        coVerify(atMost = 1) { mapper.map(input) }

        confirmVerified(mapper)
    }
}
