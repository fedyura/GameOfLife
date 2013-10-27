package com.github.dz2.gameoflife;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;

public class SettingsDialog extends DialogFragment {
	
	static final int OPENED_FIELD = 0;
	static final int CLOSED_FIELD = 1;
	
	public interface SettingsDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog, int field_size, int field_type);
        public void onDialogNegativeClick(DialogFragment dialog);
        public void onDialogIncorrectData(String field_size, int field_type);
    }
	
	SettingsDialogListener mListener;
	EditText edText;
	RadioButton rbButtonCl, rbButtonOp;
	int mFieldSize;
	int mFieldType;
	
	public SettingsDialog() {
		
	}
	
	@Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.set_dialog, null);
        
        String field_size = getArguments().getString("fieldsize");
        mFieldType = getArguments().getInt("fieldtype");
        
        edText = (EditText) dialogView.findViewById(R.id.FieldEdit);
        edText.setText(field_size);
        
        rbButtonCl = (RadioButton) dialogView.findViewById(R.id.closed);
        rbButtonOp = (RadioButton) dialogView.findViewById(R.id.opened);
        
        if (mFieldType == OPENED_FIELD) { 
        	rbButtonOp.setChecked(true);
        	rbButtonCl.setChecked(false);
        }
        
        if (mFieldType == CLOSED_FIELD) { 
        	rbButtonCl.setChecked(true);
        	rbButtonOp.setChecked(false);
        }
        
		builder.setView(dialogView)
        	.setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                // sign in the user ...
            		String message = edText.getText().toString();
            		
            		if (rbButtonOp.isChecked() == true) mFieldType = OPENED_FIELD;
            		if (rbButtonCl.isChecked() == true) mFieldType = CLOSED_FIELD;
            		
            		/*if (message == null || message == "") {
            			mListener.onDialogIncorrectData();
            			return;
            		}*/
            		try {
            			mFieldSize = Integer.parseInt(message);
            		}
            		catch (NumberFormatException ex) {
            			
            			mListener.onDialogIncorrectData(message, mFieldType);
            			return;
            		}
            		if (mFieldSize <= 3 || mFieldSize >= 26) {
            			
            			mListener.onDialogIncorrectData(message, mFieldType);
            			return;
            		}
            		
            		mListener.onDialogPositiveClick(SettingsDialog.this, mFieldSize, mFieldType);
            	}
        	})
        	.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
               public void onClick(DialogInterface dialog, int id) {
                   mListener.onDialogNegativeClick(SettingsDialog.this);
               }
           });
        
        // Create the AlertDialog object and return it
        return builder.create();
    }
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (SettingsDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

}
