package com.github.dz2.gameoflife;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends FragmentActivity
	implements SettingsDialog.SettingsDialogListener {

	static final String MSG_FIELD_SIZE = "com.github.dz2.gameoflife.MSG_FIELD_SIZE";
	static final String MSG_FIELD_TYPE = "com.github.dz2.gameoflife.MSG_FIELD_TYPE";
	
	int mFieldSize;
	int mFieldType; //1 - closed field, 0 - open field
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mFieldSize = 10;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void startGame(View view) {
    	
    	Intent intent = new Intent(this, GameActivity.class);
    	intent.putExtra(MSG_FIELD_SIZE, mFieldSize);
    	intent.putExtra(MSG_FIELD_TYPE, mFieldType);
    	startActivity(intent);
    }
	
	public void openSettings(View view) {
	    // Do something in response to button
		Bundle bundle = new Bundle();
		bundle.putString("fieldsize", String.valueOf(mFieldSize));
		bundle.putInt("fieldtype", mFieldType);
		
		DialogFragment dialog = new SettingsDialog();
		
		dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SettingsDialog");
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, int field_size, int field_type) {
		// TODO Auto-generated method stub
		mFieldSize = field_size;
		mFieldType = field_type;
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDialogIncorrectData(String field_size, int field_type) {
		// TODO Auto-generated method stub
		DialogFragment dialog_new = new SettingsDialog();
		
		Bundle bundle=new Bundle();
		bundle.putString("fieldsize", field_size);
		bundle.putInt("fieldtype", field_type);
		dialog_new.setArguments(bundle);
		
		dialog_new.show(getSupportFragmentManager(), "SettingsDialog");
        
        Toast.makeText(getApplicationContext(), "Input correct data", Toast.LENGTH_LONG).show();
	}
}
