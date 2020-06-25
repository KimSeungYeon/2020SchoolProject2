package com.example.ex

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class FreeBoardAdapter(list:ArrayList<Recruit>): BaseAdapter() {
    val mlist = list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        var holder:ViewHolder? = null
        if(v == null){
            holder = ViewHolder()
            v = LayoutInflater.from(parent!!.context).inflate(R.layout.freeboard_item, parent, false)
            holder.title = v.findViewById<TextView>(R.id.freeboard_title)
            holder.content = v.findViewById<TextView>(R.id.freeboard_content)
            holder.writer = v.findViewById<TextView>(R.id.freeboard_writer)
            holder.time = v.findViewById<TextView>(R.id.freeboard_time)
            v!!.tag = holder
        }else {
            holder = v!!.tag as ViewHolder
        }
        val freeboard = mlist[position]
        holder.title!!.text = "제목: "+freeboard.title
        holder.content!!.text = "내용: "+freeboard.content
        holder.writer!!.text = "작성자: "+freeboard.writer
        holder.time!!.text = "작성시간: "+AppUtil.Get_TimeToMin(freeboard.time)

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
        var title: TextView? = null
        var content: TextView? = null
        var writer: TextView? = null
        var time: TextView? = null
    }
}