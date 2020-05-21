package com.example.ex

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

class SaleItemAdapter(val list:ArrayList<SaleItem>):BaseAdapter() {
    val mlist = list
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var v:View? = convertView
        var holder:ViewHolder? = null
        when{
            v == null -> {
                holder = ViewHolder()
                v = LayoutInflater.from(parent!!.context).inflate(R.layout.saleitem, parent, false)
                holder.image = v.findViewById<ImageView>(R.id.sale_image)
                holder.writer = v.findViewById<TextView>(R.id.sale_writer)
                holder.writing = v.findViewById<TextView>(R.id.sale_writing)
                holder.price = v.findViewById<TextView>(R.id.sale_price)
                holder.area = v.findViewById<TextView>(R.id.sale_area)
                holder.name = v.findViewById<TextView>(R.id.sale_name)
                holder.floor = v.findViewById<TextView>(R.id.sale_floor)
                holder.type = v.findViewById<TextView>(R.id.sale_type)
                holder.time = v.findViewById<TextView>(R.id.sale_time)
                v!!.tag = holder
                Log.d("이거","ㅇㅇ")
            }
            else ->  holder = v!!.tag as ViewHolder
        }

        val sale = mlist[position]
        holder.image!!.setImageResource(R.drawable.ic_home_black_24dp)
        holder.name!!.text = sale.name
        holder.writing!!.text = sale.writing
        holder.writer!!.text = "작성자: "+sale.writer
        holder.price!!.text = sale.price+"만원"
        holder.floor!!.text = sale.floor
        when(sale.type){
            0 -> {
                holder.type!!.setText("판매중")
                holder.type!!.setBackgroundColor(Color.CYAN)
            }
            1 -> {
                holder.type!!.setText("판매완료")
                holder.type!!.setBackgroundColor(Color.GRAY)
            }
        }
        holder.area!!.text = "면적(공급/전용): "+sale.area
        holder.time!!.text = sale.time

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
        var image: ImageView? = null
        var writer: TextView? = null
        var writing: TextView? = null
        var price: TextView? = null
        var area: TextView? = null
        var name: TextView? = null
        var floor: TextView? = null
        var type: TextView? = null
        var time: TextView? = null
    }
}