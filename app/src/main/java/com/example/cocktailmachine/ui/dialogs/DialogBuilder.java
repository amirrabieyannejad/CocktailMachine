package com.example.cocktailmachine.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.provider.ContactsContract;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.cocktailmachine.R;
import com.example.cocktailmachine.data.CocktailMachine;
import com.example.cocktailmachine.data.enums.AdminRights;
import com.example.cocktailmachine.data.enums.UserPrivilegeLevel;

/**
 * @author Johanna Reidt
 * @created Fr. 23.Jun 2023 - 16:40
 * @project CocktailMachine
 */
public class DialogBuilder {


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
        DialogBuilder.EditView loginView = new
                DialogBuilder.FloatEditView(
                        v,
                        editText,
                        editHint);

        builder.setView(v);
        builder.setPositiveButton("Weiter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO: resolve the onclick situation
                /*if(loginView.getEntered()){
                    AdminRights.setUserPrivilegeLevel(UserPrivilegeLevel.Admin);
                    Toast.makeText(activity,"Skalierung im Gange!",Toast.LENGTH_SHORT).show();
                }

                 */
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
    private abstract static class EditView<T>{
        private final TextView t;
        private final EditText e;
        private final View v;
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
