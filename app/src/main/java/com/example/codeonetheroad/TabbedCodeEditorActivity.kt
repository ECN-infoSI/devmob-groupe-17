package com.example.codeonetheroad

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.codeontheroad.ui.theme.CodeOnTheRoadTheme
import java.io.OutputStreamWriter
import java.io.BufferedReader

class TabbedCodeEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CodeOnTheRoadTheme {
                val context = LocalContext.current
                val openTabs = remember { mutableStateListOf<OpenFileTab>().apply { addAll(TabSessionManager.openTabs) } }
                var selectedTabIndex by remember { mutableStateOf(TabSessionManager.selectedIndex) }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        openTabs.forEachIndexed { index, tab ->
                            TabItem(
                                title = tab.fileName,
                                selected = index == selectedTabIndex,
                                onClick = {
                                    selectedTabIndex = index
                                    TabSessionManager.selectedIndex = index
                                },
                                onClose = {
                                    TabSessionManager.removeTabAt(index)
                                    openTabs.clear()
                                    openTabs.addAll(TabSessionManager.openTabs)
                                    selectedTabIndex = TabSessionManager.selectedIndex
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        TabItem(title = "+", selected = false, onClick = {
                            startActivity(Intent(this@TabbedCodeEditorActivity, MainActivity::class.java))
                        })
                    }

                    if (openTabs.isNotEmpty()) {
                        val currentTab = openTabs[selectedTabIndex]
                        CodeEditor(
                            text = currentTab.content,
                            onTextChange = { newText ->
                                openTabs[selectedTabIndex] = currentTab.copy(content = newText)
                                TabSessionManager.openTabs[selectedTabIndex].content = newText
                            },
                            onSave = {
                                saveTextToUri(currentTab.uri, currentTab.content)
                            }
                        )
                    } else {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Open a file using the + tab", color = Color.Gray)
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        intent?.getStringExtra("newFileUri")?.let { uriString ->
            val uri = Uri.parse(uriString)
            if (TabSessionManager.openTabs.none { it.uri == uri }) {
                val content = readTextFromUri(uri)
                val name = uri.lastPathSegment?.split("/")?.last() ?: "Untitled"
                val newTab = OpenFileTab(uri, content, name)
                TabSessionManager.addTab(newTab)
            }
            intent.removeExtra("newFileUri")
        }
    }

    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use(BufferedReader::readText)
            ?: ""
    }

    private fun saveTextToUri(uri: Uri, content: String) {
        try {
            val outputStream = contentResolver.openOutputStream(uri, "wt")
            val writer = OutputStreamWriter(outputStream)
            writer.write(content)
            writer.flush()
            writer.close()
            Toast.makeText(this, "File saved ✅", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Failed to save ❌", Toast.LENGTH_SHORT).show()
        }
    }
}