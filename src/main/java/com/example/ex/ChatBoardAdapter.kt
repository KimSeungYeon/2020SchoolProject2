package com.example.ex

import android.graphics.Color
import android.graphics.ColorSpace
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class ChatBoardAdapter(val list:ArrayList<Chat>):BaseAdapter(){
    val mlist = list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v:View? = convertView
        var holder:ViewHolder? = null
        when{
            v == null -> {
                holder = ViewHolder()
                v = LayoutInflater.from(parent!!.context).inflate(R.layout.chatboard, parent, false)
                holder.chatName = v.findViewById<TextView>(R.id.chatboard_title)
                holder.chatType = v.findViewById<TextView>(R.id.chatboard_type)
                holder.lastMessage = v.findViewById<TextView>(R.id.chatboard_lastchat)
                v!!.tag = holder
            }
            else -> holder = v!!.tag as ViewHolder
        }
        val chatboard = mlist[position]
        holder.chatName!!.text = chatboard.chatName
        holder.chatType!!.text = chatboard.chatType
        when(chatboard.chatType){
            "private" -> {
                holder.chatName!!.setText("대화상대:"+holder!!.chatName!!.text)
                holder.chatType!!.setBackgroundColor(Color.CYAN)
                holder.chatType!!.text = "개인톡"
            }
            "public" -> {
                holder.chatName!!.setText("대화방 이름:"+holder!!.chatName!!.text)
                holder.chatType!!.setBackgroundColor(Color.RED)
                holder.chatType!!.text = "단체톡"
            }
        }

        holder.lastMessage!!.text = ""

        return v
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getCount(): Int {
        return list.size
    }
    class ViewHolder{
        var chatType:TextView? = null
        var chatName:TextView? = null
        var lastMessage:TextView? = null
    }
}