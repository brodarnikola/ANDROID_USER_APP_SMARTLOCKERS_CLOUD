package hr.sil.android.myappbox.activities.send_parcel

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.*
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.RAvailableLockerSize
import hr.sil.android.smartlockers.enduser.core.remote.model.RLockerSize
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.ActivityLoginBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivityPickupParcelBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivitySelectParcelSizeBinding
import hr.sil.android.smartlockers.enduser.events.MPLDevicesUpdatedEvent
import hr.sil.android.smartlockers.enduser.events.UnauthorizedUserEvent
import hr.sil.android.smartlockers.enduser.store.MPLDeviceStore
import hr.sil.android.smartlockers.enduser.store.model.MPLDevice
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.SupportEmailPhoneDialog
import hr.sil.android.smartlockers.enduser.view.ui.activities.sendparcel.GeneratedPinDialog
import hr.sil.android.smartlockers.enduser.view.ui.activities.sendparcel.PinManagmentDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.toast
import java.util.*

class SelectParcelSizeActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout) {

    var availableLockers: List<RAvailableLockerSize> = listOf()
    var selectedLockerSize = RLockerSize.UNKNOWN
    var macAddress: String = ""
    var device: MPLDevice? = null
    val log = logger()

    private var coroutineJob: Job? = null

