package com.movie.binged.ui.screens

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.movie.binged.R
import com.movie.binged.api.client.RetrofitClient
import com.movie.binged.model.HomeScreenSection
import com.movie.binged.model.UiPosterData
import com.movie.binged.data.repository.ApiRepository
import com.movie.binged.data.repository.UserRepository
import com.movie.binged.data.room.db.AppDatabase
import com.movie.binged.ui.navigation.Screens
import com.movie.binged.viewmodel.HomeViewModel
import com.movie.binged.viewmodel.HomeViewModelFactory
import com.movie.binged.viewmodel.Status
import com.movie.binged.viewmodel.UserViewModel
import com.movie.binged.viewmodel.UserViewModelFactory
import kotlinx.coroutines.delay


@Composable
fun Homepage(
    onCardClick: (String, String) -> Unit,
    navController: NavController,
    userRepository: UserRepository
) {
    val context = LocalContext.current
    val api = RetrofitClient.api
    val apiRepo = ApiRepository(api)
    val homeViewModel = viewModel<HomeViewModel>(
        factory = HomeViewModelFactory(apiRepo, context, userRepository)
    )
    val retry = remember { mutableIntStateOf(0) }
    val userViewModel: UserViewModel = viewModel(
        factory = UserViewModelFactory(userRepository)
    )

    val profile by userViewModel.profileFlow.collectAsState(initial = null)

    // Wait until profile is loaded then trigger data fetch
    LaunchedEffect(profile,retry.intValue) {
        profile?.let { (_, genres) ->
            homeViewModel.loadTrendingData(userGenres = genres)
        }
    }

    val homeScreenSection = homeViewModel.homeSection.collectAsState().value
    val historySection by homeViewModel.historySection.collectAsState()
    val mergeSections = remember (homeScreenSection, historySection){
        if(homeScreenSection.isEmpty()) return@remember homeScreenSection
        homeScreenSection.toMutableList().also {
            it[1] = historySection
        }
    }
    val currentStatus = homeViewModel.uiState.collectAsState().value
    var currentlySelected by rememberSaveable { mutableStateOf(SelectedIcon.HOME) }

    Box(modifier = Modifier.fillMaxSize()) {

        // ── Content Area ──────────────────────────────────────────────
        when (currentlySelected) {
            SelectedIcon.HOME -> {
                when (currentStatus) {
                    is Status.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.warning),
                                    contentDescription = "Error icon",
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colorScheme.error
                                )
                                Text(
                                    text = currentStatus.message,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 6.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Button(
                                    onClick = {
                                        retry.intValue = retry.intValue + 1
                                        Log.d("TAG", " retry clicked")
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    )
                                ) {
                                    Text(text = "Retry", fontSize = 16.sp)
                                }
                            }
                        }
                    }

                    Status.Loading -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(MaterialTheme.colorScheme.background),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Text(
                                    text = "Please Wait",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 6.dp),
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Status.Success -> {
                        if (mergeSections.isNotEmpty()) {
                            val updatedList = mergeSections.drop(1)
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                // Top spacer so content clears the floating header
                                item { Spacer(modifier = Modifier.height(80.dp)) }

                                item {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(300.dp)
                                    ) {
                                        HeroPage(
                                            mergeSections[0].data,
                                            onCardClick = { onCardClick(it, "movie") }
                                        )
                                    }
                                }

                                items(updatedList) { item ->
                                    Spacer(modifier = Modifier.height(3.dp))
                                    if (item.data.isNotEmpty()  ) {
                                        GenericRowStructure(item) { id, type ->
                                            onCardClick(id, type)
                                        }
                                    }
                                    if (item.title == "Popular Shows"){
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ){
                                            Text(
                                                text = "Selected Genres List ",
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onBackground
                                            )
                                        }
                                    }
                                }

                                // Bottom spacer so last item clears the floating nav bar
                                item { Spacer(modifier = Modifier.height(100.dp)) }
                            }
                        }
                    }
                }
            }

            SelectedIcon.SEARCH -> {
                BackHandler { currentlySelected = SelectedIcon.HOME }
                SearchScreen(onResultClick = { id, type -> onCardClick(id, type) })
            }

            SelectedIcon.FAVORITES -> {
                BackHandler { currentlySelected = SelectedIcon.HOME }
                FavoriteScreen(navController,userRepository)
            }
        }

        // ── Floating Header ───────────────────────────────────────────
        if(currentlySelected != SelectedIcon.SEARCH && currentlySelected != SelectedIcon.FAVORITES){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
                                Color.Transparent
                            )
                        )
                    )
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Logo + app name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(R.drawable.bingedlogo),
                        contentDescription = "Binged Logo",
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.secondary, CircleShape)
                    )
                    Text(
                        text = "Binged",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }

                // Profile icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable {
                            navController.navigate(Screens.UserProfile.route)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.profileicon),
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // ── Floating Bottom Nav Pill ──────────────────────────────────
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp, start = 16.dp, end = 16.dp) // ← start/end padding
                .fillMaxWidth()                                        // ← full width
                .clip(RoundedCornerShape(50))
                .background(Color.Black.copy(alpha = 0.75f))
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,          // ← spread icons evenly
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavPillIcon(
                icon = Icons.Default.Home,
                label = "Home",
                selected = currentlySelected == SelectedIcon.HOME,
                onClick = { currentlySelected = SelectedIcon.HOME }
            )
            NavPillIcon(
                icon = Icons.Default.Search,
                label = "Search",
                selected = currentlySelected == SelectedIcon.SEARCH,
                onClick = { currentlySelected = SelectedIcon.SEARCH }
            )
            NavPillIcon(
                icon = Icons.Default.Favorite,
                label = "Favorites",
                selected = currentlySelected == SelectedIcon.FAVORITES,
                onClick = { currentlySelected = SelectedIcon.FAVORITES }
            )
        }
    }
}

