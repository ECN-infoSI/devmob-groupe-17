package com.example.codeonetheroad

// Importation des composants nécessaires à Jetpack Compose
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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

// 🔹 Onglet d’un fichier ouvert
@Composable
fun TabItem(
    title: String,                // Nom du fichier (ou "+" pour en ouvrir un autre)
    selected: Boolean,           // Est-ce que cet onglet est sélectionné ?
    onClick: () -> Unit,         // Action à effectuer lorsqu’on clique sur l’onglet
    onClose: (() -> Unit)? = null // Action à effectuer lorsqu’on clique sur le "✖" (fermeture)
) {
    Surface(
        tonalElevation = if (selected) 4.dp else 0.dp, // Légère élévation visuelle si l’onglet est actif
        color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
        modifier = Modifier.clickable { onClick() }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Titre de l’onglet (nom du fichier ou "+")
            Text(
                text = title,
                color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            // Bouton de fermeture ✖ si applicable
            if (onClose != null) {
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "✖",
                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.clickable { onClose() }
                )
            }
        }
    }
}

// 🔹 Barre de raccourcis personnalisée pour insérer rapidement des symboles utiles en programmation
@Composable
fun CodeKeyboardBar(onInsert: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Liste des symboles à insérer
        val symbols = listOf("(", ")", "{", "}", "[", "]", ";", "<", ">", "=", "\"", "'", ".", ",", "+", "-", "*", "/")
        symbols.forEach { symbol ->
            Button(
                onClick = { onInsert(symbol) },         // Quand on clique, on insère le symbole
                contentPadding = PaddingValues(4.dp),
                modifier = Modifier.width(40.dp)
            ) {
                Text(symbol)
            }
        }
    }
}

// 🔹 Éditeur de code principal
@Composable
fun CodeEditor(text: String, onTextChange: (String) -> Unit, onSave: () -> Unit) {
    var cursorPosition by remember { mutableStateOf(text.length) } // Position du curseur (simplifiée ici)

    Column(modifier = Modifier.fillMaxSize()) {
        // Zone principale d'édition de texte
        Box(
            modifier = Modifier
                .weight(1f) // Prend tout l’espace vertical disponible
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(12.dp)
        ) {
            // Texte affiché avec coloration syntaxique
            Text(
                text = highlightJavaCode(text),
                style = TextStyle(color = Color.Transparent) // Invisible (recouvert par l’éditeur)
            )

            // Zone de texte modifiable (transparent pour superposer au texte coloré)
            BasicTextField(
                value = text,
                onValueChange = {
                    onTextChange(it)
                    cursorPosition = it.length // Mise à jour basique du curseur (à améliorer)
                },
                modifier = Modifier.matchParentSize(),
                textStyle = TextStyle.Default.copy(color = Color.Transparent),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
            )
        }

        // 🔸 Affichage de la barre de raccourcis clavier personnalisée
        CodeKeyboardBar { symbol ->
            // Insertion du symbole à la position actuelle
            val newText = text.substring(0, cursorPosition) + symbol + text.substring(cursorPosition)
            onTextChange(newText)
            cursorPosition += symbol.length
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 🔸 Bouton de sauvegarde du fichier
        Button(
            onClick = onSave,
            modifier = Modifier.align(Alignment.End),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
        ) {
            Text("💾 Save")
        }
    }
}
