package hr.sil.android.myappbox.view.ui.activities



import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import hr.sil.android.myappbox.util.backend.UserUtil
import hr.sil.android.myappbox.view.ui.BaseActivity
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class TCInvitedUserActivity : BaseActivity() {

//    val email: String by lazy { intent.getStringExtra("email") ?: "" }
//    val password: String by lazy { intent.getStringExtra("password") ?: "" }
//    val goToMainActivity: Boolean by lazy { intent.getBooleanExtra("goToMainActivity", false) }
//    val isComingFromLoginActivity: Boolean by lazy { intent.getBooleanExtra("isComingFromLoginActivity", false) }
//    var didUserScrollToEnd:Boolean = false
//
//    private lateinit var binding: ActivityTcinvitedUserBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityTcinvitedUserBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        val toolbar: Toolbar = binding.toolbar
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//    }
//
//    override fun onResume() {
//        super.onResume()
//
//        binding.btnAcceptTermsConditions.setOnClickListener {
//
//            // if( didUserScrollToEnd ) {
//
//                GlobalScope.launch {
//                    UserUtil.acceptedTerms()
//                }
//                if( goToMainActivity ) {
//                    val intent = Intent()
//                    val packageName = this@TCInvitedUserActivity.packageName
//                    val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
//                    intent.component = componentName
//
//                    startActivity(intent)
//                    finish()
//                }
//                else {
//                    val intent = Intent()
//                    val packageName = this@TCInvitedUserActivity.packageName
//                    val componentName = ComponentName(packageName, packageName + ".aliasStartInvitationThroughtEmail")
//                    intent.component = componentName
//
//                    if( isComingFromLoginActivity ) {
//                        intent.putExtra("acceptTermsAndCondition", true)
//                        intent.putExtra("email", email)
//                        intent.putExtra("password", password)
//                        intent.putExtra("isComingFromLoginActivity", true)
//                    }
//
//                    startActivity(intent)
//                    finish()
//                }
//            // }
//        }
//
//        val viewTreeObserver: ViewTreeObserver = binding.scrollViewTerms.getViewTreeObserver()
//
//        viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                binding.scrollViewTerms.getViewTreeObserver().removeOnGlobalLayoutListener(this)
//                val childHeight = (findViewById(R.id.llTermsAndConditions) as LinearLayout).height
//                val isScrollable: Boolean = binding.scrollViewTerms.getHeight() < childHeight + binding.scrollViewTerms.getPaddingTop() + binding.scrollViewTerms.getPaddingBottom()
//                if (!isScrollable) {
//                    binding.btnAcceptTermsConditions.isClickable = true
//                    binding.btnAcceptTermsConditions.alpha = 1.0f
//                    didUserScrollToEnd = true
//                }
//            }
//        })
//
//        viewTreeObserver.addOnScrollChangedListener {
//            if (binding.scrollViewTerms.getChildAt(0).getBottom() <= binding.scrollViewTerms.getHeight() + binding.scrollViewTerms.getScrollY()) {
//
//                binding.btnAcceptTermsConditions.isClickable = true
//                binding.btnAcceptTermsConditions.alpha = 1.0f
//                didUserScrollToEnd = true
//                //scroll view is at bottom
//            } else {
//                //scroll view is not at bottom
//            }
//        }
//    }
//
//    override fun onBackPressed() {
//        GlobalScope.launch {
//            UserUtil.updateUserHash(null, null)
//        }
//        val intent = Intent(this@TCInvitedUserActivity, LoginActivity::class.java)
//        startActivity(intent)
//        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//        finish()
//        super.onBackPressed()
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                GlobalScope.launch {
//                    UserUtil.updateUserHash(null, null)
//                }
//                val startIntent = Intent(this@TCInvitedUserActivity, LoginActivity::class.java)
//                startActivity(startIntent)
//                finish()
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
}
