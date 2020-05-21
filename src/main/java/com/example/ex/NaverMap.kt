package com.example.ex

import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_naver_map.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.jetbrains.anko.doAsync
import org.jsoup.Jsoup
import org.jsoup.nodes.Document


class NaverMap : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_naver_map)
        //val mapView = MapView(this)
        //map_view.addView(mapView)
        //New_Marker(mapView)

        //val asy = JsoupAsyncTask()
        //asy.execute()
        button2.setOnClickListener {
            val ref = FirebaseDatabase.getInstance().getReference("/아파트/경기도/의왕시/고천동")
            ref.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    var str:String? = null
                    loop@ for(snapshot in p0.children){
                        for(snapshot2 in snapshot.children){
                            when(snapshot2.key){
                                "시세URL" -> {
                                    str = snapshot2.value.toString()
                                    Log.d("이거",str)
                                    break@loop
                                }
                            }
                        }
                    }

                    doAsync {
                        val doc =
                            Jsoup.connect(str)
                                .get()
                        Log.d("이거", doc.toString())
                    }
                }

            })
        }

    }
    fun New_Marker(map:MapView){
        val marker = MapPOIItem()
        marker.itemName = "Default Marker"
        marker.tag = 0
        marker.mapPoint = MapPoint.mapPointWithGeoCoord(37.567425, 126.977722)
        marker.markerType = MapPOIItem.MarkerType.BluePin // 기본으로 제공하는 BluePin 마커 모양.
        marker.selectedMarkerType =
            MapPOIItem.MarkerType.RedPin // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
        map.addPOIItem(marker)
    }
}
class JsoupAsyncTask:AsyncTask<Int,Int,Int>(){
    var doc:Document? = null
    override fun doInBackground(vararg params: Int?): Int {
        doc =
            Jsoup.connect("https://m.r114.com/?_c=memul&_m=complex&_a=detail&complexCd=A02104424700017&mmcode=A01001")
                .get()
        val AA = doc!!.getElementsByClass("tbl_type1 exc").toString()
        Log.d("이거",AA)
        return 0
    }

    override fun onPostExecute(result: Int?) {
    }

}