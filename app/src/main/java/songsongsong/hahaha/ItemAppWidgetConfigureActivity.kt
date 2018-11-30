package songsongsong.hahaha

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ListView
import songsongsong.Item
import songsongsong.ItemAdapter
import songsongsong.ItemDAO

/**
 * The configuration screen for the [ItemAppWidget] AppWidget.
 */
class ItemAppWidgetConfigureActivity : Activity()
{
    internal var mAppWidgetID = AppWidgetManager.INVALID_APPWIDGET_ID

    //選擇小工具使用的記事項目
    private val itemListView: ListView by bind(R.id.item_list)
    private val clsItemDAO: ItemDAO by lazy {ItemDAO(applicationContext)}
    private val arrlstclsItem: ArrayList<Item> by lazy { clsItemDAO.arrAll }
    private val itemAdapter: ItemAdapter by lazy { ItemAdapter(this, R.layout.single_item, arrlstclsItem) }

    public override fun onCreate(icicle:Bundle?)
    {
        super.onCreate(icicle)
        setResult(Activity.RESULT_CANCELED)

        //改為使用應用程式主畫面
        setContentView(R.layout.activity_main)

        //建立與設定選擇小工具使用的記事項目需要的物件
        itemListView.adapter = itemAdapter
        itemListView.onItemClickListener = itemListener

        val extras = intent.extras

        if(extras != null)
        {
            mAppWidgetID = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,AppWidgetManager.INVALID_APPWIDGET_ID)
        }

        if(mAppWidgetID == AppWidgetManager.INVALID_APPWIDGET_ID)
        {
            finish()
            return
        }
    }

    companion object
    {
        private const val strPrefsName = "songsongsong.ItemAppWidget"
        private const val strPrefPrefixKey = "appwidget_"

        fun saveItemPref(_context: Context, _appWidgetID: Int, _id: Long)
        {
            val prefs = _context.getSharedPreferences(strPrefsName, 0).edit()
            prefs.putLong(strPrefPrefixKey + _appWidgetID, _id)
            prefs.apply()
        }

        //讀取記事編號
        fun loadItemPref(_context: Context, _appWidgetID: Int): Long
        {
            val prefs = _context.getSharedPreferences(strPrefsName, 0)

            return prefs.getLong(strPrefPrefixKey + _appWidgetID, 0)
        }

        //刪除記事編號
        fun deleteItemPref(_context: Context, _appWidgetID: Int)
        {
            val prefs = _context.getSharedPreferences(strPrefsName, 0).edit()
            prefs.remove(strPrefPrefixKey + _appWidgetID)
            prefs.apply()
        }
    }

    //選擇記事項目
    internal val itemListener: AdapterView.OnItemClickListener = AdapterView.OnItemClickListener { _, _, position, _ ->
        val context = this@ItemAppWidgetConfigureActivity

        //讀取與儲存選擇的記事物件
        val item = itemAdapter.getItem(position)
        saveItemPref(context, mAppWidgetID, item.id)

        val appWidgetManager = AppWidgetManager.getInstance(context)
        ItemAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetID)
        val resultValue = Intent()
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetID)
        setResult(Activity.RESULT_OK, resultValue)

        finish()
    }
}

