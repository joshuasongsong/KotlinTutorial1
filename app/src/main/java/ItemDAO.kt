package songsongsong

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import songsongsong.hahaha.ItemActivity
import java.util.*
import kotlin.collections.ArrayList

//資料功能類別
class ItemDAO(_context: Context)
{
    companion object
    {
        //表格名稱
        const val strTableName = "item"

        //編號表格欄位名稱，固定不變
        const val strKeyID = "_id"

        //其他表格欄位名稱
        private const val strDateTimeColumn = "datetime"
        private const val strColorColumn = "color"
        private const val strTitleColumn = "title"
        private const val strContentColumn = "content"
        private const val strFileNameColumn = "filename"
        private const val strRecfilenameColumn = "recfilename"
        private const val strLatitudeColumn = "latitude"
        private const val strLongitudeColumn = "longitude"
        private const val strLastModifyColumn = "lastmodify"

        //提醒日期時間
        private const val strALARMDATETIME_COLUMN = "alarmdatetime"

        //使用上面宣告的變數建立表格的SQL敘述
        var strSQLCreateTable = "CREATE TABLE " + strTableName + "(" +
                strKeyID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                strDateTimeColumn + " INTEGER NOT NULL, " +
                strColorColumn + " INTEGER NOT NULL, " +
                strTitleColumn + " TEXT NOT NULL, " +
                strContentColumn + " TEXT NOT NULL, " +
                strFileNameColumn + " TEXT, " +
                strRecfilenameColumn + " TEXT, " +
                strLatitudeColumn + " REAL, " +
                strLongitudeColumn + " REAL, " +
                strLastModifyColumn + " INTEGER, " +
                strALARMDATETIME_COLUMN + " INTEGER) "
    }

    //資料庫物件
    private val sqlDB: SQLiteDatabase = MyDBHelper.getDatabase(_context)

    //讀取所有記事物件
    val arrAll: ArrayList<Item>
        get()
        {
            val arrResult = ArrayList<Item>()
            val sqlCursor = sqlDB.query(strTableName, null, null, null, null, null, null, null)

            while (sqlCursor.moveToNext())
            {
                arrResult.add(getRecord(sqlCursor))
            }
            sqlCursor.close()
            return arrResult
        }
    //取得資料數量
    val intCount: Int
        get()
        {
            var intResult = 0
            val sqlCursor = sqlDB.rawQuery("SELECT COUNT(*) FROM " + strTableName, null)

            if (sqlCursor.moveToNext())
            {
                intResult = sqlCursor.getInt(0)
            }

            return intResult
        }

    //關閉資料庫，一般的應用都不需要修改
    fun close()
    {
        sqlDB.close()
    }

    fun insert(item: Item): Item
    {
        //建立準備新增資料的ContentValues物件
        val cv = ContentValues()

        //加入ContentValues物件包裝的新增資料
        itemToContentValues(item, cv)

        //新增第一筆資料並取得編號
        //第一個參數是表格名稱
        //第二個參數是沒有指定欄位值的預設值
        //第三個參數是包裝新增資料的ContentValues物件
        val getInsertID = sqlDB.insert(strTableName, null, cv)

        //設定編號
        item.id = getInsertID

        //回傳結果
        return item
    }

    //修改參數指定的物件
    fun upDate(item: Item): Boolean
    {
        //建立準備修改資料的ContentValues物件
        val cv = ContentValues()

        //加入ContentValues物件包裝的修改資料
        itemToContentValues(item, cv)

        //設定修改資料的條件為編號
        //格式為「欄位名稱=資料」
        val strSQLWhere = strKeyID + "=" + item.id

        //執行修改資料病回傳修改的資料數量是否成功
        return sqlDB.update(strTableName, cv, strSQLWhere, null) > 0
    }

    private fun itemToContentValues(_item: Item, _cv: ContentValues)
    {
        //第一個參數是欄位名稱，第二個參數是欄位的資料
        _cv.put(strDateTimeColumn, _item.dat)
        _cv.put(strColorColumn, _item.color.parseColor())
        _cv.put(strTitleColumn, _item.title)
        _cv.put(strContentColumn, _item.content)
        _cv.put(strFileNameColumn, _item.fileName)
        _cv.put(strRecfilenameColumn, _item.recFileName)
        _cv.put(strLatitudeColumn, _item.latitude)
        _cv.put(strLongitudeColumn, _item.longitude)
        _cv.put(strLastModifyColumn, _item.lastModify)
        _cv.put(strALARMDATETIME_COLUMN,_item.alarmDatetime)
    }

    fun delete(_id: Long): Boolean
    {
        //設定條件為編號，格式為「欄位名稱=資料」
        val strSQLWhere = strKeyID + "=" + _id.toString()
        //刪除指定編號資料並回傳刪除是否成功
        return sqlDB.delete(strTableName, strSQLWhere, null) > 0
    }

    //取得指定編號的資料物件
    operator fun get(_id: Long): Item?
    {
        //準備回傳結果用的物件
        var item: Item? = null
        //使用編號為查詢物件
        val strSQLWhere = strKeyID + "=" + _id.toString()
        //執行查詢
        val sqlCursor = sqlDB.query(strTableName, null, strSQLWhere, null, null, null, null, null)

        //如果有查詢結果
        if (sqlCursor.moveToFirst())
        {
            //讀取包裝一筆資料的物件
            item = getRecord(sqlCursor)
        }
        //關閉Cursor物件
        sqlCursor.close()
        //回傳結果
        return item
    }

    //把Cursor目前的資料包裝為物件
    private fun getRecord(_cursor: Cursor): Item
    {
        val clsItem = Item()

        clsItem.id = _cursor.getLong(0)
        clsItem.dat = _cursor.getLong(1)
        clsItem.color = ItemActivity.getColors(_cursor.getInt(2))
        clsItem.title = _cursor.getString(3)
        clsItem.content = _cursor.getString(4)
        clsItem.fileName = _cursor.getString(5)
        clsItem.recFileName = _cursor.getString(6)
        clsItem.latitude = _cursor.getDouble(7)
        clsItem.longitude = _cursor.getDouble(8)
        clsItem.lastModify = _cursor.getLong(9)
        clsItem.alarmDatetime = _cursor.getLong(10)
        //回傳結果
        return clsItem
    }

    //建立範例資料
    fun createSampleData()
    {
        val itemRed = Item(0, Date().time, ClsColors.RED, "關於Android Tutorial的事情.", "Hello content", "", "", 0.0, 0.0, 0)
        val itemBlue = Item(0, Date().time, ClsColors.BLUE, "一隻非常可愛的小狗狗!", "她的名字叫「大熱狗」，又叫\n作「奶嘴」，是一隻非常可愛\n的小狗。", "", "", 25.04719, 121.516981, 0)
        val itemGreen = Item(0, Date().time, ClsColors.GREEN, "一首非常好聽的音樂！", "Hello content", "", "", 0.0, 0.0, 0)
        val itemOrange = Item(0, Date().time, ClsColors.ORANGE, "儲存在資料庫的資料", "Hello content", "", "", 0.0, 0.0, 0)

        insert(itemRed)
        insert(itemBlue)
        insert(itemGreen)
        insert(itemOrange)
    }
}