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

package reprator.gojek.trendingList.screen

import android.view.View
import com.kaspersky.kaspresso.screens.KScreen
import io.github.kakaocup.kakao.image.KImageView
import io.github.kakaocup.kakao.progress.KProgressBar
import io.github.kakaocup.kakao.recycler.KRecyclerItem
import io.github.kakaocup.kakao.recycler.KRecyclerView
import io.github.kakaocup.kakao.swiperefresh.KSwipeRefreshLayout
import io.github.kakaocup.kakao.text.KButton
import io.github.kakaocup.kakao.text.KSnackbar
import io.github.kakaocup.kakao.text.KTextView
import org.hamcrest.Matcher
import reprator.gojek.trendingList.R
import reprator.gojek.trendingList.TrendingList

object TrendingListKaspressoScreen : KScreen<TrendingListKaspressoScreen>() {

    override val layoutId: Int = R.layout.fragment_trendinglist
    override val viewClass: Class<*> = TrendingList::class.java

    val snackbar = KSnackbar()

    val toolBarTitle = KTextView { withId(R.id.trending_toolbar_title) }
    val toolBarMore = KImageView { withId(R.id.trending_toolbar_more) }

    val swipeToRefresh = KSwipeRefreshLayout {
        withId(R.id.trendingListSwipe)
    }

    val progress = KProgressBar { withId(R.id.lee_progress) }
    val empty = KTextView { withId(R.id.lee_empty) }
    val errorRetry = KButton { withId(R.id.lee_error_retry) }

    val trendingList = KRecyclerView(
        { withId(R.id.trendingListRecyclerView) },
        itemTypeBuilder = {
            itemType(TrendingListKaspressoScreen::Item)
        }
    )

    class Item(parent: Matcher<View>) : KRecyclerItem<Item>(parent) {
        val avatar: KImageView = KImageView(parent) { withId(R.id.trendingImage) }
        val author: KTextView = KTextView(parent) { withId(R.id.trendingAuthor) }
        val description: KTextView = KTextView(parent) { withId(R.id.trendingDescription) }
        val trendingLanguage: KTextView = KTextView(parent) { withId(R.id.trendingLanguage) }
        val star: KTextView = KTextView(parent) { withId(R.id.trendingStar) }
        val fork: KTextView = KTextView(parent) { withId(R.id.trendingFork) }
    }
}
