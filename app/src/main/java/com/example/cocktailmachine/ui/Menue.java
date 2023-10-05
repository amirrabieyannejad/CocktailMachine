package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;
import com.example.cocktailmachine.databinding.ActivityMenueBinding;
import com.example.cocktailmachine.ui.ListOfIngredience.ListIngredience;
import com.example.cocktailmachine.ui.calibration.scale.calibrationScale;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.model.ModelType;
import com.example.cocktailmachine.ui.model.v2.CocktailMachineCalibration;
import com.example.cocktailmachine.ui.model.v2.DialogListOfPumps;
import com.example.cocktailmachine.ui.model.v2.GetActivity;
import com.example.cocktailmachine.ui.settings.SettingsActivity;
import com.example.cocktailmachine.ui.singleCocktailChoice.SingleCocktailChoice;

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
        Log.i(TAG, "onCreate");
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
        if(Dummy.asAdmin){
            Log.i(TAG, "onCreate: dummy asadmin");
            AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
        }
        if(AdminRights.isAdmin()){
            Log.i(TAG, "onCreate: Admin Modus");
            binding.activityMenueLogout.setVisibility(View.VISIBLE);
            binding.activityMenueLogin.setVisibility(View.GONE);
        }else{
            Log.i(TAG, "onCreate: User Modus");
            binding.activityMenueLogout.setVisibility(View.GONE);
            binding.activityMenueLogin.setVisibility(View.VISIBLE);
        }
        if(!Dummy.withSetCalibration){
            CocktailMachineCalibration.setIsDone(true);
            Log.i(TAG, "onCreate: dummy: isDummy und not withSetCalibration ");
        }
        if (!CocktailMachineCalibration.isIsDone()){
            Log.i(TAG, "onCreate: start calibration ");
            CocktailMachineCalibration.start(this);
        }
        if(!Dummy.withTestEnvs){
            Log.i(TAG, "onCreate: without test envs  ");
            binding.imageViewTestBlue.setVisibility(View.GONE);
            binding.imageViewTestIngList.setVisibility(View.GONE);
            binding.imageViewTestFillAn.setVisibility(View.GONE);
            binding.imageViewTestGrafik.setVisibility(View.GONE);
            binding.imageViewTestSingleCockt.setVisibility(View.GONE);
            binding.imageViewTestCal.setVisibility(View.GONE);
            binding.imageViewTestPumpCalib.setVisibility(View.GONE);
        }else{
            Log.i(TAG, "onCreate: with test envs  ");
        }



    }

    /**
     * opens recipe list
     * @author Johanna Reidt
     * @param view
     */
    public void openRecipeList(View view) {
        Log.i(TAG, "openRecipeList");
        /*
        Intent success = new Intent(this, ModelActivity.class);
        Bundle b = new Bundle();
        b.putString("FragmentType", FragmentType.List.toString());
        b.putString("ModelType", ModelType.RECIPE.toString());
        success.putExtras(b);
        startActivity(success);

         */

        GetActivity.goToList(this, ModelType.RECIPE );
    }

    /**
     * open recipe creator
     * @author Johanna Reidt
     * @param view
     */
    public void openRecipeCreator(View view){
        Log.i(TAG, "openRecipeCreator");
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
        Log.i(TAG, "openSettings");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * open single cocktail choice
     * TODO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void openSingleCocktailView(View view){
        Log.i(TAG, "openSingleCocktailView");
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
        Log.i(TAG, "openGlassFillAnimationView");
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
        Log.i(TAG, "openGrafik");
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
        Log.i(TAG, "openDeviceScan");
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
        Log.i(TAG, "exit");
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
        Log.i(TAG, "testEnviroment");
        Intent success = new Intent(this, BluetoothTestEnviroment.class);
        startActivity(success);

    }

    public void calibration(View view){
        Log.i(TAG, "calibration");
        Intent success = new Intent(this, calibrationScale.class);
        startActivity(success);
    }

    public void listIngedients(View view){
        Log.i(TAG, "listIngedients");
        Intent success = new Intent(this, ListIngredience.class);
        startActivity(success);

    }

    public void pumpCalibration(View view){
        Log.i(TAG, "pumpCalibration");
        //Intent success = new Intent(this, ListOfPumps.class);
        //startActivity(success);
        new DialogListOfPumps(this);
    }


}