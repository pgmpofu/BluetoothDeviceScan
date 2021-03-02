package com.ai.bluetoothdevicescan

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_devices.*
import timber.log.Timber

class ConnectedDevicesActivity : AppCompatActivity() {

    private val permissions = arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN)
    val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    val pairedDevices: Set<BluetoothDevice>? = bluetoothAdapter?.bondedDevices
    val discoveredDevices: MutableSet<BluetoothDevice> = mutableSetOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)
        if(!hasPermission()) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSION_CODE)
        }
        if (bluetoothAdapter == null) {
            Toast.makeText(applicationContext, "Not supported", Toast.LENGTH_LONG).show()
        }

        if (bluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        pairedDevicesList.layoutManager = LinearLayoutManager(this)
        availableDevicesList.layoutManager = LinearLayoutManager(this)
        pairedDevicesList.adapter = pairedDevices?.let { PairedDevicesAdapter(it.toTypedArray()) }

        if(bluetoothAdapter?.isDiscovering == true) {
            bluetoothAdapter.cancelDiscovery()
        }
        bluetoothAdapter?.startDiscovery()
        // Register for broadcasts when a device is discovered.
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)
        Timber.i("Receiver registered")
        doneButton.setOnClickListener { finish() }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            when(intent.action) {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    Timber.i("Device Found!!")
                    val device: BluetoothDevice =
                        intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
                    discoveredDevices.add(device)
                }
            }
            availableDevicesList.adapter =
                pairedDevices?.let { AvailableDevicesAdapter(discoveredDevices.toTypedArray(), it) }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when(resultCode) {
            RESULT_CANCELED -> Toast.makeText(applicationContext, "Cannot proceed without bluetooth", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }


    private fun hasPermission(): Boolean {
        return ContextCompat.checkSelfPermission(baseContext, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(baseContext, Manifest.permission.BLUETOOTH) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(baseContext, Manifest.permission.BLUETOOTH_ADMIN) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        const val REQUEST_ENABLE_BT = 0
        const val PERMISSION_CODE = 1
    }
}