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

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.template.base.util.isNull
import app.template.base_android.util.ItemOffsetDecoration
import dagger.hilt.android.AndroidEntryPoint
import reprator.gojek.trendingList.databinding.FragmentTrendinglistBinding
import javax.inject.Inject

@AndroidEntryPoint
class TrendingList : Fragment(R.layout.fragment_trendinglist), ItemClickListener {

    private var _binding: FragmentTrendinglistBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var trendingListAdapter: TrendingListAdapter

    private val viewModel: TrendingListViewModal by viewModels()

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTrendinglistBinding.bind(view).also {
            it.trendingListAdapter = trendingListAdapter
            it.trendingViewModal = viewModel
            it.lifecycleOwner = viewLifecycleOwner
        }

        setUpRecyclerView()
        initializeObserver()

        if (savedInstanceState.isNull()) {
            viewModel.fetchTrendingList()
        }
    }

    private fun setUpRecyclerView() {
        with(binding.trendingListRecyclerView) {
            layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            addItemDecoration(
                ItemOffsetDecoration(requireContext(), R.dimen.list_item_padding)
            )
        }
    }

    private fun initializeObserver() {
        viewModel.trendingList.observe(
            viewLifecycleOwner,
            {
                trendingListAdapter.submitList(it)
            }
        )
    }

    override fun itemClicked(position: Int) {
        viewModel.changeViewStatus(position)
    }
}
