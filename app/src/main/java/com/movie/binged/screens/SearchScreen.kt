package com.movie.binged.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.movie.binged.R
import com.movie.binged.api.client.RetrofitClient
import com.movie.binged.api.model.searchResult.SearchResultItem
import com.movie.binged.repository.ApiRepository
import com.movie.binged.viewmodel.SearchViewModel
import com.movie.binged.viewmodel.SearchViewModelFactory
import com.movie.binged.viewmodel.UiState
@Composable
fun SearchScreen(
    onResultClick: (String, String) -> Unit = { _, _ -> }
) {
    var searchValue by remember { mutableStateOf("") }
    val context = LocalContext.current
    val api = RetrofitClient.api
    val apiRepo = ApiRepository(api)
    val searchViewModel = viewModel<SearchViewModel>(
        factory = SearchViewModelFactory(apiRepo)
    )

    val uiState by searchViewModel.uiState.collectAsState()
    val searchResult by searchViewModel.searchResult.collectAsState()
    val errorMessage by searchViewModel.errorMessage.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.background)
    ) {
        // Search Bar Section
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colors.surface,
            tonalElevation = 2.dp
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = { searchValue = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top=30.dp, start = 16.dp, end = 16.dp),
                placeholder = {
                    Text(
                        "Search movies, TV shows...",
                        color = MaterialTheme.colors.onSurface
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colors.primary
                    )
                },
                trailingIcon = {
                    if (searchValue.isNotEmpty()) {
                        IconButton(onClick = {
                            searchValue = ""
                            searchViewModel.clearSearch()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = MaterialTheme.colors.onSurface
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(16.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colors.primary,
                    unfocusedBorderColor = MaterialTheme.colors.secondary.copy(alpha = 0.5f),
                    focusedTextColor = MaterialTheme.colors.onSurface,
                    cursorColor = MaterialTheme.colors.primary,
                    unfocusedTextColor = MaterialTheme.colors.onSurface
                ),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchValue.isNotBlank()) {
                            searchViewModel.getSearchData(searchValue)
                            focusManager.clearFocus()
                        }
                    }
                )
            )
        }

        // Content Section
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(MaterialTheme.colors.background)
        ) {
            when (uiState) {
                UiState.NOQUERY -> {
                    EmptySearchState()
                }

                UiState.LOADING -> {
                    LoadingState()
                }

                UiState.SUCCESS -> {
                    if (searchResult.isEmpty()) {
                        NoResultsState(query = searchValue)
                    } else {
                        SearchResultsList(
                            results = searchResult,
                            onItemClick = { item ->
                                val type = item.type
                                val id = if (type == "movie") {
                                    item.movie.ids.imdb
                                } else {
                                    item.show.ids.imdb
                                }
                                onResultClick(id, type)
                            },
                            searchViewModel
                        )
                    }
                }

                UiState.ERROR -> {
                    ErrorState(
                        message = errorMessage ?: "Something went wrong",
                        onRetry = {
                            if (searchValue.isNotBlank()) {
                                searchViewModel.getSearchData(searchValue)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun SearchResultsList(
    results: List<SearchResultItem>,
    onItemClick: (SearchResultItem) -> Unit,
    searchViewModel: SearchViewModel
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 72.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "${results.size} Results",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        items(results) { item ->
            if ((item.type == "movie" && item.movie.year != 0) || (item.type == "show" && item.show.year != 0)){
                SearchResultCard(
                    item = item,
                    onClick = { onItemClick(item) },
                    searchViewModel
                )
            }
        }
    }
}

@Composable
fun SearchResultCard(
    item: SearchResultItem,
    onClick: () -> Unit,
    viewModel: SearchViewModel
) {
    val title = if (item.type == "movie") item.movie.title else item.show.title
    val year = if (item.type == "movie") item.movie.year else item.show.year
    val posterMap = viewModel.posterMap.collectAsState().value
    val posterUrl = posterMap[if (item.type == "movie") item.movie.ids.tmdb else item.show.ids.tmdb]

    LaunchedEffect(if (item.type == "movie") item.movie.ids.tmdb else item.show.ids.tmdb) {
        viewModel.loadPoster(if (item.type == "movie") item.movie.ids.tmdb else item.show.ids.tmdb, item.type)
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colors.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Poster Image
            Card(
                modifier = Modifier
                    .width(60.dp)
                    .height(100.dp),
                shape = RoundedCornerShape(8.dp),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                AsyncImage(
                    model = posterUrl,
                    contentDescription = title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    placeholder = painterResource(R.drawable.bingedlogo),
                    error = painterResource(R.drawable.bingedlogo)
                )
            }

            // Content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .height(100.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = title,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colors.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MetadataChipSearch(text = year.toString())
                        MetadataChipSearch(text = item.type.replaceFirstChar { it.uppercase() })
                    }

                }
            }

            // Arrow icon
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colors.onSurface,
                modifier = Modifier.align(Alignment.CenterVertically)
            )
        }
    }
}

@Composable
fun EmptySearchState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Search for movies and TV shows",
                fontSize = 18.sp,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Start typing to find what you're looking for",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Searching...",
                fontSize = 16.sp,
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
fun NoResultsState(query: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colors.onSurface.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No results found",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = "We couldn't find anything for \"$query\"",
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.warning),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colors.error
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Oops!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = MaterialTheme.colors.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colors.primary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Retry")
            }
        }
    }
}

@Composable
fun MetadataChipSearch(text: String) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.Gray
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 14.sp,
            color = MaterialTheme.colors.onSurface
        )
    }
}
