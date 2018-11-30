package songsongsong

import java.util.*

class Item : java.io.Serializable
{
    //編號、日期時間、顏色、標題、內容、照相檔案名稱、錄音檔案名稱、經度、緯度、修改、已選擇
    var id: Long = 0
    var dat: Long = 0
    var color: ClsColors
    var title: String? = null
    var content: String? = null
    var fileName: String? = null
    var recFileName: String? = null
    var latitude: Double = 0.toDouble()
    var longitude: Double = 0.toDouble()
    var lastModify: Long = 0
    var isSelected: Boolean = false

    //裝置區域的日期時間
    val localeDat: String
        get() = String.format(Locale.getDefault(), "%tF %<tR", Date(dat))

    //裝置區域的日期
    val localeDa: String
        get() = String.format(Locale.getDefault(), "%tF", Date(dat))

    //裝置區域的時間
    val localeTime: String
        get() = String.format(Locale.getDefault(), "%tR", Date(dat))

    //提醒日期時間
    var alarmDatetime: Long = 0

    constructor()
    {
        title = ""
        content = ""
        color = ClsColors.LIGHTGREY
    }

    constructor(_id: Long, _dat: Long, _color: ClsColors, _title: String,
                _content: String, _fileName: String, _recFileName: String,
                _latitude: Double, _longitude: Double, _lastModify: Long)
    {
        this.id = _id
        this.dat = _dat
        this.color = _color
        this.title = _title
        this.content = _content
        this.fileName = _fileName
        this.recFileName = _recFileName
        this.latitude = _latitude
        this.longitude = _longitude
        this.lastModify = _lastModify
    }
}