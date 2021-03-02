package com.ai.bluetoothdevicescan

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import timber.log.Timber
import java.io.IOException

class AvailableDevicesAdapter(private val availableDevices: Array<BluetoothDevice>) :
        Adapter<AvailableDevicesAdapter.ViewHolder>() {

    private val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()

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
        if(availableDevices[position].name.isNullOrEmpty()) {
            viewHolder.textView.text= availableDevices[position].address
        } else {
            viewHolder.textView.text= availableDevices[position].name
        }

        viewHolder.textView.setOnClickListener {
            ConnectThread(availableDevices[position]).run()
        }
    }

    override fun getItemCount(): Int {
        return availableDevices.size
    }

    private inner class ConnectThread(device: BluetoothDevice) : Thread() {

        private val mmSocket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(device.uuids[0].uuid)
        }

        override fun run() {
            // Cancel discovery because it otherwise slows down the connection.
            bluetoothAdapter?.cancelDiscovery()

            mmSocket?.use { socket ->
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.

                socket.connect()

                // The connection attempt succeeded. Perform work associated with
                // the connection in a separate thread.
                //ToDo add preference manager stuff here

            }
        }

        // Closes the client socket and causes the thread to finish.
        fun cancel() {
            try {
                mmSocket?.close()
            } catch (e: IOException) {
                Timber.i( "Could not close the client socket")
            }
        }
    }
}