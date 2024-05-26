package com.tmdbapi.cowlar.task.utility

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.net.NetworkRequest
import android.os.Build
import androidx.lifecycle.LiveData

class NetworkConnection(
    private val context: Context
) : LiveData<Boolean>() {

    private var connectivityManager: ConnectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
        }
    }

    override fun onActive() {
        super.onActive()
        updateConnection()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.registerDefaultNetworkCallback(networkCallback)
        } else {
            lollipopNetworkRequest()
            context.registerReceiver(
                networkReceiver,
                IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
            )
        }
    }

    override fun onInactive() {
        super.onInactive()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } else {
            connectivityManager.unregisterNetworkCallback(networkCallback)
            context.unregisterReceiver(networkReceiver)
        }
    }

    private fun lollipopNetworkRequest() {
        val requestBuilder = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        connectivityManager.registerNetworkCallback(
            requestBuilder.build(),
            networkCallback
        )
    }

    private val networkReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateConnection()
        }
    }

    private fun updateConnection() {
        postValue(isNetworkConnected())
    }

    private fun isNetworkConnected(): Boolean {
        var networkCapabilities: NetworkCapabilities? = null
        if (Build.VERSION.SDK_INT < 23) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected) {
                return false
            }
            return activeNetworkInfo.type == ConnectivityManager.TYPE_WIFI ||
                    activeNetworkInfo.type == ConnectivityManager.TYPE_MOBILE ||
                    activeNetworkInfo.type == ConnectivityManager.TYPE_ETHERNET
        }
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null || connectivityManager.getNetworkCapabilities(activeNetwork)
                .also { networkCapabilities = it } == null) {
            return false
        }
        return (networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN) ||
                networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI))
    }
    private fun isConnected(): Boolean {
        var result = false
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
            if (capabilities != null) {
                result = when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN) -> true
                    else -> false
                }
            }
        } else {
            val activeNetwork = cm.activeNetworkInfo
            if (activeNetwork != null) {
                // connected to the internet
                result = when (activeNetwork.type) {
                    ConnectivityManager.TYPE_WIFI,
                    ConnectivityManager.TYPE_MOBILE,
                    ConnectivityManager.TYPE_VPN -> true
                    else -> false
                }
            }
        }
        return result
    }
}

