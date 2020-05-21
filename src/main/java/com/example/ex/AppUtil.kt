package com.example.ex

import android.R
import android.app.Activity
import android.content.Context
import android.os.Build

import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime


object AppUtil {
    private  val databaseRefer = FirebaseDatabase.getInstance().reference
    fun ShowDialogNoAction(act:Activity,title:String){
        val builder = AlertDialog.Builder(act)
        builder.setTitle(title)
            .setPositiveButton("확인"){dialogInterface, i ->
            }
            .setNegativeButton("취소",null)
            .show()
    }
    fun ShowDialog(act:Activity,title:String){
        val builder = AlertDialog.Builder(act)
        builder.setTitle(title)
            .setPositiveButton("확인"){dialogInterface, i ->
                act.finish()
            }
            .setNegativeButton("취소",null)
            .show()
    }
    fun ShowYesDialog(act:Activity,title:String){
        val builder = AlertDialog.Builder(act)
        builder.setTitle(title)
            .setPositiveButton("확인"){dialogInterface, i ->
                act.finish()
            }
            .setOnCancelListener {
                act.finish()
            }
            .show()
    }

    fun isEmail(context:Context,str:String?):Boolean{
        if(str.isNullOrEmpty()) return false
        val array = str.split("@")
        if(array[0].length < 7){
            Toast.makeText(context,"아이디는 7자리 이상입니다.",Toast.LENGTH_SHORT)
            return false
        }
        if( array[0].isNotBlank() && array.size==2 ) {
            if(array[1].endsWith(".co.kr") || array[1].endsWith(".com") || array[1].endsWith(".net") )
                return true
            else return false
        } else return false
    }
    fun isMatchStrings(context:Context,str1:String,str2:String):Boolean{
        var bool = false
        if( str1.length < 8 ) {
            Toast.makeText(context,"비밀번호는 8자리 이상입니다.",Toast.LENGTH_SHORT).show()
        }
        for( i in 0 until str1.length){
            val char = str1[i]
            when(char.toInt()){
                in 97 .. 122 -> null
                in 65 .. 90 -> null
                in 48 .. 57 -> null
                else -> {
                    Toast.makeText(context,"비밀번호에 특수문자가 들어있어요",Toast.LENGTH_SHORT).show()
                }
            }
        }
        if(str1.isBlank() || str2.isBlank()) return false
        if(str1 == str2) bool = true
        else  Toast.makeText(context,"비밀번호를 일치시켜 주세요",Toast.LENGTH_SHORT).show()
        return bool
    }
    fun isPhoneNumber(str: String):Boolean{
        val array = str.split("-")
        if( array.size == 3 && (array[0] == "031" || array[0] == "010") ) {
            if (array[1].length >= 3 && array[2].length == 4)
                return true
            else return false
        }
        return false
    }
    fun isAvailableNickName(act: Activity,user:User){
        if (user.nickname.length < 2) {
            Toast.makeText(act, "닉네임이 너무 짧아요", Toast.LENGTH_SHORT).show()
            return
        }
        databaseRefer.child("nickname")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    TODO("Not yet implemented")
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if( p0.childrenCount == 0L )
                        SignUpCommit(act,user)
                    for(snapshot in p0.children){
                        if(snapshot.key == user.nickname){
                            Toast.makeText(act, "중복된 닉네임이 있어요.", Toast.LENGTH_SHORT).show()
                            return
                        }else{
                            SignUpCommit(act,user)
                        }
                    }
                }

            } )

    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun Get_Time():String{
        val time = LocalDateTime.now().toString()
        return time.replace(".","_")
    }
    fun Get_TimeToMin(time:String):String{
        var temp = time
        temp = temp.substring(temp.indexOf("-")+1,temp.lastIndex)
        temp = temp.split(".")[0]
        temp = temp.replace("-","월")
        temp = temp.replace("T","일")
        return temp
    }
    @RequiresApi(Build.VERSION_CODES.O)
    fun Get_Time_Min():String{
        var time = LocalDateTime.now().toString()
        time = time.substring(time.indexOf("-")+1,time.lastIndex)
        time = time.split(".")[0]
        time = time.replace("-","월")
        time = time.replace("T","일")
        return time
    }

    fun SignUpCommit(act: Activity,user:User) {
            databaseRefer.child("user")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onCancelled(p0: DatabaseError) {
                        TODO("Not yet implemented")
                    }

                    override fun onDataChange(p0: DataSnapshot) {
                        for (snapshot in p0.children) {
                            if (snapshot.key == user.id.replace(".","_")) {
                                Toast.makeText(act, "가입된 아이디에요.", Toast.LENGTH_SHORT).show()
                                return
                            }
                        }
                        val dataset = User(
                            user.type,
                            user.id,
                            user.pw,
                            user.phone,
                            user.name,
                            user.nickname,
                            user.address
                        )
                        databaseRefer.child("user").child(user.id.replace(".", "_"))
                            .setValue(dataset)
                        databaseRefer.child("nickname").child(user.nickname).setValue(user.id)
                        var auth = FirebaseAuth.getInstance()
                        // auth?.currentUser?.sendEmailVerification()
                        auth?.createUserWithEmailAndPassword(user.id, user.pw)
                            ?.addOnCompleteListener {
                                    auth?.currentUser?.sendEmailVerification()
                                    ShowYesDialog(act, "메일로 인증을 해주세요.")

                            }

                    }
                })

    }
}
