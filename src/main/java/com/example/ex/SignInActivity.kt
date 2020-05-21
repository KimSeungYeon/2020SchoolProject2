package com.example.ex

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_sign_in.*


class SignInActivity : AppCompatActivity() {//,GoogleApiClient.OnConnectionFailedListener
    private var mFirebaseAuth:FirebaseAuth? = null
    private var check = 0
    private var isLogout = false
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        mFirebaseAuth = FirebaseAuth.getInstance()

        isLogout = intent.getBooleanExtra("logout",false) // 로그아웃 했는지

        search_radio.setOnCheckedChangeListener { group, checkedId ->
            when(search_radio.checkedRadioButtonId){
                R.id.radio_pw -> input_id.visibility = View.VISIBLE
                R.id.radio_id -> input_id.visibility = View.GONE
            }
        }
        checkPermission()
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermission(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //권한이 없을 경우 최초 권한 요청 또는 사용자에 의한 재요청 확인
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // 권한 재요청
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            }
        } else { //허용된 경우
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 100)
            requestPermissions(arrayOf<String>(Manifest.permission.ACCESS_FINE_LOCATION),100)
        }
    }
    override fun onBackPressed() {
        AppUtil.ShowDialog(this,"아파To를 종료하시겠습니까?")
    }
    fun OnSignUp(v: View) {
        startActivity(Intent(this,SignUpActivity::class.java))
    }
    fun OnSearchView(v: View) {
        when(search_window.visibility) {
            View.GONE -> search_window.setVisibility(View.VISIBLE)
            View.VISIBLE -> search_window.setVisibility(View.GONE)
        }
    }
    fun OnLoginButton(view: View) {
            val id = text_id.text.toString()
            val pw = text_pw.text.toString()
            when{
                id.isBlank() -> {
                    Toast.makeText(this,"아이디를 입력해주세요",Toast.LENGTH_SHORT).show()
                    return
                }
                AppUtil.isEmail(this,id) == false -> {
                    Toast.makeText(this,"아이디는 이메일형식으로 입력해주세요.",Toast.LENGTH_SHORT).show()
                    return
                }
                pw.isBlank() -> {
                    Toast.makeText(this,"비밀번호를 입력해주세요",Toast.LENGTH_SHORT).show()
                    return
                }
            }
            mFirebaseAuth?.signInWithEmailAndPassword(id,pw)
            ?.addOnCompleteListener {
                when( it.isSuccessful){
                    true -> {
                        if(!mFirebaseAuth?.currentUser!!.isEmailVerified) {
                            Toast.makeText(this, "가입메일로 인증을 해주세요.", Toast.LENGTH_SHORT).show()
                        }else {
                            Toast.makeText(this, "인증아이디", Toast.LENGTH_SHORT).show()
                            ReturnAct()
                        }
                    }
                    false ->  Toast.makeText(this, "아이디 또는 비밀번호가 잘못되었습니다.", Toast.LENGTH_SHORT).show()

                }
            }

    }
    fun ReturnAct(){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("ID",text_id.text.toString())
        intent.putExtra("PW",text_pw.text.toString())
        intent.putExtra("logout",true)
        when(isLogout) {
            false -> setResult(REQUEST_LOGIN_CODE, intent)
            true -> startActivity(intent)
        }
        finish()
    }
    fun OnClose(view:View){
        search_window.visibility = View.GONE
    }
    fun OnSearch(view: View) {
        val ref = FirebaseDatabase.getInstance().getReference("/user")
        when(input_id.visibility){
            View.VISIBLE -> { // 비밀번호 찾음
                ref.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        var signal = 0
                        var signal2 = 0
                        var id:String? = null
                        if(p0.childrenCount == 0L){
                            AppUtil.ShowDialogNoAction(this@SignInActivity,"존재하는 아이디가 없습니다.")
                        }
                        for(snapshot in p0.children){
                            for(snapshot2 in snapshot.children){
                                when(snapshot2.key){
                                    "name" -> if(snapshot2.value==search_name.text.toString()){signal++}
                                    "phone" -> if(snapshot2.value==search_phone.text.toString()){signal++}
                                    "id" -> if(snapshot2.value==search_id.text.toString()){
                                        id = search_id.text.toString()
                                        signal++
                                        signal2++
                                    }
                                }
                            }
                            if(signal == 3){
                                mFirebaseAuth!!.sendPasswordResetEmail(id!!)
                                AppUtil.ShowDialogNoAction(this@SignInActivity,"고객님의 메일로 비밀번호 재전송 메일을 보냈습니다.")
                                search_window.visibility = View.GONE
                                return
                            } else if(signal2 == 1) {
                                AppUtil.ShowDialogNoAction(this@SignInActivity,"아이디의 정보와 일치하지 않습니다.")
                                return
                            }
                            signal = 0
                            signal2 = 0
                        }
                        AppUtil.ShowDialogNoAction(this@SignInActivity,"존재하는 아이디가 없습니다.")
                    }
                })
            }
            View.GONE -> { //아이디 찾음
                ref.addListenerForSingleValueEvent(object :ValueEventListener{
                    override fun onCancelled(p0: DatabaseError) {}
                    override fun onDataChange(p0: DataSnapshot) {
                        var signal = 0
                        var id:String? = null
                        if(p0.childrenCount == 0L){
                            AppUtil.ShowDialogNoAction(this@SignInActivity,"존재하는 아이디가 없습니다.")
                        }
                        for(snapshot in p0.children){
                            for(snapshot2 in snapshot.children){
                                when(snapshot2.key){
                                    "name" -> if(snapshot2.value==search_name.text.toString()){signal++}
                                    "phone" -> if(snapshot2.value==search_phone.text.toString()){signal++}
                                    "id" -> id = snapshot2.value.toString()
                                }
                            }
                            if(signal == 2){
                                AppUtil.ShowDialogNoAction(this@SignInActivity,"고객님의 아이디는 $id 입니다.")
                                search_window.visibility = View.GONE
                                return
                            }
                            signal = 0
                        }
                        AppUtil.ShowDialogNoAction(this@SignInActivity,"존재하는 아이디가 없습니다.")
                    }
                })
            }
        }
    }
}
