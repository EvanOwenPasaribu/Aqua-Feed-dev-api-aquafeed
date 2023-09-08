package com.aqua_feed.app.callback

import com.aquafeed.app.model.response.LogResponseX

interface FetchRecyclerViewItems {
    fun onItemClicked(logs : LogResponseX)
}