package songsongsong

import android.content.Context
import android.database.sqlite.SQLiteCursorDriver
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper

class MyDBHelper(_context: Context, _name: String, _factory: CursorFactory?, _version: Int) : SQLiteOpenHelper(_context, _name, _factory, _version)
{
    override fun onCreate(_db: SQLiteDatabase)
    {
        //建立應用程式需要的表格
        _db.execSQL(ItemDAO.strSQLCreateTable)

    }

    override fun onUpgrade(_db: SQLiteDatabase, _oldVersion: Int, _newVersion: Int)
    {
        //刪除原有的表格
        _db.execSQL("DROP TABLE IF EXISTS " + ItemDAO.strTableName)

        //呼叫onCreate建立新版的表格
        onCreate(_db)
    }

    companion object
    {
        //資料庫名稱
        private var strDBName = "mydata.db"
        //資料庫版本，資料結構改變的時候要更改這個數值，通常為加一
        private val intVersion = 2

        //需要資料庫的元件呼叫這個函式，這個函式在一般的應用都不需要更改
        fun getDatabase(_context: Context): SQLiteDatabase
        {
            return MyDBHelper(_context, strDBName, null, intVersion).writableDatabase
        }
    }
}