package hr.sil.android.myappbox.activities.collect_parcel

import android.content.ComponentName
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import hr.sil.android.ble.scanner.scan_multi.properties.advv2.common.MPLDeviceStatus
import hr.sil.android.myappbox.adapters.ParcelPickupKeysAdapter
import hr.sil.android.myappbox.data.DeliveryKey
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.cache.DatabaseHandler
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.*
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.core.util.macCleanToBytes
import hr.sil.android.smartlockers.enduser.core.util.macCleanToReal
import hr.sil.android.smartlockers.enduser.databinding.ActivityListOfDeliveriesBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivityLoginBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivityPickupParcelBinding
import hr.sil.android.smartlockers.enduser.events.MPLDevicesUpdatedEvent
import hr.sil.android.smartlockers.enduser.events.UnauthorizedUserEvent
import hr.sil.android.smartlockers.enduser.store.MPLDeviceStore
import hr.sil.android.smartlockers.enduser.store.model.MPLDevice
import hr.sil.android.smartlockers.enduser.util.NotificationHelper
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.DisplayQrCodeActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.LoginActivity
import hr.sil.android.util.general.extensions.format
import hr.sil.android.util.general.extensions.hexToByteArray
import hr.sil.smartlockers.smartlockers.enduser.core.ble.comm.model.LockerFlagsUtil
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.find
import org.jetbrains.anko.toast
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

class PickupParcelActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout) {

    val log = logger()
    private lateinit var macAddress: String

    private val lockerLoaderRunning = AtomicBoolean(false)
    private val startingTime = Date()
    private var exitTime: Date? = null
    private val denyProcedureDuration = 60000L

    private lateinit var keyListAdapter: ParcelPickupKeysAdapter
    private val connecting = AtomicBoolean(false)

    var lockerDisabled: Drawable? = null
    var lockerEnabled: Drawable? = null
    var lockerNotInProximity: Drawable? = null

    var errorColor = 0
    var successColor = 0

    private val MAC_ADDRESS_7_BYTE_LENGTH = 14
    private val MAC_ADDRESS_6_BYTE_LENGTH = 12
    private val MAC_ADDRESS_LAST_BYTE_LENGTH = 2


    private var device: MPLDevice? = null
    private val openedParcels = mutableListOf<String>()

