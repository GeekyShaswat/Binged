package com.movie.binged.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UserProfileScreen(
    name: String,
    savedGenres: List<String>,
    onGenresSaved: (List<String>) -> Unit,
    navController : NavController,
    onLogout: () -> Unit
) {
    // Local edit state — only committed on Save
    var editingGenres by remember { mutableStateOf(false) }
    val tempSelected = remember { mutableStateListOf<String>().also { it.addAll(savedGenres) } }
    var showLogoutDialog by remember { mutableStateOf(false) }

    // Sync tempSelected if parent data changes (e.g. after first load)
    LaunchedEffect(savedGenres) {
        tempSelected.clear()
        tempSelected.addAll(savedGenres)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row (
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier
                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.onBackground,
                        shape = CircleShape
                    )
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
        }
        // Avatar
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(60.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = name,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(36.dp))

        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)

        Spacer(modifier = Modifier.height(24.dp))

        // Genres section header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "My Genres",
                fontSize = 17.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            IconButton(onClick = { editingGenres = !editingGenres }) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit genres",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (editingGenres) {
            Text(
                text = "Select 3–7 genres  •  ${tempSelected.size} selected",
                fontSize = 12.sp,
                color = if (tempSelected.size < 3)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 12.dp)
            )
        } else {
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Chips — read-only or editable
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val displayList = if (editingGenres) ALL_GENRES else savedGenres

            displayList.forEach { genre ->
                val isSelected = genre in tempSelected
                GenreChip(
                    label = genre,
                    selected = if (editingGenres) isSelected else true,
                    onClick = {
                        if (!editingGenres) return@GenreChip
                        if (isSelected) {
                            tempSelected.remove(genre)
                        } else if (tempSelected.size < 7) {
                            tempSelected.add(genre)
                        }
                    }
                )
            }
        }

        // Save / Cancel row — only shown while editing
        if (editingGenres) {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        tempSelected.clear()
                        tempSelected.addAll(savedGenres)
                        editingGenres = false
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        onGenresSaved(tempSelected.toList())
                        editingGenres = false
                    },
                    enabled = tempSelected.size in 3..7,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Save")
                }
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        // Logout button at the bottom
        OutlinedButton(
            onClick = { showLogoutDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            )
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Logout", fontSize = 15.sp, fontWeight = FontWeight.Medium)
        }
    }
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.error
                )
            },
            title = {
                Text(
                    text = "Logout",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to logout? Your profile and preferences will be cleared.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Yes, Logout")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showLogoutDialog = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}