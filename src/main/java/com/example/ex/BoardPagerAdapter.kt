package com.example.ex

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.groupchat.*
import org.jetbrains.anko.sdk27.coroutines.onItemSelectedListener

var recruitlist = ArrayList<Recruit>()
class BoardPagerAdapter(activity:Activity,list:ArrayList<View>,pager:ViewPager) : PagerAdapter() {
    val mlist = list
    val mpager = pager
    val mactivity = activity
    var currentpage = 0
    val ref = FirebaseDatabase.getInstance().reference
    var recruit_talk_name:String? = null
    var recruit_talk_time:String? = null

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var temp = mlist[position]
        container.addView(temp)
        when(position){
            0 -> Set_Listener_One()
            1 -> {
                //어뎁터 연결
                if(recruitlist.size == 0)
                    Set_RecruitList()
                else {
                    mactivity.findViewById<ListView>(R.id.recruit_listview).adapter = RecruitAdapter(recruitlist)
                }
                Set_Listener_Two()
            }
        }
        return mlist[position]
    }
    override fun getCount(): Int {
        return mlist.size
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    override fun getPageTitle(position: Int): CharSequence? {
        when(position){
            0 -> return "자유게시판"
            1 -> return "단체톡모집"
            2 -> return "분양권정보"
            else -> return ""
        }
    }
    fun Set_Listener_One(){

    }
    fun Set_Listener_Two(){

        mactivity.findViewById<ListView>(R.id.recruit_listview).setOnItemClickListener { parent, view, position, id -> // 글 선택
            val selected_item = recruitlist[position]
            mactivity.findViewById<RelativeLayout>(R.id.recruit_layout).visibility = View.GONE
            mactivity.findViewById<LinearLayout>(R.id.recruit_uploader).visibility = View.VISIBLE
            mactivity.findViewById<Button>(R.id.recruit_upload_button).visibility = View.GONE
            mactivity.findViewById<Button>(R.id.recruit_talk_button).visibility = View.VISIBLE

            val title = mactivity.findViewById<TextView>(R.id.recruit_uploader_title)
            title.isFocusableInTouchMode = false
            title.text = selected_item.title
            val content =  mactivity.findViewById<TextView>(R.id.recruit_uploader_content)
            content.isFocusableInTouchMode = false
            content.text = selected_item.content

            mactivity.findViewById<TextView>(R.id.recruit_uploader_writer).text = "작성자: "+selected_item.writer+"  |  작성시간: "+selected_item.time

            recruit_talk_name = selected_item.title
            recruit_talk_time = selected_item.time
        }

        mactivity.findViewById<Button>(R.id.recruit_button).setOnClickListener {  // 글추가 버튼
            mactivity.findViewById<RelativeLayout>(R.id.recruit_layout).visibility = View.GONE
            mactivity.findViewById<LinearLayout>(R.id.recruit_uploader).visibility = View.VISIBLE
            mactivity.findViewById<Button>(R.id.recruit_upload_button).visibility = View.VISIBLE
            mactivity.findViewById<Button>(R.id.recruit_talk_button).visibility = View.GONE
            val title = mactivity.findViewById<TextView>(R.id.recruit_uploader_title)
            title.isFocusableInTouchMode = true
            title.text = ""
            val content =  mactivity.findViewById<TextView>(R.id.recruit_uploader_content)
            content.isFocusableInTouchMode = true
            content.text = ""
            mactivity.findViewById<TextView>(R.id.recruit_uploader_writer).text = "작성자 : "+Singleton.getuser().nickname
        }

        mactivity.findViewById<Button>(R.id.recruit_upload_button).setOnClickListener {  // 톡모집 글 디비에 저장
            val time = AppUtil.Get_Time()
            val title = mactivity.findViewById<TextView>(R.id.recruit_uploader_title).text.toString()
            val content =  mactivity.findViewById<TextView>(R.id.recruit_uploader_content).text.toString()
            val writer = Singleton.getuser().nickname
            val type = "0"
            ref.child("groupchat").child(time).setValue(Recruit(title,content,writer, time, type))
            mactivity.findViewById<RelativeLayout>(R.id.recruit_layout).visibility = View.VISIBLE
            mactivity.findViewById<LinearLayout>(R.id.recruit_uploader).visibility = View.GONE

            recruit_talk_name = title
            recruit_talk_time = time

            ref.child(user_chat_board!!).child(time!!).setValue(Chat(title!!,"public","",time!!,AppUtil.Get_TimeToMin(time!!)))
            ref.child("chat/$time").push().child(Singleton.getuser().nickname)
                .setValue(ChatMessage("%%##%%##${Singleton.getuser().nickname}님이 입장하셨습니다.",Singleton.getuser().nickname,"","", user_id,AppUtil.Get_TimeToMin(time!!)))
            AppUtil.ShowDialogNoAction(mactivity,"모집글이 정상적으로 등록되었습니다.")
        }

        mactivity.findViewById<Button>(R.id.recruit_talk_button).setOnClickListener {  // 단체톡방 입장
            val time = recruit_talk_time
            val time_min = AppUtil.Get_TimeToMin(time!!)
            val title = recruit_talk_name

            for(i in 0 until chatlist!!.size)
                if( chatlist!![i].time == time){
                    Toast.makeText(mactivity,"이미 입장해 있으셔서 입장할 수 없습니다.",Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

            val viewpager = mactivity.findViewById(R.id.pager) as ViewPager
            viewpager.currentItem = 1

            ref.child(user_chat_board!!).child(time!!).setValue(Chat(title!!,"public","",time!!,time_min))
            ref.child("chat/$time").push().child(Singleton.getuser().nickname)
                .setValue(ChatMessage("%%##%%##${Singleton.getuser().nickname}님이 입장하셨습니다.",Singleton.getuser().nickname,"","", user_id,time_min))
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun Set_RecruitList(){
        ref.child("groupchat").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var count = 0
                var title = ""
                var content = ""
                var writer = ""
                var time = ""
                var type = ""
                for(snapshot in p0.children){
                        when(snapshot.key){
                            "title" -> title = snapshot.value.toString()
                            "content" -> content = snapshot.value.toString()
                            "writer" -> writer = snapshot.value.toString()
                            "time" -> time = snapshot.value.toString()
                            "type" -> type = snapshot.value.toString()
                        }
                        count++
                        if(count == 5){
                            count = 0
                            if(type == "0")
                                recruitlist.add(0,Recruit(title, content, writer, time, type))
                        }
                }
                mactivity.findViewById<ListView>(R.id.recruit_listview).adapter = RecruitAdapter(recruitlist)
            }
            override fun onChildRemoved(p0: DataSnapshot) {}
        })
    }
}