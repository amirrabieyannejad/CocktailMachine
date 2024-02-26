package com.example.cocktailmachine.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.cocktailmachine.Dummy;
import com.example.cocktailmachine.bluetoothlegatt.DeviceScanActivity;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.Recipe;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.Postexecute;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;
import com.example.cocktailmachine.databinding.ActivityMenueBinding;
import com.example.cocktailmachine.ui.ListOfIngredients.ListIngredients;
import com.example.cocktailmachine.ui.manualtestingsuit.calibration.scale.calibrationScale;
import com.example.cocktailmachine.ui.fillAnimation.FillAnimation;
import com.example.cocktailmachine.ui.manualtestingsuit.BluetoothNotFound;
import com.example.cocktailmachine.ui.manualtestingsuit.BluetoothTestEnviroment;
import com.example.cocktailmachine.ui.manualtestingsuit.Grafik;
import com.example.cocktailmachine.ui.manualtestingsuit.LoadScreen;
import com.example.cocktailmachine.ui.model.enums.ModelType;
import com.example.cocktailmachine.ui.model.helper.DialogListOfPumps;
import com.example.cocktailmachine.ui.model.helper.GetActivity;
import com.example.cocktailmachine.ui.model.helper.GetDialog;
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
        Log.v(TAG, "onCreate");
        //setContentView(R.layout.activity_menue);
        binding = ActivityMenueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        /*
        if(!DatabaseConnection.isInitialized()) {
            Log.v(TAG, "onCreate: DataBase is not yet initialized");
            DatabaseConnection.initializeSingleton(this, UserPrivilegeLevel.Admin);
            try {
                DatabaseConnection.getDataBase();
                Log.v(TAG, "onCreate: DataBase is initialized");
                //Log.v(TAG, Recipe.getAllRecipesAsMessage().toString());
            } catch (NotInitializedDBException e) {
                Log.getStackTrace(e);
                Log.e(TAG, "onCreate: DataBase is not initialized");
            }
        }

         */
        //ExtraHandlingDB.loadPrepedDB(this);
        if(Dummy.asAdmin){
            Log.v(TAG, "onCreate: dummy asadmin");
            AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
        }
        if(AdminRights.isAdmin()){
            Log.v(TAG, "onCreate: Admin Modus");
            binding.activityMenueLogout.setVisibility(View.VISIBLE);
            binding.activityMenueLogin.setVisibility(View.GONE);
        }else{
            Log.v(TAG, "onCreate: User Modus");
            binding.activityMenueLogout.setVisibility(View.GONE);
            binding.activityMenueLogin.setVisibility(View.VISIBLE);
        }


        if(!CocktailMachine.getSingleton().isIsDone()) {
            CocktailMachine.getSingleton().askIsDone(this, new Postexecute() {
                @Override
                public void post() {
                    if (!CocktailMachine.getSingleton().isIsDone()) {
                        Log.w(TAG, "onCreate: start calibration ");
                        GetActivity.waitNotSet(Menue.this);
                        ///CocktailMachineCalibration.start(Menue.this);
                        Log.v(TAG, "onCreate: start done ");
                    }
                }
            });
        }


        if(!Dummy.withTestEnvs){
            Log.v(TAG, "onCreate: without test envs  ");
            binding.imageViewTestBlue.setVisibility(View.GONE);
            binding.imageViewTestIngList.setVisibility(View.GONE);
            binding.imageViewTestFillAn.setVisibility(View.GONE);
            binding.imageViewTestGrafik.setVisibility(View.GONE);
            binding.imageViewTestSingleCockt.setVisibility(View.GONE);
            binding.imageViewTestCal.setVisibility(View.GONE);
            binding.imageViewTestBluetoothNotFound.setVisibility(View.GONE);
            binding.imageViewTestPump.setVisibility(View.GONE);
        }else{
            Log.v(TAG, "onCreate: with test envs  ");
        }



    }

    /**
     * opens recipe list
     * @author Johanna Reidt
     * @param view
     */
    public void openRecipeList(View view) {
        Log.v(TAG, "openRecipeList");
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
        Log.v(TAG, "openRecipeCreator");
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
        Log.v(TAG, "openSettings");
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }


    /**
     * open single cocktail choice
     * TO DO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void openSingleCocktailView(View view){
        Log.v(TAG, "openSingleCocktailView");
        Intent success = new Intent(this, SingleCocktailChoice.class);
        startActivity(success);

    }

    /**
     * opens GlassFillAnimation
     * TO DO: put in respective place and delete here
     * @author  Wieber
     * @param view
     */
    public void openGlassFillAnimationView(View view){
        Log.v(TAG, "openGlassFillAnimationView");
        Intent success = new Intent(this, FillAnimation.class);
        success.putExtra(GetActivity.ID, Recipe.getAllRecipes(this).get(0).getID());
        startActivity(success);

    }

    /**
     * opens Grafik
     * TO DO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void openGrafik(View view){
        Log.v(TAG, "openGrafik");
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
        Log.v(TAG, "openDeviceScan");
        Intent success = new Intent(this, DeviceScanActivity.class);
        startActivity(success);
    }

    /**
     * opens login dialog
     * @author Johanna Reidt
     * @param view
     */
    public void login(View view){
        Log.v(TAG, "login");
        AdminRights.login(this,
                getLayoutInflater(),
                dialog -> successfulLogin());
        Log.v(TAG, "finished login");
    }

    /**
     * if login successful display logout symbol
     * @author Johanna Reidt
     */
    public void successfulLogin(){
        Log.v(TAG, "successfulLogin");
        if(AdminRights.isAdmin()) {
            Log.v(TAG, "successful login: admin");
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
        Log.v(TAG, "logout");
        AdminRights.logout();
        binding.activityMenueLogout.setVisibility(View.GONE);
        binding.activityMenueLogin.setVisibility(View.VISIBLE);
        Toast.makeText(this,"Ausgeloggt!",Toast.LENGTH_SHORT).show();
        Log.v(TAG, "finished logout");
    }


    /**
     *  ????????
     * TO DO: dont know the use
     * @author Wieber
     * @param view
     */
    public void exit(View view){
        Log.v(TAG, "exit");
        Intent success = new Intent(this, Grafik.class);
        startActivity(success);

    }

    /**
     * opens BluetoothTestEnviroment
     * TO DO: put in respective place and delete here
     * @author Wieber
     * @param view
     */
    public void testEnviroment(View view){
        Log.v(TAG, "testEnviroment");
        Intent success = new Intent(this, BluetoothTestEnviroment.class);
        startActivity(success);
    }

    public void calibration(View view){
        Log.v(TAG, "calibration");
        Intent success = new Intent(this, calibrationScale.class);
        startActivity(success);
    }

    public void listIngedients(View view){
        Log.v(TAG, "listIngedients");
        Intent success = new Intent(this, ListIngredients.class);
        startActivity(success);

    }

    public void pumpCalibration(View view){
        Log.v(TAG, "pumpCalibration");
        //Intent success = new Intent(this, ListOfPumps.class);
        //startActivity(success);
        new DialogListOfPumps(this);
    }

    public void bluetoothNotFound(View view){
        Log.i(TAG, "listIngedients");
        Intent success = new Intent(this, BluetoothNotFound.class);
        startActivity(success);

    }

    public void pumpSetting(View view){
        Log.i(TAG, " activity Pump Setting");
        Intent success = new Intent(this, LoadScreen.class);
        startActivity(success);

    }

    public void loadAnimation(View view){
        Log.i(TAG, " activity LoadDataAnimation");
        //Intent success = new Intent(this, LoadDataAnimation.class);
        //startActivity(success);
        GetDialog.loadingBluetooth(this).show();

    }

}