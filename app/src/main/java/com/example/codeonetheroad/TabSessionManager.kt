package com.example.codeonetheroad


import android.net.Uri
data class OpenFileTab(
    val uri: Uri,
    var content: String,
    val fileName: String
)
object TabSessionManager {
    val openTabs = mutableListOf<OpenFileTab>()
    var selectedIndex = 0

    fun addTab(tab: OpenFileTab) {
        if (openTabs.none { it.uri == tab.uri }) {
            openTabs.add(tab)
            selectedIndex = openTabs.lastIndex
        }
    }

    fun removeTabAt(index: Int) {
        openTabs.removeAt(index)
        selectedIndex = selectedIndex.coerceAtMost(openTabs.lastIndex)
    }
}
