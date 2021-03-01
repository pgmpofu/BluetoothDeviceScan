package com.ai.bluetoothdevicescan

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter

class PairedDevicesAdapter(private val dataSet:Array<BluetoothDevice>) :
        Adapter<PairedDevicesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView

        init {
            textView = view.findViewById(R.id.textViewId) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_device_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
       if(dataSet[position].name.isNullOrEmpty()) {
           viewHolder.textView.text= dataSet[position].address
       } else {
           viewHolder.textView.text= dataSet[position].name
       }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }
}