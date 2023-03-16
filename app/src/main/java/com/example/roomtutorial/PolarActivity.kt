package com.example.roomtutorial

import android.app.Application
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.polar.androidcommunications.api.ble.model.gatt.client.pmd.model.PpgData
import com.polar.androidcommunications.api.ble.model.gatt.client.pmd.model.PpgDataSample
import com.polar.sdk.api.PolarBleApi
import com.polar.sdk.api.PolarBleApiCallback
import com.polar.sdk.api.PolarBleApiDefaultImpl
import com.polar.sdk.api.errors.PolarInvalidArgument
import com.polar.sdk.api.model.PolarAccelerometerData
import com.polar.sdk.api.model.PolarDeviceInfo
import com.polar.sdk.api.model.PolarHrData
import com.polar.sdk.api.model.PolarOhrData
import com.polar.sdk.api.model.PolarOhrPPIData
import com.polar.sdk.api.model.PolarOhrPPIData.PolarOhrPPISample
import com.polar.sdk.api.model.PolarSensorSetting
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import java.sql.Timestamp
import java.util.*
import java.util.stream.Collectors

class PolarActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "PolarActivity"
    }

    private lateinit var deviceId: String
    private lateinit var api: PolarBleApi
    private var accDisposable: Disposable? = null
    private var ppiDisposable: Disposable? = null
    private lateinit var ppiViewModel: PpiViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_polar)



        ppiViewModel = ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory.getInstance(
                application
            )
        )[PpiViewModel::class.java]
        deviceId = intent.getStringExtra("id")
            ?: throw Exception("PolarActivity couldn't be created, no deviceId given")

        api = PolarBleApiDefaultImpl.defaultImplementation(
            applicationContext,
            PolarBleApi.ALL_FEATURES
        )
        api.setApiLogger { str: String -> Log.d("SDK", str) }
        api.setApiCallback(object : PolarBleApiCallback() {
            override fun blePowerStateChanged(blePowerState: Boolean) {
                Log.d(TAG, "BluetoothStateChanged $blePowerState")
            }

            override fun deviceConnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "Device connected ${polarDeviceInfo.deviceId}")
                showToast("Device connected")
            }

            override fun deviceConnecting(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "Device connecting ${polarDeviceInfo.deviceId}")
            }

            override fun deviceDisconnected(polarDeviceInfo: PolarDeviceInfo) {
                Log.d(TAG, "Device disconnected ${polarDeviceInfo.deviceId}")
            }

            override fun streamingFeaturesReady(
                identifier: String,
                features: Set<PolarBleApi.DeviceStreamingFeature>
            ) {
                for (feature in features) {
                    Log.d(TAG, "Streaming feature is ready: $feature")
                    when (feature) {
                        PolarBleApi.DeviceStreamingFeature.ACC -> streamACC()
                        //PolarBleApi.DeviceStreamingFeature.PPI->streamPPI()
                        else -> {}
                    }
                }
            }

            override fun hrFeatureReady(identifier: String) {
                Log.d(TAG, "HR Feature ready $identifier")
            }

            override fun disInformationReceived(identifier: String, uuid: UUID, value: String) {
                if (uuid == UUID.fromString("00002a28-0000-1000-8000-00805f9b34fb")) {
                    Log.d(TAG, "Firmware: " + identifier + " " + value.trim { it <= ' ' })
                }
            }

            override fun batteryLevelReceived(identifier: String, batteryLevel: Int) {
                Log.d(TAG, "Battery level $identifier $batteryLevel%")
            }

            override fun hrNotificationReceived(s: String, polarHrData: PolarHrData) {
                Log.d(TAG, "HR " + polarHrData.hr)


            }

            override fun polarFtpFeatureReady(identifier: String) {
                Log.d(TAG, "Polar FTP ready $identifier")
            }


        })

        try {
            api.connectToDevice(deviceId)
        } catch (a: PolarInvalidArgument) {
            a.printStackTrace()
        }

    }

    private fun showToast(message: String) {
        val toast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        toast.show()
    }

    fun streamACC() {
        if (accDisposable == null) {
            accDisposable =
                api.requestStreamSettings(deviceId, PolarBleApi.DeviceStreamingFeature.ACC)
                    .toFlowable()
                    .flatMap { sensorSetting: PolarSensorSetting ->
                        api.startAccStreaming(
                            deviceId,
                            sensorSetting.maxSettings()
                        )
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { polarAccData: PolarAccelerometerData ->
                            Log.d(TAG, "ACC update")
                            val size = polarAccData.samples.size
                            Log.d(TAG, "ACC size:$size")
                            var accDataSampleString = ""
                            for (data in polarAccData.samples) {
                                val x = data.x
                                val y = data.y
                                val z = data.z
                                accDataSampleString += "(x=$x, y=$y, z=$z) "
                            }
                            Log.d(TAG, "Acc stream data: $accDataSampleString")
                        },
                        { error: Throwable ->
                            Log.e(TAG, "Acc stream failed $error")
                            accDisposable = null
                        },
                        {
                            Log.d(TAG, "Acc stream complete")
                        }
                    )
        } else {
            // NOTE stops streaming if it is "running"
            accDisposable?.dispose()
            accDisposable = null
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        accDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }

        ppiDisposable?.let {
            if (!it.isDisposed) it.dispose()
        }
        api.shutDown()
    }


    fun streamPPI() {
        if (ppiDisposable == null) {
            ppiDisposable = api.startOhrPPIStreaming(deviceId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { polarPpiData: PolarOhrPPIData ->
                        Log.d(TAG, "PPI update")

                        for (ppiData in polarPpiData.samples) {
                            if (ppiData.skinContactSupported and ppiData.skinContactStatus)
                                ppiViewModel.insert(Ppi(ppiData.ppi, polarPpiData.timeStamp))
                        }
                        val ppiSamples = polarPpiData.samples
                            .stream()
                            .map(PolarOhrPPISample::ppi)
                            .collect(Collectors.toList())
                            .joinToString(",")

                        Log.d(
                            TAG, polarPpiData.samples
                                .stream()
                                .map(PolarOhrPPISample::hr)
                                .collect(Collectors.toList())
                                .toString()
                        )
                        Log.d(TAG, polarPpiData.timeStamp.toString())
                        Log.d(TAG, "PPI stream data: $ppiSamples")
                    },
                    { error: Throwable ->
                        Log.e(TAG, "PPI stream failed $error")
                        ppiDisposable = null
                    },
                    {
                        Log.d(TAG, "PPI stream complete")
                    }
                )
        } else {
            // NOTE stops streaming if it is "running"
            ppiDisposable?.dispose()
            ppiDisposable = null
        }
    }
}