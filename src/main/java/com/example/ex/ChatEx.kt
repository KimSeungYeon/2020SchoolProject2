package com.example.ex


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_chat_ex.*
import kotlinx.android.synthetic.main.activity_main.*


class ChatEx : AppCompatActivity(),GoogleApiClient.OnConnectionFailedListener{

    private var mMessageRecyclerView:RecyclerView? = null

    private var mGoogleApiClient:GoogleApiClient? = null

    //Firebase 인스턴수 변수

    //채팅 구현부
    private var mFirebaseDatabaseReference:DatabaseReference? = null
    private var mMessageEditText:EditText? = null
    private val MESSAGES_CHILE = "messages"
    // 채팅 구현부 recyclerview

    //채팅 읽어오는 부분//
    private var mChatAdapter:ChatAdapter? = null
    //채팅 읽어오는 부분


    //테스트변수
    var num:Int = 0

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }

    override fun onBackPressed() {
        AppUtil.ShowDialog(this,"아파To를 종료하시겠습니까?")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_ex)

        mMessageRecyclerView = findViewById(R.id.message_recycler_view) // xml에 있는 recyclerview 연결

        //채팅
        //채팅 DB초기화, 메시지 초기화
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().reference
        //mMessageEditText = message_edit

        //send_button.setOnClickListener {
            //val chatMessage = ChatMessage(mMessageEditText!!.text.toString(),mUsername!!,mPhotoUrl!!,null,null)
            //mFirebaseDatabaseReference!!.child(MESSAGES_CHILE).child("마이크")
                //.push().setValue(chatMessage)
            //mMessageEditText!!.setText("")
        //}
        //채팅 DB초기화, 메시지 초기화  745P 될때까지 안드로이드



    }


    // 메시지 뷰 홀더
    // 메시지 뷰 홀더



    // 메시지 뷰 홀더
    // 메시지 뷰 홀더
    // 메시지 뷰 홀더
    // 메시지 뷰 홀더

    override fun onConnectionFailed(p0: ConnectionResult) {
        Toast.makeText(this,"Google Play Service error",Toast.LENGTH_SHORT).show()
    }

    override fun onStart() {
        super.onStart()
        mChatAdapter = ChatAdapter(this,mMessageRecyclerView!!)
        //message_recycler_view.adapter = mChatAdapter
        //message_recycler_view.layoutManager = LinearLayoutManager(this)

        //////////////// // 채팅창 추가되면 읽어오기  /////////////
        mFirebaseDatabaseReference!!.child(MESSAGES_CHILE).child("마이크").addChildEventListener(object : ChildEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                var name:String = ""
                var photoUrl:String = ""
                var text:String = ""
                for(snapshot in p0.children){
                    when(snapshot.key){
                        "name" -> name = snapshot.value.toString()
                        "photoUrl" -> photoUrl = snapshot.value.toString()
                        "text" -> text = snapshot.value.toString()
                    }
                }
                //mChatAdapter!!.addList(ChatMessage(text,name,photoUrl,null,null))
                num++
                Log.d("이거",num.toString())
            }
            override fun onChildRemoved(p0: DataSnapshot) {
            }
        })


        //DB 출처
        mFirebaseDatabaseReference!!.child(MESSAGES_CHILE).child("마이크").addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {
                var name:String = ""
                var photoUrl:String = ""
                var text:String = ""
                for(snapshot in p0.children){
                    when(snapshot.key){
                        "name" -> name = snapshot.value.toString()
                        "photoUrl" -> photoUrl = snapshot.value.toString()
                        "text" -> text = snapshot.value.toString()
                    }
                }
                if(!name.isNullOrEmpty()) {
                    //mChatAdapter!!.addList(ChatMessage(text, name, photoUrl, null, null))
                    num++
                    Log.d("이거", num.toString())
                }
            }
        })
        //////////////////////////////
    }

    override fun onStop() {
        super.onStop()
    }

}