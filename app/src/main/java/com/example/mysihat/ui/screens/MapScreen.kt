package com.example.mysihat.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import com.example.mysihat.data.*
import com.example.mysihat.ui.components.*
import com.example.mysihat.ui.theme.*
import java.util.Locale

/**
 * Waze / Google Maps Style Healthcare Explorer:
 * Full live interactive street map showing all government health clinics (Klinik Kesihatan)
 * and hospitals across Malaysia with real device GPS tracking, auto-suggestions, and themed shortcuts.
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val isEn = LanguageManager.isEnglish

    // Default to Perak facility directly on startup to match physical state
    val defaultPerakFacility = remember {
        MockDataRepository.clinicFacilities.firstOrNull { it.state.contains("Perak", ignoreCase = true) }
            ?: MockDataRepository.clinicFacilities.first()
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedState by remember { mutableStateOf("Perak") }
    var selectedType by remember { mutableStateOf(FacilityType.ALL) }
    var selectedFacility by remember { mutableStateOf<ClinicFacility?>(defaultPerakFacility) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isMapLoaded by remember { mutableStateOf(false) }
    var isSearchFocused by remember { mutableStateOf(false) }
    var userGpsLocation by remember { mutableStateOf<Pair<Double, Double>?>(null) }
    var userLocationLabel by remember { mutableStateOf("Tapah Road, Perak") }

    val allStatesBm = listOf(
        "Semua", "Perak", "Kuala Lumpur", "Selangor", "Putrajaya", "Johor",
        "Pulau Pinang", "Kedah", "Melaka", "Negeri Sembilan",
        "Pahang", "Terengganu", "Kelantan", "Perlis", "Sabah", "Sarawak", "Labuan"
    )
    val allStatesEn = listOf(
        "All", "Perak", "Kuala Lumpur", "Selangor", "Putrajaya", "Johor",
        "Penang", "Kedah", "Malacca", "Negeri Sembilan",
        "Pahang", "Terengganu", "Kelantan", "Perlis", "Sabah", "Sarawak", "Labuan"
    )

    val currentStatesList = if (isEn) allStatesEn else allStatesBm

    fun flyToFacility(f: ClinicFacility) {
        selectedFacility = f
        webViewRef?.evaluateJavascript(
            "if (typeof focusFacility === 'function') { focusFacility(${f.latitude}, ${f.longitude}, '${f.id}'); }",
            null
        )
    }

    // Native GPS Location Acquisition Engine
    fun acquireLiveGpsLocation(isUserExplicitAction: Boolean = false) {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager
        if (locationManager == null) {
            if (isUserExplicitAction) NotificationService.showError("Perkhidmatan lokasi tidak tersedia.")
            return
        }

        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (!hasFine && !hasCoarse) {
            return
        }

        try {
            fun applyLocation(loc: Location, explicit: Boolean) {
                val lat = loc.latitude
                val lng = loc.longitude
                userGpsLocation = Pair(lat, lng)

                var resolvedTown = "Perak"
                var resolvedState = "Perak"
                try {
                    val geocoder = Geocoder(context, Locale("ms", "MY"))
                    val addrs = geocoder.getFromLocation(lat, lng, 1)
                    val first = addrs?.firstOrNull()
                    if (first != null) {
                        resolvedTown = first.locality ?: first.subAdminArea ?: first.featureName ?: "Perak"
                        resolvedState = first.adminArea ?: "Perak"
                    }
                } catch (_: Exception) {}

                userLocationLabel = "$resolvedTown, $resolvedState"
                MockDataRepository.updateUserDistances(lat, lng)

                // Only navigate to Perak if user explicitly pressed the GPS button or on first launch
                if (explicit) {
                    selectedState = "Perak"
                    val nearest = MockDataRepository.clinicFacilities.minByOrNull { it.distanceKm }
                    if (nearest != null) {
                        flyToFacility(nearest)
                    }
                    webViewRef?.evaluateJavascript(
                        "if (typeof setUserLocation === 'function') { setUserLocation($lat, $lng, '$userLocationLabel'); }",
                        null
                    )
                    NotificationService.showSuccess(
                        if (isEn) "Live GPS detected: $resolvedTown, $resolvedState"
                        else "GPS Langsung dikesan: $resolvedTown, $resolvedState"
                    )
                }
            }

            // Inspect ALL candidate providers (fused, network, passive, gps)
            val candidateProviders = listOf("fused", LocationManager.NETWORK_PROVIDER, LocationManager.PASSIVE_PROVIDER, LocationManager.GPS_PROVIDER)
            var bestLocation: Location? = null
            for (p in candidateProviders) {
                try {
                    val loc = locationManager.getLastKnownLocation(p)
                    if (loc != null) {
                        if (bestLocation == null || loc.time > bestLocation.time) {
                            bestLocation = loc
                        }
                    }
                } catch (_: Exception) {}
            }

            if (bestLocation != null) {
                applyLocation(bestLocation, isUserExplicitAction)
            }

            // Live updates for distance accuracy (without overriding user's active state selection)
            val listener = object : LocationListener {
                override fun onLocationChanged(loc: Location) {
                    applyLocation(loc, false)
                }
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                override fun onProviderEnabled(provider: String) {}
                override fun onProviderDisabled(provider: String) {}
            }

            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                locationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    5000L,
                    20f,
                    listener,
                    Looper.getMainLooper()
                )
            }
        } catch (e: Exception) {
            if (isUserExplicitAction) NotificationService.showError("Ralat mengesan GPS: ${e.message}")
        }
    }

    // Permission launcher for GPS
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            acquireLiveGpsLocation(isUserExplicitAction = true)
        } else {
            NotificationService.showError(if (isEn) "Location permission denied" else "Kebenaran lokasi GPS tidak diberikan")
        }
    }

    // Automatic GPS acquisition on screen mount
    LaunchedEffect(Unit) {
        val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        if (hasFine || hasCoarse) {
            acquireLiveGpsLocation(isUserExplicitAction = false)
        } else {
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val filteredFacilities = MockDataRepository.clinicFacilities.filter { facility ->
        val stateMatch = selectedState == "Semua" || selectedState == "All" ||
                facility.state.equals(selectedState, ignoreCase = true) ||
                (selectedState == "Penang" && facility.state.contains("Pulau Pinang", ignoreCase = true)) ||
                (selectedState == "Malacca" && facility.state.contains("Melaka", ignoreCase = true))

        val typeMatch = (selectedType == FacilityType.ALL || facility.type == selectedType)
        val searchMatch = searchQuery.isBlank() ||
                facility.name.contains(searchQuery, ignoreCase = true) ||
                facility.state.contains(searchQuery, ignoreCase = true) ||
                facility.address.contains(searchQuery, ignoreCase = true) ||
                facility.services.any { it.name.contains(searchQuery, ignoreCase = true) }

        stateMatch && typeMatch && searchMatch
    }

    val searchSuggestions = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else MockDataRepository.clinicFacilities.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
            it.address.contains(searchQuery, ignoreCase = true) ||
            it.state.contains(searchQuery, ignoreCase = true) ||
            it.services.any { s -> s.name.contains(searchQuery, ignoreCase = true) }
        }.take(5)
    }

    val targetFacility = selectedFacility ?: filteredFacilities.firstOrNull() ?: defaultPerakFacility

    val facilitiesJson = remember(filteredFacilities) {
        val array = org.json.JSONArray()
        filteredFacilities.forEach { f ->
            val obj = org.json.JSONObject()
            obj.put("id", f.id)
            obj.put("lat", f.latitude)
            obj.put("lng", f.longitude)
            obj.put("name", f.name)
            obj.put("type", if (isEn) f.type.enLabel else f.type.bmLabel)
            obj.put("q", f.currentQueueNumber)
            obj.put("isHosp", f.type == FacilityType.HOSPITAL)
            obj.put("isOpen", f.isCurrentlyOpen)
            array.put(obj)
        }
        array.toString().replace("'", "\\'")
    }

    // Keep map markers synchronized when filters change
    LaunchedEffect(facilitiesJson, isMapLoaded) {
        if (isMapLoaded) {
            webViewRef?.evaluateJavascript(
                "if (typeof renderFacilities === 'function') { renderFacilities('$facilitiesJson'); }",
                null
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE8F1FC))
    ) {
        // Safe Top Bar Below Status Bar with Live GPS Button
        MySihatTopBar(
            screenTitle = if (isEn) "Healthcare Map Explorer" else "Peta Fasiliti & Hospital",
            subtitle = if (isEn) "All Government Health Clinics & Hospitals Malaysia" else "Semua Klinik Kesihatan & Hospital Kerajaan Malaysia",
            action = {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(listOf(Color(0xFF0077B6), Color(0xFF0096C7)))
                        )
                        .clickable {
                            val hasFine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            val hasCoarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            if (!hasFine && !hasCoarse) {
                                locationPermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION
                                    )
                                )
                            } else {
                                acquireLiveGpsLocation(isUserExplicitAction = true)
                            }
                        }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "📍", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isEn) "My GPS (Perak)" else "Lokasi Saya (Perak)",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            softWrap = false,
                            maxLines = 1
                        )
                    }
                }
            }
        )

        // Search Bar & Filter Shortcuts
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .background(SurfaceLight)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Search Input with Instant Clear and Query Suggestions
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFFF1F5F9))
                        .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        ModernRadarMapIcon(size = 18.dp, tint = BlueDark)
                        Spacer(modifier = Modifier.width(8.dp))
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = {
                                searchQuery = it
                                isSearchFocused = true
                                if (it.isNotBlank()) {
                                    val matches = MockDataRepository.clinicFacilities.filter { f ->
                                        f.name.contains(it, ignoreCase = true) ||
                                        f.address.contains(it, ignoreCase = true) ||
                                        f.state.contains(it, ignoreCase = true) ||
                                        f.services.any { s -> s.name.contains(it, ignoreCase = true) }
                                    }
                                    if (matches.isNotEmpty()) {
                                        flyToFacility(matches.first())
                                    } else if (it.length >= 3) {
                                        NotificationService.showError(
                                            if (isEn) "Search Failed: No clinic or hospital found for '$it'!"
                                            else "Carian Tidak Berjaya: Tiada klinik atau hospital dijumpai untuk '$it'!"
                                        )
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            textStyle = TextStyle(color = Color(0xFF0F172A), fontSize = 13.sp, fontWeight = FontWeight.Medium),
                            decorationBox = { innerTextField ->
                                if (searchQuery.isEmpty()) {
                                    Text(
                                        text = if (isEn) "Search clinic, hospital (e.g. HKL, HUKM, Tapah)..." else "Cari klinik, hospital (cth: HKL, HUKM, Tapah)...",
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                }
                                innerTextField()
                            }
                        )
                        if (searchQuery.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(BluePrimary)
                                    .clickable {
                                        isSearchFocused = false
                                        val matches = MockDataRepository.clinicFacilities.filter { f ->
                                            f.name.contains(searchQuery, ignoreCase = true) ||
                                            f.address.contains(searchQuery, ignoreCase = true) ||
                                            f.state.contains(searchQuery, ignoreCase = true) ||
                                            f.services.any { s -> s.name.contains(searchQuery, ignoreCase = true) }
                                        }
                                        if (matches.isNotEmpty()) {
                                            flyToFacility(matches.first())
                                            NotificationService.showSuccess(
                                                if (isEn) "Search Successful: ${matches.size} facility found (${matches.first().name})."
                                                else "Carian Berjaya: ${matches.size} fasiliti dijumpai (${matches.first().name})."
                                            )
                                        } else {
                                            NotificationService.showError(
                                                if (isEn) "Search Failed: No clinic or hospital found for '$searchQuery'!"
                                                else "Carian Tidak Berjaya: Tiada klinik atau hospital dijumpai untuk '$searchQuery'!"
                                            )
                                        }
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isEn) "Search" else "Cari",
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFCBD5E1))
                                    .clickable {
                                        searchQuery = ""
                                        isSearchFocused = false
                                        NotificationService.showInfo(
                                            if (isEn) "Search filter cleared."
                                            else "Ruangan carian telah dikosongkan."
                                        )
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Text("✕", fontSize = 11.sp, color = Color(0xFF1E293B), fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Instant Search Suggestions Dropdown
                if (searchSuggestions.isNotEmpty() && isSearchFocused) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(8.dp, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(1.dp, CardBorder, RoundedCornerShape(12.dp))
                    ) {
                        searchSuggestions.forEach { f ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        searchQuery = f.name
                                        isSearchFocused = false
                                        flyToFacility(f)
                                        NotificationService.showInfo(if (isEn) "Navigating to: ${f.name}" else "Menuju ke: ${f.name}")
                                    }
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = f.name,
                                        fontSize = 12.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Text(
                                        text = "${f.state} • ${f.address}",
                                        fontSize = 10.5.sp,
                                        color = Color(0xFF64748B),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (f.type == FacilityType.HOSPITAL) Color(0xFFFEE2E2) else Color(0xFFE0F2FE))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (f.type == FacilityType.HOSPITAL) "Hospital" else "KK",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (f.type == FacilityType.HOSPITAL) Color(0xFFDC2626) else Color(0xFF0284C7)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // State Horizontal Shortcut Chips (Solid Blue for Active, Clear High-Contrast Text)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    currentStatesList.forEach { stateName ->
                        val isSelected = selectedState.equals(stateName, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isSelected) BluePrimary else Color(0xFFF1F5F9))
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BluePrimary else CardBorder,
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .clickable {
                                    selectedState = stateName
                                    isSearchFocused = false
                                    val match = MockDataRepository.clinicFacilities.firstOrNull {
                                        val matchState = stateName == "Semua" || stateName == "All" || it.state.contains(stateName, ignoreCase = true)
                                        val matchType = selectedType == FacilityType.ALL || it.type == selectedType
                                        matchState && matchType
                                    } ?: MockDataRepository.clinicFacilities.firstOrNull {
                                        stateName == "Semua" || stateName == "All" || it.state.contains(stateName, ignoreCase = true)
                                    }
                                    if (match != null) {
                                        flyToFacility(match)
                                        NotificationService.showSuccess(
                                            if (isEn) "Search State Successful: Showing facilities in $stateName (${match.name})."
                                            else "Carian Negeri Berjaya: Memaparkan fasiliti di $stateName (${match.name})."
                                        )
                                    } else {
                                        webViewRef?.evaluateJavascript("if (typeof flyToState === 'function') { flyToState('$stateName'); }", null)
                                        NotificationService.showError(
                                            if (isEn) "Search Failed: No matching facility in $stateName."
                                            else "Carian Tidak Berjaya: Tiada fasiliti dijumpai di $stateName."
                                        )
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = stateName,
                                color = if (isSelected) Color.White else Color(0xFF0F172A),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Facility Type Filter Chips (Semua Fasiliti, Klinik Kesihatan, Hospital Kerajaan)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf(
                        FacilityType.ALL to (if (isEn) "All Facilities" else "Semua Fasiliti"),
                        FacilityType.KLINIK_KESIHATAN to (if (isEn) "Health Clinics" else "Klinik Kesihatan"),
                        FacilityType.HOSPITAL to (if (isEn) "Hospitals" else "Hospital Kerajaan")
                    ).forEach { (type, label) ->
                        val isSelected = selectedType == type
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BlueDark else Color(0xFFF8FAFC))
                                .border(1.dp, if (isSelected) BlueDark else CardBorder, RoundedCornerShape(10.dp))
                                .clickable {
                                    selectedType = type
                                    val match = MockDataRepository.clinicFacilities.firstOrNull {
                                        val matchType = type == FacilityType.ALL || it.type == type
                                        val matchState = selectedState == "Semua" || selectedState == "All" || it.state.contains(selectedState, ignoreCase = true)
                                        matchType && matchState
                                    } ?: MockDataRepository.clinicFacilities.firstOrNull {
                                        type == FacilityType.ALL || it.type == type
                                    }
                                    if (match != null) {
                                        flyToFacility(match)
                                        NotificationService.showSuccess(
                                            if (isEn) "Filter Successful: Showing $label (${match.name})."
                                            else "Penapis Berjaya: Memaparkan $label (${match.name})."
                                        )
                                    } else {
                                        NotificationService.showError(
                                            if (isEn) "Filter Failed: No $label found!"
                                            else "Penapis Tidak Berjaya: Tiada $label dijumpai!"
                                        )
                                    }
                                }
                                .padding(vertical = 7.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFF0F172A),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        // ==================== LIVE INTERACTIVE STREET MAP VIEW ====================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.15f)
        ) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    WebView(ctx).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.setGeolocationEnabled(true)
                        settings.allowFileAccess = true
                        settings.allowContentAccess = true
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        settings.loadsImagesAutomatically = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.userAgentString = "Mozilla/5.0 (Linux; Android 14; Mobile) MySihat/1.0"

                        addJavascriptInterface(object {
                            @JavascriptInterface
                            fun selectFacility(id: String) {
                                (ctx as? Activity)?.runOnUiThread {
                                    val match = MockDataRepository.clinicFacilities.firstOrNull { it.id == id }
                                    if (match != null) {
                                        selectedFacility = match
                                        NotificationService.showInfo(if (isEn) "Selected: ${match.name}" else "Dipilih: ${match.name}")
                                    }
                                }
                            }
                        }, "AndroidBridge")

                        webChromeClient = object : WebChromeClient() {
                            override fun onConsoleMessage(msg: ConsoleMessage?): Boolean {
                                android.util.Log.d("LiveMap", "${msg?.message()}")
                                return true
                            }
                            override fun onGeolocationPermissionsShowPrompt(
                                origin: String?,
                                callback: android.webkit.GeolocationPermissions.Callback?
                            ) {
                                callback?.invoke(origin, true, false)
                            }
                        }
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                isMapLoaded = true
                                view?.evaluateJavascript("if (typeof renderFacilities === 'function') { renderFacilities('$facilitiesJson'); }", null)
                                val initial = selectedFacility ?: defaultPerakFacility
                                view?.evaluateJavascript("if (typeof focusFacility === 'function') { focusFacility(${initial.latitude}, ${initial.longitude}, '${initial.id}'); }", null)
                            }
                        }

                        loadUrl("file:///android_asset/leaflet/live_map.html")
                        webViewRef = this
                    }
                }
            )

            // Themed Floating Navigation Button (Waze / Google Maps)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .shadow(8.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .clickable {
                        val lat = targetFacility.latitude
                        val lng = targetFacility.longitude
                        val name = Uri.encode(targetFacility.name)
                        try {
                            val mapUri = Uri.parse("geo:$lat,$lng?q=$lat,$lng($name)")
                            val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                            context.startActivity(mapIntent)
                        } catch (_: Exception) {
                            val webUri = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng")
                            context.startActivity(Intent(Intent.ACTION_VIEW, webUri))
                        }
                    }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    ModernThemedGoogleMapIcon(size = 16.dp, tint = BlueDark)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEn) "Maps / Waze" else "Pandu Arah",
                        color = Color(0xFF0F172A),
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // ==================== WAZE-STYLE SLIDING FACILITY DETAIL CARD ====================
        val facility = targetFacility
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.1f)
                .shadow(14.dp, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                .background(SurfaceLight)
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // Facility Quick Switcher if multiple facilities match current active filter
                if (filteredFacilities.size > 1) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            filteredFacilities.forEach { f ->
                                val isCurrent = f.id == facility.id
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isCurrent) BluePrimary else Color(0xFFF1F5F9))
                                        .border(1.dp, if (isCurrent) BluePrimary else CardBorder, RoundedCornerShape(10.dp))
                                        .clickable { flyToFacility(f) }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = f.name,
                                        color = if (isCurrent) Color.White else Color(0xFF0F172A),
                                        fontSize = 11.sp,
                                        fontWeight = if (isCurrent) FontWeight.ExtraBold else FontWeight.Medium,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }

                item {
                    // Header Status Badges (Type, Distance, Queue)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (facility.type == FacilityType.HOSPITAL) Color(0xFFFEE2E2) else Color(0xFFE0F2FE))
                                    .padding(horizontal = 9.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = if (isEn) facility.type.enLabel else facility.type.bmLabel,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (facility.type == FacilityType.HOSPITAL) Color(0xFFDC2626) else Color(0xFF0284C7)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "📍 ${facility.state} • ${facility.distanceKm} km",
                                fontSize = 11.5.sp,
                                color = TextMuted,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Facility Title
                    Text(
                        text = facility.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Address & Operating Hours
                    Text(
                        text = facility.address,
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (facility.isCurrentlyOpen) {
                                if (isEn) "● Open Now" else "● Buka Sekarang"
                            } else {
                                if (isEn) "○ Closed Now (Opens 8:00 AM)" else "○ Tutup Sekarang (Dibuka Esok 8:00 AM)"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (facility.isCurrentlyOpen) Color(0xFF10B981) else Color(0xFFEF4444)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "• ${facility.operatingHours}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                        if (facility.isCurrentlyOpen) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "• Menunggu: ~${facility.waitTimeMins} min",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // THEMED SHORTCUT BUTTONS (Consistent with MySihat App Theme)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Themed Google Maps Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = BlueDark.copy(alpha = 0.2f))
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF0077B6), Color(0xFF0096C7))
                                    )
                                )
                                .clickable {
                                    val lat = facility.latitude
                                    val lng = facility.longitude
                                    val name = Uri.encode(facility.name)
                                    try {
                                        val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:$lat,$lng?q=$lat,$lng($name)"))
                                        context.startActivity(mapIntent)
                                    } catch (_: Exception) {
                                        val webMap = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$lat,$lng"))
                                        context.startActivity(webMap)
                                    }
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ModernThemedGoogleMapIcon(size = 16.dp, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Google Maps",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Themed Waze Button
                        Box(
                            modifier = Modifier
                                .weight(0.9f)
                                .shadow(2.dp, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .background(BlueSurface)
                                .border(1.dp, BlueLight, RoundedCornerShape(12.dp))
                                .clickable {
                                    val lat = facility.latitude
                                    val lng = facility.longitude
                                    try {
                                        val wazeIntent = Intent(Intent.ACTION_VIEW, Uri.parse("waze://?ll=$lat,$lng&navigate=yes"))
                                        context.startActivity(wazeIntent)
                                    } catch (_: Exception) {
                                        val webWaze = Intent(Intent.ACTION_VIEW, Uri.parse("https://waze.com/ul?ll=$lat,$lng&navigate=yes"))
                                        context.startActivity(webWaze)
                                    }
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "🚙", fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Waze",
                                    color = BlueDark,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Themed Telefon Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(4.dp, RoundedCornerShape(12.dp), spotColor = StatusLowRisk.copy(alpha = 0.2f))
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF059669), Color(0xFF10B981))
                                    )
                                )
                                .clickable {
                                    try {
                                        val callIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${facility.phoneNumber}"))
                                        context.startActivity(callIntent)
                                    } catch (_: Exception) {
                                        NotificationService.showError("Gagal membuka telefon: ${facility.phoneNumber}")
                                    }
                                }
                                .padding(vertical = 11.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                ModernThemedPhoneCallIcon(size = 15.dp, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isEn) "Call Clinic" else "Telefon",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Clinical Services List Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isEn) "Available Services & Specialty Clinics" else "Perkhidmatan Klinikal & Klinik Pakar",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )
                        Text(
                            text = "${facility.services.size} ${if (isEn) "Services" else "Perkhidmatan"}",
                            fontSize = 11.sp,
                            color = BlueDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Authentic Clinical Services List
                items(facility.services) { service ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFF8FAFC))
                            .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEFF6FF)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = service.iconTag, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = service.name,
                                    fontSize = 12.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = service.description,
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
