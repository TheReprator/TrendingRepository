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

package reprator.gojek.trendingList

import androidx.lifecycle.Observer
import app.template.base.useCases.AppError
import app.template.base.useCases.AppSuccess
import app.template.base_android.util.event.Event
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import io.mockk.verifySequence
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runBlockingTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import reprator.gojek.trendingList.TestFakeData.getFakeManipulatedRemoteDataList
import reprator.gojek.trendingList.domain.useCase.TrendingListUseCase
import reprator.gojek.trendingList.modals.TrendingModal
import reprator.gojek.trendingList.util.CoroutinesTestExtension
import reprator.gojek.trendingList.util.InstantExecutorExtension
import reprator.gojek.trendingList.util.onChangeExtension

@ExtendWith(value = [MockKExtension::class, InstantExecutorExtension::class])
class TrendingListViewModalTest {

    @JvmField
    @RegisterExtension
    val coroutinesTestExtension = CoroutinesTestExtension()

    @MockK
    lateinit var trendingListUseCase: TrendingListUseCase

    lateinit var viewModal: TrendingListViewModal

    // create mockk object
    private val observerLoad = mockk<Observer<Boolean>>()
    private val observerError = mockk<Observer<String>>()
    private val observerSuccessList = mockk<Observer<List<TrendingModal>>>()

    // For refresh
    private val observerRefreshLoad = mockk<Observer<Boolean>>()
    private val observerRefreshError = mockk<Observer<Event<String>>>()

    // create slot
    private val slotLoad = slot<Boolean>()
    private val slotError = slot<String>()
    private val slotSuccess = slot<List<TrendingModal>>()

    private val slotRefreshLoad = slot<Boolean>()
    private val slotRefreshError = slot<Event<String>>()

    // create list to store values
    private val listError = arrayListOf<String>()
    private val listLoader = arrayListOf<Boolean>()
    private val listSuccess = arrayListOf<List<TrendingModal>>()

    private val listRefreshError = arrayListOf<Event<String>>()
    private val listRefreshLoader = arrayListOf<Boolean>()

    @BeforeEach
    fun setUp() {
        viewModal = TrendingListViewModal(
            coroutinesTestExtension.testDispatcherProvider,
            trendingListUseCase
        )

        startObservers()
        startObservingViewModal()
        scenarioForLoad_Error_Success()
        scenarioForSwipeLoad_Error_Success()
    }

    private fun startObservers() {
        observerLoad.onChangeExtension()
        observerError.onChangeExtension()
        observerSuccessList.onChangeExtension()

        observerRefreshLoad.onChangeExtension()
        observerRefreshError.onChangeExtension()
    }

    private fun startObservingViewModal() {
        viewModal.isLoading.observeForever(observerLoad)
        viewModal.errorMsg.observeForever(observerError)
        viewModal.trendingList.observeForever(observerSuccessList)

        viewModal.swipeLoading.observeForever(observerRefreshLoad)
        viewModal.swipeErrorMsg.observeForever(observerRefreshError)
    }

    private fun scenarioForLoad_Error_Success() {
        every {
            observerLoad.onChanged(capture(slotLoad))
        } answers {
            listLoader.add(slotLoad.captured)
        }

        every {
            observerError.onChanged(capture(slotError))
        } answers {
            listError.add(slotError.captured)
        }

        every {
            observerSuccessList.onChanged(capture(slotSuccess))
        } answers {
            listSuccess.add(slotSuccess.captured)
        }
    }

    private fun scenarioForSwipeLoad_Error_Success() {
        every {
            observerRefreshLoad.onChanged(capture(slotRefreshLoad))
        } answers {
            listRefreshLoader.add(slotRefreshLoad.captured)
        }

        every {
            observerRefreshError.onChanged(capture(slotRefreshError))
        } answers {
            listRefreshError.add(slotRefreshError.captured)
        }
    }

    @Test
    fun `get trendingList successfully on launch`() = coroutinesTestExtension.runBlockingTest {

        val output = getFakeManipulatedRemoteDataList()

        coEvery {
            trendingListUseCase()
        } returns flowOf(AppSuccess(output))

        viewModal.fetchTrendingList()

        assertTrue(listSuccess.isNotEmpty())
        assertEquals(output.size, listSuccess.size)

        assertTrue(listError.isEmpty())

        assertTrue(listRefreshError.isEmpty())
        assertTrue(listRefreshLoader.isEmpty())

        verifySequence {
            observerLoad.onChanged(any()) // Default Initialization
            observerError.onChanged(any()) // Default Initialization
            observerSuccessList.onChanged(any()) // Default Initialization
            observerLoad.onChanged(any())
            observerSuccessList.onChanged(any())
            observerLoad.onChanged(any())
        }
    }

