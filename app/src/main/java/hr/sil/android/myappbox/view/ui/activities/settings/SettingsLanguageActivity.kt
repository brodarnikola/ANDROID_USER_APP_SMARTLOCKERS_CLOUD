package hr.sil.android.myappbox.view.ui.activities.settings


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.CompoundButton
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

class SettingsLanguageActivity
    //: BaseActivity(0, R.id.no_internet_layout)
    //, CompoundButton.OnCheckedChangeListener
{

//    var selectedLanguageCode = ""
//
//    private lateinit var binding: ActivitySettingsLanguageBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsLanguageBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        binding.btnApplyLanguage.setOnClickListener {
//
//            lifecycleScope.launch {
//                val languagesList = DataCache.getLanguages().toList()
//                withContext(Dispatchers.Main) {
//
//                    val language: RLanguage = languagesList.first { it.code == selectedLanguageCode }
//
//                    val user = UserUtil.user
//
//                    if( binding.switchEnglish.isChecked == true || binding.switchGermany.isChecked == true ||
//                        binding.switchItalian.isChecked == true || binding.switchFranch.isChecked == true ) {
//
//                        if (language != null && UserUtil.userUpdate(user?.name.toString(),
//                                user?.address.toString(), user?.telephone.toString(),
//                                language,
//                                SettingsHelper.pushEnabled,
//                                SettingsHelper.emailEnabled, UserUtil.userGroup?.name.toString())) {
//
//                            App.Companion.ref.toast(this@SettingsLanguageActivity.getString(R.string.app_generic_success))
//
//                            SettingsHelper.languageName = language.code
//
//                            val intent = Intent(baseContext, SettingsActivity::class.java)
//                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
//                            startActivity( intent )
//                            finish()
//                        } else {
//                            App.Companion.ref.toast(this@SettingsLanguageActivity.getString(R.string.error_while_saving_user, UserUtil.user?.id.toString() + " "))
//                        }
//                    }
//                    else {
//                        App.Companion.ref.toast(this@SettingsLanguageActivity.getString(R.string.error_while_saving_user, UserUtil.user?.id.toString() + " "))
//                    }
//                }
//            }
//        }
//
//        if( App.Companion.ref.resources.getBoolean(R.bool.has_only_enabled_en_de_languages)  ) {
//            binding.clFrench.visibility = View.GONE
//            binding.clItalian.visibility = View.GONE
//        }
//
//        binding.switchEnglish.setOnCheckedChangeListener(this)
//        binding.switchGermany.setOnCheckedChangeListener(this)
//        binding.switchItalian.setOnCheckedChangeListener(this)
//        binding.switchFranch.setOnCheckedChangeListener(this)
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        binding.switchEnglish.isChecked = false
//        binding.switchGermany.isChecked = false
//        binding.switchItalian.isChecked = false
//        binding.switchFranch.isChecked = false
//
//        if (SettingsHelper.languageName == "EN") {
//            binding.switchEnglish.isChecked = true
//        } else if (SettingsHelper.languageName == "DE") {
//            binding.switchGermany.isChecked = true
//        } else if (SettingsHelper.languageName == "IT") {
//            binding.switchItalian.isChecked = true
//        } else {
//            binding.switchFranch.isChecked = true
//        }
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
//    override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
//
//        when (buttonView?.id) {
//            R.id.switchEnglish -> {
//                selectedLanguageCode = "EN"
//
//                if( isChecked )
//                    binding.switchEnglish?.isChecked = true
//
//                if ( binding.switchGermany.isChecked  ) {
//                    binding.switchGermany.setOnCheckedChangeListener (null);
//                    binding.switchGermany.isChecked = false
//                    binding.switchGermany.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchItalian.isChecked ) {
//
//                    binding.switchItalian.setOnCheckedChangeListener (null);
//                    binding.switchItalian.isChecked = false
//                    binding.switchItalian.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchFranch.isChecked ) {
//
//                    binding.switchFranch.setOnCheckedChangeListener (null);
//                    binding.switchFranch.isChecked = false
//                    binding.switchFranch.setOnCheckedChangeListener (this);
//                }
//            }
//            R.id.switchGermany -> {
//                selectedLanguageCode = "DE"
//
//                if( isChecked )
//                    binding.switchGermany?.isChecked = true
//
//                if ( binding.switchEnglish.isChecked  ) {
//                    binding.switchEnglish.setOnCheckedChangeListener (null);
//                    binding.switchEnglish.isChecked = false
//                    binding.switchEnglish.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchItalian.isChecked ) {
//
//                    binding.switchItalian.setOnCheckedChangeListener (null);
//                    binding.switchItalian.isChecked = false
//                    binding.switchItalian.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchFranch.isChecked ) {
//
//                    binding.switchFranch.setOnCheckedChangeListener (null);
//                    binding.switchFranch.isChecked = false
//                    binding.switchFranch.setOnCheckedChangeListener (this);
//                }
//            }
//            R.id.switchItalian -> {
//                selectedLanguageCode = "IT"
//
//                if( isChecked )
//                    binding.switchItalian?.isChecked = true
//
//                if ( binding.switchEnglish.isChecked  ) {
//                    binding.switchEnglish.setOnCheckedChangeListener (null);
//                    binding.switchEnglish.isChecked = false
//                    binding.switchEnglish.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchGermany.isChecked ) {
//
//                    binding.switchGermany.setOnCheckedChangeListener (null);
//                    binding.switchGermany.isChecked = false
//                    binding.switchGermany.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchFranch.isChecked ) {
//
//                    binding.switchFranch.setOnCheckedChangeListener (null);
//                    binding.switchFranch.isChecked = false
//                    binding.switchFranch.setOnCheckedChangeListener (this);
//                }
//            }
//            R.id.switchFranch -> {
//                selectedLanguageCode = "FR"
//
//                if( isChecked )
//                    binding.switchFranch?.isChecked = true
//
//                if ( binding.switchEnglish.isChecked  ) {
//                    binding.switchEnglish.setOnCheckedChangeListener (null);
//                    binding.switchEnglish.isChecked = false
//                    binding.switchEnglish.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchGermany.isChecked ) {
//
//                    binding.switchGermany.setOnCheckedChangeListener (null);
//                    binding.switchGermany.isChecked = false
//                    binding.switchGermany.setOnCheckedChangeListener (this);
//                }
//                if ( binding.switchItalian.isChecked ) {
//
//                    binding.switchItalian.setOnCheckedChangeListener (null);
//                    binding.switchItalian.isChecked = false
//                    binding.switchItalian.setOnCheckedChangeListener (this);
//                }
//            }
//            else -> {}
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
//                val intent = Intent(this@SettingsLanguageActivity, SettingsActivity::class.java)
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
//        val intent = Intent(this@SettingsLanguageActivity, SettingsActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }
}
