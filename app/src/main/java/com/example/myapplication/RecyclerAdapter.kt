package com.example.myapplication

import android.os.LocaleList
import android.text.PrecomputedText
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.textclassifier.TextClassificationManager
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.lang.ref.WeakReference
import java.util.concurrent.ScheduledThreadPoolExecutor

class RecyclerAdapter(private val data: ArrayList<String>) : RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>() {
    private val background = ScheduledThreadPoolExecutor(5)
    override fun getItemCount() = data.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerAdapter.MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerAdapter.MyViewHolder, position: Int) {
        //holder.textView.text = data[position]
        //use the background thread to execute the operation
        val ref = WeakReference<TextView>(holder.textView)
        val params = holder.textView.textMetricsParams //this is the same textview otherwise the precomputation would not work

        background.submit {
            val tv = ref.get()
            val precomputedText = PrecomputedText.create(data[position], params)
            tv?.post {
                tv.text = precomputedText
            }

        }

        holder.itemView.setOnClickListener {
            val manager = holder.itemView.context.getSystemService(TextClassificationManager::class.java)

            background.submit {
                val classification = manager.textClassifier.classifyText(data[position], 0, data[position].length, LocaleList.getDefault())
                println(classification)
            }
        }

        //will only show when creating very very sophisticated apps
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
    }

}