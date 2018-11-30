package songsongsong.hahaha

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.view.View
import android.view.Window

class AboutActivity : Activity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //取消元件的應用程式標題
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_about)
    }
    fun clickOK(view:View)
    {
        finish()
    }
}