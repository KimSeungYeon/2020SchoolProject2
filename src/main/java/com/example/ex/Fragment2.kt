package com.example.ex

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.annotation.LongDef
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_2.*
import java.net.URLDecoder

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
/**
 * A simple [Fragment] subclass.
 * Use the [Fragment2.newInstance] factory method to
 * create an instance of this fragment.
 */
var chatlist:ArrayList<Chat>? = null
class Fragment2 : Fragment() {
    // TODO: Rename and change types of parameters
    val ref = FirebaseDatabase.getInstance().reference
    var user_chat_board:String? = null
    var message_submit_on:Boolean? = null
    var read:String? = URLDecoder.decode("","utf-8")
    var ChatSet = mutableSetOf<String>()
    var private_listener:ChildEventListener? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_2, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        val chatboard = chatboard as ListView
        chatlist = ArrayList<Chat>()
        Set_ChatList()
        Set_Listener()
        super.onStart()
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Fragment2.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Fragment2().apply {
                arguments = Bundle().apply {
                }
            }
    }


    fun Set_ChatList(){
        ref.child("chatboard").child(Singleton.saveid(user_id!!)).addValueEventListener(object :ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.childrenCount == 0L)
                    return
                chatlist = arrayListOf()
                var chatName:String? = null
                var chatType:String? = null
                var lastMessage:String? = null
                var time:String? = null
                var lastMessageTime:String? = null
                var count = 0
                for(snapshot in p0.children){
                    for(snapshot2 in snapshot.children){
                        when(snapshot2.key){
                            "chatName" -> chatName = snapshot2.value.toString()
                            "chatType" -> chatType = snapshot2.value.toString()
                            "lastMessage" -> lastMessage = snapshot2.value.toString()
                            "time" -> time = snapshot2.value.toString()
                            "lastMessageTime" -> lastMessageTime = snapshot2.value.toString()
                        }
                        count++
                        if( count == 5 ){
                            count = 0
                            chatlist!!.add(Chat(chatName!!,chatType!!,lastMessage!!,time!!,lastMessageTime!!))
                        }
                    }
                }
                chatboard.adapter = ChatBoardAdapter(chatlist!!)
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun Set_Listener(){

        chatboard.setOnItemClickListener { parent, view, position, id ->
            message_submit_on = false
            val selectItem = parent.adapter.getItem(position) as Chat
            chatboard.visibility = View.GONE
            chat_view.visibility = View.VISIBLE
            val recyclerview = activity!!.findViewById(R.id.message_recycler_view) as RecyclerView
            mChatAdapter = ChatAdapter(context!!, recyclerview)
            recyclerview.adapter = mChatAdapter
            recyclerview.layoutManager = LinearLayoutManager(context)
            TalkTime = selectItem.time // 방 아이디
            var count = 2
            var people_set = mutableSetOf<String>()
            if(ChatSet.contains(TalkTime!!)){
                ref.child("chat").child(TalkTime!!).removeEventListener(private_listener!!)
            }
            when (selectItem.chatType) {
                    "private" -> {
                        private_listener = object:ChildEventListener{
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                            override fun onChildRemoved(p0: DataSnapshot) {}
                            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                                var count2 = 0
                                var id: String? = null
                                var imageUrl: String? = null
                                var name: String? = null
                                var photoUrl: String? = null
                                var text: String? = null
                                var time: String? = null
                                for (snapshot in p0.children) {
                                    if (count != 0) {
                                        count--
                                        ChatSet.add(TalkTime!!)
                                        continue
                                    }
                                    for (snapshot2 in snapshot.children) {
                                        when (snapshot2.key) {
                                            "id" -> id = snapshot2.value.toString()
                                            "imageUrl" -> imageUrl = snapshot2.value.toString()
                                            "name" -> name = snapshot2.value.toString()
                                            "photoUrl" -> photoUrl = snapshot2.value.toString()
                                            "text" -> text = snapshot2.value.toString()
                                            "time" -> time = snapshot2.value.toString()
                                        }
                                        count2++
                                        if (count2 == 6) {
                                            if (name != "알림")
                                                people_set.add(name!!)
                                            if (text!!.contains("%%##%%##")) {  // %%##%%## 나갈떄 코드
                                                people_set.remove(name!!)
                                                ref.child("chat").child(selectItem.time).push()
                                                    .child(people_set.elementAt(0))
                                                    .setValue(ChatMessage("상대방이 나가서 응답할 수 없습니다.", "알림", photoUrl, imageUrl, id, time))
                                            }
                                            if (user_id != id || message_submit_on == false)
                                                mChatAdapter!!.addList(ChatMessage(text!!,name!!,photoUrl,imageUrl,id,time))
                                            count2 = 0
                                        }
                                    }
                                }
                            }
                        }
                        ref.child("chat").child(selectItem.time)
                            .addChildEventListener(private_listener!!)
                    }
                    "public" -> {
                        private_listener = object:ChildEventListener{
                            override fun onCancelled(p0: DatabaseError) {}
                            override fun onChildMoved(p0: DataSnapshot, p1: String?) {}
                            override fun onChildRemoved(p0: DataSnapshot) {}
                            override fun onChildChanged(p0: DataSnapshot, p1: String?) {}
                            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                                var count2 = 0
                                var id: String? = null
                                var imageUrl: String? = null
                                var name: String? = null
                                var photoUrl: String? = null
                                var text: String? = null
                                var time: String? = null
                                for (snapshot in p0.children) {
                                    if (count != 0) {
                                        count--
                                        ChatSet.add(TalkTime!!)
                                    }
                                    for (snapshot2 in snapshot.children) {
                                        when (snapshot2.key) {
                                            "id" -> id = snapshot2.value.toString()
                                            "imageUrl" -> imageUrl = snapshot2.value.toString()
                                            "name" -> name = snapshot2.value.toString()
                                            "photoUrl" -> photoUrl = snapshot2.value.toString()
                                            "text" -> text = snapshot2.value.toString()
                                            "time" -> time = snapshot2.value.toString()
                                        }
                                        count2++
                                        if (count2 == 6) {
                                            if (name != "알림")
                                                people_set.add(name!!)
                                            if (text!!.contains("%%##%%##{$name}님이 퇴장")) {  // %%##%%## 나갈떄 코드
                                                people_set.remove(name!!)
                                            }
                                            if (user_id != id || message_submit_on == false)
                                                mChatAdapter!!.addList(ChatMessage(text!!,name!!,photoUrl,imageUrl,id,time))
                                            count2 = 0
                                        }
                                    }
                                }
                            }
                        }
                        ref.child("chat").child(selectItem.time)
                            .addChildEventListener(private_listener!!)
                    }
                }
        }

        send_button.setOnClickListener {  // 메시지 보내기 여기서부터  %%##%%## 이거 입장 하는거 구분
            message_submit_on = true
            if(message_edit.text.toString().isNullOrBlank())
                return@setOnClickListener
            val chat = ChatMessage(message_edit!!.text.toString(),Singleton.getuser().nickname,"","", user_id,AppUtil.Get_Time_Min())
            mChatAdapter!!.addList(chat)
            ref.child("chat/$TalkTime").push().child(Singleton.getuser().nickname)
                .setValue(chat)
            message_edit!!.setText("")
        }
    }
}
