package com.movie.binged.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movie.binged.room.entities.GenreEntity

@Composable
fun UserSetupScreen() {

    var name = remember { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ){
        OutlinedTextField(
            value = name.value,
            onValueChange = { it ->
                name.value = it
                            },
            modifier = Modifier.padding(8.dp).height(50.dp).fillMaxWidth(),
            label = {
                Text("Enter your name")
            }
        )

        Spacer( modifier = Modifier.padding(top = 16.dp))


    }
}