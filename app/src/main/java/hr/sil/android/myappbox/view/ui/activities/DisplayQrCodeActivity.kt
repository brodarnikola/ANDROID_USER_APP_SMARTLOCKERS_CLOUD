package hr.sil.android.myappbox.view.ui.activities


import android.content.ComponentName
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.events.QrCodeScannedEvent
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.settings.SettingsActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*



class DisplayQrCodeActivity
    //: BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout)
{

//    private lateinit var binding: ActivityDisplayQrcodeBinding
//
//    val log = logger()
//
//    val GO_TO_PICKUP_PARCEL_SCREEN = 1
//    val GO_TO_SETTINGS_SCREEN = 2
//    val GO_TO_HOME_SCREEN = 3
//
//    //var checkQrCodeJob: Job? = null
//
//    var returnToCorrectScreen = 0
//    var macAddress = ""
//
////    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
////    private lateinit var cameraExecutor: ExecutorService
////    private lateinit var analyzer: QrCodeImageAnalyzer
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityDisplayQrcodeBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        returnToCorrectScreen = intent.getIntExtra("returnToCorrectScreen", 0)
//        if( intent.getStringExtra("rMacAddress") != null )
//            macAddress = intent.getStringExtra("rMacAddress") ?: ""
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        if( returnToCorrectScreen == GO_TO_PICKUP_PARCEL_SCREEN )
//            binding.tvTitle.text = getString(R.string.collect_QR_code)
//        else
//            binding.tvTitle.text = getString(R.string.user_identification_QR_code)
//
//        //exampleOfFunctionToPingEvery2Second()
//
//        initQrCodeImage()
//    }
//
//    /*private fun tryToPingEvery2Seconds() {
//        checkQrCodeJob = lifecycleScope.launch {
//            withTimeoutOrNull(7000) {
//                try {
//                    var backendResponse = false
//                    while (!backendResponse) {
//                        log.info("hoce li ispisati svakih 2 sekundi poruku")
//                        //backendResponse = WSUser.checkQrCode(5) ?: false
//                        if (backendResponse)
//                            break
//                        delay(2000)
//                    }
//
//                    withContext(Dispatchers.Main) {
//                        if (backendResponse) {
//                            App.ref.toast(R.string.app_generic_success)
//                        } else {
//                            App.ref.toast(R.string.app_generic_error)
//                        }
//                    }
//                } catch (e: TimeoutCancellationException) {
//                    log.info("dogodio se je timeout: ${e.printStackTrace()}")
//                    App.ref.toast("Timeout: ${e.printStackTrace()}")
//                }
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        checkQrCodeJob?.cancel()
//    }*/
//
//    private fun initQrCodeImage() {
//        lifecycleScope.launch {
//
//            withContext(Dispatchers.Main) {
//
//                textToQrCodeImageEncode()
//
//                /*analyzer = QrCodeImageAnalyzer(this@DisplayQrCodeActivity)
//                cameraExecutor = Executors.newSingleThreadExecutor()
//                cameraProviderFuture = ProcessCameraProvider.getInstance(this@DisplayQrCodeActivity)
//
//                cameraProviderFuture.addListener(Runnable {
//                    val cameraProvider = cameraProviderFuture.get()
//                    bindPreview(cameraProvider)
//                }, ContextCompat.getMainExecutor(this@DisplayQrCodeActivity))*/
//
//                binding.container.visibility = View.VISIBLE
//                binding.progressBar.visibility = View.GONE
//            }
//        }
//    }
//
//    private fun textToQrCodeImageEncode() {
//
//        log.info("Final qrCode in displayQrCodeActivity is: ${UserUtil.user?.identificationQrCode}")
//
//        val bitMatrix: BitMatrix?
//        bitMatrix = try {
//            MultiFormatWriter().encode(
//                UserUtil.user?.identificationQrCode,
//                BarcodeFormat.QR_CODE,
//                350, 350, null
//            )
//        } catch (illegalargumentexception: IllegalArgumentException) {
//            log.info("exceptions is: ${illegalargumentexception}")
//            null
//        }
//        val bitMatrixWidth = bitMatrix?.width ?: 1
//        val bitMatrixHeight = bitMatrix?.height ?: 1
//        val pixels = IntArray(bitMatrixWidth * bitMatrixHeight)
//        for (y in 0 until bitMatrixHeight) {
//            val offset = y * bitMatrixWidth
//            for (x in 0 until bitMatrixWidth) {
//                pixels[offset + x] =
//                    if (bitMatrix!!.get(x, y)) Color.BLACK else Color.WHITE
//            }
//        }
//        val bitmap = Bitmap.createBitmap(bitMatrixWidth, bitMatrixHeight, Bitmap.Config.RGB_565)
//        bitmap.setPixels(pixels, 0, 350, 0, 0, bitMatrixWidth, bitMatrixHeight)
//        binding.ivImage.setImageBitmap(bitmap)
//    }
//
//    /*@SuppressLint("UnsafeExperimentalUsageError")
//    private fun bindPreview(cameraProvider: ProcessCameraProvider) {
//        val preview: Preview = Preview.Builder()
//            .build()
//        val cameraSelector: CameraSelector = CameraSelector.Builder()
//            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
//            .build()
//        preview.setSurfaceProvider(previewView.createSurfaceProvider(null))
//
//        val imageAnalysis = ImageAnalysis.Builder()
//            .setTargetResolution(Size(1280, 720))
//            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//            .build()
//        imageAnalysis.setAnalyzer(cameraExecutor, analyzer)
//
//        cameraProvider.unbindAll()
//        val camera = cameraProvider.bindToLifecycle(
//            this as LifecycleOwner,
//            cameraSelector,
//            imageAnalysis,
//            preview
//        )
//
//        val listener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
//            override fun onScale(detector: ScaleGestureDetector): Boolean {
//                val scale = camera.cameraInfo.zoomState.value!!.zoomRatio * detector.scaleFactor
//                camera.cameraControl.setZoomRatio(scale)
//                return true
//            }
//        }
//
//        val scaleGestureDetector = ScaleGestureDetector(this@DisplayQrCodeActivity, listener)
//
//        previewView.setOnTouchListener { _, event ->
//            scaleGestureDetector.onTouchEvent(event)
//            return@setOnTouchListener true
//        }
//
//    }*/
//
//    override fun onBluetoothStateUpdated(available: Boolean) {
//        super.onBluetoothStateUpdated(available)
//        bluetoothAvalilable = available
//        updateUI()
//    }
//
//    override fun onNetworkStateUpdated(available: Boolean) {
//        super.onNetworkStateUpdated(available)
//        networkAvailable = available
//        updateUI()
//    }
//
//    override fun onLocationGPSStateUpdated(available: Boolean) {
//        super.onLocationGPSStateUpdated(available)
//        locationGPSAvalilable = available
//        updateUI()
//    }
//
//    override fun onResume() {
//        super.onResume()
//        App.Companion.ref.eventBus.register(this)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        App.Companion.ref.eventBus.unregister(this)
//    }
//
//    @Subscribe(threadMode = ThreadMode.BACKGROUND)
//    fun userHasScannedQrCode(event: QrCodeScannedEvent) {
//
//        log.info("Received qrCodeScanned event. Redirecting user to MainActivity")
//
//        val intent = Intent()
//        val packageName = this@DisplayQrCodeActivity.packageName
//        val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//        intent.component = componentName
//
//        startActivity(intent)
//        finish()
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
//        log.info("Received unauthorized event, user will now be log outed")
//        val intent = Intent(this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                if( returnToCorrectScreen == GO_TO_PICKUP_PARCEL_SCREEN ) {
//
//                    val intentQrCodeImage = Intent()
//                    val packageName = this@DisplayQrCodeActivity.packageName
//                    val componentName = ComponentName(packageName, packageName + ".aliasPickupParcelActivity")
//                    intentQrCodeImage.component = componentName
//                    intentQrCodeImage.putExtra("rMacAddress", macAddress)
//
//                    startActivity(intentQrCodeImage)
//                    finish()
//                }
//                else if( returnToCorrectScreen == GO_TO_SETTINGS_SCREEN ) {
//                    val intent = Intent(this, SettingsActivity::class.java)
//                    startActivity(intent)
//                    overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                    finish()
//                }
//                else if( returnToCorrectScreen == GO_TO_HOME_SCREEN ) {
//                    val intentQrCodeImage = Intent()
//                    val packageName = this@DisplayQrCodeActivity.packageName
//                    val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//                    intentQrCodeImage.component = componentName
//
//                    startActivity(intentQrCodeImage)
//                    finish()
//                }
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onBackPressed() {
//        if( returnToCorrectScreen == GO_TO_PICKUP_PARCEL_SCREEN ) {
//
//            val intentQrCodeImage = Intent()
//            val packageName = this@DisplayQrCodeActivity.packageName
//            val componentName = ComponentName(packageName, packageName + ".aliasPickupParcelActivity")
//            intentQrCodeImage.component = componentName
//            intentQrCodeImage.putExtra("rMacAddress", macAddress)
//
//            startActivity(intentQrCodeImage)
//            finish()
//        }
//        else if( returnToCorrectScreen == GO_TO_SETTINGS_SCREEN ) {
//            val intent = Intent(this, SettingsActivity::class.java)
//            startActivity(intent)
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//            finish()
//        }
//        else if( returnToCorrectScreen == GO_TO_HOME_SCREEN ) {
//            val intentQrCodeImage = Intent()
//            val packageName = this@DisplayQrCodeActivity.packageName
//            val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//            intentQrCodeImage.component = componentName
//
//            startActivity(intentQrCodeImage)
//            finish()
//        }
//        super.onBackPressed()
//    }


}
