package hr.sil.android.myappbox.view.ui.activities


import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.view.ui.BaseActivity

class TtcActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_ttc)
//
//        val toolbar: Toolbar = find(R.id.toolbar)
//        setSupportActionBar(toolbar)
//        supportActionBar?.setDisplayHomeAsUpEnabled(true);
//        supportActionBar?.setDisplayShowHomeEnabled(true);
//        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.home -> {
//                finish()
//                return true
//            }
//            else -> return super.onOptionsItemSelected(item)
//        }
        return true
    }
}
