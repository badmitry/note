package com.badmitry.kotlingeekbrains.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build


class CheckerInternetConnection(val context: Context) {

    fun isConnection() : Boolean {
        val cm: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(nw) ?: return false
            return when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> isInternetAvailable()
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> isInternetAvailable()
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> isInternetAvailable()
                else -> false
            }
        } else {
            val nwInfo = cm.activeNetworkInfo ?: return false
            return nwInfo.isConnected
        }
    }

    private fun isInternetAvailable(): Boolean{
//        return try {
//            val sock = Socket()
//            val sockaddr: SocketAddress = InetSocketAddress("8.8.8.8", 53)
//            sock.connect(sockaddr, 1) // This will block no more than timeoutMs
//            sock.close()
//            true
//        } catch (e: IOException) {
//            false
//        }
////or///////
//        try {
//            val address: InetAddress = InetAddress.getByName("www.google.com")
//            return !address.equals("")
//        } catch (e: UnknownHostException) {
//            // Log error
//        }
//        return false
        //add AsyncTask
        return true
    }

}