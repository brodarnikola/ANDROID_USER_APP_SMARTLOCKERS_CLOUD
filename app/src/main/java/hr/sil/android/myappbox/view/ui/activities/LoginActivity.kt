package hr.sil.android.myappbox.view.ui.activities

import android.content.ComponentName
import android.content.Intent
import android.graphics.Paint
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.widget.RelativeLayout
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.remote.WSUser
import hr.sil.android.myappbox.core.remote.model.UserStatus
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.util.connectivity.NetworkChecker
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : BaseActivity() {
 
//    var correctPassword: Boolean = true
//    //var parentJob = Job()
//
//    private var binding: ActivityLoginBinding? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityLoginBinding.inflate(layoutInflater)
//        setContentView(binding?.root)
//
//        setAllHintToUpperCase()
//
//        supportActionBar?.setDisplayHomeAsUpEnabled(false)
//        supportActionBar?.setDisplayShowHomeEnabled(false)
//
//        binding!!.tvShowPasswords.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
//        binding!!.tvForgotPassword.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
//        binding!!.tvRegister.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG)
//
//        val loginTitle = getDrawableAttrValue(R.attr.thmLoginTitle)
//        when {
//            loginTitle != null -> binding!!.tvTitle.visibility = View.VISIBLE
//            else -> binding!!.tvTitle.visibility = View.INVISIBLE
//        }
//
//
//        checkIfHasEmailAndMobilePhoneSupport()
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        binding!!.btnLogin.setOnClickListener {
//            ActionStatusHandler.log.info("Login activity,,Click cached..")
//
//            ActionStatusHandler.log.info("Login activity,, two factory authentification is enabled: ${UserUtil.twoFactoryAuth?.twoFactorAuth}, is email code enabled: ${UserUtil.showMailCodeInput}")
//
//            if (NetworkChecker.isInternetConnectionAvailable()) {
//
//                if (validate()) {
//
//                    binding!!.btnLogin.visibility = View.INVISIBLE
//                    correctPassword = false
//                    binding!!.progressBar.visibility = View.VISIBLE
//
//                    val params = RelativeLayout.LayoutParams(
//                        RelativeLayout.LayoutParams.WRAP_CONTENT,
//                        RelativeLayout.LayoutParams.WRAP_CONTENT
//                    )
//                    params.setMargins(0, dip(-15), 0, 0)
//                    params.below(binding!!.clPassword)
//                    params.centerHorizontally()
//                    binding!!.tvShowPasswords.layoutParams = params
//
//                    lifecycleScope.launch(/*parentJob*/) {
//
//                        val userStatus = UserUtil.loginCheckUserStatus(
//                            binding!!.etEmail.text.toString(),
//                            binding!!.etPassword.text.toString()
//                        )
//                        withContext(Dispatchers.Main) {
//
//                            if( resources.getBoolean(R.bool.scan_advertise_only_tablet)  ) {
//                                UserUtil.showMailCodeInput = WSUser.showMailCodeInput()
//                                ActionStatusHandler.log.info("Login activity,, two factory authentification is enabled: ${UserUtil.twoFactoryAuth?.twoFactorAuth}, is email code enabled: ${UserUtil.showMailCodeInput}")
//                            }
//
//                            if (userStatus == UserStatus.ACTIVE) {
//                                InstallationKeyHandler.key.clear()
//
//                                if( resources.getBoolean(R.bool.has_email_verification) ) {
//
//                                    if( UserUtil.user?.emailCode == null ) {
//                                        checkIfUserHasAcceptedTerms()
//                                    }
//                                    else {
//                                        val intent = Intent()
//                                        val packageName = this@LoginActivity.packageName
//                                        val componentName = ComponentName(packageName, packageName + ".aliasDidNotConfirmEmailRegistration")
//                                        intent.component = componentName
//
//                                        intent.putExtra("isComingFromLogin", true)
//                                        intent.putExtra("email", binding!!.etEmail.text.toString())
//                                        intent.putExtra("password", binding!!.etPassword.text.toString())
//                                        startActivity(intent)
//                                        finish()
//                                    }
//                                }
//                                else {
//                                    checkIfUserHasAcceptedTerms()
//                                }
//                            } else if (userStatus == UserStatus.INVITED) {
//                                val startIntent =
//                                    Intent(this@LoginActivity, TCInvitedUserActivity::class.java)
//                                startIntent.putExtra("email", binding!!.etEmail.text.toString())
//                                startIntent.putExtra("password", binding!!.etPassword.text.toString())
//                                startIntent.putExtra("isComingFromLoginActivity", true)
//                                startActivity(startIntent)
//                                finish()
//                            } else {
//                                ActionStatusHandler.log.info("Error while login device")
//                                App.Companion.ref.toast(R.string.edit_user_validation_current_password_invalid)
//                            }
//                            binding!!.progressBar.visibility = View.GONE
//                            binding!!.btnLogin.visibility = View.VISIBLE
//                        }
//                    }
//                } else {
//
//                    ActionStatusHandler.log.error("Error while registering the user")
//                    if (correctPassword) {
//                        val params = RelativeLayout.LayoutParams(
//                            RelativeLayout.LayoutParams.WRAP_CONTENT,
//                            RelativeLayout.LayoutParams.WRAP_CONTENT
//                        )
//                        params.setMargins(0, dip(-15), 0, 0)
//                        params.below(binding!!.clPassword)
//                        params.centerHorizontally()
//                        binding!!.tvShowPasswords.layoutParams = params
//                    } else {
//                        val params = RelativeLayout.LayoutParams(
//                            RelativeLayout.LayoutParams.WRAP_CONTENT,
//                            RelativeLayout.LayoutParams.WRAP_CONTENT
//                        )
//                        params.setMargins(0, 0, 0, 0)
//                        params.below(binding!!.clPassword)
//                        params.centerHorizontally()
//                        binding!!.tvShowPasswords.layoutParams = params
//                    }
//                }
//            } else {
//                App.Companion.ref.toast(R.string.app_generic_no_network)
//            }
//        }
//
//        binding!!.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
//
//        binding!!.tvShowPasswords.setOnTouchListener { view, event ->
//            when (event.action) {
//                MotionEvent.ACTION_DOWN -> binding!!.etPassword.setInputType(InputType.TYPE_CLASS_TEXT)
//
//                MotionEvent.ACTION_UP -> binding!!.etPassword.setInputType(InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD)
//            }
//            true
//        }
//
//        if( SettingsHelper.usernameLogin != "" ) {
//            binding!!.etEmail.text = Editable.Factory.getInstance().newEditable( SettingsHelper.usernameLogin )
//        }
//
//        binding!!.tvForgotPassword.setOnClickListener {
//            val startIntent = Intent(this@LoginActivity, PasswordRecoveryActivity::class.java)
//            startActivity(startIntent)
//            finish()
//        }
//
//        binding!!.llRegister.setOnClickListener {
//
//            GlobalScope.launch {
//                if( resources.getBoolean(R.bool.scan_advertise_only_tablet)  ) {
//                    UserUtil.twoFactoryAuth = WSUser.getTwoFactoryAuthForRegisterinCPLUsers()
//                    ActionStatusHandler.log.info("Two factory authentification is enabled: ${UserUtil.twoFactoryAuth?.twoFactorAuth}")
//                }
//            }
//
//            val intent = Intent()
//            val packageName = this@LoginActivity.packageName
//            val componentName = ComponentName(packageName, packageName + ".aliasRegistration")
//            intent.component = componentName
//
//            intent.putExtra("isComingFromLoginActivity", false)
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        if( resources.getBoolean(R.bool.has_mobile_and_email_support) ) {
//            binding!!.ivSupportImage.visibility = View.VISIBLE
//            binding!!.ivSupportImage.setOnClickListener {
//                val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//                supportEmailPhoneDialog.show(
//                    supportFragmentManager,
//                    ""
//                )
//            }
//        }
//        else
//            binding!!.ivSupportImage.visibility = View.GONE
//    }
//
//    private fun checkIfUserHasAcceptedTerms() {
//        if (UserUtil.user?.hasAcceptedTerms == false) {
//            SettingsHelper.userPasswordWithoutEncryption =
//                binding!!.etPassword.text.toString()
//            val startIntent = Intent(
//                this@LoginActivity,
//                TCInvitedUserActivity::class.java
//            )
//            startIntent.putExtra("email", binding!!.etEmail.text.toString())
//            startIntent.putExtra("password", binding!!.etPassword.text.toString())
//            startIntent.putExtra("goToMainActivity", true)
//            startIntent.putExtra("isComingFromLoginActivity", true)
//            startActivity(startIntent)
//            finish()
//        } else {
//            SettingsHelper.userPasswordWithoutEncryption =
//                binding!!.etPassword.text.toString()
//            val intent = Intent()
//            val packageName = this@LoginActivity.packageName
//            val componentName = ComponentName(
//                packageName,
//                packageName + ".aliasMainActivity"
//            )
//            intent.component = componentName
//
//            startActivity(intent)
//            finish()
//        }
//    }
//
//    private fun setAllHintToUpperCase() {
//        binding!!.tilEmail.hint = resources.getString(R.string.app_generic_email).uppercase()
//        binding!!.tilPassword.hint = resources.getString(R.string.app_generic_password).uppercase()
//    }
//
//    private fun validate(): Boolean {
//        if (!validateEmail(binding!!.tilEmail, binding!!.etEmail)) return false
//
//        if (!validateNewPassword())
//            return false
//
//        return true
//    }
//
//    private fun validateNewPassword(): Boolean {
//        return validateEditText(binding!!.tilPassword, binding!!.etPassword) { newPassword ->
//            when {
//                newPassword.isBlank() -> {
//                    correctPassword = false
//                    ValidationResult.INVALID_PASSWORD_BLANK
//                }
//                newPassword.length < 6 -> {
//                    correctPassword = false
//                    ValidationResult.INVALID_PASSWORD_MIN_6_CHARACTERS
//                }
//                else -> {
//                    correctPassword = true
//                    ValidationResult.VALID
//                }
//            }
//        }
//    }
//
//    private fun getDrawableAttrValue(attr: Int): String? {
//        val attrArray = intArrayOf(attr)
//        val typedArray = obtainStyledAttributes(attrArray)
//        val result = typedArray.getString(0)
//        typedArray.recycle()
//        return result
//    }

    /*override fun onPause() {
        super.onPause()
        parentJob.cancel()
    }*/

}
