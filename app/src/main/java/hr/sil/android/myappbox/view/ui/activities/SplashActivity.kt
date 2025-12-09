package hr.sil.android.myappbox.view.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.preferences.PreferenceStore
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.activities.intro.IntroductionSlidePagerActivity
import kotlinx.coroutines.*
import java.util.*

class SplashActivity : AppCompatActivity() {

    private val log = logger()
    val SPLASH_START = "SPLASH_START"
    private val SPLASH_DISPLAY_LENGTH = 2000

    private var coroutineJob: Job? = null
    private var fusedLocationClient: FusedLocationProviderClient? = null
    private var locationCallback: LocationCallback? = null

    private var currentTime = Date()
    private var PASSED_120_SECONDS = 120000

    var currentSystemTime = System.currentTimeMillis()

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

        preloadAndStartMain()
    }

    // Maybe we will need to add again to look for user location on splash screen
    // That why I have left this code -> commented
    /*override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if( grantResults.isNotEmpty() ) {
            val permissionRequestGranted =
                requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED
            if( permissionRequestGranted ) {
                preloadAndStartMain()
            }
        }
    }*/

    private fun preloadAndStartMain() {
        GlobalScope.launch {

            // Maybe we will need to add again to look for user location on splash screen
            // That why I have left this code -> commented
            /*if (ActivityCompat.checkSelfPermission(
                    this@SplashActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(this@SplashActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            }
            else {
                getGoodUserGpsLocation()
            }*/

            val beginTimestamp = System.currentTimeMillis()
            val duration = System.currentTimeMillis() - beginTimestamp
            log.info("App Start length:" + duration)
            if (duration < SPLASH_DISPLAY_LENGTH) {
                delay(SPLASH_DISPLAY_LENGTH - duration)
            }

            withContext(Dispatchers.Main) {
                startApp()
                finish()
            }
        }
    }

    // Maybe we will need to add again to look for user location on splash screen
    // That why I have left this code -> commented
    /*@SuppressLint("MissingPermission")
    private fun getGoodUserGpsLocation() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this@SplashActivity)
        val locationRequest = LocationRequest.create().apply {
            interval = 1000
            fastestInterval = 1000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    // Update UI with location data
                    val timePassed = Date().time - currentTime.time

                    Log.i("GpsAccuracy", " Time passed to get new gps coord 5555: ${System.currentTimeMillis() - currentSystemTime} " +
                            ",, Gps accuracy is: ${location.accuracy} " )

                    if ( location != null && location.accuracy < 60f ) {
                        fusedLocationClient?.removeLocationUpdates(locationCallback)
                        Log.i("Tag", "New location received: ${location.latitude}, " +
                                " longitude: ${location.longitude}, gps accuracy is: ${location.accuracy}, time passed: ${Date().time}, global time passed: ${timePassed}")
                        val userLastLocation = UserLastLocationGps()
                        userLastLocation.lastGoodLocation = location
                        userLastLocation.lastFetchedGpsLocation = Date()

                        App.ref.userLastLocation = userLastLocation

                        Log.i("Tag", "User last location latitude is: ${userLastLocation.lastGoodLocation.latitude}," +
                                " User last location longitude is: ${userLastLocation.lastGoodLocation.longitude} " )
                    }

                    if (timePassed > PASSED_120_SECONDS) {
                        if (coroutineJob?.isActive != false)
                            coroutineJob?.cancel()
                        fusedLocationClient?.removeLocationUpdates(locationCallback)
                        log.info("Time passed to get new gps coordinate: ${timePassed}")
                        finish()
                    }

                }
            }
        }

        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }*/

    private suspend fun startApp() {

        Log.i("SplashActivity", "This is second start")
        App.Companion.ref.isFirstStart = false
        if (SettingsHelper.firstRun && resources.getBoolean(R.bool.dipslay_onboarding_screen)) {

            val systemLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                getResources().getConfiguration().getLocales().get(0).language.toString()
            } else{
                getResources().getConfiguration().locale.language.toString()
            }

            log.info("System language is: ${systemLanguage}")
            log.info("Shared preference language is: ${SettingsHelper.languageName}")

            if( systemLanguage == "de" ) {
                SettingsHelper.languageName = "DE"
            }
            else if( systemLanguage == "fr" ) {
                SettingsHelper.languageName = "FR"
            }
            else if( systemLanguage == "it" ) {
                SettingsHelper.languageName = "IT"
            }
            else {
                SettingsHelper.languageName = "EN"
            }

            Log.i("SplashActivity", "This is first start")
            val startIntent = Intent(this@SplashActivity, IntroductionSlidePagerActivity::class.java)
            startIntent.putExtra(SPLASH_START, App.Companion.ref.isFirstStart)
            startActivity(startIntent)
            finish()
        }
        else if( !PreferenceStore.userHash.isNullOrBlank() && UserUtil.login((SettingsHelper.usernameLogin)) ) {
            log.info("Splash duration is: ${Date().time - currentTime.time}, ${Date().time}")
            val intent = Intent()
            val packageName = this@SplashActivity.packageName
            val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
            intent.component = componentName
            startActivity(intent)
            finish()
        }
        else {

            val systemLanguage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                getResources().getConfiguration().getLocales().get(0).language.toString()
            } else{
                getResources().getConfiguration().locale.language.toString()
            }

            log.info("System language is: ${systemLanguage}")
            log.info("Shared preference language is: ${SettingsHelper.languageName}")

            if( systemLanguage  == "de" ) {
                SettingsHelper.languageName = "DE"
            }
            else if( systemLanguage == "fr" ) {
                SettingsHelper.languageName = "FR"
            }
            else if( systemLanguage == "it" ) {
                SettingsHelper.languageName = "IT"
            }
            else {
                SettingsHelper.languageName = "EN"
            }
            val startIntent = Intent(this@SplashActivity, LoginActivity::class.java)
            startIntent.putExtra(SPLASH_START, App.Companion.ref.isFirstStart)
            startActivity(startIntent)
            finish()
        }

    }

}
