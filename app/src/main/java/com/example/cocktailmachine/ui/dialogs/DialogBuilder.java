package com.example.cocktailmachine.ui.dialogs;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.telecom.Call;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

import java.util.concurrent.Callable;

/**
 * @author Johanna Reidt
 * @created Fr. 23.Jun 2023 - 16:40
 * @project CocktailMachine
 */
public class DialogBuilder {
    //TODO Dialog Builder


    private static final String TAG = "DialogBuilder" ;

    public static boolean getPasswordDialog(Activity activity){
        EditView res = get(activity,
                EditType.String,
                "Bitte geben Sie das Passwort ein!",
                "Passwort",
                "");
        return AdminRights.PASSWORD.equals(res.getEntered());
    }


    /**
     *
     * @author Johanna Reidt
     * @param activity
     * @param type
     * @param title
     * @param editText
     * @param editHint
     * @return
     */
    @NonNull
    public static EditView get(
            Activity activity,
            EditType type,
            String title,
            String editText,
            String editHint
    ){

        Log.i(TAG, "get" );

        AlertDialog.Builder builder = new AlertDialog.Builder(activity.getBaseContext());
        builder.setTitle(title);
        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        final EditView editView = getEditView(type, v, editText, editHint);
        assert editView != null;

        builder.setView(v);
        builder.setPositiveButton("Weiter",
                (dialog, which) ->
                    editView.done());
        builder.setNegativeButton("Abbrechen",
                (dialog, which) -> dialog.dismiss());
        builder.setOnDismissListener(
                dialog -> editView.dismiss()
        );
        builder.show();
        return editView;
    }


    @Nullable
    public static EditView getEditView(EditType type, View view, String editText, String editHint){
        switch (type){
            case String: return new StringEditView(view, editText, editHint);
            case Int: return new IntEditView(view, editText, editHint);
            case Float: return new FloatEditView(view, editText, editHint);
        }
        return null;
    }


/*
    public static void getEditDialog(
            Activity activity,
            DialogInterface.OnDismissListener dismissListener,
            String title,
            String editText,
            String editHint,
            int inputType,
            ContactsContract.CommonDataKinds.Callable callable){

        androidx.appcompat.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title);
        View v = activity.getLayoutInflater().inflate(R.layout.layout_login, null);
        DialogBuilder.EditView edit = new
                DialogBuilder.FloatEditView(
                        v,
                        editText,
                        editHint);

        builder.setView(v);
        builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

 */
                //TO DO: resolve the onclick situation
                /*if(loginView.getEntered()){
                    AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
                    Toast.makeText(activity,"Skalierung im Gange!",Toast.LENGTH_SHORT).show();
                }

                 */
    /*
            }
        });
        builder.setNegativeButton("Abbrechen", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setOnDismissListener(dismissListener);
        builder.show();
    }

     */

    private abstract static class EditView<T>{
        private final TextView t;
        private final EditText e;
        private final View v;
        private boolean wasDismissed = false;
        private boolean isDone = false;
        public EditView(View v, String editText,String editHint, int inputType) {
            this.v = v;
            t = (TextView) v.findViewById(R.id.textView_edit_text);
            e = (EditText) v.findViewById(R.id.editText_edit_text);
            e.setHint(editHint);
            t.setText(editText);
            e.setInputType(inputType);

        }

        protected  EditText getE(){
            return e;
        }

        protected void dismiss(){
            this.wasDismissed = true;
            this.isDone();
        }

        private boolean wasDismissed(){
            return wasDismissed;
        }

        protected void done(){
            this.isDone = true;
        }

        private boolean isDone(){
            return isDone;
        }

        abstract T getEntered();
    }

    private static class IntEditView extends EditView<Integer>{
        public IntEditView(View v, String editText, String editHint) {
            super(v, editText, editHint, InputType.TYPE_CLASS_NUMBER);
        }

        /**
         *
         * @author Johanna Reidt
         * @return
         */
        @Override
        Integer getEntered() {
            return Integer.valueOf(getE().getText().toString());
        }
    }

    private static class FloatEditView extends EditView<Float>{
        public FloatEditView(View v, String editText, String editHint) {
            super(v, editText, editHint, InputType.TYPE_NUMBER_FLAG_DECIMAL);
        }

        /**
         *
         * @author Johanna Reidt
         * @return
         */
        @Override
        Float getEntered() {
            return Float.valueOf(getE().getText().toString());
        }
    }


    private static class StringEditView extends EditView<String>{
        public StringEditView(View v, String editText, String editHint) {
            super(v, editText, editHint, InputType.TYPE_CLASS_TEXT);
        }

        /**
         *
         * @author Johanna Reidt
         * @return
         */
        @Override
        String getEntered() {
            return getE().getText().toString();
        }
    }
}
