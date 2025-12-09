package hr.sil.android.myappbox.view.ui.activities.settings

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import hr.sil.android.myappbox.view.ui.settings.help.HelpSlidePagerAdapter
import hr.sil.android.myappbox.view.ui.BaseActivity

class SettingsHelpActivity : BaseActivity(0, 0) {

//    /**
//     * The pager widget, which handles animation and allows swiping horizontally to access previous
//     * and next wizard steps.
//     */
//    private lateinit var viewPager: ViewPager
//
//    /**
//     * The pager adapters, which provides the pages to the view pager widget.
//     */
//    private var mPagerAdapter: PagerAdapter? = null
//
//
//    private lateinit var binding: ActivitySettingsHelpBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivitySettingsHelpBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//
//        // Instantiate a ViewPager and a PagerAdapter.
//        viewPager = findViewById<ViewPager>(R.id.viewPager)
//        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
//        mPagerAdapter = HelpSlidePagerAdapter(this.supportFragmentManager)
//        viewPager.adapter = mPagerAdapter
//        dotsIndicator.setViewPager(viewPager)
//        val intent = intent
//        val s1 = intent.getBooleanExtra("Back", false)
//
//        initializeLinkableTextView(BuildConfig.APP_BASE_EMAIL, binding.tvSendEmail) {
//            binding.tvSendEmail.setOnClickListener {
//                val emailIntent = Intent(
//                    Intent.ACTION_SENDTO,
//                    Uri.parse("mailto:${  BuildConfig.APP_BASE_EMAIL /* this.getString( R.string.app_generic_support_email) */}")
//                )
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_generic_help))
//                startActivity(Intent.createChooser(emailIntent, ""))
//            }
//        }
//
//       /* tvSendEmail.setOnClickListener {
//            val emailIntent = Intent(
//                Intent.ACTION_SENDTO,
//                Uri.parse("mailto:${  BuildConfig.APP_BASE_EMAIL *//* this.getString( R.string.app_generic_support_email) *//*}")
//            )
//            emailIntent.putExtra(Intent.EXTRA_SUBJECT, this.getString(R.string.app_generic_help))
//            startActivity(Intent.createChooser(emailIntent, ""))
//        }*/
//
//        if (s1) {
//            viewPager.setCurrentItem(3, false)
//        }
//    }
//
//    override fun onBackPressed() {
//        if (viewPager.currentItem == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            val intent = Intent(this@SettingsHelpActivity, SettingsActivity::class.java)
//            startActivity(intent)
//            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//            finish()
//            super.onBackPressed()
//        } else {
//            // Otherwise, select the previous step.
//            viewPager.currentItem = viewPager.currentItem - 1
//        }
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//
//                val intent = Intent(this@SettingsHelpActivity, SettingsActivity::class.java)
//                startActivity(intent)
//                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
//                finish()
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }

}
