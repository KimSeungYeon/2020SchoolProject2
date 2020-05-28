package com.example.ex

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_1.*
import kotlinx.android.synthetic.main.fragment_2.*
import kotlinx.android.synthetic.main.freeboard.*
import kotlinx.android.synthetic.main.groupchat.*

var user_chat_board:String? = null
var user_id:String? = null
val REQUEST_LOGIN_CODE = 10001
class MainActivity : AppCompatActivity(){
    private var previous_login_id:String? = null
    private var id:String = ""
    private var pw:String = ""
    private var user:User? = null
    private var selected_page = 0
    private var isReLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        isReLogin = intent.getBooleanExtra("logout",false)
        Get_User()
        if(!Exist_User()) {
            previous_login_id = id
            startActivityForResult(Intent(this, SignInActivity::class.java), REQUEST_LOGIN_CODE)
        } else {
            FirebaseAuth.getInstance().signInWithEmailAndPassword(id,pw)
                ?.addOnCompleteListener {
                    if(!it.isSuccessful)
                        startActivityForResult(Intent(this, SignInActivity::class.java), REQUEST_LOGIN_CODE)
                }
            Get_UserInfo()
            user_id = id
            user_chat_board = "chatboard/"+id!!.replace(".","_")
            Get_ViewPager()
        }


    }
    fun Get_ViewPager(){
        val viewPager = pager as CustomViewPager//ViewPager
        val adapter = MyPagerAdapter(supportFragmentManager)
        viewPager.setPagingEnabled(false)
        viewPager.adapter = adapter
        viewPager.setAdapter(adapter)
        val tabLayout = tab as TabLayout
        tabLayout.setupWithViewPager(viewPager)
        viewPager.addOnPageChangeListener(object:ViewPager.OnPageChangeListener{
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageSelected(position: Int) {
                selected_page = position
                if(position == 1){
                    chat_view.visibility = View.GONE
                    chatboard.visibility = View.VISIBLE
                }
            }
        })
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == REQUEST_LOGIN_CODE){
            val sf = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            val editor = sf.edit()
            id = data!!.getStringExtra("ID")
            pw = data!!.getStringExtra("PW")
            editor.putStringSet("LOGIN", setOf(id,pw))
            editor.commit()
            Get_UserInfo()
            Get_ViewPager()

            /*if(previous_login_id != id){
                Get_UserInfo()
            }
            if(previous_login_id == ""){
                Get_ViewPager()
            }*/

        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item!!.itemId){
            R.id.sign_out_menu ->{
                Log_Out()
                return true
            }
            R.id.chat_menu -> {
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        if( selected_page == 0) {
            when {
                board.visibility == View.VISIBLE -> {
                    board.visibility = View.GONE
                    map_view.visibility = View.VISIBLE
                }
                sale_uploader.visibility == View.VISIBLE -> {
                    sale_uploader.visibility = View.GONE
                    board.visibility = View.VISIBLE
                }
                SaleLayoutScroll.visibility == View.VISIBLE -> {
                    SaleLayoutScroll.visibility = View.GONE
                    board.visibility = View.VISIBLE
                }
                else -> AppUtil.ShowDialog(this, "아파To를 종료하시겠습니까?")
            }
        }else if( selected_page == 1 ) {
            when {
                chat_view.visibility == View.VISIBLE -> {
                    chat_view.visibility = View.GONE
                    chatboard.visibility = View.VISIBLE
                }
                else -> AppUtil.ShowDialog(this, "아파To를 종료하시겠습니까?")
            }
        } else if (selected_page == 2) {
            when(board_currentpage){
                0 -> {
                    when {
                        freeboard_uploader.visibility == View.VISIBLE -> {
                            freeboard_layout.visibility = View.VISIBLE
                            freeboard_uploader.visibility = View.GONE
                        }
                        else -> AppUtil.ShowDialog(this, "아파To를 종료하시겠습니까?")
                    }
                }
                1 -> {
                    when {
                        recruit_uploader.visibility == View.VISIBLE -> {
                            recruit_layout.visibility = View.VISIBLE
                            recruit_uploader.visibility = View.GONE
                        }
                        else -> AppUtil.ShowDialog(this, "아파To를 종료하시겠습니까?")
                    }
                }
                else -> Toast.makeText(applicationContext,"아직 구현안함",Toast.LENGTH_LONG).show()
            }
        }
    }
    fun Exist_User():Boolean = (id.isNotBlank()&&pw.isNotBlank())
    fun Get_User(){
        var sf:SharedPreferences? = null

        if(isReLogin == true){
            val sf = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            val editor = sf.edit()
            id = intent.getStringExtra("ID")
            pw = intent.getStringExtra("PW")
            editor.putStringSet("LOGIN", setOf(id,pw))
            editor.commit()
            Get_UserInfo()
            Get_ViewPager()
            return
        }

        try {
            sf = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
            val IDAndPW = sf.getStringSet("LOGIN", setOf("", ""))
            id = IDAndPW.elementAt(0)
            pw = IDAndPW.elementAt(1)
        }catch (e:Exception){
            val editor = sf!!.edit()
            editor.putStringSet("LOGIN", setOf("",""))
            editor.commit()
            id = ""
            pw = ""
        }
    }
    fun Log_Out(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃 하실건가요?")
                .setPositiveButton("확인"){dialogInterface, i ->
                    val sf:SharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                    val editor = sf!!.edit()
                    editor.putStringSet("LOGIN", setOf("",""))
                    editor.commit()
                    previous_login_id = id
                    id = ""
                    pw = ""
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.putExtra("logout",true)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("취소",null)
                .show()
    }
    fun Get_UserInfo(){
            val stored_id = id.replace(".","_")
            var address = ""
            var name = ""
            var nickname = ""
            var phone = ""
            var type = ""
            FirebaseDatabase.getInstance().reference.child("user/$stored_id").addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.d("이거","오류")
                }
                override fun onDataChange(p0: DataSnapshot) {
                    for(snapshot in p0.children){
                        when(snapshot.key){
                            "type" -> type = snapshot.value.toString()
                            "name" -> name = snapshot.value.toString()
                            "address" -> address = snapshot.value.toString()
                            "nickname" -> nickname = snapshot.value.toString()
                            "phone" -> phone = snapshot.value.toString()
                        }
                    }
                    user = User(type, id, pw, phone, name, nickname, address)
                    Singleton.setuser(user!!)
                }
            })
    }


}
