package com.movie.binged.ui.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie.binged.api.client.RetrofitClient
import com.movie.binged.ui.navigation.Screens
import com.movie.binged.data.repository.ApiRepository
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.data.room.db.AppDatabase
import com.movie.binged.data.room.entities.FavoriteEntity
import com.movie.binged.data.room.entities.HistoryEntity
import com.movie.binged.viewmodel.DetailViewModelFactory
import com.movie.binged.viewmodel.DetailsViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    id: String?,
    type: String?,
    navController: NavController,
    userRepository: UserRepository
) {
    val scrollState = rememberScrollState()
    val headerHeight = 400.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val retrofit = RetrofitClient.api
    val detailViewModel = viewModel<DetailsViewModel>(
        factory = DetailViewModelFactory(ApiRepository(retrofit),userRepository)
    )
    val isFavorite = detailViewModel.isFavorite.collectAsState().value
    LaunchedEffect(Unit) {
        detailViewModel.loadData(id,type)
        if (type.equals("show")) detailViewModel.getSeasonData(id)
    }


    val seasonCount = rememberSaveable { mutableStateOf<Int?>(null) }
    val episodeCount = rememberSaveable { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    val detailScreenData = detailViewModel.dataObject.collectAsState().value
    val seasonData = detailViewModel.seasonData.collectAsState().value

    val savedSeason = detailViewModel.savedSeason.collectAsState().value
    val savedEpisode = detailViewModel.savedEpisode.collectAsState().value
    val historyChecked = detailViewModel.historyChecked.collectAsState().value

    LaunchedEffect(detailScreenData) {
        val imdbId = detailScreenData?.ids?.imdb
        if (imdbId != null) {
            detailViewModel.checkFavorite(imdbId)  // ← add this line
            if (type == "show") {
                detailViewModel.loadHistoryFor(imdbId)
            }
        }
    }
    LaunchedEffect(savedSeason, savedEpisode) {
        if (savedSeason != null) seasonCount.value = savedSeason
        if (savedEpisode != null) episodeCount.value = savedEpisode
        Log.d("History","data for display season is: ${seasonCount.value} and episode is: ${episodeCount.value}")
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .graphicsLayer {
                        translationY = scrollState.value * 0.5f
                        alpha = 1f - (scrollState.value / headerHeightPx).coerceIn(0f, 1f)
                    }
            ) {
                val posterUrl = detailScreenData?.images?.thumb?.firstOrNull()?.let {
                    if (it.startsWith("http")) it else "https://$it"
                }
                AsyncImage(
                    model = posterUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.3f),
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        )
                )

                Card(
                    modifier = Modifier
                        .width(150.dp)
                        .height(200.dp)
                        .align(Alignment.BottomStart)
                        .padding(start = 16.dp, bottom = 16.dp),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    val posterUrl = detailScreenData?.images?.poster?.firstOrNull()?.let {
                        if (it.startsWith("http")) it else "https://$it"
                    }
                    AsyncImage(
                        model = posterUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = detailScreenData?.title ?: "",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = detailScreenData?.tagline ?: "TAGLINE",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetadataChip("${detailScreenData?.year}")
                    MetadataChip("★ ${"%.1f".format(detailScreenData?.rating)}")
                    MetadataChip("${detailScreenData?.runtime} min")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = detailScreenData?.overview ?: "OVERVIEW",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Justify,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colorScheme.onSurface
                )

                val selectorReady = type == "show" && historyChecked && seasonData.isNotEmpty()

                if (selectorReady) {
                    SeasonEpisodeSelector(
                        data = seasonData,
                        initialSeason = seasonCount.value,    // ← pass saved season
                        initialEpisode = episodeCount.value,  // ← pass saved episode
                        onSeasonEpisodeSelected = { season, episode ->
                            seasonCount.value = season
                            episodeCount.value = episode
                        }
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            Log.d("TAG","play movie / show clicked ")
                            val item : HistoryEntity?
                            if (type.equals("movie")){
                                navController.navigate(Screens.VideoPlayer.createRoute("${detailScreenData?.ids?.tmdb}","movie",-1,-1))
                                item  = HistoryEntity(
                                    ids = detailScreenData?.ids ,
                                    title = detailScreenData?.title ,
                                    images = detailScreenData?.images ,
                                    overviewText = detailScreenData?.overview,
                                    mediaType = type ?: "",
                                    season = null,
                                    episode = null,
                                    year = detailScreenData?.year ?: 0
                                )
                            } else {
                                Log.d("TAG","series data ${seasonCount.value} and ${episodeCount.value} ")
                                navController.navigate(
                                    Screens.VideoPlayer.createRoute(
                                        id = detailScreenData!!.ids.tmdb.toString(),
                                        type = "show",
                                        season = seasonCount.value,
                                        episode = episodeCount.value
                                    )
                                )
                                item  = HistoryEntity(
                                    ids = detailScreenData.ids,
                                    title = detailScreenData.title ,
                                    images = detailScreenData.images ,
                                    overviewText = detailScreenData.overview,
                                    mediaType = type ?: "",
                                    season = seasonCount.value,
                                    episode = episodeCount.value,
                                    year = detailScreenData.year
                                )
                            }
                            Log.d("TAG","the item added in db : $item")
                            detailViewModel.insertHistory(item)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Play")
                    }

                    OutlinedButton(
                        onClick = {
                            if (detailScreenData?.trailer != null) {
                                Log.d("TAG", "play trailer clicked ")
                                val intent = Intent(Intent.ACTION_VIEW, detailScreenData.trailer.toUri())
                                context.startActivity(intent)
                            } else {
                                Toast.makeText(context,"Trailer not available", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Trailer")
                    }
                }
            }
        }

        // Top overlay row with back + favorite
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp, start = 16.dp, bottom = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Back button (your existing one)
            IconButton(
                onClick = { navController.navigateUp() },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)

            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }

            // Favorite button
            IconButton(
                onClick = {
                    val imdbId = detailScreenData?.ids?.imdb ?: return@IconButton
                    val entity = FavoriteEntity(
                        imdbId = imdbId,
                        title = detailScreenData.title,
                        overviewText = detailScreenData.overview,
                        mediaType = type ?: "",
                        year = detailScreenData.year,
                        ids = detailScreenData.ids,
                        images = detailScreenData.images
                    )
                    detailViewModel.toggleFavorite(entity)
                },
                modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), CircleShape)
                    .border(width = 1.dp, color = MaterialTheme.colorScheme.onBackground, shape = CircleShape)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) Color.Red else Color.White
                )
            }
        }
    }
}

@Composable
fun MetadataChip(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Gray
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontSize = 14.sp,
            color = Color.White
        )
    }
}