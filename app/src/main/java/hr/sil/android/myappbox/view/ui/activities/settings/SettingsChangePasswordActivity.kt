package hr.sil.android.myappbox.view.ui.activities.settings

import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.InputType
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.util.connectivity.NetworkChecker
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsChangePasswordActivity //: BaseActivity(0, R.id.no_internet_layout)
{

//    private var wrongPassword: Boolean = false
//    val log = logger()
//
//    private lateinit var binding: ActivitySettingsChangePasswordBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsChangePasswordBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setAllHintToUpperCase()
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
//
//        binding.btnChangePassword.setOnClickListener {
//
//            if (NetworkChecker.isInternetConnectionAvailable()) {
//                if (validate()) {
//                    binding.progressBar.visibility = View.VISIBLE
//
//                    val params = ConstraintLayout.LayoutParams(
//                        ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                        ConstraintLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    params.setMargins(0, dip(-20), 0, 0)
//                    params.topToBottom = R.id.clConfirmPassword
//                    params.leftToLeft = ConstraintSet.PARENT_ID
//                    params.rightToRight = ConstraintSet.PARENT_ID
//                    binding.tvShowPasswords.layoutParams = params
//
//                    lifecycleScope.launch {
//
//                       val result = UserUtil.passwordUpdate(oldPassword = binding.currentPassworEditText.text.toString(),
//                           newPassword = binding.newPasswordEditText.text.toString())
//
//                        if( result ) {
//                            UserUtil.updateUserHash(UserUtil.user?.email, binding.newPasswordEditText.text.toString())
//                            SettingsHelper.userPasswordWithoutEncryption = binding.newPasswordEditText.text.toString()
//                        }
//
//                        withContext(Dispatchers.Main) {
//                            if (UserUtil.user?.email != null && result ) {
//                                App.Companion.ref.toast(baseContext.getString(R.string.nav_settings_password_update_success))
//
//                                val intent = Intent(
//                                    this@SettingsChangePasswordActivity,
//                                    SettingsActivity::class.java
//                                )
//                                startActivity(intent)
//                                finish()
//                            } else {
//                                log.error("Error while updating user password")
//                            }
//                        }
//                    }
//                } else {
//                    if (wrongPassword) {
//
//                        val params = ConstraintLayout.LayoutParams(
//                            ConstraintLayout.LayoutParams.WRAP_CONTENT,
//                            ConstraintLayout.LayoutParams.WRAP_CONTENT
//                        )
//                        params.setMargins(0, 0, 0, 0)
//                        params.topToBottom = R.id.clConfirmPassword
//                        params.leftToLeft = ConstraintSet.PARENT_ID
//                        params.rightToRight = ConstraintSet.PARENT_ID
//                        binding.tvShowPasswords.layoutParams = params
//                    }
//                }
//
//                binding.progressBar.visibility = View.GONE
//            } else {
//                App.Companion.ref.toast(R.string.app_generic_no_network)
//            }
//        }
//
//        binding.currentPassworEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//        binding.newPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//        binding.retypePasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//
//        binding.tvShowPasswords.setOnTouchListener { _, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> {
//                    binding.currentPassworEditText.inputType = InputType.TYPE_CLASS_TEXT
//                    binding.newPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT
//                    binding.retypePasswordEditText.inputType = InputType.TYPE_CLASS_TEXT
//                }
//
//                MotionEvent.ACTION_UP -> {
//                    binding.currentPassworEditText.inputType =
//                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                    binding.newPasswordEditText.inputType =
//                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                    binding.retypePasswordEditText.inputType =
//                        InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//                }
//            }
//            true
//        }
//
//        binding.tvShowPasswords.paintFlags = Paint.UNDERLINE_TEXT_FLAG
//
//        checkIfHasEmailAndMobilePhoneSupport()
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
//        binding.currentPassword.hint = resources.getString(R.string.current_password).uppercase()
//        binding.newPassword.hint = resources.getString(R.string.new_password).uppercase()
//        binding.retypePassword.hint = resources.getString(R.string.retype_password).uppercase()
//    }
//
//    private fun validate(): Boolean {
//
//        var validated = true
//        if (!validateOldPassword()) {
//            validated = false
//        }
//        if (!validateNewPassword()) {
//            validated = false
//            wrongPassword = true
//        }
//        if (!validateRetypePassword()) {
//            validated = false
//            wrongPassword = true
//        }
//        return validated
//    }
//
//    private fun validateOldPassword(): Boolean {
//        return validateEditText(binding.currentPassword, binding.currentPassworEditText) { oldPassword ->
//            when {
//                oldPassword.isEmpty() -> {
//                    binding.tvCurrentPassword.visibility = View.VISIBLE
//                    binding.tvCurrentPassword.setText(R.string.edit_user_validation_blank_fields_exist)
//                    ValidationResult.INVALID_PASSWORD_BLANK
//                }
//                oldPassword.length < 6 -> {
//                    binding.tvCurrentPassword.visibility = View.VISIBLE
//                    binding.tvCurrentPassword.setText(R.string.edit_user_validation_password_min_6_characters)
//                    ValidationResult.INVALID_PASSWORD_MIN_6_CHARACTERS
//                }
//                oldPassword != SettingsHelper.userPasswordWithoutEncryption -> {
//                    binding.tvCurrentPassword.visibility = View.VISIBLE
//                    binding.tvCurrentPassword.setText(R.string.edit_user_validation_current_password_invalid)
//                    ValidationResult.INVALID_OLD_PASSWORD
//                }
//                else -> {
//                    binding.tvCurrentPassword.visibility = View.GONE
//                    ValidationResult.VALID
//                }
//            }
//        }
//    }
//
//    private fun validateNewPassword(): Boolean {
//        return validateEditText(binding.newPassword, binding.newPasswordEditText) { newPassword ->
//
//            when {
//                newPassword.isEmpty() -> {
//                    binding.tvNewPassword.visibility = View.VISIBLE
//                    binding.tvNewPassword.setText(R.string.edit_user_validation_blank_fields_exist)
//                    ValidationResult.INVALID_PASSWORD_BLANK
//                }
//                newPassword.length < 6 -> {
//                    binding.tvNewPassword.visibility = View.VISIBLE
//                    binding.tvNewPassword.setText(R.string.edit_user_validation_password_min_6_characters)
//                    ValidationResult.INVALID_PASSWORD_MIN_6_CHARACTERS
//                }
//                else -> {
//                    binding.tvNewPassword.visibility = View.GONE
//                    ValidationResult.VALID
//                }
//            }
//        }
//    }
//
//    private fun validateRetypePassword(): Boolean {
//        return validateEditText(binding.retypePassword, binding.retypePasswordEditText) { repeatPassword ->
//            val newPassword = binding.newPasswordEditText.text.toString().trim()
//            if (repeatPassword != newPassword) {
//                binding.tvConfirmPassword.visibility = View.VISIBLE
//                binding.tvConfirmPassword.setText(R.string.edit_user_validation_passwords_do_not_match)
//                ValidationResult.INVALID_PASSWORDS_DO_NOT_MATCH
//            } else {
//                binding.tvConfirmPassword.visibility = View.GONE
//                ValidationResult.VALID
//            }
//        }
//    }
//
//    override fun onNetworkStateUpdated(available: Boolean) {
//        super.onNetworkStateUpdated(available)
//        networkAvailable = available
//        updateUI()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                val intent = Intent(baseContext, SettingsActivity::class.java)
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
//        val intent = Intent(baseContext, SettingsActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }

}
