package com.example.codeonetheroad

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import com.example.codeontheroad.ui.theme.CodeOnTheRoadTheme
import java.io.OutputStreamWriter
import java.io.BufferedReader

class CodeEditorActivity : ComponentActivity() {

    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val fileUriString = intent.getStringExtra("fileUri")
        fileUri = fileUriString?.let { Uri.parse(it) }
        val fileContent = fileUri?.let { readTextFromUri(this, it) } ?: ""

        setContent {
            CodeOnTheRoadTheme() {
                CodeEditorScreen(
                    initialText = fileContent,
                    onSave = { text -> fileUri?.let { saveTextToUri(it, text) } }
                )
            }
        }
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

    private fun readTextFromUri(context: ComponentActivity, uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        return inputStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
    }
}

@Composable
fun CodeEditorScreen(initialText: String, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(initialText) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    )
    {
        Text(
            text = "Code Editor",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        BasicTextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(12.dp),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSave(text) },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("💾 Save")
        }
    }
}
