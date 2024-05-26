package com.tmdbapi.cowlar.task.utility

import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.StringRes


object CommonFunctions {


    private var progressDialog: ProgressDialog? = null

    inline fun <reified T : Activity> Activity.lunchActivity(
            options: Bundle? = null,
            noinline init: Intent.() -> Unit = {}
    ) {
        val intent = Intent(this, T::class.java)
        intent.init()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent, options)
    }

    fun Context.shortToast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun Context.shortToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun Context.longToast(@StringRes message: Int) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun Context.longToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
    fun Context.isNetworkConnected(): Boolean {
        var networkCapabilities: NetworkCapabilities?=null
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT < 23) {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo == null || !activeNetworkInfo.isConnected) {
                return false
            }
            return activeNetworkInfo.type == NetworkCapabilities.TRANSPORT_WIFI || activeNetworkInfo.type == NetworkCapabilities.TRANSPORT_CELLULAR || activeNetworkInfo.type == NetworkCapabilities.TRANSPORT_CELLULAR
        }
        val activeNetwork = connectivityManager.activeNetwork
        if (activeNetwork == null || connectivityManager.getNetworkCapabilities(activeNetwork)
                .also {
                    networkCapabilities =
                        it!!
                } == null
        ) {
            return false
        }
        return (networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) ||
                networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_VPN)
                || networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
                || networkCapabilities!!.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH))
    }

    inline fun <reified T : Any> Context.launchActivity(
            options: Bundle? = null,
            noinline init: Intent.() -> Unit = {}) {

        val intent = newIntent<T>(this)
        intent.init()
        startActivity(intent, options)
    }

    inline fun <reified T : Any> newIntent(context: Context): Intent =
            Intent(context, T::class.java)


    fun showProgressDialog(mContext: Context, message: String?) {
        progressDialog = ProgressDialog(mContext)
        progressDialog!!.setMessage(message)
        progressDialog!!.isIndeterminate = false
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    fun hideProgressDialog() {
        progressDialog!!.dismiss()
        progressDialog = null
    }
}