    private lateinit var binding: ActivityPickupParcelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPickupParcelBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar = binding.toolbar
        this.setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false);
        macAddress = intent.getStringExtra("rMacAddress") ?: ""
        log.info("Pickup mpl = $macAddress")

        device = MPLDeviceStore.uniqueDevices[macAddress]
    }

    private fun setUnSuccessOpenView(errorText: String) {
        log.info("Connection failed!")
        binding.llClean.visibility = View.GONE
        binding.ivCirclePickupParcel.clearAnimation()
        binding.ivCirclePickupParcel.setImageDrawable(
            ContextCompat.getDrawable(
                applicationContext,
                R.drawable.progress_stopped
            )
        )
        binding.clOpenPickuParcel.isEnabled = true
        binding.statusText.text = errorText
        binding.statusText.setTextColor(errorColor)
        binding.pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_lock))
    }

    private fun denyOpenProcedure() {
        if (lockerLoaderRunning.compareAndSet(false, true)) {
            GlobalScope.launch(Dispatchers.Default) {
                val time = exitTime?.time ?: 0L
                log.debug("Exit time $time")
                val compare = Math.abs(time - startingTime.time)
                log.debug("compare time $compare")
                var timeForOpen = denyProcedureDuration
                if (compare in 1..denyProcedureDuration) {
                    timeForOpen = denyProcedureDuration - compare
                }
                delay(timeForOpen)
                log.debug("Starting erase procedure")
                withContext(Dispatchers.Main) {
                    binding.forceOpen.visibility = View.GONE
                }
            }
        }
    }

    private fun persistActionOpenKey(id: Int) {
        val deliveryKeys = DatabaseHandler.deliveryKeyDb.get(macAddress)
        if (deliveryKeys == null)
            DatabaseHandler.deliveryKeyDb.put(DeliveryKey(macAddress, listOf(id)))
        else {
            if (!deliveryKeys.keyIds.contains(id)) {
                val listOfIds = deliveryKeys.keyIds.plus(id)
                DatabaseHandler.deliveryKeyDb.put(DeliveryKey(macAddress, listOfIds))
            }
        }
    }

    private fun isOpenDoorPossible(): Boolean {

        var hasUnusedKeys = false
        val keys = DatabaseHandler.deliveryKeyDb.get(macAddress)
        if (keys == null) {
            return device?.activeKeys?.filter { it.purpose != RLockerKeyPurpose.PAH }?.isNotEmpty()
                ?: false
        } else {
            device?.activeKeys?.forEach {
                if (it.purpose != RLockerKeyPurpose.PAH && !keys.keyIds.contains(it.id)) {
                    hasUnusedKeys = true
                    return@forEach
                }
            }
        }

        return device?.isInBleProximity ?: false && device?.hasUserRightsOnLocker() ?: false && hasUnusedKeys

    }

    override fun onStart() {
        super.onStart()

        successColor = with(TypedValue()) {
            this@PickupParcelActivity.theme.resolveAttribute(R.attr.thmDescriptionTextColor, this, true)
            ContextCompat.getColor(this@PickupParcelActivity, resourceId)
        }
        errorColor = with(TypedValue()) {
            this@PickupParcelActivity.theme.resolveAttribute(R.attr.thmErrorTextColor, this, true)
            ContextCompat.getColor(this@PickupParcelActivity, resourceId)
        }

        lockerDisabled = with(TypedValue()) {
            this@PickupParcelActivity.theme.resolveAttribute(R.attr.thmCPLockedDisabled, this, true)
            ContextCompat.getDrawable(this@PickupParcelActivity, resourceId)
        }

        lockerEnabled = with(TypedValue()) {
            this@PickupParcelActivity.theme.resolveAttribute(R.attr.thmCPLockedEnabled, this, true)
            ContextCompat.getDrawable(this@PickupParcelActivity, resourceId)
        }

        lockerNotInProximity = with(TypedValue()) {
            this@PickupParcelActivity.theme.resolveAttribute(R.attr.thmCPLockedNotInProximity, this, true)
            ContextCompat.getDrawable(this@PickupParcelActivity, resourceId)
        }


        binding.forceOpen.setOnClickListener {
            val animationZoomImageView =
                AnimationUtils.loadAnimation(this@PickupParcelActivity, R.anim.rotate_animation)

            binding.ivCirclePickupParcel.setImageDrawable(
                ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.progress_spinning
                )
            )
            binding.ivCirclePickupParcel.animation = animationZoomImageView

            binding.clOpenPickuParcel.isEnabled = false

            binding.statusText.setTextColor(successColor)
            binding.statusText.text =
                this@PickupParcelActivity.getString(R.string.nav_pickup_parcel_connecting)
            log.info("Trying to open communicator")

            var actionSuccessfull = true
            GlobalScope.launch {
                val device = MPLDeviceStore.uniqueDevices[macAddress]
                val mac =
                    if (device?.masterUnitType == RMasterUnitType.SPL) device.macAddress else ""
                val communicator = device?.createBLECommunicator(this@PickupParcelActivity)
                if (communicator?.connect() == true) {
                    openedParcels.forEach {
                        log.info("Requesting force pickup for ${it} ")
                        val bleResponse = communicator.forceOpenDoor(it)

                        withContext(Dispatchers.Main) {
                            if (!bleResponse) {
                                actionSuccessfull = false
                                log.error(bleResponse.toString())
                            } else {
                                log.info("Success delivery on $it")
                            }
                        }
                    }
                    withContext(Dispatchers.Main) {
                        if (actionSuccessfull) {
                            binding.clOpenPickuParcel.isEnabled = false
                            binding.ivCirclePickupParcel.clearAnimation()
                            binding.ivCirclePickupParcel.setImageDrawable(
                                ContextCompat.getDrawable(
                                    applicationContext,
                                    R.drawable.progress_stopped
                                )
                            )

                            val clp = ConstraintLayout.LayoutParams(binding.ivLock.getLayoutParams())
                            clp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                            clp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                            clp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                            clp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                            val sizeInDP = 60

                            val marginInDp = TypedValue.applyDimension(
                                TypedValue.COMPLEX_UNIT_DIP, sizeInDP.toFloat(), resources
                                    .displayMetrics
                            ).toInt()

                            clp.setMargins(0, 0, 0, marginInDp)

                            binding.ivLock.setLayoutParams(clp)
                            binding.statusText.text =
                                this@PickupParcelActivity.getString(R.string.nav_pickup_parcel_content_unlock)
                            binding.pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_unlock))


                            communicator.disconnect()
                        } else {
                            communicator.disconnect()
                            setUnSuccessOpenView(getString(R.string.something_went_wrong))
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        App.ref.toast(R.string.app_generic_error)
                        binding.llClean.visibility = View.GONE

                        binding.clOpenPickuParcel.isEnabled = true

                        binding.ivCirclePickupParcel.clearAnimation()
                        binding.ivCirclePickupParcel.setImageDrawable(
                            ContextCompat.getDrawable(
                                applicationContext,
                                R.drawable.progress_stopped
                            )
                        )
                        binding.statusText.setTextColor(successColor)
                        binding.statusText.text =
                            this@PickupParcelActivity.getString(R.string.nav_pickup_parcel_content_lock)
                        binding.pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_lock))
                        log.error("Error while connecting the device")
                    }
                }
            }
        }

        binding.clOpenPickuParcel.setOnClickListener {

            if (connecting.compareAndSet(false, true)) {

                if( device?.installationType == InstalationType.LINUX ) {
                    val intentQrCodeImage = Intent(this, DisplayQrCodeActivity::class.java)
                    intentQrCodeImage.putExtra("returnToCorrectScreen", 1)
                    intentQrCodeImage.putExtra("rMacAddress", macAddress)
                    startActivity(intentQrCodeImage)
                    finish()
                }
                else {
                    val animationZoomImageView =
                        AnimationUtils.loadAnimation(this@PickupParcelActivity, R.anim.rotate_animation)

                    binding.ivCirclePickupParcel.setImageDrawable(
                        ContextCompat.getDrawable(
                            applicationContext,
                            R.drawable.progress_spinning
                        )
                    )
                    binding.ivCirclePickupParcel.animation = animationZoomImageView

                    binding.clOpenPickuParcel.isEnabled = false

                    binding.statusText.setTextColor(successColor)
                    binding.statusText.text =
                        this@PickupParcelActivity.getString(R.string.nav_pickup_parcel_connecting)
                    log.info("Trying to open communicator")

                    GlobalScope.launch {
                        if (isOpenDoorPossible() && UserUtil.user?.id != null) {

                            val mac = macAddress
                            log.info("Connecting to $mac...")
                            val comunicator =
                                MPLDeviceStore.uniqueDevices[mac]?.createBLECommunicator(this@PickupParcelActivity)

                            if (comunicator?.connect() == true) {
                                log.info("Connected!")

                                var lockerMacAddress = ""
                                var actionSuccessfull = true
                                val keys = keyListAdapter.keys
                                MPLDeviceStore.uniqueDevices[macAddress]?.activeKeys?.filter { it.purpose == RLockerKeyPurpose.DELIVERY || it.purpose == RLockerKeyPurpose.PAF }
                                    ?.forEach {
                                        log.info("Requesting pickup for ${it.lockerMac} , ${UserUtil.user?.id ?: 0}")
                                        val openedMac = it.lockerMac
                                        val bleResponse = comunicator.requestParcelPickup(
                                            it.lockerMac,
                                            UserUtil.user?.id ?: 0
                                        )
                                        withContext(Dispatchers.Main) {
                                            val id = it.id
                                            if (!bleResponse.isSuccessful) {
                                                log.error(bleResponse.toString())
                                                actionSuccessfull = false
                                            } else {
                                                lockerMacAddress = it.lockerMac
                                                openedParcels.add(it.lockerMac)
                                                keyListAdapter.keys.removeAll { it.lockerMac == openedMac }
                                                keyListAdapter.notifyDataSetChanged()
                                                binding.keysList.visibility = View.INVISIBLE
                                                binding.pickupParcelFinish.visibility = View.VISIBLE
                                                binding.forceOpen.visibility = View.VISIBLE
                                                persistActionOpenKey(it.id)
                                                NotificationHelper(App.ref).clearNotification()
                                                denyOpenProcedure()
                                            }
                                        }
                                    }
                                comunicator.disconnect()
                                withContext(Dispatchers.Main) {
                                    if (actionSuccessfull) {
                                        binding.clOpenPickuParcel.isEnabled = false
                                        binding.ivCirclePickupParcel.clearAnimation()
                                        binding.ivCirclePickupParcel.setImageDrawable(
                                            ContextCompat.getDrawable(
                                                applicationContext,
                                                R.drawable.progress_stopped
                                            )
                                        )

                                        val clp = ConstraintLayout.LayoutParams(binding.ivLock.getLayoutParams())
                                        clp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
                                        clp.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
                                        clp.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
                                        clp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                                        val sizeInDP = 60

                                        val marginInDp = TypedValue.applyDimension(
                                            TypedValue.COMPLEX_UNIT_DIP, sizeInDP.toFloat(), resources
                                                .displayMetrics
                                        ).toInt()

                                        clp.setMargins(0, 0, 0, marginInDp)

                                        binding.ivLock.setLayoutParams(clp)
                                        binding.statusText.text =
                                            this@PickupParcelActivity.getString(R.string.nav_pickup_parcel_content_unlock)
                                        binding.pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_unlock))

                                        if (MPLDeviceStore.uniqueDevices[macAddress]?.activeKeys?.size ?: 0 == 1) {
                                            binding.llClean.visibility = View.VISIBLE
                                            setLockerCleaningCheckBoxListener(lockerMacAddress)
                                        }

                                        comunicator.disconnect()
                                        connecting.set(false)
                                    } else {
                                        comunicator.disconnect()
                                        setUnSuccessOpenView(getString(R.string.something_went_wrong))
                                    }
                                }
                            } else {
                                comunicator?.disconnect()
                                withContext(Dispatchers.Main) {
                                    setUnSuccessOpenView(getString(R.string.main_locker_ble_connection_error))
                                }
                            }

                        } else {
                            log.error("Error while connecting the MPL device please check device proximity and user id ${UserUtil.user?.id}")
                            withContext(Dispatchers.Main) {
                                binding.llClean.visibility = View.GONE
                                App.ref.toast(
                                    this@PickupParcelActivity.getString(
                                        R.string.toast_pickup_parcel_error,
                                        UserUtil.user?.id.toString()
                                    )
                                )
                                binding.clOpenPickuParcel.isEnabled = true

                                binding.ivCirclePickupParcel.clearAnimation()
                                binding.ivCirclePickupParcel.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        applicationContext,
                                        R.drawable.progress_stopped
                                    )
                                )
                                binding.statusText.setTextColor(successColor)
                                binding.statusText.text =
                                    this@PickupParcelActivity.getString(R.string.nav_pickup_parcel_content_lock)
                                binding.pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_lock))
                                connecting.set(false)
                            }
                        }

                        connecting.set(false)
                    }
                }
            }
        }

        displayTelemetryOfDevice()
    }

    override fun onResume() {
        super.onResume()
        App.ref.eventBus.register(this)
    }

    override fun onPause() {
        super.onPause()
        App.ref.eventBus.unregister(this)
        exitTime = Date()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: MPLDevicesUpdatedEvent) {
        log.info("Received MPL event")

        if (!connecting.get() && device?.installationType != InstalationType.LINUX) {
            device = MPLDeviceStore.uniqueDevices.values.find { it.macAddress == macAddress }
            setupOpenButton()
        }
    }

    private fun setupOpenButton() {
        displayTelemetryOfDevice()
        /*if (device == null) {
            statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            statusText.setText(R.string.nav_pickup_parcel_content_unlock)
            pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_unlock))
        }*/
        if ( ( device?.installationType == InstalationType.LINUX ) || device?.isInBleProximity == true && isOpenDoorPossible()) {
            binding.clInProximity.visibility = View.VISIBLE
            binding.clNotInProximity.visibility = View.GONE

            binding.pickupParcelTitle.setText(getString(R.string.nav_pickup_parcel_lock))
            binding.statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            binding.statusText.text = this.getString(R.string.nav_pickup_parcel_content_lock)
            binding.clOpenPickuParcel.isEnabled = true
        } else {
            if (device?.isInBleProximity == true) {
                binding.clInProximity.visibility = View.VISIBLE
                binding.clNotInProximity.visibility = View.GONE
                binding.pickupParcelTitle.setText(R.string.nav_pickup_parcel_unlock)
                binding.statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
                binding.statusText.text = this.getString(R.string.nav_pickup_parcel_content_unlock)
            } else {
                binding.clInProximity.visibility = View.GONE
                binding.clNotInProximity.visibility = View.VISIBLE
                binding.statusText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15f)
                binding.statusTextNotInProximity.text =
                    getString(R.string.not_in_proximity_first_description)
                binding.notInProximityLocker.text =
                    getString(R.string.not_in_proximity_second_description, device?.name)
                binding.pickupParcelTitle.setText(R.string.app_generic_enter_ble)
            }
            binding.clOpenPickuParcel.isEnabled = false
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
        log.info("Received unauthorized event, user will now be log outed")
        val intent = Intent( this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }


}
