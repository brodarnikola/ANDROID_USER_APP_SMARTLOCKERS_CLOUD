package hr.sil.android.myappbox.view.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.*
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import hr.sil.android.myappbox.util.SettingsHelper
import hr.sil.android.myappbox.view.ui.BaseActivity
import hr.sil.android.myappbox.view.ui.activities.dialogs.SupportEmailPhoneDialog

class OnboardingTermsConditionsActivity : BaseActivity(0,0) {

//    private lateinit var binding: ActivityOnboardingTermsConditionsBinding
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityOnboardingTermsConditionsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//
//        val toolbar = binding.toolbar
//        this.setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)
//        supportActionBar?.setDisplayShowHomeEnabled(true)
//        supportActionBar?.setDisplayShowTitleEnabled(false)
//    }
//
//    override fun onStart() {
//        super.onStart()
//        displayTermsInsideTextView()
//        setOnClickListener()
//
//        checkIfHasEmailAndMobilePhoneSupport()
//    }
//
//    private fun checkIfHasEmailAndMobilePhoneSupport() {
//        if (resources.getBoolean(R.bool.has_mobile_and_email_support)) {
//            binding.ivSupportImage.visibility = View.VISIBLE
//            binding.ivSupportImage.setOnClickListener {
//                val supportEmailPhoneDialog = SupportEmailPhoneDialog()
//                supportEmailPhoneDialog.show(
//                    supportFragmentManager,
//                    ""
//                )
//            }
//        } else
//            binding.ivSupportImage.visibility = View.GONE
//    }
//
//    private fun setOnClickListener() {
//        binding.btnOk.setOnClickListener {
//            finish()
//        }
//    }
//
//    private fun displayTermsInsideTextView() {
//        if( resources.getBoolean(R.bool.has_terms_conditions_cpl_basel_with_web_pages_link) || resources.getBoolean(R.bool.has_terms_conditions_cpl_flexilocker_with_web_pages_link) )
//            setupCorrectTextForTermsAndCondtions()
//    }
//
//    private fun setupCorrectTextForTermsAndCondtions() {
//
//        var termsAndConditions =
//            if (resources.getBoolean(R.bool.has_terms_conditions_cpl_basel_with_web_pages_link)) getString(
//                R.string.terms_and_conditions_cpl_basel
//            )
//            else getString(R.string.terms_and_conditions_cpl_flexilocker)
//
//        if (resources.getBoolean(R.bool.has_terms_conditions_cpl_basel_with_web_pages_link)) {
//            setupTextForSmartBoxBaselUserapp(termsAndConditions)
//        } else {
//            setupTextForCPLFlexilockerUserapp(termsAndConditions)
//        }
//    }
//
//
//    private fun setupTextForSmartBoxBaselUserapp(termsAndConditions: String) {
//        val link1GetLocatios = getString(R.string.terms_and_conditions_cpl_basel_gen_link_1)
//        val link2GetLocations = getString(R.string.terms_and_conditions_cpl_basel_gen_link_2)
//        val link3GenUserManuals =
//            getString(R.string.terms_and_conditions_cpl_basel_gen_link_3)
//        val link4GenRegistration =
//            getString(R.string.terms_and_conditions_cpl_basel_gen_link_4)
//
//
//        val urlRegistration = getString(R.string.link_cpl_basel_registration)
//        val urlLocations = getString(R.string.link_cpl_basel_locations)
//        val urlUserManual = getString(R.string.link_cpl_basel_manual)
//
//        val startLocationFirst: Int = termsAndConditions.indexOf("smartboxbasel.ch")
//        val lengthLocationFirst = link1GetLocatios.length
//
//        // Increased manually, to get the right index position of text.
//        val startLocationThird: Int = termsAndConditions.indexOf("guide.smartboxbasel.ch")
//        val lengthLocationThird = link2GetLocations.length
//
//        val startUserManual: Int = termsAndConditions.indexOf("www.smartboxbasel.ch")
//        val lengthUserManual = link3GenUserManuals.length
//
//        val startRegistration: Int = termsAndConditions.lastIndexOf("smartboxbasel.ch")
//        val lengthRegistration = link4GenRegistration.length
//
//        val correctReplaceText = termsAndConditions.trim()
//            .replace("smartboxbasel.ch", link1GetLocatios)
//            .replace("guide.smartboxbasel.ch", link2GetLocations)
//            .replace("www.smartboxbasel.ch", link3GenUserManuals)
//            .replace("smartboxbasel.ch", link4GenRegistration)
//
//        val finalText: SpannableStringBuilder =
//            SpannableStringBuilder().append(correctReplaceText.trim())
//
//        val spannableString = SpannableString(finalText)
//
//        setupSpanPropertie(
//            startLocationFirst,
//            lengthLocationFirst,
//            spannableString,
//            urlLocations
//        )
//        setupSpanPropertie(
//            startLocationThird,
//            lengthLocationThird,
//            spannableString,
//            urlUserManual
//        )
//        setupSpanPropertie(
//            startUserManual,
//            lengthUserManual,
//            spannableString,
//            urlRegistration
//        )
//        setupSpanPropertie(
//            startRegistration,
//            lengthRegistration,
//            spannableString,
//            urlLocations
//        )
//
//        val finalRegistration = TextUtils.concat(spannableString)
//
//        binding.tvSettings.setText(finalRegistration)
//        binding.tvSettings.setMovementMethod(LinkMovementMethod.getInstance())
//    }
//
//    private fun setupTextForCPLFlexilockerUserapp(termsAndConditions: String) {
//        val link1GetLocatios = getString(R.string.terms_and_conditions_cpl_flexilocker_gen_link_1)
//        val link2GetLocations = getString(R.string.terms_and_conditions_cpl_flexilocker_gen_link_2)
//        val link3GenUserManuals =
//            getString(R.string.terms_and_conditions_cpl_flexilocker_gen_link_3)
//        val link4GenRegistration =
//            getString(R.string.terms_and_conditions_cpl_flexilocker_gen_link_4)
//
//
//        val urlRegistration = getString(R.string.link_cpl_flexilokcer_register)
//        val urlLocations = getString(R.string.link_cpl_flexilokcer_locations)
//        val urlUserManual = getString(R.string.link_cpl_flexilokcer_manual)
//
//        if (SettingsHelper.languageName == "DE") {
//            val startLocationFirst: Int = termsAndConditions.indexOf("link1_gen")
//            val lengthLocationFirst = link1GetLocatios.length
//
//            val startUserManual: Int = termsAndConditions.indexOf("link2_gen")
//            val lengthUserManual = link3GenUserManuals.length - 6
//
//            val startRegistration: Int = termsAndConditions.indexOf("link3_gen") + 5
//            val lengthRegistration = link4GenRegistration.length  + 11
//
//            val startLocationSecond: Int = termsAndConditions.indexOf("link4_gen")  + 16
//            val lengthLocationSecond = link2GetLocations.length - 4
//
//            val correctReplaceText = termsAndConditions.trim()
//                .replace("link1_gen", link1GetLocatios)
//                .replace("link2_gen", link2GetLocations)
//                .replace("link3_gen", link3GenUserManuals)
//                .replace("link4_gen", link4GenRegistration)
//
//            val finalText: SpannableStringBuilder =
//                SpannableStringBuilder().append(correctReplaceText.trim())
//
//            val spannableString = SpannableString(finalText)
//
//            setupSpanPropertie(
//                startLocationFirst,
//                lengthLocationFirst,
//                spannableString,
//                urlLocations
//            )
//            setupSpanPropertie(
//                startUserManual,
//                lengthUserManual,
//                spannableString,
//                urlUserManual
//            )
//            setupSpanPropertie(
//                startRegistration,
//                lengthRegistration,
//                spannableString,
//                urlRegistration
//            )
//            setupSpanPropertie(
//                startLocationSecond,
//                lengthLocationSecond,
//                spannableString,
//                urlLocations
//            )
//
//            val finalRegistration = TextUtils.concat(spannableString)
//
//            binding.tvSettings.setText(finalRegistration)
//            binding.tvSettings.setMovementMethod(LinkMovementMethod.getInstance())
//        } else {
//
//            val startLocationFirst: Int = termsAndConditions.indexOf("link1_gen")
//            val lengthLocationFirst = link1GetLocatios.length
//
//            val startUserManual: Int = termsAndConditions.lastIndexOf("link2_gen")
//            val lengthUserManual = link3GenUserManuals.length -7
//
//            val startRegistration: Int = termsAndConditions.lastIndexOf("link3_gen") + 2
//            val lengthRegistration = link4GenRegistration.length + 10
//
//            val startLocationSecond: Int = termsAndConditions.indexOf("link4_gen") + 12
//            val lengthLocationSecond = link2GetLocations.length - 2
//
//            val correctReplaceText = termsAndConditions.trim()
//                .replace("link1_gen", link1GetLocatios)
//                .replace("link2_gen", link2GetLocations)
//                .replace("link3_gen", link3GenUserManuals)
//                .replace("link4_gen", link4GenRegistration)
//
//            val finalText: SpannableStringBuilder =
//                SpannableStringBuilder().append(correctReplaceText.trim())
//
//            val spannableString = SpannableString(finalText)
//
//            setupSpanPropertie(
//                startLocationFirst,
//                lengthLocationFirst,
//                spannableString,
//                urlLocations
//            )
//            setupSpanPropertie(
//                startUserManual,
//                lengthUserManual,
//                spannableString,
//                urlUserManual
//            )
//            setupSpanPropertie(
//                startRegistration,
//                lengthRegistration,
//                spannableString,
//                urlRegistration
//            )
//            setupSpanPropertie(
//                startLocationSecond,
//                lengthLocationSecond,
//                spannableString,
//                urlLocations
//            )
//
//            val finalRegistration = TextUtils.concat(spannableString)
//
//            binding.tvSettings.setText(finalRegistration)
//            binding.tvSettings.setMovementMethod(LinkMovementMethod.getInstance())
//
//        }
//    }
//
//    private fun setupSpanPropertie(
//        startLocation: Int,
//        lengthLocation: Int,
//        spannableString: SpannableString,
//        urlString: String
//    ) {
//        spannableString.setSpan(
//            object : ClickableSpan() {
//                override fun onClick(widget: View) {
//                    val browserIntent = Intent(
//                        Intent.ACTION_VIEW,
//                        Uri.parse(urlString)
//                    )
//                    startActivity(browserIntent)
//                }
//
//                override fun updateDrawState(ds: TextPaint) {
//                    ds.setColor(
//                        ContextCompat.getColor(
//                            this@OnboardingTermsConditionsActivity.baseContext,
//                            R.color.colorBlueBorderInSendParcel
//                        )
//                    );
//                    ds.setUnderlineText(true)
//                }
//            },
//            startLocation, startLocation + lengthLocation, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
//        )
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.getItemId()) {
//            R.id.home -> {
//                finish()
//                return true
//            }
//
//            else -> return super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onBackPressed() {
//        finish()
//        super.onBackPressed()
//    }
}
