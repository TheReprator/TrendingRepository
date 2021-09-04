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

import app.template.base.util.network.bodyOrThrow
import io.mockk.junit5.MockKExtension
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import reprator.gojek.trendingList.TestFakeData.getFakeRemoteData
import reprator.gojek.trendingList.dispatcherWithCustomBody
import reprator.gojek.trendingList.dispatcherWithErrorTimeOut
import retrofit2.Retrofit
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@ExtendWith(MockKExtension::class)
class TrendingListApiServiceTest {

    companion object {
        private const val COUNT = 25
        private const val REQUEST_PATH = "/repositories"
    }

    private lateinit var service: TrendingListApiService

    private lateinit var mockWebServer: MockWebServer

    @BeforeEach
    fun createService() {
        mockWebServer = MockWebServer()

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS) // For testing purposes
            .readTimeout(2, TimeUnit.SECONDS) // For testing purposes
            .writeTimeout(2, TimeUnit.SECONDS)
            .build()

        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(okHttpClient)
            .addConverterFactory(createJacksonConverterFactory())
            .build()
            .create(TrendingListApiService::class.java)
    }

    @AfterEach
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun `get Trending list successfully`() = runBlocking {
        mockWebServer.dispatcher = dispatcherWithCustomBody()

        val factListEntity =
            service.fetchTrendingRepo().body()

        assertNotNull(factListEntity)
        assertEquals(COUNT, factListEntity!!.size)
        assertEquals(getFakeRemoteData().author, factListEntity[0].author)
    }

    @Test
    fun `get Trending list request check`() = runBlocking {
        mockWebServer.dispatcher = dispatcherWithCustomBody()

        service.fetchTrendingRepo().body()
        val request = mockWebServer.takeRequest()

        assertEquals(1, request.requestUrl!!.pathSegments.size)
        assertEquals("repositories", request.requestUrl!!.pathSegments[0])

        val queryParams = request.requestUrl?.queryParameterNames
        assertTrue(queryParams!!.isEmpty())

        assertEquals(REQUEST_PATH, request.path)
        assertEquals("GET", request.method)
    }

    @Test
    fun `Timeout or error throw from server`(): Unit = runBlocking {
        mockWebServer.dispatcher = dispatcherWithErrorTimeOut()

        assertThrows<SocketTimeoutException> {
            service.fetchTrendingRepo().bodyOrThrow()
        }
    }
}
