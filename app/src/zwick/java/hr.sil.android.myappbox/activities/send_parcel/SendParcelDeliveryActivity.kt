package hr.sil.android.myappbox.activities.send_parcel

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.gms.location.*
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.cache.status.ActionStatusHandler
import hr.sil.android.smartlockers.enduser.cache.status.ActionStatusKey
import hr.sil.android.smartlockers.enduser.cache.status.ActionStatusType
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.InstalationType
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerSize
import hr.sil.android.smartlockers.enduser.core.remote.model.RMasterUnitType
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.ActivityLoginBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivitySelectParcelSizeBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivitySendParcelDeliveryBinding
import hr.sil.android.smartlockers.enduser.events.UnauthorizedUserEvent
import hr.sil.android.smartlockers.enduser.store.MPLDeviceStore
import hr.sil.android.smartlockers.enduser.store.model.MPLDevice
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.*

class SendParcelDeliveryActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout) 
{

    val macAddress: String by lazy { intent.getStringExtra("rMacAddress") ?: ""}
    var mac: String? = null
    var pin: Int = 0
    var size: String? = null
    val log = logger()
    var successColor = 0
    var errorColor = 0
    var device: MPLDevice? = null
    val masterUnitId: Int by lazy { intent.getIntExtra("masterUnitId", 0) }
    var groupId: Int = 0

    private lateinit var binding: ActivitySendParcelDeliveryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySendParcelDeliveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        mac = intent.getStringExtra("rMacAddress")
        pin = intent.getIntExtra("pin", 0)
        size = intent.getStringExtra("size") ?: RLockerSize.L.name
        groupId = intent.getIntExtra("groupId", 0)

        log.info("Device pin is: ${pin}")

