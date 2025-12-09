package hr.sil.android.myappbox.view.ui.activities.intro

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.core.util.logger
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.view.ui.activities.LoginActivity

class WelcomeSlideFragment : Fragment() {

//    private val log = logger()
//    private lateinit var binding: FragmentIntroductionWelcomeBinding
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_introduction_welcome, container, false)
//
//        binding = FragmentIntroductionWelcomeBinding.inflate(inflater)
//        return view
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        binding.tvOnlineHelp.setOnClickListener {
//            if( resources.getBoolean(R.bool.has_online_help) ) {
//                val mobilePhoneLanguage = when {
//                    SettingsHelper.languageName.toLowerCase() == "en" || SettingsHelper.languageName.toLowerCase() == "de" -> SettingsHelper.languageName.toLowerCase()
//                    else -> "en"
//                }
//                val browserIntent =
//                    Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse(BuildConfig.ONLINE_HELP_LINK + mobilePhoneLanguage)
//                    )
//                startActivity(browserIntent)
//            }
//        }
//
//        binding.tvSkip.setOnClickListener {
//            SettingsHelper.firstRun = false
//            val startupClass = LoginActivity::class.java
//            val startIntent = Intent(this@WelcomeSlideFragment.activity, startupClass)
//            startIntent.putExtra("SPLASH_START", false)
//            startActivity(startIntent)
//            this@WelcomeSlideFragment.activity?.finish()
//        }
//    }
}