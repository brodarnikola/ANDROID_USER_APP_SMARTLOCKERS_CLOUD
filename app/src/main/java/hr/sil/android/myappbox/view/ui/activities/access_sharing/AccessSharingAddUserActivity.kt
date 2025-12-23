package hr.sil.android.myappbox.view.ui.activities.access_sharing

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import hr.sil.android.myappbox.view.ui.BaseActivity
import kotlinx.coroutines.*
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import hr.sil.android.myappbox.R

class AccessSharingAddUserActivity //  :  BaseActivity(R.id.no_ble_layout, R.id.no_internet_layout, R.id.no_location_gps_layout) 
{

//    private val log = logger()
//
//    private val PROJECTION = arrayOf(
//        ContactsContract.Contacts._ID,
//        ContactsContract.Contacts.DISPLAY_NAME,
//        ContactsContract.CommonDataKinds.Email.DATA
//    )
//    lateinit var selectedItem: UserGroup
//
//    private val droidPermission by lazy { DroidPermission.init(this) }
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) = droidPermission.link(requestCode, permissions, grantResults)
//
//    private val activityForResultWrapper = ActivityForResultWrapper()
//
//    private val masterMacAddress by lazy { intent.getStringExtra("rMacAddress") ?: "" }
//
//    private val nameOfDevice by lazy { intent.getStringExtra("nameOfDevice") ?: "" }
//    private val nameOfGroup by lazy { intent.getStringExtra("nameOfGroup") ?: "" }
//    private var groupIdParameter:Int = 0
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        activityForResultWrapper.onActivityResult(requestCode, resultCode, data)
//    }
//
//    private lateinit var binding: ActivityAccessSharingAddUserBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityAccessSharingAddUserBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar: Toolbar = binding.toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            android.R.id.home -> {
//
//                val startIntent =
//                    Intent(this@AccessSharingAddUserActivity, AccessSharingActivity::class.java)
//                startIntent.putExtra("rMacAddress", masterMacAddress)
//                startIntent.putExtra("nameOfDevice", nameOfDevice)
//                startActivity(startIntent)
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
//        val startIntent =
//            Intent(this@AccessSharingAddUserActivity, AccessSharingActivity::class.java)
//        startIntent.putExtra("rMacAddress", masterMacAddress)
//        startIntent.putExtra("nameOfDevice", nameOfDevice)
//        startActivity(startIntent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//
//        groupIdParameter = intent.getIntExtra("groupId", 0)
//
//        if(  groupIdParameter == 0 )
//            groupIdParameter = UserUtil.userGroup?.id ?: 0
//        log.info("Group id is: ${groupIdParameter}")
//
//        val groups = listOf<UserGroup>(
//            UserGroup(1, this.getString(R.string.access_sharing_admin_role), RUserAccessRole.ADMIN),
//            UserGroup(1, this.getString(R.string.access_sharing_user_role), RUserAccessRole.USER)
//        )
//
//        binding.roleTypeSpinner.adapter = GroupRoleAdapter(groups)
//
//        binding.selectEmailFromContact.setOnClickListener {
//
//            GlobalScope.launch {
//                val phoneContact = askForEmailFromContacts()
//                withContext(Dispatchers.Main) {
//                    binding.emailEditText.text =
//                        Editable.Factory.getInstance().newEditable(phoneContact?.email ?: "")
//                }
//            }
//        }
//
//        binding.roleTypeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//            }
//
//            override fun onItemSelected(
//                adapterView: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//
//                selectedItem = adapterView?.getItemAtPosition(position) as UserGroup
//            }
//        }
//
//        val log = logger()
//        binding.btnGrantAccess.setOnClickListener {
//
//            binding.wrongEmail.visibility = View.GONE
//
//            val correctMasterId =
//                MPLDeviceStore.uniqueDevices.values.filter { it.macAddress == masterMacAddress }
//                    .first().masterUnitId
//
//            val userAccess = RUserAccess().apply {
//
//                this.groupId = groupIdParameter
//                this.groupUserEmail = binding.emailEditText.text.toString()
//                this.role = selectedItem.value.name
//                this.masterId = correctMasterId
//
//            }
//            if (userAccess.groupUserEmail.isNotEmpty() && (".+@.+".toRegex().matches(binding.emailEditText.text.toString()))) {
//                if (UserUtil.user?.email == userAccess.groupUserEmail && UserUtil.userGroup?.id == userAccess.groupId) {
//                    App.ref.toast("You can not add your self to your group")
//                } else {
//                    binding.progressBar.visibility = View.VISIBLE
//                    binding.btnGrantAccess.visibility = View.GONE
//                    lifecycleScope.launch {
//                        if (WSUser.addUserAccess(userAccess)) {
//
//                            // add new user to admin data cache
//                            if (UserUtil.userGroup?.name != nameOfGroup) {
//
//                                userAccess.groupId?.toLong()
//                                    ?.let { it -> DataCache.groupMemberships(it, true) }
//                            }
//                            // add new user to owner data cache
//                            else {
//                                DataCache.getGroupMembers()
//                            }
//
//                            withContext(Dispatchers.Main) {
//                                log.info("Successfully add user ${userAccess.groupUserEmail} to group")
//                                App.ref.toast(this@AccessSharingAddUserActivity.getString(R.string.app_generic_success))
//                                val startIntent = Intent(
//                                    this@AccessSharingAddUserActivity,
//                                    AccessSharingActivity::class.java
//                                )
//                                startIntent.putExtra("rMacAddress", masterMacAddress)
//                                startIntent.putExtra("nameOfDevice", nameOfDevice)
//                                startActivity(startIntent)
//                                finish()
//                            }
//                        } else {
//
//                            withContext(Dispatchers.Main) {
//
//                                val shareAppDialog = ShareAppDialog(
//                                    this@AccessSharingAddUserActivity,
//                                    userAccess.groupUserEmail
//                                )
//                                shareAppDialog.show(
//                                    this@AccessSharingAddUserActivity.supportFragmentManager,
//                                    ""
//                                )
//                                binding.progressBar.visibility = View.GONE
//                                binding.btnGrantAccess.visibility = View.VISIBLE
//                            }
//                        }
//                    }
//                }
//            } else if( userAccess.groupUserEmail.isEmpty() ){
//                binding.wrongEmail.visibility = View.VISIBLE
//                binding.wrongEmail.text = getString(R.string.edit_user_validation_blank_fields_exist)
//                //validateNewEmail(false, emailEditText)
//            }
//            else {
//                binding.wrongEmail.visibility = View.VISIBLE
//                binding.wrongEmail.text = getString(R.string.message_email_invalid)
//            }
//        }
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        binding.ivSupportImage.setOnClickListener {
//            val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//            supportEmailPhoneDialog.show(
//                supportFragmentManager,
//                ""
//            )
//        }
//    }
//
//    override fun onNetworkStateUpdated(available: Boolean) {
//        super.onNetworkStateUpdated(available)
//        networkAvailable = available
//        updateUI()
//    }
//
//    override fun onBluetoothStateUpdated(available: Boolean) {
//        super.onBluetoothStateUpdated(available)
//        bluetoothAvalilable = available
//        updateUI()
//    }
//
//    override fun onLocationGPSStateUpdated(available: Boolean) {
//        super.onLocationGPSStateUpdated(available)
//        locationGPSAvalilable = available
//        updateUI()
//    }
//
//    private fun requestReadContactsPermission(): Deferred<Boolean> {
//        val deferred = CompletableDeferred<Boolean>()
//        droidPermission
//            .request(Manifest.permission.READ_CONTACTS)
//            .done { _, deniedPermissions ->
//                if (deniedPermissions.isNotEmpty()) {
//                    log.info("Permissions were denied!")
//                    deferred.complete(false)
//                } else {
//                    log.info("Permissions granted!")
//                    deferred.complete(true)
//                }
//            }
//            .execute()
//        return deferred
//    }
//
//    private suspend fun askForEmailFromContacts(): PhoneContact? {
//        var emailResult: String?
//        var nameResult: String?
//        var contact: PhoneContact? = null
//
//        if (!requestReadContactsPermission().await()) {
//            this@AccessSharingAddUserActivity.alert("No address")
//        } else {
//            val uri = selectFromContacts().await()?.data
//            if (uri != null) {
//                try {
//                    this.contentResolver.query(uri, PROJECTION, null, null, null).use { cursor ->
//                        if (cursor != null && cursor.moveToFirst()) {
//                            // get the contact's information
//                            nameResult =
//                                cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME) ?: 0)
//                            emailResult =
//                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA) ?: 0)
//
//                            // if the user user has an roleText or phone then add it to contacts
//                            if ((!TextUtils.isEmpty(emailResult) && android.util.Patterns.EMAIL_ADDRESS.matcher(
//                                    emailResult!!
//                                ).matches() /* && !emailResult.equals(nameResult)) */)
//                            ) {
//                                contact = PhoneContact(nameResult, emailResult)
//                            }
//                        }
//                    }
//                } catch (ex: Exception) {
//                    //ignore
//                }
//            }
//        }
//        return contact
//    }
//
//    private fun selectFromContacts(): Deferred<Intent?> {
//        val intent = Intent(Intent.ACTION_PICK)
//        intent.type = ContactsContract.CommonDataKinds.Email.CONTENT_TYPE
//        return activityForResultWrapper.call(this, intent)
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onMplDeviceNotify(event: UnauthorizedUserEvent) {
//        log.info("Received unauthorized event, user will now be log outed")
//        val intent = Intent( this, LoginActivity::class.java)
//        startActivity(intent)
//        finish()
//    }

}
