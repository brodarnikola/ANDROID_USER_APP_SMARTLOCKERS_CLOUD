package hr.sil.android.myappbox.view.ui.activities.sendparcel

import android.content.ComponentName
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App

import hr.sil.android.myappbox.data.DeactivateSPLInterface
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.remote.model.RMasterUnit
import hr.sil.android.myappbox.core.remote.model.RMasterUnitType
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.store.DeviceStoreRemoteUpdater
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import hr.sil.android.myappbox.R

class EditSplActivity
    //: BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout)
//    ,DeactivateSPLInterface
{

//    val macAddress by lazy { intent.getStringExtra("rMacAddress") ?: "" }
//    lateinit var pahKeyy: List<RCreatedLockerKey>
//    val log = logger()
//    var device: MPLDevice? = null
//
//    private lateinit var binding: ActivityEditSplBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityEditSplBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        device = MPLDeviceStore.uniqueDevices[macAddress]
//
//        binding.keySharingButton.setOnClickListener {
//            if ( binding.etLockerName.text.isNotEmpty() &&  binding.etLockerAddress.text.isNotEmpty() && device != null) {
//                binding.progressEditSpl.visibility = View.VISIBLE
//                binding.keySharingButton.visibility = View.GONE
//                binding.pickupPinForm.error = null
//                binding.pickupHelpWrapper.error = null
//                val splUnit = RMasterUnit().apply {
//                    this.address =  binding.etLockerAddress.text.toString()
//                    this.name =  binding.etLockerName.text.toString()
//                    this.id = device?.masterUnitId ?: 0
//                    this.mac = device?.macAddress?.macRealToClean().toString()
//                    this.type = device?.masterUnitType ?: RMasterUnitType.SPL_PLUS
//                }
//                log.info("Trying to save SPL unit data ${splUnit.address} ${splUnit.name} ${splUnit.id} ${splUnit.mac} ${splUnit.type}")
//
//                lifecycleScope.launch {
//
//                    val result = WSUser.modifyMasterUnit(splUnit)
//                    withContext(Dispatchers.Main) {
//                        when {
//                            result -> {
//                                App.Companion.ref.toast(R.string.app_generic_success)
//                                log.info("Successfully saved SPL data unit")
//                            }
//                            else -> log.error("Error while saving SPL data")
//                        }
//                        binding.progressEditSpl.visibility = View.GONE
//                        binding.keySharingButton.visibility = View.VISIBLE
//                    }
//                }
//            } else {
//                binding.progressEditSpl.visibility = View.GONE
//                binding.keySharingButton.visibility = View.VISIBLE
//                if (binding.etLockerName.text.isNullOrEmpty()) {
//                    binding.pickupPinForm.error =
//                        getString(R.string.edit_user_validation_blank_fields_exist)
//                } else {
//                    binding.pickupPinForm.error = null
//                }
//
//                if (binding.etLockerAddress.text.isNullOrEmpty()) {
//                    binding.pickupHelpWrapper.error =
//                        getString(R.string.edit_user_validation_blank_fields_exist)
//                } else {
//                    binding.pickupHelpWrapper.error = null
//                }
//            }
//        }
//
//        binding.tvDeactivateLocker.setOnClickListener {
//            if( device?.masterModemQueueSize == 0 ) {
//                val dialog = DeactivateSPLDialog(this@EditSplActivity)
//                val args = Bundle()
//                args.putString("rMacAddress", macAddress)
//                dialog.arguments = args
//                dialog.show(supportFragmentManager, "")
//            }
//            else {
//                val modemQueueNotZeroDialog = ModemQueueNotZeroDialog()
//                modemQueueNotZeroDialog.show(supportFragmentManager, "")
//            }
//        }
//
//        binding.tvDeactivateLocker.setPaintFlags(binding.tvDeactivateLocker.getPaintFlags() or Paint.UNDERLINE_TEXT_FLAG)
//    }
//
//    override fun deactivateSPL() {
//
//        binding.progressEditSpl.visibility = View.VISIBLE
//        binding.tvDeactivateLocker.visibility = View.GONE
//        lifecycleScope.launch(Dispatchers.IO) {
//
//            if( device?.isInBleProximity ?: false ) {
//                if( device?.activeKeys?.isNotEmpty() ?: false ) {
//                    val communicator = device?.createBLECommunicator(this@EditSplActivity)
//                    if (communicator?.connect() == true) {
//                        val bleResponse = communicator.invalidateAllKeysOnMasterUnit()
//                        communicator.disconnect()
//                        if (!bleResponse) {
//                            withContext(Dispatchers.Main) {
//                                App.Companion.ref.toast(getString(R.string.app_generic_error))
//                                log.error(bleResponse.toString())
//                                binding.progressEditSpl.visibility = View.GONE
//                                binding.tvDeactivateLocker.visibility = View.VISIBLE
//                            }
//                        } else {
//                            deactivateSPLOnlyOnBackend()
//                        }
//                    }
//                    else {
//                        communicator?.disconnect()
//                    }
//                }
//                else {
//                    deactivateSPLOnlyOnBackend()
//                }
//            }
//            else {
//                if( device?.activeKeys?.isNotEmpty() ?: false ) {
//                    withContext(Dispatchers.Main) {
//                        val canNotDeactivateSPLDialog = CanNotDeactivateSPLDialog()
//                        canNotDeactivateSPLDialog.show(supportFragmentManager, "")
//                    }
//                }
//                else {
//                    deactivateSPLOnlyOnBackend()
//                }
//            }
//        }
//    }
//
//    private suspend fun deactivateSPLOnlyOnBackend() {
//        val deactivateSplPlusBackend = WSUser.deactivateSPL(macAddress.macRealToClean())
//        if( deactivateSplPlusBackend ) {
//            DataCache.clearMasterUnitCache()
//            DataCache.clearLockerInfoCache()
//            DataCache.getDevicesInfo(true)
//            DeviceStoreRemoteUpdater.forceUpdate()
//        }
//        withContext(Dispatchers.Main) {
//            if( deactivateSplPlusBackend ) {
//                App.Companion.ref.toast(getString(R.string.app_generic_success))
//                val intent = Intent()
//                val packageName = packageName ?: ""
//                val componentName = ComponentName(packageName, packageName + ".aliasSelectCityParcelLocker")
//                intent.component = componentName
//
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                finish()
//                log.info("Success deactivation device on ${device?.macAddress}")
//            }
//            else {
//                binding.progressEditSpl.visibility = View.GONE
//                binding.tvDeactivateLocker.visibility = View.VISIBLE
//                App.Companion.ref.toast(getString(R.string.app_generic_error))
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        val device = MPLDeviceStore.uniqueDevices[macAddress]
//
//        binding.etLockerName.setText(device?.name ?: "")
//        binding.etLockerAddress.setText(device?.address ?: "")
//
//        App.Companion.ref.eventBus.register(this@EditSplActivity)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if( App.Companion.ref.eventBus.isRegistered(this@EditSplActivity) )
//            App.Companion.ref.eventBus.unregister(this)
//    }
//
//    private fun emptyUserActiveKeys(): Boolean {
//        val device = MPLDeviceStore.uniqueDevices[macAddress]
//        return device != null && device.activeKeys.isEmpty() && pahKeyy.isEmpty()
//    }
//
//    override fun onBluetoothStateUpdated(available: Boolean) {
//        super.onBluetoothStateUpdated(available)
//        bluetoothAvalilable = available
//        if (viewLoaded) {
//            updateUI()
//        }
//    }
//
//    override fun onLocationGPSStateUpdated(available: Boolean) {
//        super.onLocationGPSStateUpdated(available)
//        locationGPSAvalilable = available
//        updateUI()
//    }
//
//    override fun onNetworkStateUpdated(available: Boolean) {
//        super.onNetworkStateUpdated(available)
//        networkAvailable = available
//        if (viewLoaded) {
//            updateUI()
//        }
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
//        log.info("Received unauthorized event, user will now be log outed")
//        val intent = Intent( this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                val intent = Intent()
//                val packageName = this@EditSplActivity.packageName
//                val componentName = ComponentName(packageName, packageName + ".aliasSelectCityParcelLocker")
//                intent.component = componentName
//
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                finish()
//                return true
//            }
//
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onBackPressed() {
//        val intent = Intent()
//        val packageName = this@EditSplActivity.packageName
//        val componentName = ComponentName(packageName, packageName + ".aliasSelectCityParcelLocker")
//        intent.component = componentName
//
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }


}
