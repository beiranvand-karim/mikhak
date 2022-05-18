package com.example.roadmaintenance.network

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow


class NetworkConnection(private val context: Context) {

    private var _notifyValidNetwork = MutableSharedFlow<Boolean>()
    val notifyValidNetwork = _notifyValidNetwork

    private val manager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    private lateinit var networkCallback: NetworkCallback
    private val validNetwork: MutableSet<Network> = HashSet()

    private fun checkValidNetwork() {
        val isValidNetworkExists = validNetwork.size > 0
        CoroutineScope(Dispatchers.IO).launch {
            _notifyValidNetwork.emit(isValidNetworkExists)
        }
    }

    @SuppressLint("MissingPermission")
    fun onActive() {
        networkCallback = createNetworkCallBack()
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        manager.registerNetworkCallback(networkRequest, networkCallback)
    }

    private fun createNetworkCallBack() = object : ConnectivityManager.NetworkCallback() {
        @SuppressLint("MissingPermission")
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            val networkCapabilities = manager.getNetworkCapabilities(network)
            val hasInternetCapabilities =
                networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)

            if (hasInternetCapabilities == true) {
                CoroutineScope(Dispatchers.IO).launch {
                    val hasInternet = InternetConnection.execute(network.socketFactory)
                    if (hasInternet) {
                        withContext(Dispatchers.Main) {
                            validNetwork.add(network)
                            checkValidNetwork()
                        }
                    }
                }
            }
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            validNetwork.remove(network)
            checkValidNetwork()
        }
    }

    fun onInactive() {
        manager.unregisterNetworkCallback(networkCallback)
    }

    companion object {
        var IsInternetAvailable : Boolean = false
    }
}
