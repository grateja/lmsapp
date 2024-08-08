package com.vag.lmsapp.app.remote

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.GridLayoutManager
import com.vag.lmsapp.R
import com.vag.lmsapp.app.machines.options.BottomSheetMachineOptionsFragment
import com.vag.lmsapp.app.machines.MachineListItem
import com.vag.lmsapp.app.remote.activate.RemoteActivationPreviewActivity
import com.vag.lmsapp.app.remote.customer.RemoteActivationCustomerActivity
import com.vag.lmsapp.app.remote.panel.RemotePanelAdapter
import com.vag.lmsapp.app.remote.running.RemoteActivationRunningActivity
import com.vag.lmsapp.databinding.ActivityRemoteActivationPanelBinding
import com.vag.lmsapp.model.MachineActivationQueues
import com.vag.lmsapp.services.MachineActivationService
import com.vag.lmsapp.util.ActivityLauncher
import com.vag.lmsapp.util.Constants
import com.vag.lmsapp.util.calculateSpanCount
import com.vag.lmsapp.model.EnumMachineType
import com.vag.lmsapp.model.EnumServiceType
import com.vag.lmsapp.model.MachineConnectionStatus
import com.vag.lmsapp.util.NetworkHelper
import com.vag.lmsapp.util.showMessageDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RemoteActivationPanelActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRemoteActivationPanelBinding
    private lateinit var machineOption: BottomSheetMachineOptionsFragment
    private val viewModel: RemotePanelViewModel by viewModels()
    private val networkHelper = NetworkHelper(this)

    private val adapter = RemotePanelAdapter() //Adapter<MachineListItem>(R.layout.recycler_item_machine_tile)
//    private val regularDryersAdapter = RemotePanelAdapter() //Adapter<MachineListItem>(R.layout.recycler_item_machine_tile)
//    private val titanWashersAdapter = RemotePanelAdapter() //Adapter<MachineListItem>(R.layout.recycler_item_machine_tile)
//    private val titanDryersAdapter = RemotePanelAdapter() //Adapter<MachineListItem>(R.layout.recycler_item_machine_tile)

    private val launcher = ActivityLauncher(this)

    private fun isWifiConnected(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = resources.getColor(R.color.color_code_machines, null)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_remote_activation_panel)
        setSupportActionBar(binding.toolbar)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.recycler.layoutManager = GridLayoutManager(this, spanCount)
        binding.recycler.adapter = adapter
//
//        binding.inclRegularDryers.recycler.layoutManager = GridLayoutManager(this, spanCount)
//        binding.inclRegularDryers.recycler.adapter = regularDryersAdapter
//
//        binding.inclTitanWashers.recycler.layoutManager = GridLayoutManager(this, spanCount)
//        binding.inclTitanWashers.recycler.adapter = titanWashersAdapter
//
//        binding.inclTitanDryers.recycler.layoutManager = GridLayoutManager(this, spanCount)
//        binding.inclTitanDryers.recycler.adapter = titanDryersAdapter

        subscribeEvents()
        subscribeListeners()

        println("is connected ${isWifiConnected()}")

        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(wifiStateCallback)

        networkHelper.suggestWifiNetwork(
            getString(R.string.wifi_ssid),
            getString(R.string.wifi_password)
        )
    }

    private val wifiStateCallback = object: ConnectivityManager.NetworkCallback() {

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            Log.d("WifiStateCallback", "Network available")
            checkWifiConnection()
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            Log.d("WifiStateCallback", "Network lost")
            runOnUiThread {
                viewModel.setWiFiConnectionState(false)
            }
            // Handle Wi-Fi disconnected state
        }

        private fun checkWifiConnection() {
            val connectivityManager =
                getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            val network = connectivityManager.activeNetwork
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network)

            runOnUiThread {
                viewModel.setWiFiConnectionState(networkCapabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true)
            }
        }
    }

    override fun onPause() {
        super.onPause()
        adapter.stopUpdatingTime()
    }

    override fun onResume() {
        super.onResume()
        adapter.startUpdatingTime()
    }

    private fun subscribeEvents() {
        adapter.onItemClick = { selectMachine(it) }
        adapter.onOptionClick = { showOptions(it) }
        binding.tabMachineType.cardRegularWasher.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR, EnumServiceType.WASH)
        }
        binding.tabMachineType.cardRegularDryer.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.REGULAR, EnumServiceType.DRY)
        }
        binding.tabMachineType.cardTitanWasher.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN, EnumServiceType.WASH)
        }
        binding.tabMachineType.cardTitanDryer.setOnClickListener {
            viewModel.setMachineType(EnumMachineType.TITAN, EnumServiceType.DRY)
        }
//        binding.tabMachineType.tabMachineType.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(tab: TabLayout.Tab?) {
//                viewModel.setMachineType(tab?.text.toString())
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab?) {
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab?) {
//            }
//        })
//        regularDryersAdapter.onItemClick = { selectMachine(it) }
//        titanWashersAdapter.onItemClick = { selectMachine(it) }
//        titanDryersAdapter.onItemClick = { selectMachine(it) }
    }

    private fun subscribeListeners() {
        viewModel.machines.observe(this, Observer {
            adapter.setData(it)
            adapter.startUpdatingTime()
//            regularDryersAdapter.setData(it.filter { it.machine.machineType == EnumMachineType.REGULAR_DRYER })
//            titanWashersAdapter.setData(it.filter { it.machine.machineType == EnumMachineType.TITAN_WASHER })
//            titanDryersAdapter.setData(it.filter { it.machine.machineType == EnumMachineType.TITAN_DRYER })
        })
    }

    private fun showOptions(item: MachineListItem) {
        machineOption = BottomSheetMachineOptionsFragment.getInstance(item.machine.id)
        machineOption.show(supportFragmentManager, null)
    }

    private fun selectMachine(item: MachineListItem) {
        val intent = if(item.machine.activationRef?.running() == true) {
            Intent(this, RemoteActivationRunningActivity::class.java).apply {
                putExtra(Constants.MACHINE_ID, item.machine.id.toString())
            }
        } else if(item.machine.serviceActivationId != null) {
            Intent(this, RemoteActivationPreviewActivity::class.java).apply {
                val machineId = item.machine.id
                val joServiceId = item.machine.serviceActivationId
                val customerId = item.customer?.id
                val queue = MachineActivationQueues(machineId, joServiceId, customerId)
                putExtra(MachineActivationService.ACTIVATION_QUEUES_EXTRA, queue)
            }
        } else {
            Intent(this, RemoteActivationCustomerActivity::class.java).apply {
                putExtra(Constants.MACHINE_ID, item.machine.id.toString())
            }
        }
        launcher.launch(intent)
    }

    private val spanCount: Int by lazy {
        applicationContext.calculateSpanCount(R.dimen.machine_tile_width, R.dimen.activity_horizontal_margin)
    }

    private val receiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.getParcelableExtra<MachineActivationQueues>(MachineActivationService.ACTIVATION_QUEUES_EXTRA)?.let {
                if(it.status == MachineConnectionStatus.FAILED) {
                    showMessageDialog("Machine activation failed!", it.message)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver)
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, IntentFilter(MachineActivationService.MACHINE_ACTIVATION))
    }
}