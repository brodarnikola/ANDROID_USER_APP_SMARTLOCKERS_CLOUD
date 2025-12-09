package hr.sil.android.myappbox.view.ui.activities.intro

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class IntroductionSlidePagerAdapter(fragmentManager: FragmentManager) :
        FragmentStatePagerAdapter(fragmentManager) {

    // 2
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return WelcomeSlideFragment()
            }
            1 -> {
                return PickupSlideFragment()
            }
            2 -> {
                return SendParcelSlideFragment()
            }
            3 -> {
                return KeySharingSlideFragment()
            }
            4 -> {
                return IntroStartSlideFragment()
            }
            else -> {
                return WelcomeSlideFragment()
            }

        }
    }

    // 3
    override fun getCount(): Int {
        return 5
    }
}