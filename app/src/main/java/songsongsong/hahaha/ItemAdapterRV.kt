package songsongsong.hahaha

import android.graphics.drawable.GradientDrawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import songsongsong.Item

open class ItemAdapterRV(private val _items: MutableList<Item>) : RecyclerView.Adapter<ItemAdapterRV.ViewHolder>()
{
    override fun onCreateViewHolder(_parent: ViewGroup, _viewType: Int): ItemAdapterRV.ViewHolder
    {
        val v = LayoutInflater.from(_parent.context).inflate(R.layout.single_item, _parent, false)

        return ViewHolder(v)
    }

    override fun onBindViewHolder(_holder: ItemAdapterRV.ViewHolder, _position: Int)
    {
        val item = _items[_position]

        //設定記事顏色
        val background = _holder.rltTypecolor.background as GradientDrawable
        background.setColor(item.color.parseColor())

        //設定記事標題與日期時間
        _holder.txtvTitle.text = item.title
        _holder.txtvDate.text = item.localeDa

        //設定是否已選擇
        _holder.ivwSelectedItem.visibility = if (item.isSelected) View.VISIBLE else View.INVISIBLE
    }

    override fun getItemCount(): Int
    {
        return _items.size
    }

    fun add(item: Item)
    {
        _items.add(item)
        notifyItemInserted(_items.size)
    }

    inner class ViewHolder(var rootView: View) : RecyclerView.ViewHolder(rootView)
    {
        var rltTypecolor: RelativeLayout = itemView.findViewById(R.id.type_color)
        var ivwSelectedItem: ImageView = itemView.findViewById(R.id.selected_item)
        var txtvTitle: TextView = itemView.findViewById(R.id.title_text)
        var txtvDate: TextView = itemView.findViewById(R.id.dateText)
    }
}