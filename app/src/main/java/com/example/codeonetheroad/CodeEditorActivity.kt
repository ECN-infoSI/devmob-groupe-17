package com.example.codeonetheroad

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.codeonetheroad.ui.theme.CodeOnTheRoadTheme

class CodeEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileUriString = intent.getStringExtra("fileUri")
        val fileUri = fileUriString?.let { Uri.parse(it) }
        val fileContent = fileUri?.let { readTextFromUri(this, it) } ?: ""

        setContent {
            CodeOnTheRoadTheme(darkTheme = true) {
                CodeEditorScreen(fileContent)
            }
        }
    }
}

@Composable
fun CodeEditorScreen(initialText: String) {
    var text by remember { mutableStateOf(initialText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
            .padding(16.dp)
    ) {
        Text(
            text = "📝 Code Editor",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            textStyle = TextStyle(
                color = Color(0xFF00FFB3),
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Default),
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF1E1E1E))
                .padding(12.dp)
        )
    }
}
