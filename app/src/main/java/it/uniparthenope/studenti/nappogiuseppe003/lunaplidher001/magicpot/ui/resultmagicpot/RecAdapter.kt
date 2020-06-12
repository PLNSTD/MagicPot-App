package it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.ui.resultmagicpot

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import it.uniparthenope.studenti.nappogiuseppe003.lunaplidher001.magicpot.R
import kotlinx.android.synthetic.main.view_recipes.view.*

class RecAdapter(val arrayList: ArrayList<RecModel>) : RecyclerView.Adapter<RecAdapter.ViewHolder>() {

    interface OnClick {
        fun onItemClick(index: Int)
    }

    var mOnItem: OnClick? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItems(model: RecModel) {
            itemView.titleRec.text = model.title
            itemView.imageRec.setImageBitmap(model.image)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.view_recipes,parent,false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItems(arrayList[position])
        holder.itemView.setOnClickListener(){
            mOnItem?.onItemClick(position)
        }
    }
}