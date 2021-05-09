package com.example.android.politicalpreparedness.ui

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter

@BindingAdapter("loadingStatus")
fun bindLoadingStatus(progressBar: ProgressBar, loadingStatus: Boolean) {
    if (loadingStatus) {
        progressBar.visibility = VISIBLE
    }
    else {
        progressBar.visibility = GONE
    }

}