    private lateinit var binding: ActivitySelectParcelSizeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectParcelSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        macAddress = intent.getStringExtra("rMacAddress") ?: ""
        device = MPLDeviceStore.uniqueDevices[macAddress]
    }

    override fun onStart() {
        super.onStart()

        checkIfHasEmailAndMobilePhoneSupport()

        setupOpenButton()

        initButtons()
    }

    private fun lockerSizeSetOnClickListeners() {
        binding.rlLockerXS.setOnClickListener { v ->
            if (availableLockers.filter { it.size == RLockerSize.XS && it.count > 0 }
                    .isNotEmpty() ) {
                selectedLockerSize = RLockerSize.XS
                if (device?.pinManagementAllowed == false) {
                    displayLockerPinActivity(selectedLockerSize)
                } else {
                    device?.let { displayPinManagmentDialog(it) }
                }
            }
        }

        binding.rlLockerS.setOnClickListener { v ->
            if (availableLockers.filter { it.size == RLockerSize.S && it.count > 0 }
                    .isNotEmpty() ) {
                selectedLockerSize = RLockerSize.S
                if (device?.pinManagementAllowed == false) {
                    displayLockerPinActivity(selectedLockerSize)
                } else {
                    device?.let { displayPinManagmentDialog(it) }
                }
            }
        }

        binding.rlLockerM.setOnClickListener { v ->
            if (availableLockers.filter { it.size == RLockerSize.M && it.count > 0 }
                    .isNotEmpty()) {
                selectedLockerSize = RLockerSize.M
                if (device?.pinManagementAllowed == false) {
                    displayLockerPinActivity(selectedLockerSize)
                } else {
                    device?.let { displayPinManagmentDialog(it) }
                }
            }
        }

        binding.rlLockerL.setOnClickListener { v ->
            if (availableLockers.filter { it.size == RLockerSize.L && it.count > 0 }
                    .isNotEmpty() ) {
                selectedLockerSize = RLockerSize.L
                if (device?.pinManagementAllowed == false) {
                    displayLockerPinActivity(selectedLockerSize)
                } else {
                    device?.let { displayPinManagmentDialog(it) }
                }
            }
        }

        binding.rlLockerXL.setOnClickListener { v ->
            if (availableLockers.filter { it.size == RLockerSize.XL && it.count > 0 }
                    .isNotEmpty() ) {
                selectedLockerSize = RLockerSize.XL
                if (device?.pinManagementAllowed == false) {
                    displayLockerPinActivity(selectedLockerSize)
                } else {
                    device?.let { displayPinManagmentDialog(it) }
                }
            }
        }
    }

    private fun initButtons() {
        log.info("Initialisation of the buttons")
        coroutineJob = lifecycleScope.launch {

            availableLockers = WSUser.getAvailableLockerSizes(
                    MPLDeviceStore.uniqueDevices[macAddress]?.masterUnitId ?: 0
                )
                    ?: listOf()

            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.GONE
                setupOpenButton()

                var counter = 0

                // this is bottom right image
                initDimensionImage()
                lockerSizeSetOnClickListeners()

                val lockerDisabled = with(TypedValue()) {
                    theme.resolveAttribute(R.attr.thmSPLockerDisabled, this, true)
                    ContextCompat.getDrawable(this@SelectParcelSizeActivity, resourceId)
                }

                val lockerEnabled = with(TypedValue()) {
                    theme.resolveAttribute(R.attr.thmSPLockerAvailable, this, true)
                    ContextCompat.getDrawable(this@SelectParcelSizeActivity, resourceId)
                }

                counter += handleAccessability(
                    binding.xsImage,
                    binding.xsAvailableText,
                    availableLockers.filter { it.size == RLockerSize.XS && it.count > 0 },
                    lockerDisabled,
                    lockerEnabled
                )
                counter += handleAccessability(
                    binding.sImage,
                    binding.sAvailableText,
                    availableLockers.filter { it.size == RLockerSize.S && it.count > 0 },
                    lockerDisabled,
                    lockerEnabled
                )
                counter += handleAccessability(
                    binding.mImage,
                    binding.mAvailableText,
                    availableLockers.filter { it.size == RLockerSize.M && it.count > 0 },
                    lockerDisabled,
                    lockerEnabled
                )
                counter += handleAccessability(
                    binding.lImage,
                    binding.lAvailableText,
                    availableLockers.filter { it.size == RLockerSize.L && it.count > 0 },
                    lockerDisabled,
                    lockerEnabled
                )
                counter += handleAccessability(
                    binding.xlImage,
                    binding.xlAvailableText,
                    availableLockers.filter { it.size == RLockerSize.XL && it.count > 0 },
                    lockerDisabled,
                    lockerEnabled
                )
            }
        }
    }

    private fun handleAccessability(
        pickedButton: ImageView,
        pickedButtonText: TextView,
        availableLockers: List<RAvailableLockerSize>,
        unselectedDrawable: Drawable?,
        selectedDrawable: Drawable?
    ): Int {

        val count = availableLockers.firstOrNull()?.count ?: 0
        if (availableLockers.isEmpty()) {
            //pickedButton.setImageDrawable(ContextCompat.getDrawable(applicationContext, unselectedDrawable))
            pickedButton.setImageDrawable(unselectedDrawable)
            //pickedButton.backgroundDrawable = ContextCompat.getDrawable(this, unselectedDrawable)
            pickedButton.isEnabled = false
        } else {
            pickedButton.setImageDrawable(selectedDrawable)
            //pickedButton.setImageDrawable(ContextCompat.getDrawable(applicationContext,  R.drawable.btn_parcel_size))
            pickedButton.isEnabled = true
        }
        pickedButtonText.text = this.getString(R.string.send_parcel_available, count.toString())
        return count
    }

    override fun onResume() {
        super.onResume()
        App.ref.eventBus.register(this)
    }

    override fun onPause() {
        super.onPause()
        if(coroutineJob?.isActive != false)
            coroutineJob?.cancel()
        App.ref.eventBus.unregister(this)
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: MPLDevicesUpdatedEvent) {
        log.info("Received MPL event $macAddress")

        device = MPLDeviceStore.uniqueDevices.values.find { it.macAddress == macAddress }
        setupOpenButton()
    }

    private fun setupOpenButton() {
        //displayTelemetryOfDevice()
        if (device?.isInBleProximity == true ) {
            binding.scrollView.visibility = View.VISIBLE
            binding.clNotInProximity.visibility = View.GONE
            binding.tvSendParcelTitle.setText(R.string.app_generic_send_parcel)
        } else {
            binding.clNotInProximity.visibility = View.VISIBLE
            binding.scrollView.visibility = View.GONE
            binding.tvSendParcelTitle.setText(R.string.app_generic_enter_ble)
            binding.notInProximityLocker.visibility = View.VISIBLE
            //notInProximityLocker.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
            binding.notInProximityLocker.text = getString(R.string.not_in_proximity_second_description, device?.name)
        }
        binding.clNotInProximity.isEnabled = false
    }

    private fun displayPinManagmentDialog(parcelLocker: MPLDevice) {
        val pinManagmentDialog =
            PinManagmentDialog(parcelLocker, selectedLockerSize.name, this@SelectParcelSizeActivity)
        log.info("Selected locker size name is: ${selectedLockerSize.name} , code is ${selectedLockerSize.code}")
        pinManagmentDialog.show(this@SelectParcelSizeActivity.supportFragmentManager, "")
    }

    private fun displayLockerPinActivity(selectedLockerSize: RLockerSize) {

        var availableLocker = RAvailableLockerSize(RLockerSize.XS, 0)
        for (items in availableLockers) {

            if (items.size == selectedLockerSize && items.count > 0) {
                availableLocker = items
                break
            }
        }

        if (availableLocker.count > 0) {

            val parcelLocker = MPLDeviceStore.uniqueDevices[macAddress]

            val generatedPinDialog = GeneratedPinDialog(parcelLocker, selectedLockerSize.name)
            generatedPinDialog.show(this@SelectParcelSizeActivity.supportFragmentManager, "")
        } else {
            App.ref.toast(getString(R.string.locker_not_empty))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
        log.info("Received unauthorized event, user will now be log outed")
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

}
