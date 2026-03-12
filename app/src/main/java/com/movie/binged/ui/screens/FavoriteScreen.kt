package com.movie.binged.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie.binged.data.room.entities.FavoriteEntity
import com.movie.binged.ui.navigation.Screens
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.viewmodel.FavoritesViewModel
import com.movie.binged.viewmodel.FavoritesViewModelFactory

@Composable
fun FavoriteScreen(navController: NavController, userRepo: UserRepository) {
    val favoritesViewModel = viewModel<FavoritesViewModel>(
        factory = FavoritesViewModelFactory(userRepo)
    )
    val favorites = favoritesViewModel.favorites.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top= 24.dp, start = 16.dp)
    ) {
        Text(
            text = "Favorites",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No favorites yet", color = MaterialTheme.colorScheme.onSurface)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(favorites) { item ->
                    FavoriteCard(item = item, onClick = {
                        navController.navigate(
                            Screens.Detail.createRoute(
                                id = item.ids?.trakt.toString(),
                                type = item.mediaType
                            )
                        )
                    })
                }
            }
        }
    }
}

@Composable
fun FavoriteCard(item: FavoriteEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .height(250.dp)
            .width(150.dp)
            .padding(4.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            val posterUrl = item.images?.poster?.firstOrNull()?.let {
                if (it.startsWith("http")) it else "https://$it"
            }
            AsyncImage(
                model = posterUrl,
                contentDescription = "image for lazy row",
                modifier = Modifier
                    .height(190.dp)
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            item.title?.let {
                Text(
                    text = it,
                    maxLines = 1,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 6.dp)
                )
            }
            Text(
                text = "${item.year}",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}