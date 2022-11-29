package com.tfg.gasstations

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map : GoogleMap
    private lateinit var buttonCalculateRoute : Button
    private lateinit var markerDefault : BitmapDescriptor
    private lateinit var markerMinGas95 : BitmapDescriptor
    private lateinit var markerMinGas98 : BitmapDescriptor
    private lateinit var markerMinGasol : BitmapDescriptor
    private lateinit var markerAvia : BitmapDescriptor
    private lateinit var markerBp : BitmapDescriptor
    private lateinit var markerCarrefour : BitmapDescriptor
    private lateinit var markerCepsa : BitmapDescriptor
    private lateinit var markerDisa : BitmapDescriptor
    private lateinit var markerEroski : BitmapDescriptor
    private lateinit var markerGalp : BitmapDescriptor
    private lateinit var markerHam : BitmapDescriptor
    private lateinit var markerNaturgy : BitmapDescriptor
    private lateinit var markerPetronor : BitmapDescriptor
    private lateinit var markerRepsol : BitmapDescriptor
    private lateinit var markerShell : BitmapDescriptor
    private lateinit var spinnerCities : Spinner
    private lateinit var selectedCity : String
    private var idSelectedCity : String = "00"

    private var start : String = ""
    private var end   : String = ""
    companion object {
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        createMapFragment()
        listCities()
        //markerMinGas95
        val markerViewMinGas95 = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewMinGas95 : ImageView = markerViewMinGas95.findViewById(R.id.imageViewMarkerMinGas95)
        val bitmapMinGas95 = Bitmap.createScaledBitmap(viewToBitmap(imageViewMinGas95), 126, 126, false)
        markerMinGas95 = BitmapDescriptorFactory.fromBitmap(bitmapMinGas95)
        //markerMinGas98
        val markerViewMinGas98 = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewMinGas98 : ImageView = markerViewMinGas98.findViewById(R.id.imageViewMarkerMinGas98)
        val bitmapMinGas98 = Bitmap.createScaledBitmap(viewToBitmap(imageViewMinGas98), 126, 126, false)
        markerMinGas98= BitmapDescriptorFactory.fromBitmap(bitmapMinGas98)
        //markerMinGasol
        val markerViewMinGasol = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewMinGasol : ImageView = markerViewMinGasol.findViewById(R.id.imageViewMarkerMinGasoleo)
        val bitmapMinGasol = Bitmap.createScaledBitmap(viewToBitmap(imageViewMinGasol), 126, 126, false)
        markerMinGasol = BitmapDescriptorFactory.fromBitmap(bitmapMinGasol)
        //markerAVIA
        val markerViewAvia = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewAvia : ImageView = markerViewAvia.findViewById(R.id.imageViewMarkerAvia)
        val bitmapAvia = Bitmap.createScaledBitmap(viewToBitmap(imageViewAvia), 96, 96, false)
        markerAvia = BitmapDescriptorFactory.fromBitmap(bitmapAvia)
        //markerBp
        val markerViewBp = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewBp : ImageView = markerViewBp.findViewById(R.id.imageViewMarkerBp)
        val bitmapBp = Bitmap.createScaledBitmap(viewToBitmap(imageViewBp), 96, 96, false)
        markerBp = BitmapDescriptorFactory.fromBitmap(bitmapBp)
        //markerCarrefour
        val markerViewCarrefour = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewCarrefour : ImageView = markerViewCarrefour.findViewById(R.id.imageViewMarkerCarrefour)
        val bitmapCarrefour = Bitmap.createScaledBitmap(viewToBitmap(imageViewCarrefour), 96, 96, false)
        markerCarrefour = BitmapDescriptorFactory.fromBitmap(bitmapCarrefour)
        //markerCepsa
        val markerViewCepsa = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewCepsa : ImageView = markerViewCepsa.findViewById(R.id.imageViewMarkerCepsa)
        val bitmapCepsa = Bitmap.createScaledBitmap(viewToBitmap(imageViewCepsa), 96, 96, false)
        markerCepsa = BitmapDescriptorFactory.fromBitmap(bitmapCepsa)
        //markerDisa
        val markerViewDisa = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewDisa : ImageView = markerViewDisa.findViewById(R.id.imageViewMarkerDisa)
        val bitmapDisa = Bitmap.createScaledBitmap(viewToBitmap(imageViewDisa), 96, 96, false)
        markerDisa = BitmapDescriptorFactory.fromBitmap(bitmapDisa)
        //markerEroski
        val markerViewEroski = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewEroski : ImageView = markerViewEroski.findViewById(R.id.imageViewMarkerEroski)
        val bitmapEroski = Bitmap.createScaledBitmap(viewToBitmap(imageViewEroski), 96, 96, false)
        markerEroski = BitmapDescriptorFactory.fromBitmap(bitmapEroski)
        //markerGalp
        val markerViewGalp = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewGalp : ImageView = markerViewGalp.findViewById(R.id.imageViewMarkerGalp)
        val bitmapGalp = Bitmap.createScaledBitmap(viewToBitmap(imageViewGalp), 96, 96, false)
        markerGalp = BitmapDescriptorFactory.fromBitmap(bitmapGalp)
        //markerHam
        val markerViewHam = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewHam : ImageView = markerViewHam.findViewById(R.id.imageViewMarkerHam)
        val bitmapHam = Bitmap.createScaledBitmap(viewToBitmap(imageViewHam), 96, 96, false)
        markerHam = BitmapDescriptorFactory.fromBitmap(bitmapHam)
        //markerNaturgy
        val markerViewNaturgy = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewNaturgy : ImageView = markerViewNaturgy.findViewById(R.id.imageViewMarkerNaturgy)
        val bitmapNaturgy = Bitmap.createScaledBitmap(viewToBitmap(imageViewNaturgy), 96, 96, false)
        markerNaturgy = BitmapDescriptorFactory.fromBitmap(bitmapNaturgy)
        //markerPetronor
        val markerViewPetronor = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewPetronor : ImageView = markerViewPetronor.findViewById(R.id.imageViewMarkerPetronor)
        val bitmapPetronor = Bitmap.createScaledBitmap(viewToBitmap(imageViewPetronor), 96, 96, false)
        markerPetronor = BitmapDescriptorFactory.fromBitmap(bitmapPetronor)
        //markerRepsol
        val markerViewRepsol = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewRepsol : ImageView = markerViewRepsol.findViewById(R.id.imageViewMarkerRepsol)
        val bitmapRepsol = Bitmap.createScaledBitmap(viewToBitmap(imageViewRepsol), 96, 96, false)
        markerRepsol = BitmapDescriptorFactory.fromBitmap(bitmapRepsol)
        //markerShell
        val markerViewShell = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewShell : ImageView = markerViewShell.findViewById(R.id.imageViewMarkerShell)
        val bitmapShell = Bitmap.createScaledBitmap(viewToBitmap(imageViewShell), 96, 96, false)
        markerShell = BitmapDescriptorFactory.fromBitmap(bitmapShell)

        val buttonClear : Button = findViewById(R.id.buttonClear)

        //Clickar para elegir el inicio y final de la ruta y llamar la funcion de crear la ruta
        buttonCalculateRoute = findViewById(R.id.buttonCalculateRoute)
        buttonCalculateRoute.setOnClickListener{
            start = ""
            end = ""
            var startAnimatedRoute = LatLng(0.0,0.0)
            if(::map.isInitialized){
                map.setOnMapClickListener {
                    if (start.isEmpty()){
                        start = "${it.longitude}"+","+"${it.latitude}"
                        val startRoute = LatLng(it.latitude,it.longitude)
                        map.addMarker(MarkerOptions().position(startRoute).title("Inicio de ruta").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
                        startAnimatedRoute = startRoute
                    }
                    else if(end.isEmpty()){
                        end = "${it.longitude}"+","+"${it.latitude}"
                        val endRoute = LatLng(it.latitude,it.longitude)
                        map.addMarker(MarkerOptions().position(endRoute).title("Destino").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)))
                    }
                    else{
                        createRoute()
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(startAnimatedRoute, 16f),
                            4000,
                            null)
                    }
                }
            }
        }
        buttonClear.setOnClickListener{
            map.clear()
        }
        //Spinner para filtro por ciudades
        var citiesArrayList: List<String> = listCities()
        spinnerCities = findViewById(R.id.spinnerCities)
        spinnerCities.adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, citiesArrayList)
        spinnerCities.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedCity = citiesArrayList[p2]
                searchIdCity(selectedCity)
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
                "Error"
            }
        }
    }
    //Arrancar GoogleMaps
    override fun onMapReady(googleMap: GoogleMap){
        map = googleMap
        enableMyLocation()
    }
    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray){
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                map.isMyLocationEnabled = true
            }
            else{
                Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT).show()
            }
            else -> {}
        }
    }
    //Al maximizar la aplicación, vuelve a comprobar los permisos de ubicación
    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if(!isPermissionsFineGranted()){
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun createMapFragment(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }
    //Comprueba si están los permisos de ubicación
    private fun isPermissionsFineGranted() = ContextCompat.checkSelfPermission(
        this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    //Activa la localización si están los permisos de ubicación concedidos
    @SuppressLint("MissingPermission")
    private fun enableMyLocation(){
        if (!::map.isInitialized) return
        if (isPermissionsFineGranted()){
            map.isMyLocationEnabled = true
        }
        else{
            requestLocationPermission()
        }
    }
    //Solicita permisos de ubicación
    private fun requestLocationPermission(){
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Active permisos de localización", Toast.LENGTH_SHORT)
                .show()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_LOCATION)
        }
    }
    //TODO función para iniciar en tu localización actual
    private fun myStartingLocalization(){}

    //Llamada a la API y conversión del JSON
    private fun getRoute() : Retrofit {
        val baseURLRoutes : String = "https://api.openrouteservice.org/"
        return Retrofit.Builder().baseUrl(baseURLRoutes)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    //Crear rutas
    private fun createRoute(){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getRoute().create(ApiServiceRoutes::class.java)
                .getRoute("5b3ce3597851110001cf624863cc2b9245844cacb6cf551d2d8490c1",
                    start,end)
            if (call.isSuccessful){
                drawRoute(call.body())
            }
            else{
                Log.i("DEPURANDO","NOT SUCCESSFUL")
            }
        }
    }
    //Dibujar ruta
    private fun drawRoute(body : RouteResponse?){
        val polyLineOptions = PolylineOptions()
        body?.features?.first()?.geometry?.coordinates?.forEach{
            polyLineOptions.add(LatLng(it[1],it[0])).width(6.8F)
                .color(Color.RED)
        }
        runOnUiThread{
            val route = map.addPolyline(polyLineOptions)
        }
    }
    //Llamada a la API y conversión del JSON
    private fun getGasStations() : Retrofit {
        val baseURLGas : String = "https://sedeaplicaciones.minetur.gob.es/"
        return Retrofit.Builder().baseUrl(baseURLGas)
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    //Crear markes de las gasolineras con sus descripciones
    private fun markerGasByCity(): ArrayList<String> {
        val gasArrayList = ArrayList<String>()
        CoroutineScope(Dispatchers.IO).launch {
            val call = getGasStations().create(ApiServiceGasByCity::class.java)
                .getGasStationsByCity(idSelectedCity)
            //35 41 38
            if(call.isSuccessful){
                if(::map.isInitialized){
                    runOnUiThread {
                        var minGas95 = 99.99
                        var minGas98 = 99.99
                        var minGasol = 99.99
                        for (i in call.body()!!.gasList){
                            var markerIcon = selectMarkerIcon(i.label)
                            var position = convertApiPosToLatLng(i.lati, i.long)
                            var gasTypeForSnippet = isGasType(i.gas95, i.gas98, i.gasol)
                            if(i.gas95.isNotEmpty()){ minGas95 = minPrice(minGas95, i.gas95) }
                            if(i.gas98.isNotEmpty()){ minGas98 = minPrice(minGas98, i.gas98) }
                            if(i.gasol.isNotEmpty()){ minGasol = minPrice(minGasol, i.gasol) }
                            map.addMarker(
                                MarkerOptions().position(position).title(
                                    "${i.label}, ${i.address} Horario: ${i.schedule}")
                                    .snippet(gasTypeForSnippet[0]+gasTypeForSnippet[1]+gasTypeForSnippet[2]).icon(markerIcon)
                            )
                        }
                        for(i in call.body()!!.gasList){
                            if(i.gas95.isNotEmpty()){
                                if(i.gas95.replace(",",".").toDouble() == minGas95){
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    var gasTypeForSnippet = isGasType(i.gas95, i.gas98, i.gasol)
                                    map.addMarker(
                                        MarkerOptions().position(position).title(
                                            "${i.label}, ${i.address} Horario: ${i.schedule}")
                                            .snippet(gasTypeForSnippet[0]+gasTypeForSnippet[1]+gasTypeForSnippet[2]).icon(markerMinGas95))
                                }
                            }
                        }
                        for(i in call.body()!!.gasList){
                            if(i.gas98.isNotEmpty()){
                                if(i.gas98.replace(",",".").toDouble() == minGas95){
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    var gasTypeForSnippet = isGasType(i.gas95, i.gas98, i.gasol)
                                    map.addMarker(
                                        MarkerOptions().position(position).title(
                                            "${i.label}, ${i.address} Horario: ${i.schedule}")
                                            .snippet(gasTypeForSnippet[0]+gasTypeForSnippet[1]+gasTypeForSnippet[2]).icon(markerMinGas98))
                                }
                            }
                        }
                        for(i in call.body()!!.gasList){
                            if(i.gasol.isNotEmpty()){
                                if(i.gasol.replace(",",".").toDouble() == minGas95){
                                    var position = convertApiPosToLatLng(i.lati, i.long)
                                    var gasTypeForSnippet = isGasType(i.gas95, i.gas98, i.gasol)
                                    map.addMarker(
                                        MarkerOptions().position(position).title(
                                            "${i.label}, ${i.address} Horario: ${i.schedule}")
                                            .snippet(gasTypeForSnippet[0]+gasTypeForSnippet[1]+gasTypeForSnippet[2]).icon(markerMinGasol))
                                }
                            }
                        }
                    }
                }
            }
            else{
                Log.i("DEPURANDO", "NOT SUCCES")
            }
        }
        return gasArrayList
    }
    //Crear custom markers
    private fun viewToBitmap(view: View) : Bitmap{
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.layout(0,0,view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }
    //Selector de iconos para los markers
    private fun selectMarkerIcon(label : String): BitmapDescriptor{
        //markerDefault
        val MarkerViewDefault = (getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custom_markers, null)
        val imageViewDefault : ImageView = MarkerViewDefault.findViewById(R.id.imageViewMarkerDefault)
        val bitmapDefault = Bitmap.createScaledBitmap(viewToBitmap(imageViewDefault), 96, 96, false)
        markerDefault = BitmapDescriptorFactory.fromBitmap(bitmapDefault)
        var markerRes = BitmapDescriptorFactory.fromBitmap(bitmapDefault)
        if(label.contains("AVIA")){ markerRes = markerAvia }
        if(label.contains("BP")){ markerRes = markerBp }
        if(label.contains("CARREFOUR")){ markerRes = markerCarrefour }
        if(label.contains("CEPSA")){ markerRes = markerCepsa }
        if(label.contains("DISA")){ markerRes = markerDisa }
        if(label.contains("EROSKI")){ markerRes = markerEroski }
        if(label.contains("GALP")){ markerRes = markerGalp }
        if(label.contains("HAM")){ markerRes = markerHam }
        if(label.contains("NATURGY")){ markerRes = markerNaturgy }
        if(label.contains("PETRONOR")){ markerRes = markerPetronor }
        if(label.contains("REPSOL")){ markerRes = markerRepsol }
        if(label.contains("SHELL")){ markerRes = markerShell }
        return markerRes
    }
    //Convierte las coordenadas de latitud y longitud de la api en LatLng utilizables por googleMaps
    private fun convertApiPosToLatLng(lat : String, lng : String): LatLng{
        return LatLng(lat.replace(",",".").toDouble(),
                                lng.replace(",",".").toDouble())
    }
    //Mostrará unicamente los productos que tengan el precio en la API
    private fun isGasType(apiGas95 : String, apigas98 : String, apigasol : String):ArrayList<String>{
        var gas95 = ""
        var gas98 = ""
        var gasol = ""
        val gasTypeArrayList = ArrayList<String>()
        if(apiGas95.isNotEmpty()){ gas95 = "Gas 95: "+ apiGas95 +"€, " }
        if(apigas98.isNotEmpty()){ gas98 = "Gas 98: "+ apigas98 +"€, " }
        if(apigasol.isNotEmpty()){ gasol = "Gasoleo: "+ apigasol +"€, " }
        gasTypeArrayList.add(gas95)
        gasTypeArrayList.add(gas98)
        gasTypeArrayList.add(gasol)
        return gasTypeArrayList
    }
    //Calcula el precio mínimo de cada combustible
    private fun minPrice(minPrice : Double, newPrice : String) : Double{
        var res = newPrice.replace(",",".").toDouble()
        if(res > minPrice){ res = minPrice }
        return res
    }

    //Llamada a la API y conversión del JSON
    private fun getCities(): Retrofit {
        return Retrofit.Builder().baseUrl("https://sedeaplicaciones.minetur.gob.es/")
            .addConverterFactory(GsonConverterFactory.create()).build()
    }
    //Crear lista de ciudades para mostrarlas en el Spinner
    private fun listCities(): List<String> {
        val citiesArrayList : MutableList<String> = mutableListOf("")
        CoroutineScope(Dispatchers.IO).launch {
            val call = getCities().create(ApiServiceCities::class.java).getCities()
            if (call.isSuccessful) {
                for (i in call.body()!!) {
                    citiesArrayList.add(i.provincia)
                }
                Log.i("DEPURANDO", citiesArrayList.toString())
            } else {
                Log.i("DEPURANDO", "NOT SUCCES")
            }
        }
        return citiesArrayList
    }
    //Buscar la id de la ciudad elegida en el Spinner
    private fun searchIdCity(city : String){
        CoroutineScope(Dispatchers.IO).launch {
            val call = getCities().create(ApiServiceCities::class.java).getCities()
            if (call.isSuccessful) {
                Log.i("DEPURANDO", "SUCCESsssss")
                for (i in call.body()!!) {
                    if(city == i.provincia){
                        idSelectedCity = i.idProvincia
                        Log.i("DEPURANDO", "id"+idSelectedCity)
                    }
                }
                markerGasByCity()
            }
        }
    }
}