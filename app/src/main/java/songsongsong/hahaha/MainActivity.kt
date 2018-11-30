package songsongsong.hahaha

import android.app.*
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.MenuItem
import android.widget.*
import android.content.Intent
import android.os.Build
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import songsongsong.Item
import songsongsong.ItemDAO
import java.util.*

class MainActivity : Activity()
{
    private val showRecyclerView: RecyclerView by bind(R.id.show_listView)
    private lateinit var itemAdapter: RecyclerView.Adapter<ItemAdapterRV.ViewHolder>
    private val rvLayoutManager: RecyclerView.LayoutManager by lazy{LinearLayoutManager(this)}

    private val showAppName: TextView by bind(R.id.show_app_name)

    private val arrlstData = ArrayList<String>()
    private val arradrStr: ArrayAdapter<String> by lazy { ArrayAdapter(this, android.R.layout.simple_list_item_1, arrlstData) }

    //儲存所有記事本的list物件
    private val arrlstItems: ArrayList<Item> = ArrayList()

    //移除原來ListView元件所需的變數
    //private val showListView: ListView by bind(R.id.show_listView)
    //ListView使用的自訂adapter物件
    //private val itemAdr: ItemAdapter by lazy { ItemAdapter(this, R.layout.single_item, arrlstItems) }

    //選單項目物件
    private lateinit var add_item: MenuItem
    private lateinit var search_item: MenuItem
    private lateinit var revert_item: MenuItem
    private lateinit var delete_item: MenuItem

    //已選擇項目數量
    private var selectedCount = 0

    //宣告資料庫功能類別欄位變數
    private val clsItemDAO: ItemDAO by lazy { ItemDAO(applicationContext) }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //加入範例資料
        //arrlstItems.add(Item(1, Date().time, ClsColors.RED, "關於Android Tutorial的事情.", "Hello content", "", "", 0.0, 0.0, 0))
        //arrlstItems.add(Item(2, Date().time, ClsColors.RED, "一隻可愛的小狗狗!", "她的名字叫大熱狗", "", "", 0.0, 0.0, 0))
        //arrlstItems.add(Item(3, Date().time, ClsColors.RED, "一首非常好聽的音樂.", "Hello content", "", "", 0.0, 0.0, 0))

        //如果資料庫是空的，就建立一些範例資料
        if (clsItemDAO.intCount == 0)
        {
            clsItemDAO.createSampleData()
        }
        //取得所有記事資料
        arrlstItems.addAll(clsItemDAO.arrAll)

        //執行RecyclerView元件的設定
        showRecyclerView.setHasFixedSize(true)
        showRecyclerView.layoutManager = rvLayoutManager

        processControllerGetRecycler()
        //processControllersGetEditTextResult()
        //processControllerGetAdapterItem()
        //processControllersShowMessage()

