package songsongsong.hahaha

import android.Manifest
import android.app.*
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.preference.PreferenceManager
import android.provider.MediaStore
import android.support.v4.content.ContextCompat
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.*
import songsongsong.ClsColors
import songsongsong.Item
import songsongsong.fileToImageView
import songsongsong.getUniqueFileName
import java.io.File
import java.util.*

class ItemActivity : Activity()
{
    private val titleText: EditText by bind(R.id.title_text)
    private val contentText: EditText by bind(R.id.content_text)

    //照片檔案功能
    private var pictureFileName: String? = null
    //照片元件
    private val picture: ImageView by bind(R.id.picture)
    //寫入外部儲存設備授權請求代碼
    private val intRequestWriteExtenalStoragePermission = 100
    //錄音設備授權請求代碼
    private val intRequestRecordAudioPermission = 101
    //錄音檔案名稱
    private var recFileName: String? = null

    //啟動功能用的請求代碼
    enum class ItemAction
    {
        CAMERA, RECORD, LOCATION, ALARM, COLOR
    }

    //記事物件
    private var item: Item = Item()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        //讀取Action名稱
        val intentAction = intent.action

        // 如果是修改記事
        if (intentAction == "songsongsong.EDIT_ITEM")
        {
            //接收記事物件與設定標題、內容
            item = intent.extras.getSerializable("songsongsong.Item") as Item
            titleText.setText(item.title)
            contentText.setText(item.content)

            //根據記事物件的顏色設定畫面的背景顏色
            findViewById<TableLayout>(R.id.item_container).setBackgroundColor(item.color.parseColor())

            //接收與設定記事標題
            //val intenTitleText = intent.getStringExtra("titleText")
            //titleText.setText(intenTitleText)
        }
    }

    override fun onResume()
    {
        super.onResume()

        //如果有照片檔案名稱
        if (!item.fileName.isNullOrEmpty())
        {
            //照片檔案物件
            val file = getFileName("P", ",jpg")

            //如果照片檔案存在
            if (file.exists())
            {
                //顯示照片元件
                picture.visibility = View.VISIBLE
                //設定照片
                fileToImageView(file.absolutePath, picture)
            }
        }
    }

    //點擊確定與取消按鈕都會呼叫這個函式
    fun onSubmit(view: View)
    {
        //確定按鈕
        if (view.id == R.id.ok_item)
        {
            //讀取使用者輸入的標題與內容
            val titleText = titleText.text.toString()
            val contentText = contentText.text.toString()

            //設定記事物件的標題與內容
            item.title = titleText
            item.content = contentText

            //如果是修改記事
            if (intent.action == "songsongsong.EDIT_ITEM")
            {
                item.lastModify = Date().time
            }
            else
            {
                item.dat = Date().time

                //建立SharedPreferences物件
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
                //讀取設定的預設顏色
                val sharedPreferencesGetColorValue = sharedPreferences.getInt("DEFAULT_COLOR", -1)
                item.color = getColors(sharedPreferencesGetColorValue)
            }

            //設定回傳的記事物件
            intent.putExtra("songsongsong.Item", item)
            setResult(Activity.RESULT_OK, intent)
        }
        else
        {
            //設定回應結果為取消
            setResult(Activity.RESULT_CANCELED, intent)
        }
        //結束
        finish()
    }

    fun clickFunction(view: View)
    {
        when (view.id)
        {
            R.id.take_picture ->
            {
                //讀取與處理寫入外部儲存設備授權請求
                requestStoragePermission()
            }

            R.id.record_sound ->
            {
                //讀取與處理錄音設備授權請求
                requestRecordPermission()
            }

            R.id.set_location ->
            {
            }

            R.id.set_alarm ->
            {
                //設定提醒日期時間
                processSetAlarm()
            }

            R.id.select_color ->
            {
                //選擇設定顏色功能
                startActivityForResult(Intent(this, ColorActivity::class.java), ItemAction.COLOR.ordinal)
            }
        }
    }

    //更改參數data的型態為Intent?
    override fun onActivityResult(_requestCode: Int, _resultCode: Int, _data: Intent?)
    {
        if (_resultCode == Activity.RESULT_OK)
        {
            val actionRequest = ItemAction.values()[_requestCode]

            when (actionRequest)
            {
            //照片
                ItemAction.CAMERA ->
                {
                    //設定照片檔案名稱
                    item.fileName = pictureFileName
                }

                ItemAction.RECORD ->
                {
                    item.recFileName = recFileName
                }

                ItemAction.LOCATION ->
                {

                }

                ItemAction.ALARM ->
                {

                }
            //設定顏色
                ItemAction.COLOR ->
                {
                    if (_data != null)
                    {
                        var colorID = _data.getIntExtra("colorID", ClsColors.LIGHTGREY.parseColor())
                        item.color = getColors(colorID)

                        //根據選擇的顏色設定畫面的背景顏色
                        findViewById<TableLayout>(R.id.item_container).setBackgroundColor(item.color.parseColor())
                    }
                }
            }
        }
    }

    //改為可以使用類別名稱呼叫這個函式
    companion object
    {
        //轉換顏色值為Colors型態
        fun getColors(_color: Int): ClsColors
        {
            var colorResult = ClsColors.LIGHTGREY

            when (_color)
            {
                ClsColors.BLUE.parseColor() -> colorResult = ClsColors.BLUE
                ClsColors.PURPLE.parseColor() -> colorResult = ClsColors.PURPLE
                ClsColors.GREEN.parseColor() -> colorResult = ClsColors.GREEN
                ClsColors.ORANGE.parseColor() -> colorResult = ClsColors.ORANGE
                ClsColors.RED.parseColor() -> colorResult = ClsColors.RED
            }
            return colorResult
        }
    }

    //拍攝照片
    private fun takePicture()
    {
        //取得照片檔案物件
        val file = getFileName("P", ".jpg")
        val uri: Uri

        //如果是LOLLIPOP_MR1或更新的版本

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            //使用FileProvider建立Uri物件
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", file)
        }
        else
        {
            uri = Uri.fromFile(file)
        }

        //啟動相機元件用的Intent物件
        val intentCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //設定檔案名稱
        intentCamera.putExtra(MediaStore.EXTRA_OUTPUT, uri)
        //啟動相機元件
        startActivityForResult(intentCamera, ItemAction.CAMERA.ordinal)
    }

    //取得照片檔案名稱物件
    private fun getFileName(_prefix: String, _extension: String): File
    {
        //如果記事資料已經有照片檔案名稱
        if (!item.fileName.isNullOrEmpty())
        {
            pictureFileName = item.fileName
        }
        //產生檔案名稱
        else
        {
            pictureFileName = getUniqueFileName()
        }
        //儲存照片的目錄
        val photoPath = File(Environment.getExternalStorageDirectory(), "photo")

        if (!photoPath.exists())
        {
            //建立儲存照片的目錄
            photoPath.mkdir()
        }
        return File(photoPath, "$_prefix$pictureFileName$_extension")
    }

    private fun requestStoragePermission()
    {
        //如果裝置版本是6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //取得授權狀態，參數是請求授權的名稱
            val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

            //如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED)
            {
                //請求授權
                //第一個參數是請求授權的名稱
                //第二個參數是請求代碼
                requestPermissions(arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), intRequestWriteExtenalStoragePermission)
                return
            }
        }
        //如果裝置版本是6.0以下，或是裝置版本6.0以上使用者已授權
        //拍攝照片
        takePicture()
    }

    override fun onRequestPermissionsResult(_requestCode: Int, _permissions: Array<out String>, _grantResults: IntArray)
    {
        //如果是寫入外部儲存設備授權請求
        if (_requestCode == intRequestWriteExtenalStoragePermission)
        {
            //如果在授權請求選擇「允許」
            if (_grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //拍攝照片
                takePicture()
            }
            //如果在授權請求選擇「拒絕」
            else
            {
                Toast.makeText(this, R.string.write_external_storage_denied, Toast.LENGTH_SHORT).show()
            }
        }
        //如果是使用錄音設備授權請求
        else if (_requestCode == intRequestRecordAudioPermission)
        {
            //如果在授權請求選擇「允許」
            if (_grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                //錄音或播放
                processRecord()
            }
        }
        else
        {
            super.onRequestPermissionsResult(_requestCode, _permissions, _grantResults)
        }
    }

    private fun processRecord()
    {
        //錄音檔案名稱
        var recordFile = getRecFileName("R", ".mp3")

        //如果已經有錄音檔，詢問播放或重新錄製
        if (recordFile.exists())
        {
            //詢問播放還是重新錄製的對話框
            val AlertDialogBuilder = AlertDialog.Builder(this)

            AlertDialogBuilder.setTitle(R.string.title_Record)
                    .setCancelable(false)
            AlertDialogBuilder.setPositiveButton(R.string.record_play, DialogInterface.OnClickListener { dialog, which ->
                //播放
                val playIntent:Intent = Intent(this,PlayActivity::class.java)
                playIntent.putExtra("fileName",recordFile.absolutePath)
                startActivity(playIntent)
            })
            AlertDialogBuilder.setNeutralButton(R.string.record_new, DialogInterface.OnClickListener { dialog, which ->
                //重新錄音
                val recordIntent = Intent(this@ItemActivity, RecordActivity::class.java)
                recordIntent.putExtra("fileName", recordFile.absolutePath)
                startActivityForResult(recordIntent, ItemAction.RECORD.ordinal)
            })
            AlertDialogBuilder.setNegativeButton(android.R.string.cancel, null)
            //顯示對話框
            AlertDialogBuilder.show()
        }
        //如果沒有錄音黨，啟動錄音元件
        else
        {
            //錄音
            val recordIntent = Intent(this, RecordActivity::class.java)
            recordIntent.putExtra("fileName", recordFile.absolutePath)
            startActivityForResult(recordIntent, ItemAction.RECORD.ordinal)
        }
    }

    private fun getRecFileName(_prefix: String, _extension: String): File
    {
        //如果記事資料已經有錄音檔案名稱
        if (!item.recFileName.isNullOrBlank())
        {
            recFileName = item.recFileName
        }
        else
        {
            //產生檔案名稱
            recFileName = getUniqueFileName()
        }

        //儲存錄音的目錄
        val recordPath = File(Environment.getExternalStorageDirectory(), "record")

        if (!recordPath.exists())
        {
            //建立儲存錄音的目錄
            recordPath.mkdir()
        }
        //傳回錄音檔案物件
        return File(recordPath, "$_prefix$recFileName$_extension")
    }

    //讀取與處理錄音設備授權請求
    private fun requestRecordPermission()
    {
        //如果裝置版本是6.0以上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            //取得授權狀態，參數是請求授權的名稱
            val hasPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)

            //如果未授權
            if (hasPermission != PackageManager.PERMISSION_GRANTED)
            {
                //請求授權
                //第一個參數是請求授權的名稱
                //第二個參數是請求代碼
                requestPermissions(arrayOf(Manifest.permission.RECORD_AUDIO),
                    intRequestRecordAudioPermission)
                return
            }
        }
        //如果裝置版本是6.0以下
        //或是裝置版本是6.0以上，使用者已經授權
        //錄音或播放
        processRecord()
    }

    private fun processSetAlarm()
    {
        val calendar = Calendar.getInstance()

        if(item.alarmDatetime != 0L)
        {
            //設定為已經儲存的提醒日期時間
            calendar.timeInMillis = item.alarmDatetime
        }

        //讀取年、月、日、時、分
        val yearNoW = calendar.get(Calendar.YEAR)
        val monthNow = calendar.get(Calendar.MONTH)
        val dayNow = calendar.get(Calendar.DAY_OF_MONTH)
        val hourNow = calendar.get(Calendar.HOUR_OF_DAY)
        val minuteNow = calendar.get(Calendar.MINUTE)

        //儲存設定的提醒日期時間
        val alarm = Calendar.getInstance()

        //設定提醒時間
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            alarm.set(Calendar.HOUR_OF_DAY,hourOfDay)
            alarm.set(Calendar.MINUTE,minute)

            item.alarmDatetime = alarm.timeInMillis
        }

        //選擇時間對話框
        val tpd = TimePickerDialog(this,timeSetListener,hourNow,minuteNow,true)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            alarm.set(Calendar.YEAR,year)
            alarm.set(Calendar.MONTH,month)
            alarm.set(Calendar.DAY_OF_MONTH,dayOfMonth)

            //繼續選擇提醒時間
            tpd.show()
        }

        val dpd = DatePickerDialog(this,dateSetListener,yearNoW,monthNow,dayNow)
        dpd.show()
    }

    //點擊畫面右下角的照片縮圖元件
    fun clickPicture(view: View)
    {
        val intent = Intent(this,PictureActivity::class.java)
        intent.putExtra("pictureName",getFileName("P",".jpg").absolutePath)

        //如果裝置的版本是LOLLIPOP
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //加入畫面轉換設定
            startActivity(intent,ActivityOptions.makeSceneTransitionAnimation(this@ItemActivity,picture,"picture").toBundle())
        }
        else
        {
            startActivity(intent)
        }
    }
}
