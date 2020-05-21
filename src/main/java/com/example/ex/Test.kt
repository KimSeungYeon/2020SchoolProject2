package com.example.ex

import android.app.Activity
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.UiThread
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.activity_test.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class Test : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        /*val task = BackgroudTask("https://m.r114.com/?_c=memul&_m=complex&_a=detail&complexCd=A02154370700018&mmcode=A01001","79.27/59.7")
        task.AddContext(this)
        task.execute()*/

        //val list = danji_search("https://m.r114.com/?_c=memul&_m=complex&_a=detail&complexCd=A02154370700018&mmcode=A01001","79.27/59.7")

    }
}
/*
class BackgroudTask(val str1:String,val str2:String): AsyncTask<String, Int, Int>() {
    var context:Context? = null
    val list = mutableListOf<String>()
    fun AddContext(context:Context){
        this.context = context
    }
    override fun doInBackground(vararg params: String?): Int {
        val doc = Jsoup.connect(str1).get()
        val parse11 = doc!!.getElementsByClass("tbl_type1 exc")
        val parse12 = parse11[0].getElementsByTag("th")
        val parse13 = parse11[0].getElementsByTag("td")
        val list = mutableListOf<String>()
        var area = "면적(공급/전용):"

        var str = str2 // area 숫자 .까지만 찾아줌
        val split = str!!.split("/") as MutableList<String>
        for(i in 0 until split.size)
            split[i] = split[i].substring(0,split[i].indexOf("."))
        str = split[0]+"/"+split[1]

        for(i in 0 until parse12.size) {
            val string = parse12[i].text()
            when(string){
                "소재지" -> list.add("소재지 "+parse13[i].text())
                "층수" ->   list.add("층수 "+parse13[i].text())
                "단지규모" ->   list.add("단지규모:"+parse13[i].text())
                "건설회사" ->   list.add("건설회사:"+parse13[i].text())
                "주차대수" ->  list.add("주차대수:"+parse13[i].text())
                "난방정보" ->  list.add("난방정보:"+parse13[i].text())
                "입주일" ->  list.add("입주일:"+parse13[i].text())
                "용도지역" ->  list.add("용도지역:"+parse13[i].text())
                else -> {
                    if(string.contains("면적") ) {
                        for(j in 0 until parse13[i].getElementsByClass("cal_chg py")[0].getElementsByTag("span").size)
                            area = area + parse13[i].getElementsByClass("cal_chg py")[0].getElementsByTag("span")[j].text()+"---"
                        list.add(area)
                    }
                }
            }
        }
        val map = mutableMapOf<String,String>()
        val set = mutableSetOf<String>()
        val parse21 = doc!!.getElementsByClass("sliderBox plan") // li 사진 주소찾기
        val parse22 = parse21[0].getElementsByClass("thumb bType")
        for(i in 0 until parse22.size){
            set.add(parse22[i].getElementsByClass("slide_txt").text())
            map.put(parse22[i].getElementsByClass("slide_txt").text(),parse22[i].getElementsByTag("img")[0].attr("src"))
        }
        for(i in 0 until parse22.size){
            if(set.elementAt(i).toString().contains(str)) {
                list.add(set.elementAt(i).toString())
                list.add(map[set.elementAt(i)]!!)
            }
        }
        val con = context as Activity
        val layout = con.findViewById(R.id.SaleLayout) as ViewGroup

        con.runOnUiThread {
            for (i in 0 until list.size) {
                when (i) {
                    list.size - 1 -> {
                        val imageview = ImageView(context)
                        Glide.with(context!!).load(list[i]).into(imageview)
                        layout.addView(imageview)
                    }
                    else -> {
                        val textview = TextView(context)
                        textview.setText(list[i])
                        layout.addView(textview)
                    }
                }
            }
        }
        return 0
    }
}*/