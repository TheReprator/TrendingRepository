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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import app.template.base_android.util.GeneralDiffUtil
import reprator.gojek.trendingList.databinding.RowTrendingBinding
import reprator.gojek.trendingList.modals.TrendingModal
import javax.inject.Inject

class TrendingListAdapter @Inject constructor(private val itemClickListener: ItemClickListener) :
    ListAdapter<TrendingModal, VHFact>(GeneralDiffUtil<TrendingModal>()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VHFact {
        val binding = RowTrendingBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        return VHFact(binding, itemClickListener)
    }

    override fun onBindViewHolder(holder: VHFact, position: Int) {
        holder.binding.trendingModal = getItem(position)
        holder.binding.executePendingBindings()
    }
}

class VHFact(val binding: RowTrendingBinding, itemClickListener: ItemClickListener) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.trendingRowRoot.setOnClickListener {
            if (-1 < absoluteAdapterPosition)
                itemClickListener.itemClicked(absoluteAdapterPosition)
        }
    }
}
