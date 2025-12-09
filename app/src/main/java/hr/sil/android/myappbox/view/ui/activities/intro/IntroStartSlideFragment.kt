package hr.sil.android.myappbox.view.ui.activities.intro

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.view.ui.activities.LoginActivity
import hr.sil.android.myappbox.view.ui.activities.OnboardingTermsConditionsActivity

class IntroStartSlideFragment : Fragment() {

//    private lateinit var binding: FragmentIntroductionStartBinding
//
//    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
//        val view = inflater.inflate(R.layout.fragment_introduction_start, container, false)
//
//        binding = FragmentIntroductionStartBinding.inflate(inflater)
//
//        return view
//    }
//
//    override fun onStart() {
//        super.onStart()
//
//        setOnClickListeners()
//    }
//
//    private fun setOnClickListeners() {
//
//        binding.introStartSignIn.setOnClickListener {
//            SettingsHelper.firstRun = false
//            val startupClass = LoginActivity::class.java
//            val startIntent = Intent(this@IntroStartSlideFragment.activity, startupClass)
//            startIntent.putExtra("SPLASH_START", false)
//            startActivity(startIntent)
//            this@IntroStartSlideFragment.activity?.finish()
//        }
//
//        binding.introRegisterYourAccount.setOnClickListener {
//            SettingsHelper.firstRun = false
//            val intent = Intent()
//            val packageName = this@IntroStartSlideFragment.activity?.packageName ?: ""
//            val componentName = ComponentName(packageName, packageName + ".aliasRegistration")
//            intent.component = componentName
//
//            startActivity(intent)
//            this@IntroStartSlideFragment.activity?.finish()
//        }
//
//        binding.introTermsAndConditions.setOnClickListener {
//
//
//            val startupClass = OnboardingTermsConditionsActivity::class.java
//            val startIntent = Intent(this@IntroStartSlideFragment.activity, startupClass)
//            startActivity(startIntent)
//
////            val intent = Intent()
////            val packageName = this@IntroStartSlideFragment.activity?.packageName ?: ""
////            val componentName = ComponentName(packageName, packageName + ".aliasOnBoardingTermsAndConditions")
////            intent.component = componentName
////
////            startActivity(intent)
//        }
//
//        binding.tvOnlineHelp.setOnClickListener {
//            if( resources.getBoolean(R.bool.has_online_help) ) {
//                val mobilePhoneLanguage = when {
//                    SettingsHelper.languageName.lowercase() == "en" || SettingsHelper.languageName.lowercase() == "de" -> SettingsHelper.languageName.lowercase()
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
//    }
}