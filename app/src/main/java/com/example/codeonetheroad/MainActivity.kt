package com.example.codeonetheroad

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.codeonetheroad.ui.theme.CodeOnTheRoadTheme
import java.io.BufferedReader

class MainActivity : ComponentActivity() {

    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            if (uri != null) {
                selectedFileUri = uri
                startActivity(Intent(this, CodeEditorActivity::class.java).apply {
                    putExtra("fileUri", uri.toString())
                })
            }
        }

        setContent {
            CodeOnTheRoadTheme(darkTheme = true) {
                FileManagerScreen(filePickerLauncher)
            }
        }
    }
}

fun readTextFromUri(context: Context, uri: Uri): String {
    val inputStream = context.contentResolver.openInputStream(uri)
    return inputStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
}

@Composable
fun FileManagerScreen(filePickerLauncher: ActivityResultLauncher<String>) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Code On The Road",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { filePickerLauncher.launch("*/*") },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E88E5)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("📂 Open File")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Choose a code file to edit (.kt, .java, .txt)",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.LightGray
        )
    }
}
