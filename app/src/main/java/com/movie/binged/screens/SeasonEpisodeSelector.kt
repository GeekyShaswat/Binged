package com.movie.binged.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.movie.binged.model.SeasonEpModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeasonDropDown(
    season : List<SeasonEpModel>,
    selectedSeason : SeasonEpModel?,
    onSeasonSelected: (SeasonEpModel) -> Unit,
    modifier: Modifier
)   {

    val expanded = remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded.value,
        modifier = modifier,
        onExpandedChange = { expanded.value= !expanded.value }
    ) {
        OutlinedTextField(
            value = selectedSeason?.let { "Season ${it.season}" } ?: "Select Season",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded.value) },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colors.primary
            )
        )
        ExposedDropdownMenu(
            expanded.value,
            onDismissRequest = { expanded.value = false },
            modifier = Modifier
                .background(
                    MaterialTheme.colors.surface,
                    RoundedCornerShape(16.dp) // Rounded menu
                )
        ) {
            season.forEach { season ->
                DropdownMenuItem(
                    text = { Text(text = "Season ${season.season}" )},
                    onClick = {
                        onSeasonSelected(season)
                        expanded.value = false
                    }
                )

            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EpisodeDropDown(
    episodeCount: Int,
    enabled: Boolean,
    selectedEpisode: Int,
    onEpisodeSelected: (Int) -> Unit,
    modifier: Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    val episodes = remember(episodeCount) {
        (1..episodeCount).toList()
    }

    ExposedDropdownMenuBox(
        expanded = expanded && enabled,
        onExpandedChange = {
            if (enabled) expanded = !expanded
        },
        modifier = modifier,
    ) {
        var crntEpisode = if (selectedEpisode == 0 ) "Select Episode" else selectedEpisode.let { "Episode $it" }
        OutlinedTextField(
            value =  crntEpisode ,
            onValueChange = {},
            readOnly = true,
            enabled = enabled,
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded)
            },
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = MaterialTheme.colors.primary.copy(alpha = 0.5f),
                focusedBorderColor = MaterialTheme.colors.primary
            )
        )

        ExposedDropdownMenu(
            expanded = expanded && enabled,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(
                    MaterialTheme.colors.surface,
                    RoundedCornerShape(16.dp) // Rounded menu
                )
        ) {
            episodes.forEach { ep ->
                DropdownMenuItem(
                    text = { Text("Episode $ep") },
                    onClick = {
                        onEpisodeSelected(ep)
                        expanded = false
                    }
                )
            }
        }
    }
}


@Composable
fun SeasonEpisodeSelector(
    data: List<SeasonEpModel>,
    onSeasonEpisodeSelected: (Int, Int) -> Unit
) {
    var seasonSelector by remember(data) {
        mutableStateOf(data.firstOrNull())
    }

    var selectedEpisode by remember(seasonSelector) {
        mutableStateOf(1)
    }

    LaunchedEffect(seasonSelector, selectedEpisode) {
        onSeasonEpisodeSelected(seasonSelector?.season ?: 1, selectedEpisode)
    }


    if (seasonSelector == null) return

    Row(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SeasonDropDown(
            season = data,
            selectedSeason = seasonSelector,
            onSeasonSelected = {
                seasonSelector = it
                selectedEpisode = 1
                onSeasonEpisodeSelected(it.season, 1)
            },
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(6.dp))

        EpisodeDropDown(
            episodeCount = seasonSelector!!.episode,
            enabled = true,
            selectedEpisode = selectedEpisode,
            onEpisodeSelected = {
                selectedEpisode = it
                onSeasonEpisodeSelected(seasonSelector!!.season, it)
            },
            modifier = Modifier.weight(1f)
        )
    }
}
