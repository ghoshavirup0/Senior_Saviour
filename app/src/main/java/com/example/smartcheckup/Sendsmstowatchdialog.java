package com.example.smartcheckup;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatDialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class Sendsmstowatchdialog extends AppCompatDialogFragment {

    private EditText editTextUsername;
    private TextView counter;
    private SendsmstowatchdialogListner listner;
    String text;int symbols;int c=30;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.TimePickerTheme);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View view = inflater.inflate(R.layout.parentsendmessage, null);

        builder.setView(view)
                .setTitle("Write Short Message")
                .setNegativeButton("Reset/Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String u="";
                        listner.applysms(u);
                    }
                })
                .setPositiveButton("Send Message", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String username = editTextUsername.getText().toString();
                        listner.applysms(username);
                    }
                });

        editTextUsername = view.findViewById(R.id.writesms);
        counter=view.findViewById(R.id.countersms);
        editTextUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                text= editTextUsername.getText().toString();
                symbols=text.length();
                counter.setText(symbols+"/30");
                if(symbols>=c) {
                    editTextUsername.setError("LIMIT EXCEEDED");
                    editTextUsername.setEnabled(false);
                }
                else
                {
                    text= editTextUsername.getText().toString();
                    symbols=text.length();
                }            }

            @Override
            public void afterTextChanged(Editable s) {
                editTextUsername.setEnabled(true);
            }
        });
        return builder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        listner=(SendsmstowatchdialogListner)context;
    }

    public interface  SendsmstowatchdialogListner
    {
        void applysms(String username);
    }

}