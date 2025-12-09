package hr.sil.android.myappbox.view.ui.settings.help

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter


class HelpSlidePagerAdapter(fragmentManager: FragmentManager) :
        //FragmentStatePagerAdapter(fragmentManager) {

    FragmentPagerAdapter(fragmentManager, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    // 2
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                return HelpCollectParcelFragment()
            }
            1 -> {
                return HelpSendParcelFragment()
            }
            2 -> {
                return HelpAccessSharingFragment()
            }
            else -> {
                return HelpCollectParcelFragment()
            }

        }
    }

    // 3
    override fun getCount(): Int {
        return 3
    }
}