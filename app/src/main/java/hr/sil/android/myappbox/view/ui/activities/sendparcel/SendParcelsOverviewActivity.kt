package hr.sil.android.myappbox.view.ui.activities.sendparcel


import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.cache.status.ActionStatusHandler
import hr.sil.android.myappbox.cache.status.ActionStatusType
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.InstalationType
import hr.sil.android.myappbox.core.remote.model.RCreatedLockerKey
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.core.util.macRealToClean
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
//import hr.sil.android.myappbox.view.ui.adapters.SendParcelsSharingAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SendParcelsOverviewActivity
    //: BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout)
{

//    val log = logger()
//
//    var listOfKeys: MutableList<RCreatedLockerKey>? = mutableListOf()
//
//    var macAddress = ""
//
//    private lateinit var binding: ActivitySendParcelsOverviewBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySendParcelsOverviewBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        macAddress = intent.getStringExtra("macAddress") ?: ""
//    }
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
//    override fun onStart() {
//        super.onStart()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        lifecycleScope.launch {
//
//            listOfKeys = WSUser.getActivePaHCreatedKeys() ?: mutableListOf()
//
//            listOfKeys?.filter { ActionStatusHandler.actionStatusDb.get(it.lockerId.toString() + ActionStatusType.PAH_ACCESS_CANCEL) == null }
//
//            val listOfDevices = if( macAddress != "" ) MPLDeviceStore.uniqueDevices.values.filter { macAddress.macRealToClean() == it.macAddress.macRealToClean() && ( it.isInBleProximity == true || it.installationType == InstalationType.LINUX ) }
//            else MPLDeviceStore.uniqueDevices.values.filter { it.isInBleProximity == true || it.installationType == InstalationType.LINUX }
//
//            if( listOfKeys != null ) {
//                for (keyItem in listOfKeys!!) {
//
//                    var isInBLEproximity = false
//
//                    for (lockerDevice in listOfDevices) {
//
//                        if (keyItem.lockerMasterMac == lockerDevice.macAddress.macRealToClean()) {
//                            keyItem.isLinuxKeyDevice = lockerDevice.installationType ?: InstalationType.LINUX
//                            if( lockerDevice.installationType == InstalationType.LINUX ) {
//                                keyItem.deviceLatitude = lockerDevice.latitude
//                                keyItem.deviceLongitude = lockerDevice.longitude
//                            }
//                            isInBLEproximity = true
//                            break
//                        }
//                    }
//
//                    keyItem.isInBleProximityOrLinuxDevice = isInBLEproximity
//                }
//            }
//
//            withContext(Dispatchers.Main) {
//                if( listOfKeys != null ) {
//                    binding.rvCancelPickedHome.layoutManager =
//                        LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
//
//                    binding.rvCancelPickedHome.adapter =
//                        SendParcelsSharingAdapter(listOfKeys!!, this@SendParcelsOverviewActivity)
//                }
//            }
//        }
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        if( resources.getBoolean(R.bool.has_mobile_and_email_support) ) {
//            binding.ivSupportImage.visibility = View.VISIBLE
//            binding.ivSupportImage.setOnClickListener {
//                val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//                supportEmailPhoneDialog.show(
//                    supportFragmentManager,
//                    ""
//                )
//            }
//        }
//        else
//            binding.ivSupportImage.visibility = View.GONE
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                val intent = Intent()
//                val packageName = this@SendParcelsOverviewActivity.packageName
//                val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
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
//        val packageName = this@SendParcelsOverviewActivity.packageName
//        val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//        intent.component = componentName
//
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }

}