        device = MPLDeviceStore.uniqueDevices[macAddress]
    }

    override fun onStart() {
        super.onStart()

        sendParcel(size)

        checkIfHasEmailAndMobilePhoneSupport()

        successColor = with(TypedValue()) {
            this@SendParcelDeliveryActivity.theme.resolveAttribute(R.attr.thmDescriptionTextColor, this, true)
            ContextCompat.getColor(this@SendParcelDeliveryActivity, resourceId)
        }
        errorColor = with(TypedValue()) {
            this@SendParcelDeliveryActivity.theme.resolveAttribute(R.attr.thmErrorTextColor, this, true)
            ContextCompat.getColor(this@SendParcelDeliveryActivity, resourceId)
        }

        binding.sendParcelFinish.setOnClickListener {
            val intent = Intent()
            val packageName = this@SendParcelDeliveryActivity.packageName
            val componentName = ComponentName(packageName, packageName + ".aliasFinishDelivery")
            intent.component = componentName

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
            finish()
        }

        binding.sendParcelRetry.setOnClickListener {
            sendParcel(size)
        }
    }

    private fun errorUpdateUi(string: String) {
        binding.progressBar.visibility = View.INVISIBLE
        binding.sendParcelRetry.visibility = View.VISIBLE
        binding.sendParcelFinish.visibility = View.INVISIBLE
        binding.sendParcelMainText.visibility = View.VISIBLE
        binding.sendParcelMainText.setText(string)
        binding.sendParcelMainText.setTextColor(errorColor)
    }

    private fun checkIfHasEmailAndMobilePhoneSupport() {
        binding.ivSupportImage.setOnClickListener {
            val supportEmailPhoneDialog = SupportEmailPhoneDialog()
            supportEmailPhoneDialog.show(
                supportFragmentManager,
                ""
            )
        }
    }

    override fun onBluetoothStateUpdated(available: Boolean) {
        super.onBluetoothStateUpdated(available)
        bluetoothAvalilable = available
        updateUI()
    }

    override fun onNetworkStateUpdated(available: Boolean) {
        super.onNetworkStateUpdated(available)
        networkAvailable = available
        updateUI()
    }

    override fun onLocationGPSStateUpdated(available: Boolean) {
        super.onLocationGPSStateUpdated(available)
        locationGPSAvalilable = available
        updateUI()
    }

    private fun sendParcel(size: String?) {
        binding.progressBar.visibility = View.VISIBLE
        binding.sendParcelMainText.visibility = View.INVISIBLE
        binding.sendParcelRetry.visibility = View.INVISIBLE
        binding.sendParcelFinish.visibility = View.INVISIBLE
        log.info("Connecting to $mac...")
        val locker = MPLDeviceStore.uniqueDevices[mac]
        GlobalScope.launch {
            val comunicator = locker?.createBLECommunicator(this@SendParcelDeliveryActivity)
            val user = UserUtil.user
            log.info("Size $size, pin $pin, User ${user?.name}")
            if (size != null && pin != 0 && user != null && comunicator?.connect() == true) {
                log.info("Connected!")

                val reducedMobilityByte = if( UserUtil.user?.reducedMobility ?: false ) 0x01.toByte() else 0x00.toByte()

                val response = if( device?.installationType == InstalationType.TABLET ) comunicator.requestParcelSendCreateForTablets(RLockerSize.valueOf(size), user.id, pin, reducedMobilityByte)
                else comunicator.requestParcelSendCreate(RLockerSize.valueOf(size), user.id, pin)

                comunicator.disconnect()

                log.info("Send parcel delivery is successfully: ${response.isSuccessful}, device data error code is ${response.bleDeviceErrorCode} , slave data error code is ${response.bleSlaveErrorCode}")
                if (response.isSuccessful) {

                    val pahKeyy = WSUser.getActivePaHCreatedKeys() ?: mutableListOf()
                    UserUtil.pahKeys = pahKeyy

                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.INVISIBLE
                        binding.sendParcelRetry.visibility = View.INVISIBLE
                        binding.sendParcelFinish.visibility = View.VISIBLE
                        log.info("LockerType : ${locker.type}")
                        binding.sendParcelMainText.visibility = View.VISIBLE
                        binding.sendParcelMainText.setText(R.string.nav_send_parcel_content_text)
                        binding.sendParcelMainText.setTextColor(successColor)
                        binding.sendParcelMainText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)

                        binding.llShareAccessPin.visibility = View.VISIBLE
                        binding.llShareAccessPin.setOnClickListener {
                            val shareBodyText = resources.getString(R.string.share_pin_device_name, locker.name) + "\n"+
                                    resources.getString(R.string.share_pin_device_address, locker.address) +  "\n" +
                                    resources.getString(R.string.share_pin_device_locker_size, size) +  "\n" +
                                    resources.getString(R.string.share_pin_device_pin, pin.toString())
                            val emailIntent = Intent(Intent.ACTION_SEND)
                            emailIntent.setType("text/plain")
                            //emailIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.share_access_pin))
                            emailIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)

                            startActivity(Intent.createChooser(emailIntent,  baseContext.getString(R.string.access_sharing_share_choose_sharing)))
                        }

                        if (locker.masterUnitType == RMasterUnitType.SPL) {
                            val action = ActionStatusKey().apply {
                                keyId = locker.macAddress + ActionStatusType.SPL_OCCUPATION
                            }
                            ActionStatusHandler.actionStatusDb.put(action)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        errorUpdateUi(getString(R.string.nav_send_parcel_failed))
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    log.info("Not Connected!")
                    errorUpdateUi(getString(R.string.main_locker_ble_connection_error))
                }
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
        log.info("Received unauthorized event, user will now be log outed")
        val intent = Intent( this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {

                val intent = Intent()
                val packageName = this@SendParcelDeliveryActivity.packageName
                val componentName = ComponentName(packageName, packageName + ".aliasFinishDelivery")
                intent.component = componentName

                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent()
        val packageName = this@SendParcelDeliveryActivity.packageName
        val componentName = ComponentName(packageName, packageName + ".aliasFinishDelivery")
        intent.component = componentName

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
        super.onBackPressed()
    }

}
