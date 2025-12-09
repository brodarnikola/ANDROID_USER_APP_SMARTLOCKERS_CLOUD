package hr.sil.android.myappbox.view.ui.activities.settings

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App

import hr.sil.android.myappbox.core.remote.model.RLanguage
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SettingsNotificationActivity
    //: BaseActivity(0, R.id.no_internet_layout)
{

//    private lateinit var binding: ActivitySettingsNotificationBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsNotificationBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        binding.btnApply.setOnClickListener {
//
//            lifecycleScope.launch {
//                val languagesList = DataCache.getLanguages().toList()
//                withContext(Dispatchers.Main) {
//
//
//                    val languageName = SettingsHelper.languageName
//                    val language: RLanguage? = languagesList.firstOrNull { it.code == languageName }!!
//
//                    val user = UserUtil.user
//
//                    if (UserUtil.userUpdate(user?.name.toString(),
//                            user?.address.toString(), user?.telephone.toString(),
//                            language!!,
//                            binding.switchPushNotification.isChecked,
//                            binding.switchEmail.isChecked, UserUtil.userGroup?.name.toString())) {
//
//                        App.Companion.ref.toast(this@SettingsNotificationActivity.getString(R.string.app_generic_success))
//
//                        SettingsHelper.pushEnabled = binding.switchPushNotification.isChecked
//                        SettingsHelper.emailEnabled = binding.switchEmail.isChecked
//                    } else {
//                        App.Companion.ref.toast(this@SettingsNotificationActivity.getString(R.string.error_while_saving_user, UserUtil.user?.id.toString() + " "))
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        binding.switchPushNotification.isChecked = SettingsHelper.pushEnabled
//        binding.switchEmail.isChecked = SettingsHelper.emailEnabled
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
//    override fun onNetworkStateUpdated(available: Boolean) {
//        super.onNetworkStateUpdated(available)
//        networkAvailable = available
//        updateUI()
//    }
//
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//                val intent = Intent(this@SettingsNotificationActivity, SettingsActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                finish()
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onBackPressed() {
//        val intent = Intent(this@SettingsNotificationActivity, SettingsActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }
}
