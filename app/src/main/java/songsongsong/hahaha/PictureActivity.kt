package songsongsong.hahaha

import android.app.Activity
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import songsongsong.fileToImageView

class PictureActivity : Activity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)

        //取得照片元件
        val pictureView: ImageView = findViewById(R.id.picture_view)
        //取得照片檔案
        val pictureName = intent.getStringExtra("pictureName")

        if(!pictureName.isNullOrBlank())
        {
            //設定照片元件
            fileToImageView(pictureName,pictureView)
        }
    }

    fun clickPicture(view:View)
    {
        //如果裝置的版本是LOLLIPOP
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            finishAfterTransition()
        }
        else
        {
            finish()
        }
    }
}
