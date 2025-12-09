package hr.sil.android.myappbox.view.ui.activities.access_sharing


import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import hr.sil.android.myappbox.App

import hr.sil.android.myappbox.events.MPLDevicesUpdatedEvent
import hr.sil.android.myappbox.events.UnauthorizedUserEvent
import hr.sil.android.myappbox.store.MPLDeviceStore
import hr.sil.android.myappbox.store.model.MPLDevice
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.LoginActivity
//import hr.sil.android.myappbox.view.ui.adapters.AccessSharingAdapter
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class AccessSharingListActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout)
{

//    private lateinit var binding: ActivityAccessSharingListBinding
//
//    private lateinit var sendParcelsAdapter: AccessSharingAdapter
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAccessSharingListBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//    }
//
//    private fun renderDeviceItems() {
//
//        val lockerList = MPLDeviceStore.uniqueDevices.values.filter { it.hasRightsToShareAccess() }.distinctBy { it.macAddress }
//
//        if (lockerList.size > 0) {
//
//            binding.rvSelectLocker.visibility = View.VISIBLE
//            binding.tvAccessSharingDescription.visibility = View.VISIBLE
//            binding.tvNoDevicesToSelect.visibility = View.GONE
//
//            sendParcelsAdapter.updateDevices(lockerList)
//        } else {
//
//            binding.rvSelectLocker.visibility = View.GONE
//            binding.tvAccessSharingDescription.visibility = View.GONE
//            binding.tvNoDevicesToSelect.visibility = View.VISIBLE
//            binding.tvNoDevicesToSelect.text = getString(R.string.no_access_sharing_rights)
//        }
//    }
//
//    private fun deviceItemClicked(parcelLocker: MPLDevice) {
//
//        val startIntent = Intent(baseContext, AccessSharingActivity::class.java)
//        startIntent.putExtra("rMacAddress", parcelLocker.macAddress)
//        startIntent.putExtra("nameOfDevice", parcelLocker.name)
//        startActivity(startIntent)
//        finish()
//    }
//
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: MPLDevicesUpdatedEvent) {
//        renderDeviceItems()
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        val lockerList = MPLDeviceStore.uniqueDevices.values.filter { it.hasRightsToShareAccess() == true }.distinctBy { it.macAddress }
//
//        binding.rvSelectLocker.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
//        //rvSelectLocker.layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
//
//        sendParcelsAdapter = AccessSharingAdapter(
//            lockerList,
//            { partItem: MPLDevice -> deviceItemClicked(partItem) },
//            this@AccessSharingListActivity
//        )
//        binding.rvSelectLocker.adapter = sendParcelsAdapter
//
//        (binding.rvSelectLocker.getItemAnimator() as SimpleItemAnimator).supportsChangeAnimations = false
//
//        if (lockerList.isEmpty()) {
//
//            binding.rvSelectLocker.visibility = View.GONE
//            binding.tvAccessSharingDescription.visibility = View.GONE
//            binding.tvNoDevicesToSelect.visibility = View.VISIBLE
//            binding. tvNoDevicesToSelect.text = getString(R.string.no_access_sharing_rights)
//        }
//        App.Companion.ref.eventBus.register(this)
//    }
//
//    override fun onPause() {
//        super.onPause()
//        App.Companion.ref.eventBus.unregister(this)
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
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
//        DataCache.log.info("Received unauthorized event, user will now be log outed")
//        val intent = Intent( this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//                val intent = Intent()
//                val packageName = this@AccessSharingListActivity.packageName
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
//        val packageName = this@AccessSharingListActivity.packageName
//        val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//        intent.component = componentName
//
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }

}
