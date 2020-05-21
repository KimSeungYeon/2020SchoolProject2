package com.example.ex


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.location.LocationManager
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.RequiresApi
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.board.*
import kotlinx.android.synthetic.main.fragment_1.*
import kotlinx.android.synthetic.main.fragment_1.sale_upload
import kotlinx.android.synthetic.main.fragment_1.writing_area
import kotlinx.android.synthetic.main.fragment_2.*
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import org.jsoup.Jsoup


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
var isTalk = false
var TalkTime:String? = null
var mChatAdapter:ChatAdapter? = null

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment1.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment1 : Fragment(),MapView.POIItemEventListener{
    // TODO: Rename and change types of parameters
    private var lm:LocationManager? = null
    private var mMapView:MapView? = null
    private var param1: String? = null
    private var param2: String? = null
    private val ref = FirebaseDatabase.getInstance().reference
    private var apartlist:MutableList<ApartInfo>? = null
    private var markers = mutableListOf<MapPOIItem>()
    private var load_marker_size = 0
    private var use_marker_size = 0
    private var last_clicked_marker = 0
    private var selected_apartInfo:ApartInfo? = null
    private var selected_si:String? = null
    private var selected_gu:String? = null
    private var selected_dong:String? = null
    private var board_list:ArrayList<SaleItem>? = null
    private var selected_saleitem:SaleItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_1, container, false)
    }


    override fun onStart() {            // setting 설정부분
        Set_Spinner(this.context!!)
        Set_MapView(this.activity!!)
        Set_MapEvent()
        View.inflate(context,R.layout.board,board) // 게시판 나올 레이아웃
        Set_ClickListener()
        super.onStart()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment1().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
    fun  Get_Array_Id(str:String):Int{
        var id:Int
        when(str){
            "수원시" -> id = R.array.수원시
            "의왕시" -> id = R.array.의왕시


            "장안구" -> id = R.array.장안구 //수원시
            "영통구" -> id = R.array.영통구
            else -> id = 1
        }
        Log.d("id값",id.toString())
        return id
    }

    fun Set_MapView(act:FragmentActivity){
        mMapView = MapView(act)
        val mapViewContainer = map_view as ViewGroup
        mapViewContainer.addView(mMapView)
        mMapView!!.setPOIItemEventListener(this)
    }
    fun Get_Marker(i:Int):MapPOIItem{
        val marker = MapPOIItem()
        marker.tag = i
        marker.markerType = MapPOIItem.MarkerType.BluePin
        marker.selectedMarkerType = MapPOIItem.MarkerType.RedPin
        return marker
    }


    fun Set_MapEvent(){

    }
    fun Set_Spinner(context:Context){
        var isAvailableSign = false
        val spinner1 = spinner_si as Spinner
        val list1 = mutableListOf<String>("시/군","의왕시","수원시")
        spinner1.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,list1)

        val spinner2 = spinner_gu as Spinner
        spinner2.adapter = ArrayAdapter<String>(context,R.layout.spinner_item, mutableListOf<String>("구"))

        val spinner3 = spinner_dong as Spinner
        spinner3.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,mutableListOf<String>("동/읍/면"))

        spinner1.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when(list1[position]){
                    "의왕시" -> {
                        selected_gu = ""
                        spinner2.visibility = View.GONE
                        spinner3.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,resources.getStringArray(Get_Array_Id(list1[position])))
                    }
                    "수원시" -> {
                        spinner2.visibility = View.VISIBLE
                        val gu = resources.getStringArray(Get_Array_Id(list1[position]))
                        val dong = resources.getStringArray(Get_Array_Id(gu[0]))
                        spinner2.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,gu)
                        spinner3.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,dong)
                        isAvailableSign = true
                    }
                    "시/군" -> {
                        isAvailableSign = false
                        spinner2.visibility = View.VISIBLE
                        spinner2.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,mutableListOf<String>("구"))
                        spinner3.adapter = ArrayAdapter<String>(context,R.layout.spinner_item, mutableListOf<String>("동/읍/면"))
                    }
                }
            }
        }
        spinner2.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if(isAvailableSign == true){
                    val dong = resources.getStringArray(Get_Array_Id(spinner2.selectedItem.toString()))
                    spinner3.adapter = ArrayAdapter<String>(context,R.layout.spinner_item,dong)
                }
            }
        }
        search_button.setOnClickListener {
            board.visibility = View.GONE
            map_view.visibility = View.VISIBLE
            if(spinner1.selectedItem.toString() != "시/군" &&
                spinner3.selectedItem.toString() != "동/읍/면") {
                val si = spinner1.selectedItem.toString()
                selected_si = si
                val gu = spinner2.selectedItem.toString()
                val dong = spinner3.selectedItem.toString()
                selected_dong = dong
                when (spinner2.visibility) {
                    View.GONE -> {  //구가 없는 시
                        ref.child("아파트/경기도/$si/$dong").addListenerForSingleValueEvent(object : ValueEventListener{
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onDataChange(p0: DataSnapshot) {
                                var lat:Double = 0.0
                                var lng:Double = 0.0
                                var name:String =""
                                var infoUrl:String =""
                                var priceUrl:String =""
                                var address:String =""
                                var list:List<String> = listOf()
                                apartlist?.clear()
                                apartlist = mutableListOf()
                               for(snapshot in p0.children){
                                   for(snapshot2 in snapshot.children) {
                                       when(snapshot2.key){
                                           "경도" -> {
                                               lng = snapshot2.value.toString().toDouble()
                                           }
                                           "위도" -> {
                                               lat = snapshot2.value.toString().toDouble()
                                           }
                                           "아파트이름" -> {
                                               name = snapshot2.value.toString()
                                           }
                                           "정보URL" -> {
                                               infoUrl = snapshot2.value.toString()
                                           }
                                           "시세URL" -> {
                                               priceUrl = snapshot2.value.toString()
                                           }
                                           "주소" -> {
                                               address = snapshot2.value.toString()
                                           }
                                           "면적" -> {
                                               list = snapshot2.value as List<String>
                                           }
                                       }
                                   }
                                   val apartInfo = ApartInfo(name,address,infoUrl,priceUrl,lat,lng,list)
                                   apartlist!!.add(apartInfo)
                               }
                                load_marker_size = apartlist!!.size
                                ShowMarkerOnMap()
                            }
                        })
                    }
                    View.VISIBLE -> {  //구가 있는 시
                        selected_gu = gu
                        Toast.makeText(context,si+gu+dong,Toast.LENGTH_LONG).show()
                    }
                }
            } else {
                Toast.makeText(context,"시/군 과 동을 선택해주세요",Toast.LENGTH_LONG).show()
            }
        }

    }

    fun ShowMarkerOnMap(){
        for(i in 0 until load_marker_size) {
            if(i >= use_marker_size) {
                markers.add(Get_Marker(i))
                markers[i].itemName = apartlist!!.get(i).name
                markers[i].mapPoint = MapPoint.mapPointWithGeoCoord(apartlist!!.get(i).Lat!!, apartlist!!.get(i).Lng!!)
                mMapView!!.addPOIItem(markers[i])
            } else {
                markers[i].itemName = apartlist!!.get(i).name
                markers[i].mapPoint = MapPoint.mapPointWithGeoCoord(apartlist!!.get(i).Lat!!, apartlist!!.get(i).Lng!!)
            }
        }
        if(use_marker_size > load_marker_size){
            for(i in load_marker_size until use_marker_size)
                markers[i].mapPoint = MapPoint.mapPointWithGeoCoord(apartlist!!.get(0).Lat!!,apartlist!!.get(0).Lng!!)
        }
        mMapView!!.deselectPOIItem(markers[last_clicked_marker])
        val move_lat = apartlist!!.sumByDouble{ it.Lat!!}/load_marker_size
        val move_lng = apartlist!!.sumByDouble{ it.Lng!!}/load_marker_size
        mMapView!!.setMapCenterPointAndZoomLevel(MapPoint.mapPointWithGeoCoord(move_lat,move_lng),3,false)
        use_marker_size = load_marker_size
    }
    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) { // 이거 먼저실행
        map_view.visibility = View.GONE
        board.visibility = View.VISIBLE
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCalloutBalloonOfPOIItemTouched(  // 이후 이거실행
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {
        selected_apartInfo = apartlist!!.get(p1!!.tag.toInt())
        Log.d("이거",selected_apartInfo!!.address)

        ref.child("sale/경기도/$selected_si/$selected_gu/$selected_dong/${selected_apartInfo!!.name}")
            .addValueEventListener(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}
                override fun onDataChange(p0: DataSnapshot) {
                    var list = arrayListOf<SaleItem>()
                    var area:String? = null
                    var floor:String? = null
                    var image:String? = null
                    var price:String? = null
                    var type:Int? = null
                    var writer:String? = null
                    var writerid:String? = null
                    var writing:String? = null
                    var time:String? = null
                    if(p0.childrenCount == 0L){
                        list.add(SaleItem("","해당 매물이 없습니다.","","","","","","",1,""))
                        sale_list.adapter = SaleItemAdapter(list)
                        board_list = list
                        return
                    }
                   for(snapshot in p0.children){
                       for(snapshot2 in snapshot.children) {
                           when(snapshot2.key){
                               "area" -> area = snapshot2.value.toString()
                               "floor" -> floor = snapshot2.value.toString()
                               "image" -> image = snapshot2.value.toString()
                               "price" -> price = snapshot2.value.toString()
                               "time" -> time = snapshot2.value.toString()
                               "type" -> type = snapshot2.value.toString().toInt()
                               "writer" -> writer = snapshot2.value.toString()
                               "writerid" -> writerid = snapshot2.value.toString()
                               "writing" -> writing = snapshot2.value.toString()
                           }
                       }
                       list.add(SaleItem(image!!,"매물이름: "+selected_apartInfo!!.name!!,"가격: "+price!!,area!!,"층수: "+floor!!,writer!!,writerid!!,writing!!,type!!,"작성시간: "+time!!))
                   }
                    if(list.size>1) {
                        list = list.reversed() as ArrayList<SaleItem>
                    }
                    board_list = list
                    sale_list.adapter = SaleItemAdapter(list)
                }
            })


        Log.d("이거",p1!!.itemName.toString()+"121")
    }
    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {}
    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {
        Log.d("이거",p1!!.itemName.toString())
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun Set_ClickListener(){
        OnSale.setOnClickListener {
            if(Singleton.getuser().type!="broker"){
                Toast.makeText(context,"중개인만 사용 가능합니다.",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            board.visibility = View.GONE
            sale_uploader.visibility = View.VISIBLE
            val spinner = writing_area as Spinner
            val data = selected_apartInfo!!.area!!
            val adapter = ArrayAdapter<String>(context,R.layout.support_simple_spinner_dropdown_item,data)
            spinner.adapter = adapter
            sale_upload.setOnClickListener {  // 매물 게시글 올리기
                val time = AppUtil.Get_Time()
                val item = SaleItem("",selected_apartInfo!!.name!!,writing_price.text.toString(),writing_area.selectedItem.toString(),writing_floor.text.toString()
                ,Singleton.getuser().nickname,Singleton.getuser().id,writing_article.text.toString(),0,time) // 타입 0 정상등록 , 타입 1 거래완료 , 타입 2 신고받은물건, 타입 3 삭제
                ref.child("sale/경기도/$selected_si/$selected_gu/$selected_dong/${selected_apartInfo!!.name}")
                    .child(time).setValue(item)
                writing_price.setText("")
                writing_floor.setText("")
                writing_article.setText("")
                sale_uploader.visibility = View.GONE
                board.visibility = View.VISIBLE
                Thread.sleep(1000)
                AppUtil.ShowDialogNoAction(activity!!,"정상적으로 등록되었습니다.")
            }
        }
        sale_list.setOnItemClickListener { parent, view, position, id -> // 매물리스트 클릭리스너
            board.visibility = View.GONE
            SaleLayoutScroll.visibility = View.VISIBLE
            selected_saleitem = board_list!![position]
            SaleLayout.removeAllViews()

            Set_SaleBoard_Text(position)
            val task = BackgroudTask(selected_apartInfo!!.infoURL!!,selected_saleitem!!.area)
            task.AddContext(context!!)
            task.execute()
        }
        CalltoBroker.setOnClickListener {  // 전화하기
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+Singleton.getuser().phone)))
        }

        TalktoBroker.setOnClickListener {   //톡하기  %%##%%## 이거 입장
            val time = AppUtil.Get_Time()
            TalkTime = time
            val time_min = AppUtil.Get_Time_Min()
            if(Singleton.saveid(selected_saleitem!!.writerid) != Singleton.saveid(Singleton.getuser().id)) {
                ref.child(user_chat_board!!).child(TalkTime!!).setValue(Chat(selected_saleitem!!.writer,"private","",time,time_min))
                ref.child("chatboard/" + Singleton.saveid(selected_saleitem!!.writerid)).child(TalkTime!!)
                    .setValue(Chat(Singleton.getuser().nickname,"private","",time,time_min))
                //chatlist!!.add(0,Chat(selected_saleitem!!.writer,"private","",time,time_min))
                val board = activity!!.findViewById(R.id.chatboard) as ListView
                board.adapter = ChatBoardAdapter(chatlist!!)
                val viewpager = activity!!.findViewById(R.id.pager) as ViewPager
                viewpager.currentItem = 1
                //val listview =  activity!!.findViewById(R.id.chatboard) as ListView
                //listview.visibility = View.GONE
                //val messagelayout =  activity!!.findViewById(R.id.chat_view) as LinearLayout
                //messagelayout.visibility = View.VISIBLE
                //val recyclerview = activity!!.findViewById(R.id.message_recycler_view) as RecyclerView
                //mChatAdapter = ChatAdapter(context!!,recyclerview)
                //recyclerview.adapter = mChatAdapter
                //recyclerview.layoutManager = LinearLayoutManager(context)

                ref.child("chat/$TalkTime").push().child(Singleton.getuser().nickname)
                    .setValue(ChatMessage("%%##%%##${Singleton.getuser().nickname}님이 입장하셨습니다.",Singleton.getuser().nickname,"","", user_id,time_min))
                ref.child("chat/$TalkTime").push().child(selected_saleitem!!.writer)
                    .setValue(ChatMessage("%%##%%##${selected_saleitem!!.writer}님이 입장하셨습니다.",selected_saleitem!!.writer,"","", selected_saleitem!!.writerid,time_min))
            } else {
                Toast.makeText(context,"자신에게 톡을 하실수 없습니다.",Toast.LENGTH_SHORT).show()
            }
        }

    }
    fun Set_SaleBoard_Text(pos:Int){
        sale_article_name.setText("매물명: "+selected_apartInfo!!.name!!)
        when(board_list!![pos].type){
            0 -> sale_article_state.setText("매물상태: 판매중")
            1 -> sale_article_state.setText("매물상태: 판매완료")
            else -> sale_article_state.setText("아직 수정중")
        }
        sale_article_price.setText(board_list!![pos].price)
        sale_article_area.setText("면적: "+board_list!![pos].area)
        sale_article_floor.setText(board_list!![pos].floor)
        sale_article_writer.setText("작성자: "+board_list!![pos].writer)
        sale_article_writing.setText(board_list!![pos].writing)
        sale_article_time.setText(board_list!![pos].time)
    }
}
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
        Log.d("이거",split[0])
        Log.d("이거",split[1])
        for(i in 0 until split.size)
            split[i] = split[i].substring(0, split[i].indexOf("."))
        str = split[0]+"/"+split[1]

        for(i in 0 until parse12.size) {
            val string = parse12[i].text()
            when(string){
                "소재지" -> list.add("소재지 :"+parse13[i].text())
                "층수" ->   list.add("층수 :"+parse13[i].text())
                "단지규모" ->   list.add("단지규모 :"+parse13[i].text())
                "건설회사" ->   list.add("건설회사 :"+parse13[i].text())
                "주차대수" ->  list.add("주차대수 :"+parse13[i].text())
                "난방정보" ->  list.add("난방정보 :"+parse13[i].text())
                "입주일" ->  list.add("입주일 :"+parse13[i].text())
                "용도지역" ->  list.add("용도지역 :"+parse13[i].text())
                else -> {
                    if(string.contains("면적") ) {
                        for(j in 0 until parse13[i].getElementsByClass("cal_chg py")[0].getElementsByTag("span").size)
                            area = area + parse13[i].getElementsByClass("cal_chg py")[0].getElementsByTag("span")[j].text()+"∏ "
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
                list.add("전체배치:"+set.elementAt(i).toString())
                list.add(map[set.elementAt(i)]!!)
            }
        }
        val con = context as Activity
        val layout = con.findViewById<ViewGroup>(R.id.SaleLayout)

        con.runOnUiThread {
            loop@ for (i in 0 until list.size) {
                when (i) {
                    list.size-1 -> {
                        if(!list[i].contains("http")){
                            val textview = TextView(context)
                            textview.setText(list[i])
                            layout.addView(textview)
                            break@loop
                        }
                        val imageview = ImageView(context)
                        Glide.with(context!!).load(list[i]).into(imageview)
                        layout.addView(imageview)
                    }
                    else -> {
                        val textview = TextView(context)
                        textview.setText(list[i])
                        textview.textSize = 17.0f
                        if(i%2==1)
                            textview.setBackgroundColor(Color.rgb(213, 232, 150))
                        else
                            textview.setBackgroundColor(Color.rgb(213, 223, 190))
                        layout.addView(textview)
                    }
                }
            }
        }
        return 0
    }
}