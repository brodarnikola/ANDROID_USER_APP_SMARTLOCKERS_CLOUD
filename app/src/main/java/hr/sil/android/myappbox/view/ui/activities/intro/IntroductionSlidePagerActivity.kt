package hr.sil.android.myappbox.view.ui.activities.intro

import android.os.Bundle
import android.os.Handler
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
//import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.view.ui.BaseActivity

class IntroductionSlidePagerActivity  : BaseActivity() {

//    /**
//     * The number of pages (wizard steps) to show in this demo.
//     */
//    private val NUM_PAGES = 5
//    private val fragmentLoaderHandler = Handler()
//    /**
//     * The pager widget, which handles animation and allows swiping horizontally to access previous
//     * and next wizard steps.
//     */
//    private lateinit var viewPager: ViewPager
//
//    /**
//     * The pager adapter, which provides the pages to the view pager widget.
//     */
//    private var mPagerAdapter: PagerAdapter? = null
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_screen_slide)
//
//        // Instantiate a ViewPager and a PagerAdapter.
//        viewPager = findViewById(R.id.viewPager) as ViewPager
//        val dotsIndicator = findViewById<WormDotsIndicator>(R.id.worm_dots_indicator)
//        mPagerAdapter = IntroductionSlidePagerAdapter(this.supportFragmentManager)
//        viewPager.adapter = mPagerAdapter
//        dotsIndicator.setViewPager(viewPager)
//        val intent = intent
//        val s1 = intent.getBooleanExtra("Back", false)
//
//        if (s1) {
//            viewPager.setCurrentItem(4, false)
//        }
//
//    }
//
//    override fun onBackPressed() {
//        if (viewPager.currentItem == 0) {
//            // If the user is currently looking at the first step, allow the system to handle the
//            // Back button. This calls finish() on this activity and pops the back stack.
//            super.onBackPressed()
//        } else {
//            // Otherwise, select the previous step.
//            viewPager.currentItem = viewPager.currentItem - 1
//        }
//    }

}