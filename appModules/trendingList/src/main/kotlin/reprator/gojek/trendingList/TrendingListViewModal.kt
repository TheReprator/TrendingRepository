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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.template.base.extensions.computationalBlock
import app.template.base.useCases.AppError
import app.template.base.useCases.AppSuccess
import app.template.base.util.network.AppCoroutineDispatchers
import app.template.base_android.util.event.Event
import dagger.hilt.android.lifecycle.HiltViewModel
import io.americanexpress.busybee.BusyBee
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import reprator.gojek.trendingList.domain.useCase.TrendingListUseCase
import reprator.gojek.trendingList.modals.TrendingModal
import javax.inject.Inject

@HiltViewModel
class TrendingListViewModal @Inject constructor(
    private val coroutineDispatcherProvider: AppCoroutineDispatchers,
    private val trendingListUseCase: TrendingListUseCase
) : ViewModel() {

    private val BUSYBEE_OPERATION_NAME = "Network Call"
    private val busyBee = BusyBee.singleton()

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMsg = MutableLiveData("")
    val errorMsg: LiveData<String> = _errorMsg

    private val _trendingList = MutableLiveData(emptyList<TrendingModal>())
    val trendingList: LiveData<List<TrendingModal>> = _trendingList

    private val _swipeErrorMsg = MutableLiveData(Event(""))
    val swipeErrorMsg: LiveData<Event<String>> = _swipeErrorMsg

    private val _swipeLoading = MutableLiveData(false)
    val swipeLoading: LiveData<Boolean> = _swipeLoading

    var previousPosition = -1

    fun fetchTrendingList() {
        useCaseCall(
            {
                _isLoading.value = it
            },
            {
                _errorMsg.value = it
            }
        )
    }

    fun retryTrendingList() {
        _isLoading.value = true
        _errorMsg.value = ""
        fetchTrendingList()
    }

    fun onRefresh() {
        useCaseCall(
            {
                _swipeLoading.value = it
            },
            {
                _swipeErrorMsg.value = Event(it)
            }
        )
    }

    private fun useCaseCall(
        blockLoader: (Boolean) -> Unit,
        blockError: (String) -> Unit
    ) {
        busyBee.busyWith(BUSYBEE_OPERATION_NAME)

        computationalBlock {
            trendingListUseCase().flowOn(coroutineDispatcherProvider.io)
                .catch { e ->
                    blockError(e.localizedMessage ?: "")
                }.onStart {
                    blockLoader(true)
                }.onCompletion {
                    blockLoader(false)
                    busyBee.completed(BUSYBEE_OPERATION_NAME)
                }.flowOn(coroutineDispatcherProvider.main)
                .collect {
                    withContext(coroutineDispatcherProvider.main) {
                        when (it) {
                            is AppSuccess -> {
                                _trendingList.value = it.data
                            }
                            is AppError -> {
                                blockError(it.message ?: it.throwable?.message ?: "")
                            }
                            else -> throw IllegalArgumentException("Illegal State")
                        }
                    }
                }
        }
    }

    fun changeViewStatus(position: Int) {

        val previousList = trendingList.value!!.toMutableList()

        if (previousPosition == position) {
            previousPosition = -1
            changeOpenStatusForItem(previousList, position)
            _trendingList.value = previousList
            return
        }
        if (-1 != previousPosition)
            changeOpenStatusForItem(previousList, previousPosition)

        changeOpenStatusForItem(previousList, position)

        previousPosition = position

        _trendingList.value = previousList
    }

    private fun changeOpenStatusForItem(list: MutableList<TrendingModal>, position: Int) {
        val item = list[position]
        val newItem = item.copy(isOpen = !item.isOpen)

        list[position] = newItem
    }

    private fun computationalBlock(
        coroutineExceptionHandler: CoroutineExceptionHandler? = null,
        block: suspend CoroutineScope.() -> Unit
    ) {
        viewModelScope.computationalBlock(
            coroutineDispatcherProvider,
            coroutineExceptionHandler,
            block
        )
    }
}
