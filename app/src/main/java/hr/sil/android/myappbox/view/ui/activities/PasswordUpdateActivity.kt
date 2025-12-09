package hr.sil.android.myappbox.view.ui.activities


import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.util.connectivity.NetworkChecker
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordUpdateActivity : BaseActivity() {

//    var wrongPassword: Boolean = false
//    val log = logger()
//
//    private lateinit var binding: ActivityPasswordUpdateBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityPasswordUpdateBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//        setAllHintToUpperCase()
//
//        val toolbar: Toolbar = binding.toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true);
//        supportActionBar?.setDisplayShowHomeEnabled(true);
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        binding.tvShowPasswords.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
//
//        binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//
//        val email = intent.getStringExtra("EMAIL")
//        log.info("Password update activity, email is: ${email}")
//        binding.btnPasswordUpdate.setOnClickListener {
//
//            if (NetworkChecker.isInternetConnectionAvailable()) {
//                if (validate()) {
//
//                    binding.progressBar.visibility = View.VISIBLE
//                    lifecycleScope.launch {
//
//                        val result = email != null && submitResetPass(email)
//                        withContext(Dispatchers.Main) {
//                            when {
//                                result -> showCustomDialog()
//                                else -> showErrorWrongPin()
//                            }
//                            binding.progressBar.visibility = View.GONE
//                        }
//                    }
//                }
//            } else {
//                App.Companion.ref.toast(R.string.app_generic_no_network)
//            }
//        }
//
//        binding.tvShowPasswords.setOnTouchListener { view, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT
//                }
//                MotionEvent.ACTION_UP -> {
//                    binding.etPassword.inputType =
//                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                }
//            }
//            true
//        }
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
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
//    private fun setAllHintToUpperCase() {
//        binding.tilPassword.hint = resources.getString(R.string.registration_password).uppercase()
//        binding.tilPin.hint = resources.getString(R.string.reset_password_pin).uppercase()
//    }
//
//    private fun showCustomDialog() {
//        val passwordUpdateSuccess = PasswordUpdateSuccessDialog(this@PasswordUpdateActivity)
//        passwordUpdateSuccess.show(this@PasswordUpdateActivity.supportFragmentManager, "")
//    }
//
//    private fun validate(): Boolean {
//
//        var validated = true
//        if (!validateNewPassword()) {
//            validated = false
//        }
//        if (!validateNewPin()) {
//            validated = false
//            wrongPassword = true
//        }
//        return validated
//    }
//
//    private fun validateNewPassword(): Boolean {
//        return validateEditText( binding.tilPassword,  binding.etPassword) { newPassword ->
//            when {
//                newPassword.isBlank() -> ValidationResult.INVALID_PASSWORD_BLANK
//                newPassword.length < 6 -> ValidationResult.INVALID_PASSWORD_MIN_6_CHARACTERS
//                else -> ValidationResult.VALID
//            }
//        }
//    }
//
//    private fun validateNewPin(): Boolean {
//        return validateEditText( binding.tilPin,  binding.etPin) { pickupPin ->
//            when {
//                pickupPin.isEmpty() -> ValidationResult.INVALID_PASSWORD_BLANK
//                else -> ValidationResult.VALID
//            }
//        }
//    }
//
//    private fun showErrorWrongPin(): Boolean {
//
//        binding.tilPassword.error = null
//        return validateEditText( binding.tilPin,  binding.etPin) { pickupPin ->
//            when {
//                pickupPin.isNotEmpty() -> ValidationResult.INVALID_PIN
//                else -> ValidationResult.VALID
//            }
//        }
//    }
//
//    suspend private fun submitResetPass(email: String): Boolean {
//        return UserUtil.passwordReset(
//            email = email,
//            passwordCode =  binding.etPin.text.toString(),
//            password =  binding.etPassword.text.toString()
//        )
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                finish()
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }


}
