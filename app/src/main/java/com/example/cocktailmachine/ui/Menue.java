package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.db.Buffer;
import com.example.cocktailmachine.data.db.exceptions.NotInitializedDBException;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.databinding.ActivityMenueBinding;
import com.example.cocktailmachine.ui.ListOfIngredience.ListIngredience;
import com.example.cocktailmachine.ui.calibration.scale.calibrationScale;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.model.FragmentType;
import com.example.cocktailmachine.ui.model.ModelType;
import com.example.cocktailmachine.ui.model.v1.ModelActivity;
import com.example.cocktailmachine.ui.model.v2.CocktailMachineCalibration;
import com.example.cocktailmachine.ui.model.v2.GetActivity;
import com.example.cocktailmachine.ui.settings.SettingsActivity;
import com.example.cocktailmachine.ui.singleCocktailChoice.SingleCocktailChoice;

import org.json.JSONException;

/**
 * Menu class
 * is the Main Activity and is intended to provide the shortcuts to the Recipe List/Creation,
 * the settings and login/logout to the admin view with appropriate symbols.
 * @created Fr. 23.Jun 2023 - 14:14
 * @project CocktailMachine
 * @author Wieber
 */
public class Menue extends AppCompatActivity {
    private static final String TAG = "Menue";
    private ActivityMenueBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_menue);
        binding = ActivityMenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*
        if(!DatabaseConnection.isInitialized()) {
            Log.i(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initializeSingleton(this, UserPrivilegeLevel.Admin);
            try {
                DatabaseConnection.getDataBase();
                Log.i(TAG, "onCreate: DataBase is initialized");
                //Log.i(TAG, Recipe.getAllRecipesAsMessage().toString());
            } catch (NotInitializedDBException e) {
                e.printStackTrace();
                Log.e(TAG, "onCreate: DataBase is not initialized");
            }
        }

         */
        if(!Buffer.isLoaded) {
            try {
                Buffer.getSingleton().load(this);
            } catch (NotInitializedDBException e) {
                Log.e(TAG, "onCreate: NotInitializedDBException");
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        }
        if(AdminRights.isAdmin()){
            binding.activityMenueLogout.setVisibility(View.VISIBLE);
            binding.activityMenueLogin.setVisibility(View.GONE);
        }else{
            binding.activityMenueLogout.setVisibility(View.GONE);
            binding.activityMenueLogin.setVisibility(View.VISIBLE);
        }
        if(Dummy.isDummy && !Dummy.withSetCalibration ){
            CocktailMachineCalibration.setIsDone(true);
        }else if (!CocktailMachineCalibration.isIsDone()){
            CocktailMachineCalibration.start(this);
        }


    }

    /**
     * opens recipe list
     * @author Johanna Reidt
     * @param view
     */
    public void openRecipeList(View view) {
        /*
        Intent success = new Intent(this, ModelActivity.class);
        Bundle b = new Bundle();
        b.putString("FragmentType", FragmentType.List.toString());
        b.putString("ModelType", ModelType.RECIPE.toString());
        success.putExtras(b);
        startActivity(success);

         */

        GetActivity.goToDisplay(this,FragmentType.List, ModelType.RECIPE );
    }

    /**
     * open recipe creator
     * @author Johanna Reidt
     * @param view
     */
    public void openRecipeCreator(View view){
        /*

        Intent success = new Intent(this,
                ModelActivity.class);
        Bundle b = new Bundle();
        b.putString(
                "FragmentType",
                FragmentType.Edit.toString());
        b.putString("ModelType",
                ModelType.RECIPE.toString());
        startActivity(success, b);

         */
        GetActivity.goToAdd(this, ModelType.RECIPE);
    }

    /**
     * opens Settings
     * @author Johanna Reidt
     * @param view
     */
    public void openSettings(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * open single cocktail choice
     * TODO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void openSingelCocktailView(View view){
        Intent success = new Intent(this, SingleCocktailChoice.class);
        startActivity(success);

    }

    /**
     * opens GlassFillAnimation
     * TODO: put in respective place and delete here
     * @author  Wieber
     * @param view
     */
    public void openGlassFillAnimationView(View view){
        Intent success = new Intent(this, FillAnimation.class);
        startActivity(success);

    }

    /**
     * opens Grafik
     * TODO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void openGrafik(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }

    /**
     * opens device scan, scans for bluetooth connection
     *
     * @author Wieber
     * @param view
     */
    public void openDeviceScan(View view){
        Intent success = new Intent(this, DeviceScanActivity.class);
        startActivity(success);
    }

    /**
     * opens login dialog
     * @author Johanna Reidt
     * @param view
     */
    public void login(View view){
        Log.i(TAG, "login");
        AdminRights.login(this,
                getLayoutInflater(),
                dialog -> successfulLogin());
        Log.i(TAG, "finished login");
    }

    /**
     * if login successful display logout symbol
     * @author Johanna Reidt
     */
    public void successfulLogin(){
        Log.i(TAG, "successfulLogin");
        if(AdminRights.isAdmin()) {
            Log.i(TAG, "successful login: admin");
            binding.activityMenueLogout.setVisibility(View.VISIBLE);
            binding.activityMenueLogin.setVisibility(View.GONE);
        }
    }

    /**
     * logs admin out, displays login symbol
     * @author Johanna Reidt
     * @param view
     */
    public void logout(View view){
        Log.i(TAG, "logout");
        AdminRights.logout();
        binding.activityMenueLogout.setVisibility(View.GONE);
        binding.activityMenueLogin.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
        Log.i(TAG, "finished logout");
    }


    /**
     *  ????????
     * TODO: dont know the use
     * @author Wieber
     * @param view
     */
    public void exit(View view){
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }

    /**
     * opens BluetoothTestEnviroment
     * TODO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void testEnviroment(View view){
        Intent success = new Intent(this, BluetoothTestEnviroment.class);
        startActivity(success);

    }

    public void calibration(View view){
        Intent success = new Intent(this, calibrationScale.class);
        startActivity(success);

    }

    public void listIngedients(View view){
        Intent success = new Intent(this, ListIngredience.class);
        startActivity(success);

    }


}