        //移除原來ListView元件執行的工作
        //showListView.adapter = itemAdr

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent)
    {
        //如果被啟動的Activity元件傳回確定的結果
        if (resultCode == Activity.RESULT_OK)
        {
            //讀取標題
            var item = data.extras.getSerializable("songsongsong.Item") as Item

            //是否修改提醒設定
            var updateAlarm = false

            //如果是新增記事
            if (requestCode == 0)
            {
                //新增記事資料到資料庫
                var itemDAOInsert: Item = clsItemDAO.insert(item)

                //設定記事物件的編號
                item.id = itemDAOInsert.id

                //加入新增的記事物件
                arrlstItems.add(item)

                //通知資料改變
                itemAdapter.notifyDataSetChanged()

                //設定為已修改提醒
                updateAlarm = true
            }
            //如果是修改記事
            else if (requestCode == 1)
            {
                //讀取記事編號
                val position = data.getIntExtra("position", -1)

                if (position != -1)
                {
                    //讀取原來的提醒設定
                    val ori = clsItemDAO[item.id]
                    //判斷是否需要設定提醒
                    updateAlarm = item.alarmDatetime != ori?.alarmDatetime

                    //修改資料庫中的記事資料
                    clsItemDAO.upDate(item)

                    arrlstItems.set(position, item)
                    itemAdapter.notifyDataSetChanged()
                }
            }

            //設定提醒
            if(item.alarmDatetime != 0L && updateAlarm)
            {
                val intent = Intent(this, AlarmReceiver::class.java)
                //移除原來的記事標題與訊息框
                intent.putExtra("title",item.title)
                //加入記事編號資料
                intent.putExtra("id",item.id)

                val pi = PendingIntent.getBroadcast(this,item.id.toInt(),intent,PendingIntent.FLAG_ONE_SHOT)

                val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                am.set(AlarmManager.RTC_WAKEUP,item.alarmDatetime,pi)
            }
        }
    }

    /*private fun processControllersShowMessage()
    {
        //建立選單項目點擊監聽物件
        val itemOnClickListener = AdapterView.OnItemClickListener {
            // parent: 使用者操作的ListView物件
            // view: 使用者選擇的項目
            // position: 使用者選擇的項目編號，第一個是0
            // id: 在這裡沒有用途
                parent, view, position, id ->
            Toast.makeText(this@MainActivity, arrlstData[position], Toast.LENGTH_LONG).show()
        }

        val viewOnLongClickListner = View.OnLongClickListener {
            val dialog = AlertDialog.Builder(this@MainActivity)
            dialog.setTitle(R.string.app_name)
                    .setMessage("Long Click: ${getString(R.string.aboutTextViewOnClick)}")
                    .show()
            false
        }
        showAppName.setOnLongClickListener(viewOnLongClickListner)
        showListView.onItemClickListener = itemOnClickListener
    }*/

    /*private fun processControllersGetEditTextResult()
    {
        //建立選單項目點擊監聽物件,position: 使用者選擇的項目編號，第一個是0
        val itemListener = AdapterView.OnItemClickListener { _, _, position, _ ->

            //使用Action名稱建立啟動另一個Activity元件需要的Intent物件
            val intent = Intent("songsongsong.EDIT_ITEM")

            //設定記事編號與標題
            intent.putExtra("position", position)
            intent.putExtra("titleText", arrlstData[position])
            // 呼叫「startActivityForResult」，第二個參數「1」表示執行修改
            startActivityForResult(intent, 1)
        }
        showListView.onItemClickListener = itemListener
    }*/

    /*private fun processControllerGetAdapterItem()
    {
        var itemClickListener = AdapterView.OnItemClickListener() { _, _, position, _ ->

            //讀取選擇的記事物件
            var item = itemAdr.getItem(position)

            //如果已經有勾選的項目
            if (selectedCount > 0)
            {
                //處理是否顯示已選擇項目
                processMenu(item)

                //重新設定記事項目
                itemAdr[position] = item
            }
            else
            {
                val intent = Intent("songsongsong.EDIT_ITEM")
                //設定記事編號與記事物件
                intent.putExtra("position", position)
                intent.putExtra("songsongsong.Item", item)
                //startActivityForResult(intent, 1)
                //改為呼叫這個函式，依照版本啟動Activity元件
                startActivityForVersion(intent,1)
            }
        }
        showListView.onItemClickListener = itemClickListener

        var itemLongClickListener = AdapterView.OnItemLongClickListener() {
            //position: 使用者選擇的項目編號，第一個是0
                _, _, poistion, _ ->
            //讀取選擇的記事物件
            var item = itemAdr.getItem(poistion)
            //處理是否顯示已選擇項目
            processMenu(item)
            //重新設定記事項目
            itemAdr[poistion] = item
            true
        }
        showListView.onItemLongClickListener = itemLongClickListener
    }*/

    private fun processControllerGetRecycler()
    {
        //實作ItemAdapterRV類別，加入註冊監聽事件的工作
        itemAdapter = object : ItemAdapterRV(arrlstItems)
        {
            override fun onBindViewHolder(_holder: ItemAdapterRV.ViewHolder,_position: Int)
            {
                super.onBindViewHolder(_holder, _position)

                //建立與註冊項目點擊監聽物件
                _holder.rootView.setOnClickListener()
                {
                    //讀取選擇的記事物件
                    val arrlstItem = arrlstItems[_position]

                    //如果已經有勾選的項目
                    if(selectedCount > 0)
                    {
                        //處理是否顯示已選擇項目
                        processMenu(arrlstItem)
                        //重新設定記事項目
                        arrlstItems[_position] = arrlstItem
                    }
                    else
                    {
                        val intent = Intent("songsongsong.EDIT_ITEM")
                        //設定記事編號與記事物件
                        intent.putExtra("position",_position)
                        intent.putExtra("songsongsong.Item",arrlstItem)

                        //依照版本啟動Activity元件
                        startActivityForVersion(intent,1)
                    }
                }

                //建立與註冊項目長按監聽物件
                _holder.rootView.setOnLongClickListener()
                {
                    //讀取選擇的記事物件
                    var arrlstItem = arrlstItems[_position]
                    //處理是否已選擇項目
                    processMenu(arrlstItem)
                    //重新設定記事項目
                    arrlstItems[_position] = arrlstItem
                    true
                }
            }
        }
        //設定RecyclerView使用的資料來源
        showRecyclerView.adapter = itemAdapter
    }

    private fun processMenu(item: Item?)
    {
        //如果需要設定記事項目
        if (item != null)
        {
            //設定已勾選的狀態
            item.isSelected = !item.isSelected

            //計算已勾選數量
            if (item.isSelected)
            {
                selectedCount++
            }
            else
            {
                selectedCount--
            }
        }
        //根據選擇的狀況，設定是否顯示選單項目
        add_item.isVisible = (selectedCount == 0)
        search_item.isVisible = (selectedCount == 0)
        revert_item.isVisible = (selectedCount > 0)
        delete_item.isVisible = (selectedCount > 0)

        //通知項目勾選狀態改變
        itemAdapter.notifyDataSetChanged()
    }

    fun aboutApp(view: View)
    {
        //建立啟動另一個Activity元件需要的Intent物件
        //建構式的第一個參數:「this」
        //建構式的第二個參數:「Activity」
        val intent = Intent(this, AboutActivity::class.java)

        //呼叫「startActivity」參數為一個建立好的Intent物件
        //這行敘述行以後，如果沒有任何錯誤，就會啟動指定的元件
        startActivity(intent)

        // 顯示訊息框，指定三個參數
        // Context：通常指定為「this」
        // String或int：設定顯示在訊息框裡面的訊息或文字資源
        // int：設定訊息框停留在畫面的時間
        //Toast.makeText(this, R.string.app_name, Toast.LENGTH_LONG).show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean
    {
        menuInflater.inflate(R.menu.main_menu, menu)

        //取得選單項目物件
        add_item = menu.findItem(R.id.add_item)
        search_item = menu.findItem(R.id.search_item)
        revert_item = menu.findItem(R.id.revert_item)
        delete_item = menu.findItem(R.id.delete_item)

        //設定選單項目
        processMenu(null)

        return true
    }

    fun clickMenuItem(item: MenuItem)
    {
        var itemId = item.itemId

        when (itemId)
        {
            R.id.add_item ->
            {
                //建立啟動另一個Activity元件需要的Intent物件
                val intent = Intent("songsongsong.ADD_ITEM")
                //呼叫「startActivityForResult」第二個參數「0」目前沒有使用
                //startActivityForResult(intent, 0)

                //改為呼叫這個函式，依照版本啟動Activity元件
                startActivityForVersion(intent, 0)
            }

            R.id.search_item ->
            {
            }
        //取消所有已勾選的項目
            R.id.revert_item ->
            {
                //改為使用arrlstItems
                for (i in 0 until arrlstItems.size)
                {
                    val ri = arrlstItems[i]

                    if (ri.isSelected)
                    {
                        ri.isSelected = false
                    }
                }
                selectedCount = 0
                processMenu(null)
            }
        //刪除
            R.id.delete_item ->
            {
                //沒有選擇
                if (selectedCount == 0)
                {
                    return
                }

                //建立與顯示詢問是否刪除的對話框
                val alertDialogBuilder = AlertDialog.Builder(this)
                val message = getString(R.string.DeleteMessage)

                alertDialogBuilder.setTitle(R.string.delete)
                        .setMessage((String.format(message, selectedCount)))
                alertDialogBuilder.setPositiveButton(android.R.string.yes) { dialog, which ->
                    //改為使用arrlstItems
                    var index = arrlstItems.size - 1

                    while (index > -1)
                    {
                        var item = arrlstItems[index]

                        if (item.isSelected)
                        {
                            arrlstItems.remove(item)
                            //刪除資料庫中的記事資料
                            clsItemDAO.delete(item.id)
                        }
                        index--
                    }
                    //通知資料改變
                    selectedCount = 0
                    processMenu(null)
                }
                alertDialogBuilder.setNegativeButton(android.R.string.no, null)
                alertDialogBuilder.show()
            }
        }
        var dialog = AlertDialog.Builder(this@MainActivity)
        dialog.setTitle("MenuItem Test")
                .setMessage(item.title)
                .setIcon(item.icon)
                .show()
    }

    fun clickPreferences(item: MenuItem)
    {
        //啟動設定元件
        //startActivity(Intent(this, PrefActivity::class.java))

        //改為呼叫這個函式，依照版本啟動Activity元件
        startActivityForVersion(Intent(this,PrefActivity::class.java))
    }

    private fun startActivityForVersion(_intent: Intent,_requestCode:Int)
    {
        //如果裝置的版本是LOLIPOP
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //加入畫面轉換設定
            startActivityForResult(_intent,_requestCode,ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())
        }
        else
        {
            startActivityForResult(_intent,_requestCode)
        }
    }

    private fun startActivityForVersion(_intent: Intent)
    {
        //如果裝置的版本是LOLIPOP
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            //加入畫面轉換設定
            startActivity(_intent,ActivityOptions.makeSceneTransitionAnimation(this@MainActivity).toBundle())
        }
        else
        {
            startActivity(_intent)
        }
    }

    fun clickAdd(view: View)
    {
        val intent = Intent("songsongsong.ADD_ITEM")
        startActivityForVersion(intent,0)
    }
}