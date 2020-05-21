package com.example.ex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class RecruitAdapter(list:ArrayList<Recruit>):BaseAdapter() {
    val mlist = list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        var holder:ViewHolder? = null
        if(v == null){
            holder = ViewHolder()
            v = LayoutInflater.from(parent!!.context).inflate(R.layout.talk_recruit_item, parent, false)
            holder.title = v.findViewById<TextView>(R.id.recruit_title)
            holder.content = v.findViewById<TextView>(R.id.recruit_content)
            holder.writer = v.findViewById<TextView>(R.id.recruit_writer)
            holder.time = v.findViewById<TextView>(R.id.recruit_time)
            v!!.tag = holder
        }else {
            holder = v!!.tag as ViewHolder
        }
        val recruit = mlist[position]
        holder.title!!.text = "제목: "+recruit.title
        holder.content!!.text = "내용: "+recruit.content
        holder.writer!!.text = "작성자: "+recruit.writer
        holder.time!!.text = "작성시간: "+AppUtil.Get_TimeToMin(recruit.time)

        return v!!
    }
    override fun getItem(position: Int): Any {
        return mlist[position]
    }
    override fun getItemId(position: Int): Long {
        return 0L
    }
    override fun getCount(): Int {
        return mlist.size
    }
    class ViewHolder{
        var title:TextView? = null
        var content:TextView? = null
        var writer:TextView? = null
        var time:TextView? = null
    }
}