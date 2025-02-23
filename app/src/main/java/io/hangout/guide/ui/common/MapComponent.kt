package io.hangout.guide.ui.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import io.hangout.guide.R
import io.hangout.guide.ui.home.MapViewModel
import io.hangout.guide.ui.navigation.PlaceAddress
import kotlinx.coroutines.launch
import timber.log.Timber

@Composable
fun MapScreen(mapViewModel: MapViewModel = hiltViewModel<MapViewModel>(), cameraPositionState: CameraPositionState, onSeeRecommendedPlaces: (PlaceAddress?) -> Unit) {
    val context = LocalContext.current
    val userLocation by mapViewModel.userLocation
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    var hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) {
            mapViewModel.fetchUserLocation(context, fusedLocationClient)
        } else {
            Timber.e("Location permission was denied by the user.")
        }
    }

    LaunchedEffect(Unit) {
        if (!hasLocationPermission) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
    val coroutineScope = rememberCoroutineScope()

    val selectedLocation by mapViewModel.selectedLocation
    val selectedAddress by mapViewModel.addressSelectedLocation

    LaunchedEffect(Unit) {
        when (PackageManager.PERMISSION_GRANTED) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                mapViewModel.fetchUserLocation(context, fusedLocationClient)
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }
    var selectedPosition by remember { mutableStateOf<Marker?>(null) }

    GoogleMap(
        modifier = Modifier.fillMaxWidth()
            .height(500.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(
            myLocationButtonEnabled = hasLocationPermission
        )
    ) {
        userLocation?.let {
            coroutineScope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f), 1000)
            }
            Marker(
                state = MarkerState(position = it),
                title = "Tu ubicación",
                snippet = "Este es el lugar en el que te encuentras ahora.",
                icon = getCustomMarkerBitmap(
                    context,
                    R.drawable.icons8_mapa_de_pin_94
                )
            )
        }

        selectedLocation?.let { it ->
            coroutineScope.launch {
                cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f), 1000)
            }
            Marker(
                state = MarkerState(position = it),
                title = "Ubicación seleccionada",
                snippet = "",
                icon = getCustomMarkerBitmap(
                    context,
                    R.drawable.icons8_aventura_94
                ),
                onClick = { marker ->
                    selectedPosition = marker
                    true
                }
            )
        }
    }
    if (selectedPosition != null) {
        Dialog(
            onDismissRequest = {
                selectedPosition = null
            }
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Ubicación seleccionada",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Text(
                        text = "¿Deseas ver lugares recomendados cerca a esta zona?",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(onClick = { selectedPosition = null }) {
                            Text("Cancelar")
                        }

                        Button(onClick = {
                            selectedPosition = null
                            onSeeRecommendedPlaces(
                                PlaceAddress(
                                    address = selectedAddress?.locality,
                                    country = selectedAddress?.countryName,
                                    featureName = selectedAddress?.featureName,
                                    postalCode = selectedAddress?.postalCode,
                                    latitude = selectedAddress?.latitude,
                                    longitude = selectedAddress?.longitude
                                )
                            )
                        }) {
                            Text("Confirmar")
                        }
                    }
                }
            }
        }
    }
}

fun getCustomMarkerBitmap(
    context: Context,
    @DrawableRes drawableRes: Int,
    size: Dp = 45.dp
): BitmapDescriptor {
    val drawable = ContextCompat.getDrawable(context, drawableRes)
        ?: return BitmapDescriptorFactory.defaultMarker()

    val density = context.resources.displayMetrics.density
    val sizePx = (size.value * density).toInt()

    val bitmap = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)

    drawable.setBounds(0, 0, sizePx, sizePx)
    drawable.draw(canvas)

    return BitmapDescriptorFactory.fromBitmap(bitmap)
}