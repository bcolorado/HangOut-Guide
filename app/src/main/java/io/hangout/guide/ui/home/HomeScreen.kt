package io.hangout.guide.ui.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import io.hangout.guide.R
import io.hangout.guide.ui.common.HomeBottomBar
import io.hangout.guide.ui.common.Keyboard
import io.hangout.guide.ui.common.MapScreen
import io.hangout.guide.ui.common.keyboardAsState
import io.hangout.guide.ui.navigation.PlaceAddress
import io.hangout.guide.ui.navigation.topLevelDestinations
import io.hangout.guide.ui.theme.HangOutGuideTheme
import io.hangout.guide.utils.AuthManager
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomePageScreen(auth: AuthManager, onNavigateLogin: () -> Unit, navController: NavController, onSeeRecommendedPlaces: (PlaceAddress?) -> Unit) {
    val onLogoutConfirmed: () -> Unit = {
        auth.signOut()
        onNavigateLogin()
    }
    val mapViewModel = hiltViewModel<MapViewModel>()
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val keyboardState by keyboardAsState()

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Hangout") },
            actions = {
                IconButton(onClick = onLogoutConfirmed, content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.logout),
                        contentDescription = "Logout",
                        tint = HangOutGuideTheme.colorScheme.onSurface
                    )
                })
            },
        )
    }, bottomBar = {
        if (keyboardState == Keyboard.Opened) return@Scaffold
        HomeBottomBar(destinations = topLevelDestinations,
            currentDestination = navController.currentBackStackEntryAsState().value?.destination,
            onNavigateToDestination = {
                navController.navigate(it) {
                    popUpTo(navController.graph.findStartDestination().id) {
                        saveState = true
                    }
                    restoreState = true
                    launchSingleTop = true
                }
            })
    }) { innerPadding ->
        ConstraintLayout(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val (map, search, title) = createRefs()

            Text(text = "¡Estás a un paso de tu próxima aventura!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                lineHeight = 48.sp,
                modifier = Modifier.padding(horizontal = 16.dp).constrainAs(title) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(search.top)
                })

            CustomSearchBar(modifier = Modifier.constrainAs(search) {
                    top.linkTo(title.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(map.top)
                }, onPlaceSelected = { place ->
                    mapViewModel.selectLocation(place, context)
                },
                placesClient = mapViewModel.getPlacesClient(),
                cameraPositionState = cameraPositionState)

            Box(modifier = Modifier
                .constrainAs(map) {
                    top.linkTo(search.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }) {
                MapScreen(mapViewModel, cameraPositionState = cameraPositionState, onSeeRecommendedPlaces = onSeeRecommendedPlaces)
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomSearchBar(
    modifier: Modifier = Modifier,
    onPlaceSelected: (String) -> Unit,
    placesClient: PlacesClient,
    cameraPositionState: CameraPositionState
) {
    val visibleRegion = cameraPositionState.projection?.visibleRegion?.latLngBounds ?: return

    var search by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    var predictions by remember { mutableStateOf<List<AutocompletePrediction>>(emptyList()) }
    val sessionToken = remember { AutocompleteSessionToken.newInstance() }

    LaunchedEffect(search) {
        if (search.isNotEmpty()) {
            val request = FindAutocompletePredictionsRequest.builder()
                .setSessionToken(sessionToken)
                .setQuery(search)
                .setLocationBias(
                    RectangularBounds.newInstance(
                        visibleRegion.southwest,
                        visibleRegion.northeast
                    ))
                .build()

            try {
                val response = placesClient.findAutocompletePredictions(request).await()
                predictions = response.autocompletePredictions
            } catch (e: Exception) {
                predictions = emptyList()
            }
        } else {
            predictions = emptyList()
        }
    }

    DockedSearchBar (
        modifier = modifier.heightIn(max = 200.dp),
        query = search,
        onQueryChange = { search = it },
        onSearch = {
            onPlaceSelected(search)
            isSearchActive = false
            predictions = emptyList()
        },
        active = isSearchActive,
        onActiveChange = { isSearchActive = it },
        placeholder = { Text("Buscar un lugar") },
        leadingIcon = {
            if (isSearchActive) {
                IconButton(onClick = { isSearchActive = false }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Clear search"
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        },
        trailingIcon = {
            if (search.isNotEmpty()) {
                IconButton(onClick = {
                    search = ""
                    predictions = emptyList()
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(predictions) { prediction ->
                ListItem(
                    headlineContent = {
                        Text(prediction.getPrimaryText(null).toString())
                    },
                    supportingContent = {
                        Text(
                            prediction.getSecondaryText(null).toString(),
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .clickable {
                            search = prediction.getFullText(null).toString()
                            onPlaceSelected(prediction.getFullText(null).toString())
                            isSearchActive = false
                            predictions = emptyList()
                        }
                        .fillMaxWidth()
                )
            }
        }
    }
}

//@Composable
//private fun BottomSheet(
//    searchPlace: SearchPlace,
//    onClose: () -> Unit
//) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .height(300.dp)
//    ) {
//        Column(
//            modifier = Modifier.padding(16.dp)
//        ) {
//            Text(
//                text = searchPlace.name,
//                style = MaterialTheme.typography.headlineLarge
//            )
//
//            searchPlace.address?.let { address ->
//                Text(
//                    text = buildString {
//                        address.street?.let { append(it) }
//                        address.houseNumber?.let { append(", $it") }
//                        address.place?.let { append(", $it") }
//                    },
//                    style = MaterialTheme.typography.headlineMedium
//                )
//            }
//
//            IconButton(
//                onClick = onClose,
//                modifier = Modifier.align(Alignment.End)
//            ) {
//                // Close icon
//            }
//        }
//    }
//}