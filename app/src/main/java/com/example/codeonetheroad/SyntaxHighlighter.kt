package com.example.codeonetheroad

// Importations pour styliser du texte avec Jetpack Compose
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight

// 🔹 Fonction de coloration syntaxique basique pour du code Java
fun highlightJavaCode(code: String): AnnotatedString {
    // Liste des mots-clés Java à reconnaître
    val keywords = listOf(
        "abstract", "assert", "boolean", "break", "byte", "case", "catch", "char",
        "class", "const", "continue", "default", "do", "double", "else", "enum",
        "extends", "final", "finally", "float", "for", "goto", "if", "implements",
        "import", "instanceof", "int", "interface", "long", "native", "new", "package",
        "private", "protected", "public", "return", "short", "static", "strictfp",
        "super", "switch", "synchronized", "this", "throw", "throws", "transient",
        "try", "void", "volatile", "while"
    )

    // Expressions régulières pour détecter différents éléments syntaxiques
    val keywordRegex = Regex("\\b(${keywords.joinToString("|")})\\b")         // Mots-clés Java
    val stringRegex = Regex("\".*?\"|'.*?'")                                   // Chaînes de caractères
    val commentRegex = Regex(                                                  // Commentaires // ou /* */
        "//.*?$|/\\*.*?\\*/",
        setOf(RegexOption.DOT_MATCHES_ALL, RegexOption.MULTILINE)
    )
    val numberRegex = Regex("\\b\\d+(\\.\\d+)?\\b")                             // Nombres (entiers ou décimaux)

    // Structure de données pour stocker les zones à colorer
    data class HighlightedMatch(val range: IntRange, val style: SpanStyle)
    val matches = mutableListOf<HighlightedMatch>()

    // Recherche de tous les mots-clés dans le texte et ajout avec leur style
    keywordRegex.findAll(code).forEach {
        matches.add(
            HighlightedMatch(
                it.range,
                SpanStyle(color = Color.Cyan, fontWeight = FontWeight.Bold)
            )
        )
    }

    // Recherche des chaînes de caractères
    stringRegex.findAll(code).forEach {
        matches.add(
            HighlightedMatch(
                it.range,
                SpanStyle(color = Color(0xFFFFC66D)) // Orange clair
            )
        )
    }

    // Recherche des commentaires
    commentRegex.findAll(code).forEach {
        matches.add(
            HighlightedMatch(
                it.range,
                SpanStyle(color = Color.Gray, fontWeight = FontWeight.Light)
            )
        )
    }

    // Recherche des nombres
    numberRegex.findAll(code).forEach {
        matches.add(
            HighlightedMatch(
                it.range,
                SpanStyle(color = Color.Magenta)
            )
        )
    }

    // On trie toutes les correspondances par ordre d'apparition dans le texte
    matches.sortBy { it.range.first }

    // Construction de la chaîne stylisée à afficher
    return buildAnnotatedString {
        // Couleur par défaut pour tout texte non coloré (vert terminal)
        pushStyle(SpanStyle(color = Color(0xFF00FF00)))
        var lastIndex = 0

        for (match in matches) {
            val start = match.range.first
            val end = match.range.last + 1

            // On ajoute le texte "non stylé" avant la zone colorée si besoin
            if (start > lastIndex) {
                append(code.substring(lastIndex, start))
            }

            // On applique le style spécifique à la zone colorée
            pushStyle(match.style)
            append(code.substring(start, end))
            pop()

            lastIndex = end
        }

        // On ajoute le reste du texte s'il y en a après le dernier match
        if (lastIndex < code.length) {
            pushStyle(SpanStyle(color = Color(0xFF00FF00))) // Vert terminal
            append(code.substring(lastIndex))
            pop()
        }
    }
}
