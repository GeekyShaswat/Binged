package com.movie.binged.screens

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.movie.binged.R
import com.movie.binged.api.client.RetrofitClient
import com.movie.binged.model.HomeScreenSection
import com.movie.binged.model.UiPosterData
import com.movie.binged.repository.ApiRepository
import com.movie.binged.viewmodel.HomeViewModel
import com.movie.binged.viewmodel.HomeViewModelFactory
import com.movie.binged.viewmodel.Status
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Homepage(
    onCardClick: (String,String) -> Unit
){
    val context = LocalContext.current
    val api = RetrofitClient.api
    val apiRepo = ApiRepository(api)
    val homeViewModel = viewModel<HomeViewModel>(
        factory = HomeViewModelFactory(apiRepo,context)
    )
    val retry =  remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit, retry.intValue) {
        Log.d("TAG"," inside LE")
        homeViewModel.loadTrendingData()
    }

    val homeScreenSection = homeViewModel.homeSection.collectAsState().value
    val currentStatus = homeViewModel.uiState.collectAsState().value

    var currentlySelected by rememberSaveable { mutableStateOf(SelectedIcon.HOME)}

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = { if (currentlySelected != SelectedIcon.SEARCH) TopAppBar(
            title = { TopBar() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colors.primary,
                titleContentColor = MaterialTheme.colors.onPrimary
            ),
            modifier = Modifier.fillMaxWidth()
        )
                 },
        bottomBar = {
            BottomAppBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp)
                    .height(80.dp),
                containerColor = MaterialTheme.colors.primary,
                content = {
                    // icon for home search and favorite
                    BottomBar(
                        currentlySelected = currentlySelected,
                        onIconClick = { selected ->
                            currentlySelected = selected
                        }
                    )
                }
            )
        }
    ) { paddingValues ->

        when(currentlySelected){
            SelectedIcon.HOME -> {
                var updatedList: List<HomeScreenSection>
                when(currentStatus){
                    is Status.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                Icon(
                                    painter = painterResource(R.drawable.warning),
                                    "This is error icon image",
                                    modifier = Modifier.size(50.dp),
                                    tint = MaterialTheme.colors.error
                                )
                                Text(
                                    text = currentStatus.message,
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 6.dp),
                                    color = MaterialTheme.colors.onSurface
                                )
                                Button(
                                    onClick = {
                                        retry.intValue  = retry.intValue + 1
                                        Log.d("TAG"," retry clicked")
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colors.primary,
                                        contentColor = MaterialTheme.colors.onPrimary )
                                ) {
                                    Row {
                                        Text(
                                            text = "Retry",
                                            fontSize = 16.sp
                                        )
                                    }
                                }
                            }
                        }

                    }
                    Status.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ){
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ){
                                CircularProgressIndicator()
                                Text(
                                    text = "Please Wait",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(top = 6.dp),
                                    color = MaterialTheme.colors.onSurface
                                )
                            }
                        }

                    }
                    Status.Success -> {
                        if (homeScreenSection.isNotEmpty()) {
                            updatedList = homeScreenSection.drop(1)


                            LazyColumn(
                                modifier = Modifier
                                    .padding(paddingValues)
                                    .background(MaterialTheme.colors.background)
                            ) {
                                item {
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .height(300.dp)) {
                                        HeroPage(
                                            homeScreenSection[0].data,
                                            onCardClick = {
                                                onCardClick(it,"movie")
                                            }
                                        )
                                    }
                                }

                                items(updatedList) { item ->


                                    Spacer(modifier = Modifier.height(3.dp))

                                    GenericRowStructure(
                                        item,
                                        onCardClick = {
                                            onCardClick(it, item.type)
                                        }

                                    )

                                }
                            }
                        }
                    }
                }
            }
            SelectedIcon.SEARCH -> {
                SearchScreen(
                    onResultClick = { id,type ->
                        onCardClick(id,type)
                    }
                )
            }
            SelectedIcon.FAVORITES -> {
                FavoriteScreen()
            }
        }

    }
}
@Composable
fun GenericRowStructure( item: HomeScreenSection, onCardClick: (String) -> Unit){
    Column {
        Text(
            text = item.title ?: "Default",
            fontSize = 18.sp,
            textAlign = TextAlign.Start,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp),
            color = MaterialTheme.colors.onBackground
        )
        LazyRow {
            items(item.data){ item ->
                ItemContainer(
                    item,
                    onCardClick = { onCardClick(it) }
                )
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
    }
}

@Composable
fun ItemContainer( item : UiPosterData , onCardClick : (String) -> Unit ){
    Card(
        modifier = Modifier
            .height(250.dp)
            .width(150.dp)
            .padding(4.dp)
            .clickable { onCardClick(item.ids.imdb) },
        elevation = CardDefaults.cardElevation(4.dp),
        border = BorderStroke(1.dp,MaterialTheme.colors.primary),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colors.surface)
    ){
        Column(
            modifier = Modifier.fillMaxSize()
        ){
            val posterUrl = item.images.poster.firstOrNull()?.let {
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
            Text(
                text = item.title,
                maxLines = 1,
                fontSize = 15.sp,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(start = 6.dp)
            )
            Text(
                text = "${item.year}",
                fontSize = 13.sp,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(start = 6.dp)
            )
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroPage(
    movieList : List<UiPosterData>,
    onCardClick: (String) -> Unit
){

    val pagerState = rememberPagerState(pageCount = { movieList.size })
    val size = movieList.size

    LaunchedEffect(key1 = Unit) {
        while (true){
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
                BorderStroke(1.dp, MaterialTheme.colors.primary),
                shape = RoundedCornerShape(10.dp)
            )
    ) { page ->
        HeroCard(
            movieList[page],
            size,
            pagerState,
            onCardClick = {
                onCardClick(it)
            }
            )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HeroCard(
    movie : UiPosterData,
    size : Int,
    pagerState: PagerState,
    onCardClick: (String) -> Unit
){

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                onCardClick(movie.ids.imdb)
            }
    ){
        val posterUrl = movie.images.fanart.firstOrNull()?.let {
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
            Text(
                text = movie.title,
                fontSize = 20.sp,
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .background(color = Color.Black.copy(alpha = 0.2f)),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
            ){
                repeat(size) { index ->
                    val color = if (pagerState.currentPage == index) {
                        Color.White
                    } else Color.White.copy(alpha = 0.5f)

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

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left: Logo + App Name
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(R.drawable.bingedlogo),
                contentDescription = "Binged Logo",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.secondary,
                        shape = CircleShape
                    )
            )
            Text(
                text = "Binged",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Right: Action Icons
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colors.primary,
                                MaterialTheme.colors.secondary
                            )
                        )
                    )
                    .clickable { /* Profile screen */ },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.profileicon),
                    contentDescription = "Profile",
                    tint = Color.White,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
fun BottomBar(
    currentlySelected : SelectedIcon,
    onIconClick : (SelectedIcon) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {

        IconButton(onClick = { if (currentlySelected != SelectedIcon.HOME) onIconClick(SelectedIcon.HOME) }) {
            Icon(
                imageVector = Icons.Default.Home,
                contentDescription = "Home",
                tint = if (currentlySelected == SelectedIcon.HOME) MaterialTheme.colors.onBackground else Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(onClick = { if (currentlySelected != SelectedIcon.SEARCH) onIconClick(SelectedIcon.SEARCH) }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = if (currentlySelected == SelectedIcon.SEARCH) MaterialTheme.colors.onBackground else Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }

        IconButton(onClick = { if (currentlySelected != SelectedIcon.FAVORITES) onIconClick(SelectedIcon.FAVORITES) }) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite",
                tint = if (currentlySelected == SelectedIcon.FAVORITES) MaterialTheme.colors.onBackground else Color.Gray,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

enum class SelectedIcon {
    HOME,
    SEARCH,
    FAVORITES
}