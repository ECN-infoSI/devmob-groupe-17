package com.example.codeonetheroad

import android.net.Uri

// 🔹 Représente un onglet ouvert dans l'éditeur (un fichier)
data class OpenFileTab(
    val uri: Uri,           // URI du fichier ouvert
    var content: String,    // Contenu du fichier (modifiable)
    val fileName: String    // Nom du fichier à afficher dans l'onglet
)

// 🔹 Objet singleton pour gérer les onglets ouverts et l'état global de la session
object TabSessionManager {
    val openTabs = mutableListOf<OpenFileTab>() // Liste des fichiers ouverts
    var selectedIndex = 0                       // Index de l’onglet actuellement sélectionné

    // Fonction pour ajouter un onglet (si le fichier n’est pas déjà ouvert)
    fun addTab(tab: OpenFileTab) {
        if (openTabs.none { it.uri == tab.uri }) {
            openTabs.add(tab)
            selectedIndex = openTabs.lastIndex // On sélectionne le nouvel onglet automatiquement
        }
    }

    // Fonction pour fermer un onglet à l’index donné
    fun removeTabAt(index: Int) {
        openTabs.removeAt(index)

        // On ajuste l’index sélectionné pour éviter qu’il dépasse la taille de la liste
        selectedIndex = selectedIndex.coerceAtMost(openTabs.lastIndex)
    }
}
