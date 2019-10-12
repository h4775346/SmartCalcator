package com.example.smartcalcator;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class ViewDialog {

    Activity activity;

    public ViewDialog(Activity activity) {

        this.activity = activity;

    }

    public void showDialog( String result){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.result_dialog);

       Button finish =  dialog.findViewById(R.id.btn_finish);
       Button btn_getText = dialog.findViewById(R.id.btn_getText);
        final TextView txt_result = dialog.findViewById(R.id.txt_result_message);

        txt_result.setText(result);





        finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        btn_getText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mResultEt.setText(txt_result.getText().toString());
                activity.finish();

                dialog.dismiss();




            }
        });

        dialog.show();
    }


}
