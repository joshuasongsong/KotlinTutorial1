package songsongsong.hahaha

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Environment
import android.support.v4.app.NotificationCompat
import android.widget.Toast
import songsongsong.ItemDAO
import java.io.File

class AlarmReceiver : BroadcastReceiver()
{
    override fun onReceive(_context: Context, _intent: Intent)
    {
        //讀取記事編號
        val id = _intent.getLongExtra("id",0)

        if(id != 0L)
        {
            sendNotify(_context,id)
        }
        //讀取記事標題
        //val title = intent.getStringExtra("title")
        //顯示訊息框
        //Toast.makeText(context, title, Toast.LENGTH_LONG).show()
    }

    private fun sendNotify(_context: Context, _id: Long)
    {
        //建立資料庫物件
        val clsItemDAO = ItemDAO(_context.applicationContext)
        //讀取指定編號的記事物件
        val item = clsItemDAO[_id]

        //建立照片檔案物件
        //儲存照片的目錄
        val photoPath = File(Environment.getExternalStorageDirectory(), "photo")

        val file = File(photoPath, "P${item!!.fileName}.jpg")

        //是否儲存照片檔案
        val pictureExist = item!!.fileName != null && item.fileName!!.length > 0 && file.exists()

        //取得NotificationManager物件
        val nm = _context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = "songsongsong.alarmnotify"
        createChannel(nm, channel, "AlarmNotify", "ATK alarm notify notify channel");

        //建立NotificationCompat.Builder物件
        val builder = NotificationCompat.Builder(_context, channel)

        //如果有儲存照片檔案
        if (pictureExist)
        {
            builder.setSmallIcon(android.R.drawable.star_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(_context.getString(R.string.app_name))

            //建立大型圖片樣式物件
            val bigPictureStyle = NotificationCompat.BigPictureStyle()
            //設定圖片與簡介
            val bitmap = BitmapFactory.decodeFile(file.absolutePath)
            bigPictureStyle.bigPicture(bitmap)
                    .setSummaryText(item.title)
            //設定樣式為大型圖片
            builder.setStyle(bigPictureStyle)
        }
        //如果沒有儲存照片檔案
        else
        {
            //設定圖示、時間、內容標題和內容訊息
            builder.setSmallIcon(android.R.drawable.star_big_on)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(_context.getString(R.string.app_name))
                    .setContentText(item.title)
        }
        //發出通知
        nm.notify(item.id.toInt(), builder.build())
    }

    //建立與設定Notify channel
    //加入裝置版本的判斷，應用程式就不用把最低版本設定為API Level26
    private fun createChannel(_nm:NotificationManager,_id:String,_name:String,_desc:String)
    {
        //如系統版本低於Android8.0(API Level 26)就不執行設定
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        {
            return
        }

        //建立channel物件，參數依序為channel代碼、名稱與等級
        val nChannel = NotificationChannel(_id,_name,NotificationManager.IMPORTANCE_DEFAULT)
        //設定channel的說明
        nChannel.description = _desc
        //設定channel的物件
        _nm.createNotificationChannel(nChannel)

    }
}
