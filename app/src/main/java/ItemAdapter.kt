package songsongsong

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import songsongsong.hahaha.R

class ItemAdapter(_context: Context, private val _resource: Int, private val _items: MutableList<Item>) : ArrayAdapter<Item>(_context, _resource, _items)
{
    override fun getView(_position: Int, _convertView: View?, _parent: ViewGroup): View
    {
        val itemView: LinearLayout

        //讀取目前位置的記事物件
        var item = getItem(_position)

        if (_convertView == null)
        {
            //建立項目畫面元件
            itemView = LinearLayout(context)
            var vInflater = Context.LAYOUT_INFLATER_SERVICE
            var li = context.getSystemService(vInflater) as LayoutInflater
            li.inflate(_resource, itemView, true)
        }
        else
        {
            itemView = _convertView as LinearLayout
        }

        //讀取記事顏色、已選擇、標題與日期時間元件
        var vTypeColor: RelativeLayout = itemView.findViewById(R.id.type_color)
        var vSelectedItem: ImageView = itemView.findViewById(R.id.selected_item)
        var vTitleView: TextView = itemView.findViewById(R.id.titleText)
        var vDateView: TextView = itemView.findViewById(R.id.dateText)

        //設定記事顏色
        val vBackground = vTypeColor.background as GradientDrawable
        vBackground.setColor(item.color.parseColor())

        //設定標題日期與時間
        vTitleView.text = item.title
        vDateView.text = item.localeDat

        //設定是否已選擇
        vSelectedItem.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE
        return itemView
    }

    //設定指定編號的記事資料
    operator fun set(index: Int, item: Item)
    {
        if (index >= 0 && index < _items.size)
        {
            _items[index] = item
            notifyDataSetChanged()
        }
    }

    //讀取指定編號的記事資料
    operator fun get(index: Int): Item
    {
        return _items[index]
    }
}