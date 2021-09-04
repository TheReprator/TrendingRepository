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

package reprator.gojek.trendingList.test

import androidx.test.espresso.IdlingRegistry
import com.jakewharton.espresso.OkHttp3IdlingResource
import com.kaspersky.kaspresso.testcases.api.testcase.TestCase
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import reprator.gojek.trendingList.CustomMockServer
import reprator.gojek.trendingList.TrendingList
import reprator.gojek.trendingList.dispatcherWithCustomBody
import reprator.gojek.trendingList.dispatcherWithEmptyBody
import reprator.gojek.trendingList.dispatcherWithErrorTimeOut
import reprator.gojek.trendingList.screen.TrendingListKaspressoScreen
import reprator.gojek.trendingList.util.launchFragmentInHiltContainer
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltAndroidTest
class TrendingListKaspressoTest : TestCase() {

    companion object {
        const val TOTAL_ITEM = 25
    }

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    private lateinit var mockWebServer: MockWebServer

    @Inject
    lateinit var okHttp3IdlingResource: OkHttp3IdlingResource

    @Before
    fun setUp() {
        mockWebServer = CustomMockServer().mockWebServer

        hiltRule.inject()

        IdlingRegistry.getInstance().register(okHttp3IdlingResource)

        mockWebServer.dispatcher = dispatcherWithCustomBody()

        launchFragmentInHiltContainer<TrendingList>()
    }

    @After
    fun cleanup() {
        mockWebServer.close()
        IdlingRegistry.getInstance().unregister(okHttp3IdlingResource)
    }

    @Test
    fun `load_item_successfully_onLaunch`() =
        run {
            step("1. Open App and show Toolbar and List") {
                testLogger.i("Main section")

                TrendingListKaspressoScreen {
                    toolBarTitle {
                        isVisible()
                        isCompletelyDisplayed()
                        hasText(R.string.trending_title)
                    }

                    flakySafely(timeoutMs = TimeUnit.SECONDS.toMillis(10)) {
                        trendingList {

                            hasSize(TOTAL_ITEM)

                            firstChild<TrendingListKaspressoScreen.Item> {
                                author {
                                    isVisible()
                                    hasText("microsoft")
                                }
                                description {
                                    isDisplayed()
                                    hasText("Tool for parsing GC logs")
                                }
                                avatar {
                                    isDisplayed()
                                }

                                star {
                                    isNotDisplayed()
                                }
                                fork {
                                    isGone()
                                }

                                trendingLanguage {
                                    isGone()
                                }
                            }

                            scrollToEnd()

                            lastChild<TrendingListKaspressoScreen.Item> {
                                author {
                                    hasText("iterativv")
                                }
                                description {
                                    hasText("Trading strategy for the Freqtrade crypto bot")
                                }
                                avatar {
                                    isDisplayed()
                                }
                            }
                        }
                    }
                }
            }
        }

    @Test
    fun `No_item_found_from_server_onLaunch`() =
        before {
            mockWebServer.dispatcher = dispatcherWithEmptyBody()
        }.after {
            testLogger.i("After section empty")
        }.run {
            step("show empty view") {

                TrendingListKaspressoScreen {
                    flakySafely(timeoutMs = TimeUnit.SECONDS.toMillis(10)) {
                        trendingList.isNotDisplayed()
                        empty {
                            isDisplayed()
                        }
                    }
                }
            }
        }

    @Test
    fun loadErrorViewOnLaunch_withSuccessfulReload() =
        before {
            testLogger.i("Before section loadErrorView")
            mockWebServer.dispatcher = dispatcherWithErrorTimeOut()
        }.after {
        }.run {
            step("1. show error view with reload button click") {

                TrendingListKaspressoScreen {

                    trendingList.isNotDisplayed()

                    mockWebServer.dispatcher = dispatcherWithCustomBody()

                    flakySafely(timeoutMs = TimeUnit.SECONDS.toMillis(10)) {
                        errorRetry {
                            isDisplayed()
                            click()
                        }
                    }
                }
            }

            step("2. verify items in recyclerview") {

                TrendingListKaspressoScreen {
                    trendingList {
                        hasSize(TOTAL_ITEM)
                        isDisplayed()
                    }
                }
            }
        }

    @Test
    fun load_item_successfully_in_recyclerview_with_error_on_pullToRefresh() =
        run {
            step("1. show list items with pull to refresh") {

                TrendingListKaspressoScreen {

                    flakySafely(timeoutMs = TimeUnit.SECONDS.toMillis(10)) {
                        trendingList.hasSize(TOTAL_ITEM)

                        mockWebServer.dispatcher = dispatcherWithErrorTimeOut()

                        swipeToRefresh {
                            isDisplayed()
                            swipeDown()
                        }
                    }
                }
            }

            step("2. verify error with snackbar") {

                TrendingListKaspressoScreen {
                    flakySafely(timeoutMs = TimeUnit.SECONDS.toMillis(10)) {
                        snackbar {
                            isDisplayed()
                            text.hasText("timeout")
                        }
                    }
                }
            }
        }

    @Test
    fun clickItemAtIndex_1_OnLoad() =
        run {
            step("1. show list items and click index First Item") {

                TrendingListKaspressoScreen {
                    trendingList {
                        firstChild<TrendingListKaspressoScreen.Item> {
                            click()
                        }
                    }
                }
            }

            step("2. verify language, Star, fork is shown") {

                TrendingListKaspressoScreen {
                    flakySafely(timeoutMs = TimeUnit.SECONDS.toMillis(10)) {
                        trendingList {
                            firstChild<TrendingListKaspressoScreen.Item> {
                                star {
                                    isVisible()
                                    hasText("445")
                                }
                                fork {
                                    isVisible()
                                    hasText("33")
                                }

                                trendingLanguage {
                                    isVisible()
                                    hasText("Java")
                                }
                            }
                        }
                    }
                }
            }
        }
}
