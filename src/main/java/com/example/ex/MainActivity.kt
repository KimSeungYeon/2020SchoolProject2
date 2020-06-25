package com.example.ex

import android.Manifest
import android.app.Activity
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
import org.jetbrains.anko.sdk27.coroutines.onFocusChange
import org.jetbrains.anko.view

var user_chat_board:String? = null
var user_id:String? = null
val REQUEST_LOGIN_CODE = 10001
class MainActivity : AppCompatActivity(){
    private var id:String = ""
    private var pw:String = ""
    private var user:User? = null
    private var selected_page = 0
    private var mAdapter:MyPagerAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val sf = getSharedPreferences("main_save",MODE_PRIVATE)
        val editor = sf.edit()
        editor.putString("user_id","")
        editor.commit()

        id = intent.getStringExtra("ID")
        pw = intent.getStringExtra("PW")

        Get_User()
        Get_UserInfo()
        user_id = id
        user_chat_board = "chatboard/"+id!!.replace(".","_")
        Get_ViewPager()
    }

    override fun onPause() {
        super.onPause()
        saveState()
    }

    override fun onResume() {
        super.onResume()
        restoreState()
    }
    fun saveState(){
        val sf = getSharedPreferences("main_save",MODE_PRIVATE)
        val editor = sf.edit()
        editor.putString("user_chat_board",user_chat_board)
        editor.putString("user_id",user_id)
        editor.putStringSet("user", setOf(user!!.type,user!!.id,user!!.pw,user!!.phone,user!!.name,user!!.nickname,user!!.address))
        editor.putInt("selected_page",selected_page)
        editor.commit()
    }
    fun restoreState(){
        val sf2 = getSharedPreferences("main_save", MODE_PRIVATE)
        if(sf2.getString("user_id","")!=""){
            pager.currentItem = 1
            pager.currentItem = 2
            user_chat_board = sf2.getString("user_chat_board", "")
            user_id = sf2.getString("user_id", "")
            val temp = sf2.getStringSet("user", setOf(""))
            user = User(
                temp.elementAt(0),
                temp.elementAt(1),
                temp.elementAt(2),
                temp.elementAt(3),
                temp.elementAt(4),
                temp.elementAt(5),
                temp.elementAt(6)
            )
            selected_page = sf2.getInt("selected_page", 2)
            id = user!!.id
            pw = user!!.pw
        }
        pager.currentItem = selected_page
    }
    fun Get_ViewPager(){
        val viewPager = pager as CustomViewPager//ViewPager
        val adapter = MyPagerAdapter(supportFragmentManager)
        mAdapter = adapter
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
                ChangeUserInfo()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
    override fun onBackPressed() {
        if(mypage.visibility == View.VISIBLE){
            main_view.visibility = View.VISIBLE
            mypage.visibility = View.GONE
            return
        }

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
                else -> AppUtil.ShowDialog(this, "아파To를 종료하시겠습니까?")
            }
        }
    }
    fun Get_User(){
        var sf:SharedPreferences? = null
        sf = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
        val editor = sf.edit()
        id = intent.getStringExtra("ID")
        pw = intent.getStringExtra("PW")
        editor.putStringSet("LOGIN", setOf(id,pw))
        editor.commit()
        Get_UserInfo()
        Get_ViewPager()
        return
    }
    fun Log_Out(){
            val builder = AlertDialog.Builder(this)
            builder.setTitle("로그아웃 하실건가요?")
                .setPositiveButton("확인"){dialogInterface, i ->
                    val sf:SharedPreferences = getSharedPreferences("LOGIN", Context.MODE_PRIVATE)
                    val editor = sf!!.edit()
                    editor.putStringSet("LOGIN", setOf("0","1"))
                    editor.commit()
                    id = "1"
                    pw = "2"
                    val intent = Intent(this, SignInActivity::class.java)
                    intent.putExtra("logout",true)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("취소",null)
                .show()
    }
    fun ChangeUserInfo(){
            if(mypage.visibility == View.GONE){
                main_view.visibility = View.GONE
                mypage.visibility = View.VISIBLE
            }
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
