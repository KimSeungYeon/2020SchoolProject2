package com.example.ex
object Singleton {
    private var user:User? = null
    fun getuser():User{
        return user!!
    }
    fun setuser(u:User){
        user = u
    }
    fun saveid(id:String):String{
        return id.replace(".","_")
    }
}