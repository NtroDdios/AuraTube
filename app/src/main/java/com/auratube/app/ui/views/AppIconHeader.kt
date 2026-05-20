package com.auratube.app.ui.views

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.auratube.app.BuildConfig
import com.auratube.app.databinding.AppIconHeaderBinding
import com.auratube.app.helpers.ClipboardHelper
import com.auratube.app.ui.activities.AboutActivity.Companion.GITHUB_URL

class AppIconHeader(context: Context, attributeSet: AttributeSet? = null) :
    LinearLayout(context, attributeSet) {
        val binding = AppIconHeaderBinding.inflate(LayoutInflater.from(context), this, true)

        init {
            val versionText = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
            binding.versionTv.text = versionText
            binding.versionCard.setOnClickListener {
                ClipboardHelper.save(context, text = versionText, notify = true)
            }

            binding.appIcon.setOnClickListener {
                val sendIntent = Intent(Intent.ACTION_SEND)
                    .putExtra(Intent.EXTRA_TEXT, GITHUB_URL)
                    .setType("text/plain")

                context.startActivity(Intent.createChooser(sendIntent, null))
            }
        }
}