package com.auratube.app.ui.sheets

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import com.auratube.app.R
import com.auratube.app.api.obj.StreamItem
import com.auratube.app.constants.IntentData
import com.auratube.app.enums.ShareObjectType
import com.auratube.app.extensions.parcelable
import com.auratube.app.extensions.serializable
import com.auratube.app.extensions.toID
import com.auratube.app.helpers.BackgroundHelper
import com.auratube.app.helpers.ContextHelper
import com.auratube.app.helpers.NavigationHelper
import com.auratube.app.obj.ShareData
import com.auratube.app.parcelable.PlayerData
import com.auratube.app.ui.activities.NoInternetActivity
import com.auratube.app.ui.dialogs.ShareDialog
import com.auratube.app.ui.fragments.DownloadTab
import com.auratube.app.util.PlayingQueue
import com.auratube.app.util.PlayingQueueMode

class DownloadOptionsBottomSheet : BaseBottomSheet() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val streamItem = arguments?.parcelable<StreamItem>(IntentData.streamItem)!!
        val videoId = streamItem.url!!.toID()
        val downloadTab = arguments?.serializable<DownloadTab>(IntentData.downloadTab)!!
        val playlistId = arguments?.getString(IntentData.playlistId)

        val options = mutableListOf(
            R.string.playOnBackground,
            R.string.share,
            R.string.delete
        )

        // can't navigate to video while in offline activity
        if (ContextHelper.tryUnwrapActivity<NoInternetActivity>(requireContext()) == null) {
            options += R.string.go_to_video
        }

        val isSelectedVideoCurrentlyPlaying = PlayingQueue.getCurrent()?.url?.toID() == videoId
        if (!isSelectedVideoCurrentlyPlaying && PlayingQueue.isNotEmpty() && PlayingQueue.queueMode == PlayingQueueMode.OFFLINE) {
            options += R.string.play_next
            options += R.string.add_to_queue
        }

        setSimpleItems(options.map { getString(it) }) { which ->
            val playerData = PlayerData(
                videoId,
                playlistId = playlistId,
                downloadTab = downloadTab,
                isOffline = true
            )

            when (options[which]) {
                R.string.playOnBackground -> {
                    BackgroundHelper.playOnBackground(requireContext(), playerData)
                }

                R.string.go_to_video -> {
                    NavigationHelper.navigateVideo(requireContext(), playerData)
                }

                R.string.share -> {
                    val shareData = ShareData(currentVideo = videoId)
                    val bundle = bundleOf(
                        IntentData.id to videoId,
                        IntentData.shareObjectType to ShareObjectType.VIDEO,
                        IntentData.shareData to shareData
                    )
                    val newShareDialog = ShareDialog()
                    newShareDialog.arguments = bundle
                    newShareDialog.show(parentFragmentManager, null)
                }

                R.string.delete -> {
                    setFragmentResult(DELETE_DOWNLOAD_REQUEST_KEY, bundleOf())
                    dialog?.dismiss()
                }

                R.string.play_next -> {
                    PlayingQueue.addAsNext(streamItem)
                }

                R.string.add_to_queue -> {
                    PlayingQueue.add(streamItem)
                }
            }
        }

        super.onCreate(savedInstanceState)
    }

    companion object {
        const val DELETE_DOWNLOAD_REQUEST_KEY = "delete_download_request_key"
    }
}
