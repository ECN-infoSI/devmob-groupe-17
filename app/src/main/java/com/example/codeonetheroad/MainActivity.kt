package com.example.codeonetheroad

// Importations des classes nécessaires
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.codeontheroad.ui.theme.CodeOnTheRoadTheme

// Activité principale de l'application
class MainActivity : ComponentActivity() {

    // Variable pour stocker l'URI du fichier sélectionné
    private var selectedFileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Déclaration du launcher qui ouvre le sélecteur de fichiers
        val filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                // On prend la permission de lire/écrire de manière persistante sur le fichier
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )

                // On stocke le fichier sélectionné
                selectedFileUri = uri

                // On lance l'activité de l'éditeur de code, en lui transmettant l'URI du fichier
                startActivity(Intent(this, TabbedCodeEditorActivity::class.java).apply {
                    putExtra("newFileUri", uri.toString())
                })
            }
        }

        // Définition du contenu de l'écran avec Jetpack Compose
        setContent {
            // Application du thème personnalisé
            CodeOnTheRoadTheme {
                // Affichage de l'écran de gestion de fichiers (le 1er écran)
                FileManagerScreen(filePickerLauncher)
            }
        }
    }
}

// Composable qui représente l'écran d'accueil avec le bouton d'ouverture de fichier
@Composable
fun FileManagerScreen(filePickerLauncher: ActivityResultLauncher<Array<String>>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Titre de l'application
        Text(
            "Code On The Road",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Bouton pour lancer l'ouverture d'un fichier
        Button(
            onClick = { filePickerLauncher.launch(arrayOf("*/*")) }, // Lance un sélecteur de n'importe quel fichier
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("📂 Open File") // Texte du bouton
        }
    }
}
