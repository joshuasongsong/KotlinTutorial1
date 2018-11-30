package songsongsong.hahaha

import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class PrefActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)

        //載入PrefFragment元件
        fragmentManager.beginTransaction().replace(android.R.id.content,PrefFragment()).commit()
    }

    // Android 4.4、API level 19加入的函式
    // API level 19以後的版本必須覆寫這個函式，
    //    檢查使用的Fragment是否有效
    fun isValidFragment(_fragmentName: String):Boolean
    {
        return PrefFragment::class.java.name == _fragmentName
    }
}
