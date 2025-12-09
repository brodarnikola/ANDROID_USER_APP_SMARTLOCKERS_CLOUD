package hr.sil.android.myappbox.view.ui.activities.access_sharing

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.BuildConfig
import hr.sil.android.myappbox.R

class ShareAppDialog constructor( val activity: AppCompatActivity, val email: String) : DialogFragment() {

//    private lateinit var binding: DialogShareAppBinding
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogShareAppBinding.inflate(LayoutInflater.from(context))
//
//        val dialog = activity?.let {
//            Dialog(it)
//        }
//
//        if(dialog != null) {
//            dialog.window?.setBackgroundDrawable( ColorDrawable(Color.TRANSPARENT))
//            dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
//            dialog.setCanceledOnTouchOutside(false)
//            dialog.setContentView(binding.root)
//
//            var inviteUserText = getStringAttrValue(R.attr.thmInviteUserText)
//
//            binding.btnSelect.setOnClickListener {
//                val appLink = BuildConfig.APP_ANDR_DOWNLOAD_URL
//                val iOSLink = BuildConfig.APP_IOS_DOWNLOAD_URL
//                val webPortal = BuildConfig.WEB_PORTAL
//
//                if( resources.getBoolean(R.bool.has_android_link) && resources.getBoolean(R.bool.has_ios_link) && resources.getBoolean(R.bool.has_web_portal_link) ) {
//                    inviteUserText += "\n" + getString(R.string.download_androdi_app) + appLink + "\n" + getString(R.string.download_ios_app) + iOSLink + "\n" + getString(R.string.web_portal) + " " + webPortal
//                }
//                else if( resources.getBoolean(R.bool.has_android_link) && resources.getBoolean(R.bool.has_ios_link) ) {
//                    inviteUserText += "\n" + getString(R.string.download_androdi_app) + appLink + "\n" + getString(R.string.download_ios_app) + iOSLink
//                }
//                else if( resources.getBoolean(R.bool.has_android_link) && !resources.getBoolean(R.bool.has_ios_link) ) {
//                    inviteUserText += "\n" + getString(R.string.download_androdi_app) + appLink
//                }
//
//                val shareBodyText = inviteUserText
//                val emailIntent = Intent(Intent.ACTION_SEND)
//                emailIntent.setType("message/rfc822")
//                emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email) /*arrayOf(userAccess.groupUserEmail)*/)
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject/Title")
//                emailIntent.putExtra(Intent.EXTRA_TEXT, shareBodyText)
//
//                startActivity(Intent.createChooser(emailIntent,  activity.baseContext.getString(R.string.access_sharing_share_choose_sharing)))
//                dismiss()
//            }
//
//            binding.btnCancel.setOnClickListener {
//                dismiss()
//            }
//        }
//
//        return dialog!!
//    }
//
//    private fun getStringAttrValue(attr: Int): String? {
//        val attrArray = intArrayOf(attr)
//        val typedArray = activity.obtainStyledAttributes(attrArray)
//        val result = typedArray.getString(0)
//        typedArray.recycle()
//        return result
//    }
}