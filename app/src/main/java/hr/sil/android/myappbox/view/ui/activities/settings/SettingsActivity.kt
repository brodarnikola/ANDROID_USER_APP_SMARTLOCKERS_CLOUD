package hr.sil.android.myappbox.view.ui.activities.settings

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.DisplayQrCodeActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog

class SettingsActivity : BaseActivity(0, 0) {

//    private lateinit var binding: ActivitySettingsBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        initializeView()
//        handleClickEvents()
//
//        binding.tvCurrentVersion.text =
//            getString(R.string.nav_settings_app_version, this.getString(R.string.app_version))
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
//    private fun initializeView() {
//
//        hasMobileSupportForCalls()
//
//        val settingsFirstSubtitle = getStringAttrValue(R.attr.thmSettingsFirstSubtitle)
//        when {
//            settingsFirstSubtitle != null -> binding.tvSettings.visibility = View.VISIBLE
//            else -> binding.tvSettings.visibility = View.GONE
//        }
//
//        val settingsAccount = getStringAttrValue(R.attr.thmSettingsAccount)
//        when {
//            settingsAccount != null -> binding.tvAccount.visibility = View.VISIBLE
//            else -> binding.tvAccount.visibility = View.INVISIBLE
//        }
//
//        val settingsSupport = getStringAttrValue(R.attr.thmSettingsSupport)
//        when {
//            settingsSupport != null -> binding.tvSupport.visibility = View.VISIBLE
//            else -> binding.tvSupport.visibility = View.INVISIBLE
//        }
//    }
//
//    private fun hasMobileSupportForCalls() {
//        if( resources.getBoolean(R.bool.has_mobile_support_for_calls) ) {
//            binding.lnCallUs.root.visibility = View.VISIBLE
//            binding.lnCallUs.root.setOnClickListener {
//                val phone = BuildConfig.MOBILE_PHONE_SUPPORT
//                val intent = Intent(
//                    Intent.ACTION_DIAL,
//                    Uri.fromParts("tel", phone, null)
//                )
//                startActivity(intent)
//            }
//        }
//        else {
//            binding.lnCallUs.root.visibility = View.GONE
//        }
//    }
//
//    private fun getStringAttrValue(attr: Int): String? {
//        val attrArray = intArrayOf(attr)
//        val typedArray = obtainStyledAttributes(attrArray)
//        val result = typedArray.getString(0)
//        typedArray.recycle()
//        return result
//    }
//
//    private fun handleClickEvents() {
//
//        binding.lnNotification.root.setOnClickListener {
//            val intent = intentFor<SettingsNotificationActivity>()
//            startActivity(intent)
//            finish()
//        }
//
//        binding.lnLanguge.root.setOnClickListener {
//            val intent = intentFor<SettingsLanguageActivity>()
//            startActivity(intent)
//            finish()
//        }
//
//        binding.lnUserDetails.root.setOnClickListener {
//            val intent = Intent()
//            val packageName = this@SettingsActivity.packageName
//            val componentName =
//                ComponentName(packageName, packageName + ".aliasSettingsUserDetailsActivity")
//            intent.component = componentName
//
//            startActivity(intent)
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//            finish()
//        }
//
//        binding.lnQrCode.root.setOnClickListener {
//            val intent = intentFor<DisplayQrCodeActivity>()
//            intent.putExtra("returnToCorrectScreen", 2)
//            startActivity(intent)
//            finish()
//        }
//
//        binding.lnChangePassword.root.setOnClickListener {
//            val intent = intentFor<SettingsChangePasswordActivity>()
//            startActivity(intent)
//            finish()
//        }
//
//        binding.lnSignOut.root.setOnClickListener {
//
//            val logoutDialog =
//                SettingsLogoutDialog()
//            logoutDialog.show(supportFragmentManager, "")
//        }
//
//        binding.lnHelp.root.setOnClickListener {
//            if( resources.getBoolean(R.bool.has_online_help) ) {
//                val mobilePhoneLanguage = when {
//                    SettingsHelper.languageName.toLowerCase() == "en" || SettingsHelper.languageName.toLowerCase() == "de" -> SettingsHelper.languageName.toLowerCase()
//                    else -> "en"
//                }
//                val browserIntent =
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse(BuildConfig.ONLINE_HELP_LINK + mobilePhoneLanguage)
//                    )
//                startActivity(browserIntent)
//            } else {
//                val intent = Intent(this@SettingsActivity, SettingsHelpActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
//
//        binding.lnTerms.root.setOnClickListener {
//
//            val intent = intentFor<SettingsTermsActivity>()
//            startActivity(intent)
//            finish()
//        }
//
//        binding.lnPrivacyPolicy.root.setOnClickListener {
//
//            val intent = intentFor<SettingsPrivacyPolicyActivity>()
//            startActivity(intent)
//            finish()
//        }
//
//        binding.lnContact.root.setOnClickListener {
//            val emailIntent =
//                Intent(Intent.ACTION_SENDTO, Uri.parse("mailto: ${BuildConfig.APP_BASE_EMAIL}"))
//            startActivity(Intent.createChooser(emailIntent, ""))
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                val intent = Intent()
//                val packageName = this@SettingsActivity.packageName
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
//        val packageName = this@SettingsActivity.packageName
//        val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//        intent.component = componentName
//
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }

}
