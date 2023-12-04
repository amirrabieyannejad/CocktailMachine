/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.cocktailmachine.bluetoothlegatt;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.MenuItem;
import android.view.Menu;

import androidx.core.app.ActivityCompat;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.ui.BluetoothNotFound;
import com.example.cocktailmachine.ui.BluetoothTestEnviroment;
import com.example.cocktailmachine.ui.Menue;
import com.example.cocktailmachine.ui.model.helper.GetActivity;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    public static final String TAG = "bluetoothScanActivity";
    private BluetoothAdapter bluetoothAdapter;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothLeScanner bluetoothLeScanner;

    private boolean scanning;
    final private Handler handler = new Handler();
    final private Handler handlerCore = new Handler();
    private Boolean test = false;
    private ScanSettings bleScanSettings = null;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    /**
     * This block is for requesting permissions up to Android 12+
     */
    public final static UUID UUID_COCKTAIL_MACHINE =
            UUID.fromString(SampleGattAttributes.COCKTAIL_MACHINE);
    private static final int PERMISSIONS_REQUEST_CODE = 191;
    private static final String[] BLE_PERMISSIONS = new String[]{
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
    };
    @SuppressLint("InlinedApi")
    private static final String[] ANDROID_12_BLE_PERMISSIONS = new String[]{
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_ADVERTISE,
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    public static void requestBlePermissions(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            ActivityCompat.requestPermissions(activity, ANDROID_12_BLE_PERMISSIONS, requestCode);
        else
            ActivityCompat.requestPermissions(activity, BLE_PERMISSIONS, requestCode);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i("Device", "onCreate");
        Toast.makeText(this, "Connecting to Cocktail Device...",
                Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        if (Dummy.isDummy) {
            GetActivity.goToMenu(this);
            return;
        } else {

            mLeDeviceListAdapter = new LeDeviceListAdapter();

            requestBlePermissions(this, PERMISSIONS_REQUEST_CODE);
            // Use this check to determine whether BLE is supported on the device.  Then you can
            // selectively disable BLE-related features.
            if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
                Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
                finish();
            }

            // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
            // BluetoothAdapter through BluetoothManager.
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            Log.d(TAG, "onCreate: we are hier");
            // Checks if Bluetooth is supported on the device.
            if (bluetoothAdapter == null) {
                Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
                finish();
            } else {
                handler.postDelayed(() -> {
                    bleConnect();
                }, 3000);


            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!scanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            //menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            //menu.findItem(R.id.menu_refresh).setActionView(
             //       R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                mLeDeviceListAdapter.notifyDataSetChanged();
                scanLeDevice(true);
                handlerCore.postDelayed(() -> {
                    bleConnect();
                }, 3000);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onResume() {
        super.onResume();
        if (Dummy.isDummy) {
            Log.i("Device", "onResume");
            //GetActivity.goToMenu(this);
            return;
        } else {

            // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
            // fire an intent to display a dialog asking the user to grant permission to enable it.
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }

            // Initializes list view adapter.
            mLeDeviceListAdapter = new LeDeviceListAdapter();
            bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
            setListAdapter(mLeDeviceListAdapter);
            scanLeDevice(true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Dummy.isDummy) {
            Log.i("Device", "onPause");
            //GetActivity.goToMenu(this);
            return;
        } else {
            scanLeDevice(false);
            mLeDeviceListAdapter.clear();
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        Intent intent;
        if (test) {
             intent = new Intent(this, BluetoothTestEnviroment.class);
        } else {
             intent = new Intent(this, Menue.class);
        }

        BluetoothSingleton settings = BluetoothSingleton.getInstance();
        settings.setEspDeviceName(device.getName());
        settings.setEspDeviceAddress(device.getAddress());

        if (scanning) {
            bluetoothLeScanner.stopScan(leScanCallback);
            scanning = false;
        }
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void bleConnect() {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(0);
        if (device == null) {
            Toast.makeText(this, "Cocktail device not found!",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent;
        if (test) {
            intent = new Intent(this, BluetoothTestEnviroment.class);
        } else {
            intent = new Intent(this, Menue.class);
        }
        BluetoothSingleton settings = BluetoothSingleton.getInstance();
        settings.setEspDeviceName(device.getName());
        settings.setEspDeviceAddress(device.getAddress());
        Toast.makeText(this, "Cocktail device has been found!",
                Toast.LENGTH_SHORT).show();

        if (scanning) {
            bluetoothLeScanner.stopScan(leScanCallback);
            scanning = false;
        }
        startActivity(intent);
    }

    @SuppressLint("MissingPermission")
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            handler.postDelayed(() -> {
                scanning = false;
                bluetoothLeScanner.stopScan(leScanCallback);

                invalidateOptionsMenu();
            }, SCAN_PERIOD);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                bleScanSettings = new ScanSettings.Builder().
                        setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).
                        setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES).
                        build();
            }

            // filter only CocktailMachine

            ArrayList<ScanFilter> filters = new ArrayList<>();
            ScanFilter.Builder builder = new ScanFilter.Builder();
            //String serviceUuidMaskString = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
            //ParcelUuid parcelUuidMask = ParcelUuid.fromString(serviceUuidMaskString);
            //builder.setServiceUuid(new ParcelUuid(UUID_COCKTAIL_MACHINE), parcelUuidMask);
            builder.setDeviceName("Cocktail Machine ESP32 v11");
            filters.add(builder.build());
            if (filters.isEmpty()) {
                Toast.makeText(this, "UUID has not found!",
                        Toast.LENGTH_LONG).show();
            }

            scanning = true;
            bluetoothLeScanner.startScan(filters, bleScanSettings, leScanCallback);
            //bluetoothLeScanner.startScan(leScanCallback);
        } else {
            scanning = false;
            bluetoothLeScanner.stopScan(leScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private final ArrayList<BluetoothDevice> mLeDevices;
        private final LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            if (!mLeDevices.isEmpty()) {
                return mLeDevices.get(position);
            }
            return null;
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @SuppressLint({"InflateParams", "SuspiciousIndentation"})
        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = view.findViewById(R.id.device_address);
                viewHolder.deviceName = view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            @SuppressLint("MissingPermission") final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
                viewHolder.deviceAddress.setText(device.getAddress());

            return view;
        }
    }


    // Device scan callback.
    final private ScanCallback leScanCallback =
            new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    super.onScanResult(callbackType, result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d(TAG, "onScanResult: Scan Call back");
                            mLeDeviceListAdapter.addDevice(result.getDevice());
                            mLeDeviceListAdapter.notifyDataSetChanged();
                        }
                    });
                }
            };


    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
    }

}
