package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.magicpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import kotlinx.android.synthetic.main.card_view.view.*

class MyAdapter(val arrayList: ArrayList<Model>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {



    interface OnClick {
        fun onItemClick(index: Int)
    }

    var mOnItem: OnClick? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val btnRem = itemView.findViewById<ImageButton>(R.id.rem_button)
        val cardText = itemView.findViewById<TextView>(R.id.titleTv)
        fun bindItems(model: Model) {
            itemView.titleTv.text = model.title
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.card_view, parent, false)

        return ViewHolder(v)
    }


    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
        holder.btnRem.setOnClickListener{
            mOnItem?.onItemClick(position)
        }
    }
}