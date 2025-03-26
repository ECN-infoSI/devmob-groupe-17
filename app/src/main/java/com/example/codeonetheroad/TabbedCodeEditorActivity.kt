package com.example.codeonetheroad

// Importations nécessaires
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

// Activité principale de l’éditeur de code à onglets
class TabbedCodeEditorActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Interface utilisateur définie avec Jetpack Compose
        setContent {
            CodeOnTheRoadTheme {
                val context = LocalContext.current

                // Liste réactive des onglets ouverts (fichiers)
                val openTabs = remember { mutableStateListOf<OpenFileTab>().apply { addAll(TabSessionManager.openTabs) } }

                // Index de l’onglet actuellement sélectionné
                var selectedTabIndex by remember { mutableStateOf(TabSessionManager.selectedIndex) }

                // Mise en page verticale principale
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                        .padding(8.dp)
                ) {
                    // Affichage horizontal des onglets en haut de l'écran
                    Row(
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        // Pour chaque onglet ouvert, on affiche un composant TabItem
                        openTabs.forEachIndexed { index, tab ->
                            TabItem(
                                title = tab.fileName,
                                selected = index == selectedTabIndex,
                                onClick = {
                                    selectedTabIndex = index
                                    TabSessionManager.selectedIndex = index
                                },
                                onClose = {
                                    // Fermeture d'un onglet
                                    TabSessionManager.removeTabAt(index)
                                    openTabs.clear()
                                    openTabs.addAll(TabSessionManager.openTabs)
                                    selectedTabIndex = TabSessionManager.selectedIndex
                                }
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        // Onglet spécial "+" pour ouvrir un nouveau fichier
                        TabItem(title = "+", selected = false, onClick = {
                            startActivity(Intent(this@TabbedCodeEditorActivity, MainActivity::class.java))
                        })
                    }

                    // Affichage de l'éditeur de texte si des onglets sont ouverts
                    if (openTabs.isNotEmpty()) {
                        val currentTab = openTabs[selectedTabIndex]
                        CodeEditor(
                            text = currentTab.content,
                            onTextChange = { newText ->
                                // Mise à jour du contenu de l’onglet sélectionné
                                openTabs[selectedTabIndex] = currentTab.copy(content = newText)
                                TabSessionManager.openTabs[selectedTabIndex].content = newText
                            },
                            onSave = {
                                // Sauvegarde du fichier
                                saveTextToUri(currentTab.uri, currentTab.content)
                            }
                        )
                    } else {
                        // Message vide si aucun fichier n’est ouvert
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

    // Fonction appelée quand l'activité revient en premier plan
    override fun onResume() {
        super.onResume()

        // Vérifie si un nouveau fichier a été sélectionné (passé en Intent)
        intent?.getStringExtra("newFileUri")?.let { uriString ->
            val uri = Uri.parse(uriString)
            // On évite d’ouvrir plusieurs fois le même fichier
            if (TabSessionManager.openTabs.none { it.uri == uri }) {
                val content = readTextFromUri(uri)
                val name = uri.lastPathSegment?.split("/")?.last() ?: "Untitled"
                val newTab = OpenFileTab(uri, content, name)
                TabSessionManager.addTab(newTab)
            }
            // Nettoyage de l’intent pour éviter de le traiter plusieurs fois
            intent.removeExtra("newFileUri")
        }
    }

    // Fonction utilitaire pour lire le contenu d’un fichier à partir de son URI
    private fun readTextFromUri(uri: Uri): String {
        return contentResolver.openInputStream(uri)
            ?.bufferedReader()
            ?.use(BufferedReader::readText)
            ?: ""
    }

    // Fonction pour sauvegarder le texte dans un fichier à partir de son URI
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
