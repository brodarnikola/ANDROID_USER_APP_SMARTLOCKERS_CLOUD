package hr.sil.android.myappbox.view.ui.activities.dialogs

import android.app.Dialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.DialogFragment
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URLEncoder


class RatingReviewAppDialog : DialogFragment() {

//    private lateinit var binding: DialogRatingReviewAppBinding
//    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
//        binding = DialogRatingReviewAppBinding.inflate(LayoutInflater.from(context))
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
//            setupButtonsCorrectly(true)
//
//            setupListeners()
//        }
//
//        return dialog!!
//    }
//
//    override fun onStop() {
//        super.onStop()
//
//        binding.webView.removeAllViews()
//        binding.webView.destroy()
//    }
//
//    private fun setupListeners() {
//        binding.etRatingApp.addTextChangedListener(object : TextWatcher {
//
//            override fun afterTextChanged(s: Editable?) {}
//
//            override fun beforeTextChanged(
//                s: CharSequence, start: Int,
//                count: Int, after: Int
//            ) {
//            }
//
//            override fun onTextChanged(
//                s: CharSequence, start: Int,
//                before: Int, count: Int
//            ) {
//                if (s.length == 0 && binding.ratingBar1.rating == 0.0f) {
//                    setupButtonsCorrectly(hideButtonSubmit = true)
//                } else {
//                    setupButtonsCorrectly(hideButtonSubmit = false)
//                }
//            }
//        })
//
//        binding.ratingBar1.setOnRatingBarChangeListener { ratingBar, rating, fromUser ->
//            if (rating == 0.0f && binding.etRatingApp.text.toString() == "") {
//                setupButtonsCorrectly(hideButtonSubmit = true)
//            } else {
//                setupButtonsCorrectly(hideButtonSubmit = false)
//            }
//        }
//
//        binding.btnSelect.setOnClickListener {
//            sendSurveyRatingDataToBackend()
//        }
//
//        binding.btnCancel.setOnClickListener {
//            dismiss()
//        }
//    }
//
//    private fun setupButtonsCorrectly(hideButtonSubmit: Boolean) {
//        if (hideButtonSubmit) {
//            binding.btnSelect.visibility = View.GONE
//            val params = binding.btnCancel.layoutParams as ConstraintLayout.LayoutParams
//            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
//            params.rightToRight = ConstraintLayout.LayoutParams.PARENT_ID
//            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//            binding.btnCancel.requestLayout()
//        } else {
//            binding.btnSelect.visibility = View.VISIBLE
//            val params = binding.btnCancel.layoutParams as ConstraintLayout.LayoutParams
//            params.leftToLeft = ConstraintLayout.LayoutParams.PARENT_ID
//            params.rightToLeft = binding.btnSelect.id
//            params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
//            binding.btnCancel.requestLayout()
//        }
//    }
//
//    private fun sendSurveyRatingDataToBackend() {
//        val rating = binding.ratingBar1.rating.toInt()
//        val comment = binding.etRatingApp.text.toString()
//        binding.progressBarRating.visibility = View.VISIBLE
//        binding.btnSelect.visibility = View.INVISIBLE
//        GlobalScope.launch(Dispatchers.Main) {
//            sendRatingDataToBackend(rating, comment)
//        }
//    }
//
//    private fun sendRatingDataToBackend(rating: Int, comment: String) {
//
//        if (rating != 0 && comment != "") {
//            val encodedComment: String = URLEncoder.encode(comment.trim(), "UTF-8")
//            val url =
//                "https://survey.smartboxbasel.ch/index.php/298291?newtest=Y&rating=${rating}&comment=${encodedComment}"
//            setupWebView(url)
//        } else if (rating != 0 && comment == "") {
//            val url = "https://survey.smartboxbasel.ch/index.php/298291?newtest=Y&rating=${rating}"
//            setupWebView(url)
//        } else {
//            val encodedComment: String = URLEncoder.encode(comment.trim(), "UTF-8")
//            val url =
//                "https://survey.smartboxbasel.ch/index.php/298291?newtest=Y&comment=${encodedComment}"
//            setupWebView(url)
//        }
//    }
//
//    private fun setupWebView(loadUrl: String) {
//        val webSettings = binding.webView.settings
//
//        webSettings.javaScriptEnabled = true
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            webSettings.safeBrowsingEnabled = true  // api 26
//        }
//
//        binding.webView.loadUrl(loadUrl)
//
//        binding.webView.webChromeClient = WebChromeClient()
//
//        binding.webView.webViewClient = object : WebViewClient() {
//            override fun onPageStarted(view: WebView, url: String, favicon: Bitmap?) {
//                view.visibility = View.GONE
//            }
//
//            override fun onPageFinished(view: WebView, url: String) {
//                App.Companion.ref.toast(resources.getString(R.string.successfully_sended_report))
//                view.removeAllViews()
//                view.destroy()
//                dismiss()
//            }
//        }
//    }

}