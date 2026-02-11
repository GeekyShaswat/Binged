package com.movie.binged.screens

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.primarySurface
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.movie.binged.navigation.Screens
import com.movie.binged.repository.ApiRepository
import com.movie.binged.viewmodel.DetailViewModelFactory
import com.movie.binged.viewmodel.DetailsViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(id: String?, type: String?, navController: NavController) {
    val scrollState = rememberScrollState()
    val headerHeight = 400.dp
    val headerHeightPx = with(LocalDensity.current) { headerHeight.toPx() }
    val retrofit = RetrofitClient.api
    val detailViewModel = viewModel<DetailsViewModel>(
        factory = DetailViewModelFactory(ApiRepository(retrofit))
    )
    LaunchedEffect(Unit) {
        detailViewModel.loadData(id,type)
        if (type.equals("show"))detailViewModel.getSeasonData(id)

    }

    var seasonCount = rememberSaveable { mutableStateOf<Int?>(null) }
    var episodeCount = rememberSaveable { mutableStateOf<Int?>(null) }
    val context = LocalContext.current

    val detailScreenData = detailViewModel.dataObject.collectAsState().value
    val seasonData = detailViewModel.seasonData.collectAsState().value

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState).background(MaterialTheme.colors.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerHeight)
                    .graphicsLayer {
                        translationY = scrollState.value * 0.5f // Parallax effect
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
                                    MaterialTheme.colors.background
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
                    .background(MaterialTheme.colors.background)
                    .padding(16.dp)
            ) {
                Text(
                    text = detailScreenData?.title ?: "",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colors.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = detailScreenData?.tagline ?: "TAGLINE",
                    fontSize = 18.sp,
                    modifier = Modifier.padding(bottom = 4.dp),
                    color = MaterialTheme.colors.onSurface
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

                    color = MaterialTheme.colors.onSurface
                )

                if (type.equals("show")){
                    SeasonEpisodeSelector(
                        data = seasonData,
                        { season, episode ->
                            seasonCount.value = season
                            episodeCount.value = episode
                        }
                    )
                }

                // Action buttons
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(
                        onClick = {
                            Log.d("TAG","play movie / show clicked ")
                            if( type.equals("movie")){
                                navController.navigate(Screens.VideoPlayer.createRoute("${detailScreenData?.ids?.tmdb}","movie",null,null))
                            }
                            else{
                                Log.d("TAG","series data ${seasonCount.value} and ${episodeCount.value} ")
                                navController.navigate(
                                Screens.VideoPlayer.createRoute(
                                    id = detailScreenData!!.ids.tmdb.toString(),
                                    type = "show",
                                    season = seasonCount.value,
                                    episode = episodeCount.value
                                ))
                            }
                                  },
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(Modifier.width(4.dp))
                        Text("Play")
                    }

                    OutlinedButton(
                        onClick = {
                            Log.d("TAG","play trailer clicked ")
                            val intent = Intent(Intent.ACTION_VIEW, detailScreenData?.trailer?.toUri())
                            context.startActivity(intent)
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Trailer")
                    }
                }
            }
        }

        // Floating back button with blur effect
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
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

