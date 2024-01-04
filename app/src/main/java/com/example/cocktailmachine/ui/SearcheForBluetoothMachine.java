package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.R;
import com.example.cocktailmachine.bluetoothlegatt.BluetoothSingleton;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.bluetoothlegatt.SampleGattAttributes;
import com.example.cocktailmachine.data.Ingredient;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.db.exceptions.NoSuchIngredientSettedException;
import com.example.cocktailmachine.data.db.exceptions.TooManyTimesSettedIngredientEcxception;
import com.example.cocktailmachine.data.enums.CocktailStatus;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.logic.Animation.CircularAnimation;
import com.example.cocktailmachine.logic.BildgeneratorGlas;
import com.example.cocktailmachine.ui.model.helper.GetActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class SearcheForBluetoothMachine extends AppCompatActivity {


    // Animationsdarstellung
    Bitmap image = null;
    ImageView backgroundImage = null;
    ImageView loopImage = null;

    //Bluetoothrealisierung

    public static final String TAG = "bluetoothScanActivity";
    private BluetoothAdapter bluetoothAdapter;
    //private DeviceScanActivity.LeDeviceListAdapter mLeDeviceListAdapter;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searche_for_bluetooth_machine);


        // Animation
        backgroundImage = findViewById(R.id.SearcheForBluetoothMachineBackgroundImage);
        loopImage = findViewById(R.id.SearcheForBluetoothMachineLoopImage);

        try {
            image = BildgeneratorGlas.bildgenerationGlas(this,this.getRandomRecipe(),(float)1.0);
        } catch (TooManyTimesSettedIngredientEcxception e) {
            throw new RuntimeException(e);
        } catch (NoSuchIngredientSettedException e) {
            throw new RuntimeException(e);
        }

        Animation anim = new CircularAnimation(loopImage, 100);
        anim.setDuration(3000);
        anim.setRepeatCount(Animation.INFINITE);
        loopImage.startAnimation(anim);

        backgroundImage.setImageBitmap(image);


        //Bluethooth Scan

        if (Dummy.isDummy) {
            GetActivity.waitNotSet(this);
            return;
        } else {
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

    private Recipe getRandomRecipe(){
        Recipe recipe;
        Random random = new Random();

        List<Recipe> list = Recipe.getAllRecipes(this);
        int recipeNr = random.nextInt(list.size());
        recipe = list.get(recipeNr);
        list = null;
        List<Ingredient> ingredience = recipe.getIngredients(this);
        //int vol = recipe.getVolume(this,ingredience.get(0).getID());
        HashMap<Long, Integer> re = recipe.getIngredientIDToVolume(this);
        return recipe;
    }

    @SuppressLint("MissingPermission")
    private void bleConnect() {

    }


}