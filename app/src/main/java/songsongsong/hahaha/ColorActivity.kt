package songsongsong.hahaha

import android.app.Activity
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import songsongsong.ClsColors

class ColorActivity : AppCompatActivity()
{
    private val colorGallery: LinearLayout by bind(R.id.color_gallery)

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_color)

        //依clsColor所有色碼動態產生BUTTON
        createColorsButton()
    }

    private fun createColorsButton()
    {
        var clsColorListener = ClsColorListener()

        for (c in ClsColors.values())
        {
            //宣告動態Button，並按照色碼轉換10進制後的代碼給予button之id使用
            val btn = Button(this)
            btn.id = c.parseColor()

            //設置Layout的長寬、邊界距離等參數
            val layout = LinearLayout.LayoutParams(128, 128)
            layout.setMargins(6, 6, 6, 6)

            btn.layoutParams = layout
            btn.setBackgroundColor(c.parseColor())
            btn.setOnClickListener(clsColorListener)
            colorGallery.addView(btn)
        }
    }

    private inner class ClsColorListener : View.OnClickListener
    {
        override fun onClick(view: View)
        {
            val clsColorActivityIntentAction = this@ColorActivity.intent.action

            //經由設定元件啟動
            if (clsColorActivityIntentAction != null && clsColorActivityIntentAction == "songsongsong.CHOOSE_COLOR")
            {
                //建立SharedPreferences物件
                val editor = PreferenceManager.getDefaultSharedPreferences(this@ColorActivity).edit()

                //儲存預設顏色
                editor.putInt("DEFAULT_COLOR",view.id)

                //寫入設定值
                editor.commit()
            }
            //經由新增或修改記事的元件啟動
            else
            {
                intent.putExtra("colorID", view.id)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }
    }
}
