package hr.sil.android.myappbox.view.ui.activities


import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.util.connectivity.NetworkChecker
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PasswordRecoveryActivity : BaseActivity() {

//    private lateinit var binding: ActivityPasswordRecoveryBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityPasswordRecoveryBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setAllHintToUpperCase()
//
//        val toolbar: Toolbar = binding.toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        binding.btnPasswordRecovery.setOnClickListener {
//
//            if (NetworkChecker.isInternetConnectionAvailable()) {
//                if (validate()) {
//
//                    binding.progressBar.visibility = View.VISIBLE
//
//                    lifecycleScope.launch {
//                        val result = UserUtil.passwordRecovery(binding.etEmail.text.toString())
//
//                        withContext(Dispatchers.Main) {
//                            if (result) {
//                                val intent = intentFor<PasswordUpdateActivity>("EMAIL" to binding.etEmail.text.toString())
//                                startActivity(intent)
//
//                            } else {
//                                binding.tilEmail.error = getString(R.string.forgot_password_email_error)
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
//        binding.tilEmail.hint = resources.getString(R.string.app_generic_email).uppercase()
//    }
//
//    private fun validate(): Boolean {
//        if (!validateEmail(binding.tilEmail, binding.etEmail)) return false
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                val intent = Intent(baseContext, LoginActivity::class.java)
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
//        val intent = Intent(baseContext, LoginActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }

}
