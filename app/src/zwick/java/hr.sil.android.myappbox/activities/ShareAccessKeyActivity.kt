package hr.sil.android.myappbox.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import hr.sil.android.myappbox.activities.collect_parcel.ListOfDeliveriesActivity
import hr.sil.android.smartlockers.enduser.App
import hr.sil.android.smartlockers.enduser.R
import hr.sil.android.smartlockers.enduser.cache.DataCache
import hr.sil.android.smartlockers.enduser.core.remote.WSUser
import hr.sil.android.smartlockers.enduser.core.util.logger
import hr.sil.android.smartlockers.enduser.databinding.ActivitySelectLockerBinding
import hr.sil.android.smartlockers.enduser.databinding.ActivityShareAccessKeyBinding
import hr.sil.android.smartlockers.enduser.view.ui.BaseActivity
import hr.sil.android.smartlockers.enduser.view.ui.activities.access_sharing.ShareAppDialog
import hr.sil.android.smartlockers.enduser.view.ui.activities.dialogs.SupportEmailPhoneDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jetbrains.anko.toast

class ShareAccessKeyActivity : BaseActivity() {

    private val log = logger()

    private val shareAccessKeyId by lazy { intent.getIntExtra("shareAccessKeyId", 0) }
    private val macAddress by lazy { intent.getStringExtra("macAddress") }

    private lateinit var binding: ActivityShareAccessKeyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShareAccessKeyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setAllHintToUpperCase()

        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        checkIfHasEmailAndMobilePhoneSupport()

        binding.btnPasswordRecovery.setOnClickListener {

            val mailAddress = binding.etEmail.text.toString()
            if( validate(binding.tilEmail, binding.etEmail)) {
                binding.progressBar.visibility = View.VISIBLE
                binding.btnPasswordRecovery.visibility = View.GONE
                lifecycleScope.launch(Dispatchers.Default) {
                    if (!isUserMemberOfGroup(mailAddress)) {
                        log.info("PaF share $shareAccessKeyId - $mailAddress")
                        log.info("P@h created $shareAccessKeyId mail: $mailAddress")
                        val returnedData = WSUser.createPaF(shareAccessKeyId, mailAddress)
                        log.info("Invitation key = ${returnedData?.invitationCode}")
                        if (returnedData?.invitationCode.isNullOrEmpty()) {
                            withContext(Dispatchers.Main) {
                                val message = resources?.getString(R.string.app_generic_success).toString()
                                App.ref.toast(message)
                                val intent = Intent(this@ShareAccessKeyActivity.applicationContext, ListOfDeliveriesActivity::class.java)
                                startActivity(intent)
                                finish()
                                //setupAdapterForKeys()
                                //dismiss()
                                //updateKeys()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                val shareAppDialog = ShareAppDialog(this@ShareAccessKeyActivity, mailAddress)
                                shareAppDialog.show( this@ShareAccessKeyActivity.supportFragmentManager, "" )
                                binding.progressBar.visibility = View.GONE
                                binding.btnPasswordRecovery.visibility = View.VISIBLE
                            }
                        }

                    } else {
                        withContext(Dispatchers.Main) {
                            App.ref.toast(R.string.grant_access_error_exists)
                            binding.progressBar.visibility = View.GONE
                            binding.btnPasswordRecovery.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
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

    suspend private fun isUserMemberOfGroup(email: String): Boolean {
        val groups = DataCache.getGroupMembers()
        val groupMemberships = groups.filter { it.email == email }
        return groupMemberships.isNotEmpty()
    }

    private fun validate(tilEmail: TextInputLayout, etEmail: EditText): Boolean {
        if (!validateEmail(tilEmail, etEmail)) return false

        return true
    }

    private fun setAllHintToUpperCase() {
        binding.tilEmail.hint = resources.getString(R.string.app_generic_email).uppercase()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(baseContext, ListOfDeliveriesActivity::class.java)
                intent.putExtra("macAddress", macAddress)
                startActivity(intent)
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
                finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        val intent = Intent(baseContext, ListOfDeliveriesActivity::class.java)
        intent.putExtra("macAddress", macAddress)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        finish()
        super.onBackPressed()
    }

}