// ── Nav Pill Icon ─────────────────────────────────────────────────────────────
@Composable
fun NavPillIcon(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(
                if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.30f)
                else Color.Transparent
            )
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray,
            modifier = Modifier.size(26.dp)
        )
    }
}

// ── Generic Row Section ───────────────────────────────────────────────────────
@Composable
fun GenericRowStructure(item: HomeScreenSection, onCardClick: (String, String) -> Unit) {
    Column {
        Text(
            text = item.title ?: "Default",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp),
            color = MaterialTheme.colorScheme.onBackground
        )
        LazyRow {
            items(item.data) { item ->
                ItemContainer(item, onCardClick = { id, type -> onCardClick(id, type) })
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

// Item Card
@Composable
fun ItemContainer(item: UiPosterData, onCardClick: (String, String) -> Unit) {
    Card(
        modifier = Modifier
            .height(250.dp)
            .width(150.dp)
            .padding(4.dp)
            .clickable { onCardClick(item.ids?.imdb ?: "", item.type) },
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

// Hero Pager
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroPage(
    movieList: List<UiPosterData>,
    onCardClick: (String) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { movieList.size })

    LaunchedEffect(key1 = Unit) {
        while (true) {
            delay(5000)
            val nextPage = (pagerState.currentPage + 1) % movieList.size
            pagerState.animateScrollToPage(nextPage)
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, end = 4.dp, top = 4.dp)
            .clip(RoundedCornerShape(10.dp))
            .height(500.dp)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(10.dp)
            )
    ) { page ->
        HeroCard(
            movieList[page],
            movieList.size,
            pagerState,
            onCardClick = { onCardClick(it) }
        )
    }
}

// Hero Card
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroCard(
    movie: UiPosterData,
    size: Int,
    pagerState: PagerState,
    onCardClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable { onCardClick(movie.ids?.imdb ?: "") }
    ) {
        val posterUrl = movie.images?.fanart?.firstOrNull()?.let {
            if (it.startsWith("http")) it else "https://$it"
        }
        AsyncImage(
            model = posterUrl,
            contentDescription = "hero page banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .padding(start = 16.dp, bottom = 8.dp)
                .fillMaxSize()
        ) {
            movie.title?.let {
                Text(
                    text = it,
                    fontSize = 20.sp,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .background(color = Color.Black.copy(alpha = 0.2f)),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = movie.year.toString(),
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.background(color = Color.Black.copy(alpha = 0.2f))
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(size) { index ->
                    val color = if (pagerState.currentPage == index) Color.White
                    else Color.White.copy(alpha = 0.5f)
                    Box(
                        modifier = Modifier
                            .padding(4.dp)
                            .size(8.dp)
                            .background(color, CircleShape)
                            .border(1.dp, shape = CircleShape, color = Color.Black)
                    )
                }
            }
        }
    }
}

// ── Selected Icon Enum ────────────────────────────────────────────────────────
enum class SelectedIcon {
    HOME,
    SEARCH,
    FAVORITES
}