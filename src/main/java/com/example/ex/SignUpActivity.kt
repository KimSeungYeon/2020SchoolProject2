package com.example.ex

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignUpActivity : AppCompatActivity() {
    private val databaseRefer = FirebaseDatabase.getInstance().reference
    private var UserType:String = "generaluser"
    private var UserId:String? = null
    private var UserPassword:String? = null
    private var UserPassword_confirm:String? = null
    private var UserPhone:String? = null
    private var UserName:String? = null
    private var UserNickName:String? = null
    private var UserAddress:String = ""
    private var User:User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val radiogroup = sign_radio_group
        radiogroup.setOnCheckedChangeListener { radioGroup, i ->
            when(i){
                R.id.generaluser -> {
                    UserType = "generaluser"
                    name_label.setText("이름")
                    phone.hint = "010-0000-0000"
                    address.setText("")
                    brokerinfo.isVisible = false
                }
                R.id.broker -> {
                    UserType = "broker"
                    name_label.setText("상호")
                    phone.hint = "031-0000-0000"
                    brokerinfo.isVisible = true
                }
            }
        }

    }

    override fun onBackPressed() {
        AppUtil.ShowDialog(this,"회원가입을 취소하시겠습니까?")
    }

    fun OnSubmit(view:View){

        /*
        var auth = FirebaseAuth.getInstance()

        auth?.createUserWithEmailAndPassword("goemtkt@naver.com","tmddus12")
            ?.addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this,"아이디 생성완료!!,메일 보냈다!!",Toast.LENGTH_LONG).show()
                    auth?.signInWithEmailAndPassword("goemtkt@naver.com","tmddus12")
                    auth?.currentUser?.sendEmailVerification()
                    Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"실패",Toast.LENGTH_LONG).show()
                }
            }
        */
        if(FormCheck()) {

            //user 베이스에 넣어주기
            User = User(UserType,UserId!!,UserPassword!!,UserPhone!!,UserName!!,UserNickName!!,UserAddress)
            AppUtil.isAvailableNickName(this,User!!) // 여기 스냅샷에 입력포함

        }
        else {
            Log.d("이거","안됨")
        }
    }

    fun FormCheck():Boolean{
        UserId = identity.text.toString()
        UserPassword = password.text.toString()
        UserPassword_confirm = password_confirm.text.toString()
        UserPhone = phone.text.toString()
        UserName = username.text.toString()
        UserNickName = nickname.text.toString()
        UserAddress = address.text.toString()


        when{
            AppUtil.isEmail(this,UserId) == false -> { Toast.makeText(this,"아이디 형식이 잘못되었어요.",Toast.LENGTH_SHORT).show();return false }
            AppUtil.isMatchStrings(this,UserPassword!!,UserPassword_confirm!!) == false -> { return false }
            AppUtil.isPhoneNumber(UserPhone!!) == false -> { Toast.makeText(this,"전화번호를 확인해주세요",Toast.LENGTH_SHORT).show();return false }
        }


        //닉네임 확인부분ㅎ

        if(UserType == "broker")
            if(UserAddress.isEmpty()) {
                Toast.makeText(this,"주소를 적어주세요",Toast.LENGTH_SHORT).show()
                return false
            }
        return true
    }
}
