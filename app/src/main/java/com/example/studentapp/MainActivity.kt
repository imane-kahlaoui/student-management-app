package com.example.studentapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.*

data class ModuleNote(
    val module: String,
    var note: String = ""
)

data class Student(
    var name: String,
    val modules: MutableList<ModuleNote>
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StudentApp()
        }
    }
}

@Composable
fun StudentApp() {
    val navController = rememberNavController()
    val students = remember { mutableStateListOf<Student>() }

    NavHost(navController = navController, startDestination = "accueil") {
        composable("accueil") {
            EcranAccueil { navController.navigate("etudiants") }
        }
        composable("etudiants") {
            EcranEtudiants(students) { navController.navigate("notes") }
        }
        composable("notes") {
            EcranNotes(
                students,
                onRetour = { navController.popBackStack() },
                onSuivant = { navController.navigate("resultats") }
            )
        }
        composable("resultats") {
            EcranResultats(students) { navController.popBackStack() }
        }
    }
}

@Composable
fun EcranAccueil(onSuivant: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1976D2)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "App Student",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(Modifier.height(30.dp))
            IconButton(onClick = onSuivant) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Suivant",
                    tint = Color.White,
                    modifier = Modifier.size(50.dp)
                )
            }
        }
    }
}

@Composable
fun EcranEtudiants(
    students: MutableList<Student>,
    onSuivant: () -> Unit
) {
    var nom by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Liste des étudiants", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = nom,
            onValueChange = { nom = it },
            label = { Text("Nom de l'étudiant") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = {
                if (nom.isNotEmpty()) {
                    students.add(
                        Student(
                            nom,
                            mutableListOf(
                                ModuleNote("Langue"),
                                ModuleNote("Dev Web"),
                                ModuleNote("JEE"),
                                ModuleNote("XML"),
                                ModuleNote("NoSQL")
                            )
                        )
                    )
                    nom = ""
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, "")
            Spacer(Modifier.width(6.dp))
            Text("Ajouter l'étudiant")
        }

        Spacer(Modifier.height(20.dp))

        Text(
            "Étudiants ajoutés :",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp
        )

        Spacer(Modifier.height(8.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(students) { s ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Row(
                        Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "",
                            tint = Color(0xFF1976D2)
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(s.name, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onSuivant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Suivant")
            Spacer(Modifier.width(6.dp))
            Icon(Icons.Default.ArrowForward, "")
        }
    }
}

@Composable
fun EcranNotes(
    students: MutableList<Student>,
    onRetour: () -> Unit,
    onSuivant: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var etudiantSelectionne by remember { mutableStateOf<Student?>(null) }
    val notesMap = remember { mutableStateMapOf<String, String>() }
    val modules = listOf("Langue", "Dev Web", "JEE", "XML", "NoSQL")

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Saisie des notes", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(12.dp))

        Box {
            OutlinedTextField(
                value = etudiantSelectionne?.name ?: "Choisir un étudiant",
                onValueChange = {},
                readOnly = true,
                label = { Text("Étudiant") },
                trailingIcon = {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Default.ArrowDropDown, "")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                students.forEach { student ->
                    DropdownMenuItem(onClick = {
                        etudiantSelectionne = student
                        expanded = false
                        notesMap.clear()
                        student.modules.forEach { mn ->
                            notesMap[mn.module] = mn.note
                        }
                    }) {
                        Text(student.name)
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (etudiantSelectionne != null) {
            Text(
                "Notes de ${etudiantSelectionne!!.name} :",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(Modifier.height(8.dp))

            modules.forEach { module ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(module, modifier = Modifier.weight(1f), fontSize = 15.sp)

                    OutlinedTextField(
                        value = notesMap[module] ?: "",
                        onValueChange = { nouvelleNote ->
                            notesMap[module] = nouvelleNote
                            etudiantSelectionne?.modules
                                ?.find { it.module == module }
                                ?.note = nouvelleNote
                        },
                        label = { Text("Note") },
                        modifier = Modifier.width(110.dp)
                    )
                }
            }
        } else {
            Text(
                "Veuillez sélectionner un étudiant pour saisir ses notes.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.weight(1f))

        Row(Modifier.fillMaxWidth()) {
            Button(onClick = onRetour, modifier = Modifier.weight(1f)) {
                Icon(Icons.Default.ArrowBack, "")
                Spacer(Modifier.width(4.dp))
                Text("Retour")
            }
            Spacer(Modifier.width(10.dp))
            Button(onClick = onSuivant, modifier = Modifier.weight(1f)) {
                Text("Résultats")
                Spacer(Modifier.width(4.dp))
                Icon(Icons.Default.ArrowForward, "")
            }
        }
    }
}

@Composable
fun EcranResultats(
    students: MutableList<Student>,
    onRetour: () -> Unit
) {
    val etudiantsAvecNotes = students.filter { s ->
        s.modules.any { it.note.isNotEmpty() }
    }

    fun moyenne(s: Student): Double {
        val remplies = s.modules.filter { it.note.isNotEmpty() }
        return if (remplies.isEmpty()) 0.0
        else remplies.map { it.note.toDoubleOrNull() ?: 0.0 }.average()
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {

        Text("Résultats", fontSize = 22.sp, fontWeight = FontWeight.Bold)

        Spacer(Modifier.height(10.dp))

        LazyColumn(Modifier.weight(1f)) {
            items(etudiantsAvecNotes) { s ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(12.dp),
                    elevation = 4.dp
                ) {
                    Column(Modifier.padding(16.dp)) {

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "",
                                tint = Color(0xFF1976D2)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "Nom étudiant : ${s.name}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Color(0xFF1976D2)
                            )
                        }

                        Spacer(Modifier.height(10.dp))

                        Row(Modifier.fillMaxWidth()) {
                            Text("Module", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Note", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Résultat", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Divider(Modifier.padding(vertical = 4.dp))

                        s.modules.forEach { mn ->
                            val noteDouble = mn.note.toDoubleOrNull()
                            val resultat = when {
                                mn.note.isEmpty() -> "-"
                                noteDouble != null && noteDouble >= 10.0 -> "✓ V"
                                else -> "✗ NV"
                            }
                            val couleur = when {
                                mn.note.isEmpty() -> Color.Gray
                                noteDouble != null && noteDouble >= 10.0 -> Color(0xFF2E7D32)
                                else -> Color(0xFFC62828)
                            }

                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                            ) {
                                Text(mn.module, modifier = Modifier.weight(2f), fontSize = 13.sp)
                                Text(
                                    if (mn.note.isEmpty()) "-" else mn.note,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp
                                )
                                Text(
                                    resultat,
                                    modifier = Modifier.weight(1f),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = couleur
                                )
                            }
                        }

                        Divider(Modifier.padding(vertical = 4.dp))

                        val moy = moyenne(s)
                        Row(Modifier.fillMaxWidth()) {
                            Text("Moyenne", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(
                                "${"%.2f".format(moy)}",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2),
                                fontSize = 13.sp
                            )

                            // Correction ici : s.modules.all au lieu de Module.all
                            val tousModulesValides = s.modules.all { (it.note.toDoubleOrNull() ?: 0.0) >= 10.0 }
                            val estValide = moy >= 10.0 && tousModulesValides

                            Text(
                                text = if (estValide) "✓ V" else "✗ NV",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Bold,
                                color = if (estValide) Color(0xFF2E7D32) else Color(0xFFC62828),
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(10.dp))

        Button(
            onClick = onRetour,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.ArrowBack, "")
            Spacer(Modifier.width(6.dp))
            Text("Retour")
        }
    }
}