package com.example.ex

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(val context:Context,val view:RecyclerView):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val list = mutableListOf<ChatMessage>()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        var view:View? = null
        when(viewType){
            1 -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_message_my, parent, false)
                return MessageViewHolderMy(view)
            }
            2 -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false)
                return MessageViewHolder(view)
            }
            3 -> {
                view = LayoutInflater.from(context).inflate(R.layout.item_inout, parent, false)
                return EnterViewHolder(view)
            }
            else -> return MessageViewHolder(view!!)
        }
    }

    override fun getItemCount() = list.size

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder) {
            is MessageViewHolderMy -> {
                holder.itemView?.run {
                    holder.messageTimeview.text = list[position].time
                    holder.messageTextview.text = list[position].text
                    holder.messengerTextView.text = list[position].name
                    holder.messengerImageView.setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_24dp))
                }
            }
            is MessageViewHolder -> {
                holder.itemView?.run {
                    holder.messageTimeview.text = list[position].time
                    holder.messageTextview.text = list[position].text
                    holder.messengerTextView.text = list[position].name
                    holder.messengerImageView.setImageDrawable(context.getDrawable(R.drawable.ic_account_circle_black_24dp))
                }
            }
            is EnterViewHolder -> {
                holder.itemView?.run {
                    holder.enter.text = list[position].text.replace("%%##%%##","")
                    if(list[position].text.contains("입장")){
                        holder.enter.setBackgroundColor(Color.rgb(255,204,153))
                    } else {
                        holder.enter.setBackgroundColor(Color.rgb(255,204,204))
                    }
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val model = list[position]
        when{
            model.text.contains("%%##%%##") -> return 3
            model.name == Singleton.getuser().nickname -> return 1 //내이름이면 오른쪽 출력
            model.name != Singleton.getuser().nickname -> return 2 //남의이름이면 왼쪽출력
            else -> return 3
        }
    }
    fun addList(chat:ChatMessage){
        list.add(chat)
        view.scrollToPosition(itemCount-1)
    }
    class MessageViewHolder(v:View):RecyclerView.ViewHolder(v){
        var messageTimeview = itemView.findViewById(R.id.messengeTime) as TextView
        var messageTextview = itemView.findViewById(R.id.messageTextView) as TextView
        var messageImageView = itemView.findViewById(R.id.messengerImageView) as ImageView
        var messengerTextView =  itemView.findViewById(R.id.messengerTextView) as TextView
        var messengerImageView =  itemView.findViewById(R.id.messengerImageView) as CircleImageView
    }
    class MessageViewHolderMy(v:View):RecyclerView.ViewHolder(v){
        var messageTimeview = itemView.findViewById(R.id.messengeTime_my) as TextView
        var messageTextview = itemView.findViewById(R.id.messageTextView_my) as TextView
        var messageImageView = itemView.findViewById(R.id.messengerImageView_my) as ImageView
        var messengerTextView =  itemView.findViewById(R.id.messengerTextView_my) as TextView
        var messengerImageView =  itemView.findViewById(R.id.messengerImageView_my) as CircleImageView
    }
    class EnterViewHolder(v:View):RecyclerView.ViewHolder(v){
        var enter = itemView.findViewById(R.id.Enter) as TextView
    }
}