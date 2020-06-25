package com.example.ex

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class BunyangAdapter(list:ArrayList<Bunyang>): BaseAdapter() {
    val mlist = list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        var holder:ViewHolder? = null
        if(v == null){
            holder = ViewHolder()
            v = LayoutInflater.from(parent!!.context).inflate(R.layout.bunyang_item, parent, false)
            holder.apartname = v.findViewById<TextView>(R.id.bunyang_apartname)
            holder.state = v.findViewById<TextView>(R.id.bunyang_state)
            holder.spplier = v.findViewById<TextView>(R.id.bunyang_supplier)
            holder.address = v.findViewById<TextView>(R.id.bunyang_address)
            holder.time = v.findViewById<TextView>(R.id.bunyang_time)
            v!!.tag = holder
        }else {
            holder = v!!.tag as ViewHolder
        }
        val bunyang = mlist[position]
        holder.apartname!!.text = bunyang.apartname
        holder.state!!.text = bunyang.state
        holder.spplier!!.text = bunyang.supplier
        holder.address!!.text = bunyang.address
        holder.time!!.text = bunyang.bunyang

        if(bunyang.state == "분양중")
            holder.state!!.setBackgroundColor(Color.rgb(102,255,255))
        else
            holder.state!!.setBackgroundColor(Color.rgb(255,102,0))

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
        var apartname: TextView? = null
        var state: TextView? = null
        var spplier: TextView? = null
        var address: TextView? = null
        var time: TextView? = null
    }
}