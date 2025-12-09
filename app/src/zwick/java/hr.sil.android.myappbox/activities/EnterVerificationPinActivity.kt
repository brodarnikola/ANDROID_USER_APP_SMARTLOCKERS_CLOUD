package hr.sil.android.myappbox.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.dialog.DisableRequestNewPinVerificationDialog
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.remote.model.PinRegistrationCodeStatus
import hr.sil.android.smartlockers.enduser.core.util.formatToViewDateTimeDefaults
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.ActivityEnterVerificationPinBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivitySettingsUserDetailsBinding
import hr.sil.android.smartlockers.enduser.util.backend.UserUtil
import hr.sil.android.smartlockers.enduser.util.connectivity.NetworkChecker
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast
import java.text.SimpleDateFormat
import java.util.*

class EnterVerificationPinActivity : BaseActivity() {

    private val log = logger()
    private val hoursPassedSinceLastSendingOfRegistrationCode = 72 // 72 hours, or 3 days
    private var allowNewRequestForVerificationPin = false
    private var lastDateAndTimeOfSendingVerificationPin = ""

    private lateinit var binding: ActivityEnterVerificationPinBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEnterVerificationPinBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        checkVerificationPinStatus()

        sendVerificationPinListener()

        requestNewPinVerificationListener()

        checkIfHasEmailAndMobilePhoneSupport()
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

    private fun checkVerificationPinStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = WSUser.getPinRegistrationCodeInfo()
            withContext(Dispatchers.Main) {
                if( result?.code() == 200 ) {
                    val responsePinStatus = result.body()
                    val checkHoursPassed = if( responsePinStatus?.status == PinRegistrationCodeStatus.SENT ) {
                        daysPassed(responsePinStatus.timeSent.formatToViewDateTimeDefaults())
                    }
                    else if( responsePinStatus?.status == PinRegistrationCodeStatus.RESENT ) {
                        daysPassed(responsePinStatus.timeResent.formatToViewDateTimeDefaults())
                    }
                    else if( responsePinStatus?.status == PinRegistrationCodeStatus.RESEND_REQUESTED ) {
                        daysPassed(responsePinStatus.timeResendRequested.formatToViewDateTimeDefaults())
                    }
                    else 0

                    lastDateAndTimeOfSendingVerificationPin = if( responsePinStatus?.status == PinRegistrationCodeStatus.SENT )
                        responsePinStatus.timeSent.formatToViewDateTimeDefaults()
                    else if( responsePinStatus?.status == PinRegistrationCodeStatus.RESENT )
                        responsePinStatus.timeResent.formatToViewDateTimeDefaults()
                    else if( responsePinStatus?.status == PinRegistrationCodeStatus.RESEND_REQUESTED )
                        responsePinStatus.timeResendRequested.formatToViewDateTimeDefaults()
                    else ""

                    log.info("responsePinStatus ${responsePinStatus?.status} ,, time sent : ${responsePinStatus?.timeSent} ,, time resent : ${responsePinStatus?.timeResent} ,, checkHoursPassed: ${checkHoursPassed} ")
                    if( responsePinStatus?.status == PinRegistrationCodeStatus.PENDING || checkHoursPassed > hoursPassedSinceLastSendingOfRegistrationCode ) {
                        allowNewRequestForVerificationPin = true
                    }
                    else
                        allowNewRequestForVerificationPin = false
                }
                else
                    allowNewRequestForVerificationPin = false
            }
        }
    }

    private fun daysPassed(dateTime: String): Int {
        val date = Calendar.getInstance()
        date.time = SimpleDateFormat(
            "dd/MM/yyyy HH:mm:ss",
            Locale.getDefault()
        ).parse(dateTime) // Parse into Date object
        val now = Calendar.getInstance() // Get time now
        val differenceInMillis = now.timeInMillis - date.timeInMillis
        val differenceInHours =
            differenceInMillis / 1000L / 60L / 60L // Divide by millis/sec, secs/min, mins/hr
        return differenceInHours.toInt()
    }

    private fun sendVerificationPinListener() {
        binding.btnSendVerificationPin.setOnClickListener {

            if (NetworkChecker.isInternetConnectionAvailable()) {
                if( validatePinRegistrationCode()) {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnSendVerificationPin.visibility = View.GONE
                    lifecycleScope.launch(Dispatchers.IO) {
                        val result = WSUser.sendPinRegistrationCode(binding.etPin.text.toString())
                        if( result?.response == "CONFIRMED" ) {
                            val responseUser = WSUser.getUserInfo()
                            UserUtil.user = responseUser
                        }
                        withContext(Dispatchers.Main) {
                            if (result?.response == "CONFIRMED" ) {
                                App.ref.toast(getString(R.string.app_generic_success))
                                val intent = Intent(this@EnterVerificationPinActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else {
                                App.ref.toast(getString(R.string.app_generic_error))
                                binding.progressBar.visibility = View.GONE
                                binding.btnSendVerificationPin.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
            else
                App.ref.toast(R.string.app_generic_no_network)
        }
    }

    private fun validatePinRegistrationCode(): Boolean {
        if( binding.etPin.text.toString() == "" ) {
            binding.tvEmptyPinError.visibility = View.VISIBLE
            binding.tvEmptyPinError.text = getString(R.string.edit_user_validation_blank_fields_exist)
            return false
        }
        else {
            binding.tvEmptyPinError.visibility = View.INVISIBLE
            return true
        }
    }

    private fun requestNewPinVerificationListener() {
        binding.btnRequestNewVerificationPin.setOnClickListener {

            if (NetworkChecker.isInternetConnectionAvailable()) {
                if( allowNewRequestForVerificationPin ) {
                    binding.progressBarRequestNew.visibility = View.VISIBLE
                    binding.btnRequestNewVerificationPin.visibility = View.GONE
                    lifecycleScope.launch(Dispatchers.IO) {

                        val result = WSUser.sendNewPinRegistrationCode()
                        withContext(Dispatchers.Main) {
                            if (result) {
                                App.ref.toast(getString(R.string.app_generic_success))
                                binding.progressBarRequestNew.visibility = View.GONE
                                binding.btnRequestNewVerificationPin.visibility = View.VISIBLE
                                allowNewRequestForVerificationPin = false
                                val currentTime = Calendar.getInstance().time
                                lastDateAndTimeOfSendingVerificationPin = currentTime.formatToViewDateTimeDefaults()
                            } else {
                                App.ref.toast(getString(R.string.app_generic_error))
                                binding.progressBarRequestNew.visibility = View.GONE
                                binding.btnRequestNewVerificationPin.visibility = View.VISIBLE
                            }
                        }
                    }
                }
                else {
                    val disableRequestNewPinVerificationDialog =
                        DisableRequestNewPinVerificationDialog(lastDateAndTimeOfSendingVerificationPin)
                    disableRequestNewPinVerificationDialog.show(
                        supportFragmentManager,
                        ""
                    )
                }
            }
            else
                App.ref.toast(R.string.app_generic_no_network)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(baseContext, MainActivity::class.java)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(baseContext, MainActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
        super.onBackPressed()
    }

}
