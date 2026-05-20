package com.auratube.app.ui.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.view.isGone
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.auratube.app.R
import com.auratube.app.api.MediaServiceRepository
import com.auratube.app.api.obj.ChannelTab
import com.auratube.app.api.obj.StreamItem
import com.auratube.app.constants.IntentData
import com.auratube.app.databinding.FragmentChannelContentBinding
import com.auratube.app.extensions.ceilHalf
import com.auratube.app.extensions.parcelable
import com.auratube.app.extensions.parcelableArrayList
import com.auratube.app.ui.adapters.SearchResultsAdapter
import com.auratube.app.ui.adapters.VideosAdapter
import com.auratube.app.ui.base.DynamicLayoutManagerFragment
import com.auratube.app.ui.extensions.addOnBottomReachedListener
import com.auratube.app.ui.models.sources.ChannelTabPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ChannelContentFragment : DynamicLayoutManagerFragment(R.layout.fragment_channel_content) {
    private var _binding: FragmentChannelContentBinding? = null
    private val binding get() = _binding!!
    private var recyclerViewState: Parcelable? = null

    override fun setLayoutManagers(gridItems: Int) {
        binding.channelRecView.layoutManager = GridLayoutManager(
            requireContext(),
            gridItems.ceilHalf()
        )
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        // manually restore the recyclerview state due to https://github.com/material-components/material-components-android/issues/3473
        binding.channelRecView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        _binding = FragmentChannelContentBinding.bind(view)
        super.onViewCreated(view, savedInstanceState)

        val arguments = requireArguments()
        val channelId = arguments.getString(IntentData.channelId)!!

        val tabData = arguments.parcelable<ChannelTab>(IntentData.tabData)

        if (tabData?.data.isNullOrEmpty()) {
            var nextPage = arguments.getString(IntentData.nextPage)
            var isLoading = false

            val channelAdapter = VideosAdapter(showChannelInfo = false).also {
                it.submitList(arguments.parcelableArrayList<StreamItem>(IntentData.videoList)!!)
            }
            binding.channelRecView.adapter = channelAdapter
            binding.progressBar.isGone = true

            binding.channelRecView.addOnBottomReachedListener {
                if (isLoading || nextPage == null) return@addOnBottomReachedListener

                isLoading = true

                lifecycleScope.launch(Dispatchers.IO) {
                    val resp = try {
                       MediaServiceRepository.instance.getChannelNextPage(channelId, nextPage!!)
                    } catch (e: Exception) {
                        return@launch
                    } finally {
                        isLoading = false
                    }

                    nextPage = resp.nextpage
                    withContext(Dispatchers.Main) {
                        channelAdapter.insertItems(resp.relatedStreams)
                    }
                }
            }
        } else {
            val searchChannelAdapter = SearchResultsAdapter()
            binding.channelRecView.adapter = searchChannelAdapter

            val pagingFlow = Pager(
                PagingConfig(pageSize = 20, enablePlaceholders = false),
                pagingSourceFactory = { ChannelTabPagingSource(tabData!!) }
            ).flow

            viewLifecycleOwner.lifecycleScope.launch {
                launch {
                    pagingFlow.collect {
                        searchChannelAdapter.submitData(it)
                    }
                }

                launch {
                    searchChannelAdapter.loadStateFlow.collect {
                        if (it.refresh is LoadState.NotLoading) {
                            binding.progressBar.isGone = true
                        }
                    }
                }
            }
        }

        binding.channelRecView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}