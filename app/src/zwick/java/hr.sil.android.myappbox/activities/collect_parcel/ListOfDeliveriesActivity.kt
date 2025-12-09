package hr.sil.android.myappbox.activities.collect_parcel

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import hr.sil.android.myappbox.adapters.ListOfDeliveriesAdapter
import hr.sil.android.myappbox.data.LockerKeyWithShareAccess
import hr.sil.android.myappbox.data.ShareAccessKey
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerKeyPurpose
import hr.sil.android.smartlockers.enduser.core.remote.model.RUserAccessRole
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.core.util.macRealToClean
import hr.sil.android.smartlockers.enduser.databinding.ActivityAccessSharingAddUserBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivityListOfDeliveriesBinding
import hr.sil.android.smartlockers.enduser.events.UnauthorizedUserEvent
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class ListOfDeliveriesActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout) 
{

    val log = logger()
    private var listOfDeliveriesAdapter: ListOfDeliveriesAdapter? = null
    private var listOfDeliveries: MutableList<LockerKeyWithShareAccess> = mutableListOf()

    var macAddress = ""
    private lateinit var binding: ActivityListOfDeliveriesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityListOfDeliveriesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        macAddress = intent.getStringExtra("macAddress") ?: ""
    }

    override fun onStart() {
        super.onStart()

        checkIfHasEmailAndMobilePhoneSupport()

        binding.progressDeliveries.visibility = View.VISIBLE
        binding.rvListOfDeliveries.visibility = View.INVISIBLE
        lifecycleScope.launch {

            val data = if( macAddress != "" ) WSUser.getActiveKeys()?.filter { it.purpose != RLockerKeyPurpose.PAH && it.lockerMasterMac == macAddress.macRealToClean()
//                    && isUserPartOfGroup(
//                it.createdForGroup,
//                it.createdForId)
            }?.toMutableList() ?: mutableListOf()
            else WSUser.getActiveKeys()?.filter { it.purpose != RLockerKeyPurpose.PAH }?.toMutableList() ?: mutableListOf()
            log.info("data is: ${data}")
            val remotePaFKeys = WSUser.getActivePaFCreatedKeys()?.toMutableList() ?: mutableListOf()
            for( activeLockerKey in data ) {
                val lockerKeyWithShareAccess = LockerKeyWithShareAccess()
                lockerKeyWithShareAccess.id = activeLockerKey.id
                lockerKeyWithShareAccess.createdById = activeLockerKey.createdById
                lockerKeyWithShareAccess.purpose = activeLockerKey.purpose
                lockerKeyWithShareAccess.createdByName = activeLockerKey.createdByName
                lockerKeyWithShareAccess.masterName = activeLockerKey.masterName
                lockerKeyWithShareAccess.masterAddress = activeLockerKey.masterAddress
                lockerKeyWithShareAccess.trackingNumber = activeLockerKey.trackingNumber
                lockerKeyWithShareAccess.tan = activeLockerKey.tan
                lockerKeyWithShareAccess.installationType = activeLockerKey.keyInstallationtype
                if( activeLockerKey.timeCreated != null )
                    lockerKeyWithShareAccess.timeCreated = activeLockerKey.timeCreated
                else
                    lockerKeyWithShareAccess.timeCreated = ""
                lockerKeyWithShareAccess.lockerSize = activeLockerKey.lockerSize

                for( pafKey in remotePaFKeys ) {

                    if( pafKey.lockerMac == activeLockerKey.lockerMac ) {
                        val shareAccessKey = ShareAccessKey()
                        shareAccessKey.id = pafKey.id
                        shareAccessKey.email = pafKey.createdForEndUserEmail ?: ""
                        lockerKeyWithShareAccess.listOfShareAccess.add(shareAccessKey)
                    }
                }
                listOfDeliveries.add(lockerKeyWithShareAccess)
            }

            withContext(Dispatchers.Main) {

                initializeRecyclerView()
            }
        }
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

    // that were conditions for before, for application like huber, zwick ( because there not all users have permission to access all keys )
    private fun isUserPartOfGroup(createdForGroup: Int?, createdForId: Int?): Boolean {
        return UserUtil.userMemberships.find { it.groupId == createdForGroup && it.role == RUserAccessRole.ADMIN.name } != null || UserUtil.userGroup?.id == createdForGroup || UserUtil.user?.id == createdForId
    }

    override fun onPause() {
        super.onPause()
        listOfDeliveries.clear()
    }

    private fun initializeRecyclerView() {

        binding.progressDeliveries.visibility = View.GONE
        binding.rvListOfDeliveries.visibility = View.VISIBLE

        binding.rvListOfDeliveries.layoutManager =
            LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)

        listOfDeliveriesAdapter = ListOfDeliveriesAdapter(
            listOfDeliveries,
            { partItem: LockerKeyWithShareAccess -> deviceItemClicked(partItem) },
            this@ListOfDeliveriesActivity,
            macAddress
        )
        binding.rvListOfDeliveries.adapter = listOfDeliveriesAdapter
    }

    private fun deviceItemClicked(deliveryParcel: LockerKeyWithShareAccess) {
        /*Toast.makeText(
            this@ListOfDeliveriesActivity,
            "It is not accessable or there are no sharedKeys.",
            Toast.LENGTH_SHORT
        ).show()*/
    }

    override fun onNetworkStateUpdated(available: Boolean) {
        super.onNetworkStateUpdated(available)
        networkAvailable = available
        updateUI()
    }

    override fun onBluetoothStateUpdated(available: Boolean) {
        super.onBluetoothStateUpdated(available)
        bluetoothAvalilable = available
        updateUI()
    }

    override fun onLocationGPSStateUpdated(available: Boolean) {
        super.onLocationGPSStateUpdated(available)
        locationGPSAvalilable = available
        updateUI()
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
                val packageName = this@ListOfDeliveriesActivity.packageName
                val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
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
        val packageName = this@ListOfDeliveriesActivity.packageName
        val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
        intent.component = componentName

        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
        super.onBackPressed()
    }


}