    @Test
    fun `get trending list, fetch failed on launch`() = coroutinesTestExtension.runBlockingTest {

        val output = "An error occurred"

        coEvery {
            trendingListUseCase()
        } returns flowOf(AppError(message = output))

        viewModal.fetchTrendingList()

        assertTrue(listSuccess.isEmpty())
        assertTrue(listLoader.isNotEmpty())
        assertTrue(listError.isNotEmpty())

        assertEquals(2, listLoader.size)
        assertEquals(output, listError[0])

        assertTrue(listRefreshError.isEmpty())
        assertTrue(listRefreshLoader.isEmpty())

        verifySequence {
            observerLoad.onChanged(any()) // Default Initialization
            observerError.onChanged(any()) // Default Initialization
            observerSuccessList.onChanged(any()) // Default Initialization
            observerLoad.onChanged(any())
            observerError.onChanged(any())
            observerLoad.onChanged(any())
        }
    }

    @Test
    fun `retry, getTrendingList successfully`() =
        coroutinesTestExtension.runBlockingTest {

            val output = getFakeManipulatedRemoteDataList()

            coEvery {
                trendingListUseCase()
            } returns flowOf(AppSuccess(output))

            viewModal.retryTrendingList()

            assertTrue(listSuccess.isNotEmpty())
            assertEquals(1, listSuccess.size)

            assertTrue(listLoader.isNotEmpty())
            assertEquals(3, listLoader.size)

            assertTrue(listError.isNotEmpty())
            assertEquals(1, listError.size)

            assertTrue(listRefreshError.isEmpty())
            assertTrue(listRefreshLoader.isEmpty())

            verifySequence {
                observerLoad.onChanged(any())
                observerError.onChanged(any())
                observerSuccessList.onChanged(any())

                observerLoad.onChanged(any())
                observerError.onChanged(any())

                observerLoad.onChanged(any())
                observerSuccessList.onChanged(any())
                observerLoad.onChanged(any())
            }
        }

    @Test
    fun `onRefresh, getTrendingList successfully`() =
        coroutinesTestExtension.runBlockingTest {

            val output = getFakeManipulatedRemoteDataList()

            coEvery {
                trendingListUseCase()
            } returns flowOf(AppSuccess(output))

            viewModal.onRefresh()

            assertTrue(listSuccess.isNotEmpty())
            assertEquals(output.size, listSuccess.size)

            assertTrue(listLoader.isEmpty())
            assertTrue(listError.isEmpty())

            assertTrue(listRefreshError.isEmpty())
            assertTrue(listRefreshLoader.isNotEmpty())
            assertEquals(2, listRefreshLoader.size)

            verifySequence {
                observerLoad.onChanged(any())
                observerError.onChanged(any())
                observerSuccessList.onChanged(any())

                observerRefreshLoad.onChanged(any())
                observerRefreshError.onChanged(any())

                observerRefreshLoad.onChanged(any())

                observerSuccessList.onChanged(any())
                observerRefreshLoad.onChanged(any())
            }
        }

    @Test
    fun `onRefresh, getTrendingList failed`() =
        coroutinesTestExtension.runBlockingTest {

            val output = "An Error occurred"

            coEvery {
                trendingListUseCase()
            } returns flowOf(AppError(message = output))

            viewModal.onRefresh()

            assertTrue(listRefreshError.isNotEmpty())
            assertEquals(1, listRefreshError.size)
            assertEquals(output, listRefreshError[0].consume())

            assertTrue(listLoader.isEmpty())
            assertTrue(listError.isEmpty())

            assertTrue(listRefreshLoader.isNotEmpty())
            assertEquals(2, listRefreshLoader.size)

            verifySequence {
                observerLoad.onChanged(any())
                observerError.onChanged(any())
                observerSuccessList.onChanged(any())

                observerRefreshLoad.onChanged(any())
                observerRefreshError.onChanged(any())

                observerRefreshLoad.onChanged(any())

                observerRefreshError.onChanged(any())
                observerRefreshLoad.onChanged(any())
            }
        }

    @Test
    fun `onItemClick for index 0, after successFull Load of Data`() =
        coroutinesTestExtension.runBlockingTest {

            val input = getFakeManipulatedRemoteDataList()

            coEvery {
                trendingListUseCase()
            } returns flowOf(AppSuccess(input))

            viewModal.fetchTrendingList()

            val item = input[0]
            val outputItem = item.copy(isOpen = !item.isOpen)

            viewModal.changeViewStatus(0)

            assertEquals(outputItem.isOpen, listSuccess[1][0].isOpen)

            verify(atMost = 3) {
                observerSuccessList.onChanged(any())
            }
        }
}
