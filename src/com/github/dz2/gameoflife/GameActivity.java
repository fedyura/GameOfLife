package com.github.dz2.gameoflife;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class GameActivity extends FragmentActivity
	implements SettingsDialog.SettingsDialogListener {

	final int max_num_gen = 10;
	
	int mFieldSize;
	int mFieldType;
	int num_generation;
	
	String[] data; 
	ArrayAdapter<String> adapter;
	
	int[][] all_gen; //Все последние поколения
	
	Handler h;
	GridView gridview;
	Button startButton, stopButton;
	TextView countGen;
	
	boolean game_state = false;
	
	int i = 0;
	boolean pressed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		
		h = new Handler();
		
		Intent intent = getIntent();
		mFieldSize = intent.getIntExtra(MainActivity.MSG_FIELD_SIZE, 10);
		mFieldType = intent.getIntExtra(MainActivity.MSG_FIELD_TYPE, SettingsDialog.OPENED_FIELD);	
		
		data = new String[mFieldSize * mFieldSize];
		//cur_field = new int[mFieldSize * mFieldSize];
		all_gen = new int[max_num_gen] [mFieldSize * mFieldSize];
		
		for (int i = 0; i < mFieldSize * mFieldSize; i++) {
			data[i] = "";
			//cur_field[i] = 0;
			
			for (int j = 0; j < max_num_gen; j++) 
				all_gen[j][i] = 0;
		}
		
		num_generation = 1;
		
		gridview = (GridView) findViewById(R.id.gamegrid);
		startButton = (Button) findViewById(R.id.startgame_button);
		stopButton = (Button) findViewById(R.id.stopgame_button);
		stopButton.setEnabled(false);
		countGen = (TextView) findViewById(R.id.count_gen);
		countGen.setText("");
		
		gridview.setNumColumns(mFieldSize);
		
		adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.tvText, data);
		gridview.setAdapter(adapter);
		
		gridview.setBackgroundColor(Color.BLACK);
		gridview.setVerticalSpacing(2);
		gridview.setHorizontalSpacing(2);
		
		Toast.makeText(getApplicationContext(), "Задайте начальную конфигурацию", Toast.LENGTH_LONG).show();
		
	    gridview.setOnItemClickListener(new OnItemClickListener() {
	    
	    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	        	
	        	if (game_state == false) {
	        		
	        		int color = Color.TRANSPARENT;
		            Drawable background = v.getBackground();
		            if (background instanceof ColorDrawable)
		                color = ((ColorDrawable) background).getColor();
		        	
		            if (color != Color.BLACK) { 
		        		v.setBackgroundColor(Color.BLACK);
		        		all_gen[num_generation % max_num_gen][position] = 1;
		        	}
		        	else { 
		        		v.setBackgroundColor(Color.WHITE);
		        		all_gen[num_generation % max_num_gen][position] = 0;
		        	}
	        	}
	        }
	    });
	}
	
	Runnable stepAlgorithm = new Runnable() {
		
		public void run() {
			
			drawGridviewCells();
			
			if (game_state == true) {
				
				if (checkGameState()) {
				
					game_state = false;
					//showEndGameDialog();
					Toast.makeText(getApplicationContext(), "Конфигурация повторилась.\n Игра окончена на " +
								   String.valueOf(num_generation) + " поколении", Toast.LENGTH_LONG).show();
					countGen.setText("");
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					for (int j = 0; j < max_num_gen; j++)
						for (int i = 0; i < mFieldSize * mFieldSize; i++)
							all_gen[j][i] = 0;
					num_generation = 1;
					h.postDelayed(stepAlgorithm, 3000);
					return;
				}
			
				countGen.setText("Generation " + String.valueOf(num_generation));
				//Для каждой клетки на поле
				for (int i = 0; i < mFieldSize; i++)
					for (int j = 0; j < mFieldSize; j++) {
					
						int num_alive_neighboors = getNumberAliveNeighboors(i, j);
						if ((getField(i, j) == 0 && num_alive_neighboors == 3) ||
							(getField(i, j) == 1 && (num_alive_neighboors == 2 || num_alive_neighboors == 3)))	
							setField(i, j, 1);
						else 
							setField(i, j, 0);
					}
				
				num_generation++;
				h.postDelayed(stepAlgorithm, 2000);
			}
		}
	};
	
	private void showEndGameDialog() {
		
		new AlertDialog.Builder(this)
	    .setTitle("Конец игры")
	    .setMessage("Конфигурация повторилась. Игра окончена на " 
	    			+ String.valueOf(num_generation) + " поколении.")
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     }).show();
	}
	
	private boolean checkGameState() {
		
		int j = 0;
		for (int i = 0; i < max_num_gen; i++) {
			
			if (i == num_generation % max_num_gen) 
				continue;
			for (j = 0; j < mFieldSize * mFieldSize; j++)
				if (all_gen[i][j] != all_gen[num_generation % max_num_gen][j])
					break;
			if (j == mFieldSize * mFieldSize)
				return true;
		}
		return false;
	}
	
	//Возвращает число живых соседей для каждой клетки. Зависит от того, замкнутое ли поле
	// i,J - строка/столбец текущей клетки
	private int getNumberAliveNeighboors(int i, int j) {
		
		return getField(i-1, j-1) + getField(i-1, j) + getField(i-1, j+1) +
			   getField(i, j-1) + getField(i, j+1) + 
			   getField(i+1, j-1) + getField(i+1, j) + getField(i+1, j+1);
	}
	
	private int getField(int i, int j) {
		
		//Для незамкнутого поля
		if (mFieldType == SettingsDialog.OPENED_FIELD) {
			
			if (i < 0) i += mFieldSize;
			if (j < 0) j += mFieldSize;
			if (i > mFieldSize - 1) i -= mFieldSize;
			if (j > mFieldSize - 1) j -= mFieldSize;
		}
		
		if (i >= 0 && j >= 0 && i <= mFieldSize - 1 && j <= mFieldSize - 1)
			return all_gen[num_generation % max_num_gen][i * mFieldSize + j];
		else 
			return 0;
	}
	
	private void setField(int i, int j, int value) {
		
		int num_next_gen = (num_generation + 1) % max_num_gen;
		//if (num_next_gen == max_num_gen) num_next_gen = 0;
		all_gen[num_next_gen][i * mFieldSize + j] = value;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.game, menu);
		return true;
	}
	
	public void onClickStartGame(View view) {
		
		game_state = true;
		stopButton.setEnabled(true);
		startButton.setEnabled(false);
		
		num_generation = 1;
		h.postDelayed(stepAlgorithm, 100);
	}
	
	public void onClickStopGame(View view) {
		
		game_state = false;
		stopButton.setEnabled(false);
		startButton.setEnabled(true);
		h.removeCallbacks(stepAlgorithm);
		countGen.setText("");
		num_generation = 1;
	}
	
	public void onClickSettings(View view) {
		
		if (game_state == true)
			h.removeCallbacks(stepAlgorithm);
		
		Bundle bundle = new Bundle();
		bundle.putString("fieldsize", String.valueOf(mFieldSize));
		bundle.putInt("fieldtype", mFieldType);
		
		DialogFragment dialog = new SettingsDialog();
		
		dialog.setArguments(bundle);
        dialog.show(getSupportFragmentManager(), "SettingsDialog");
	}
	
	public void onClickRules(View view) {
		
		new AlertDialog.Builder(this)
	    .setTitle("Conway's Game of Life. Правила игры")
	    .setMessage("В начале игры задается начальная конфигурация живых клеток." +
	                "Затем происходит их жизнедеятельность по следующим правилам:\n" +
	    		    "1. В пустой клетке, рядом с которой 3 живые клетки зарождается жизнь\n" +
	                "2. Живая клетка, рядом с которой находится менее 2 или более 3 других живых клеток, умирает\n" +
	    		    "Игра прекращается, когда полученная на следующем шаге конфигурация уже была на предыдущем шаге.\n" +
	                "Автор: Юра Федоренко")
	    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int which) { 
	            // continue with delete
	        }
	     }).show();
	}

	@Override
	public void onDialogPositiveClick(DialogFragment dialog, int field_size,
			int field_type) {
		// TODO Auto-generated method stub
		copyMas(field_size, field_type);
		
		gridview.setNumColumns(mFieldSize);
		adapter = new ArrayAdapter<String>(this, R.layout.item, R.id.tvText, data);
		
		gridview.invalidateViews();
		gridview.setAdapter(adapter);
		
		h.postDelayed(stepAlgorithm, 0);
	}

	@Override
	public void onDialogNegativeClick(DialogFragment dialog) {
		// TODO Auto-generated method stub
		if (game_state == true)
			h.postDelayed(stepAlgorithm, 0);
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
        
        Toast.makeText(getApplicationContext(), "Input correct data", Toast.LENGTH_SHORT).show();
	}
	
	private void copyMas(int field_size, int field_type) {
		
		int[] field_copy = new int[mFieldSize*mFieldSize];
		int old_size = mFieldSize;
		for (int i = 0; i < mFieldSize*mFieldSize; i++) 
			field_copy[i] = all_gen[num_generation % max_num_gen] [i];
		
		mFieldSize = field_size;
		mFieldType = field_type;
		
		num_generation = 1;
		
		//Изменяем массив
		data = new String[mFieldSize * mFieldSize];
		all_gen = new int[max_num_gen][mFieldSize * mFieldSize];
		
		for (int i = 0; i < mFieldSize * mFieldSize; i++) {
			data[i] = "";
			for (int j = 0; j < max_num_gen; j++)
				all_gen[j][i] = 0;
		}
		
		for (int i = 0; i < mFieldSize && i < old_size; i++)
			for (int j = 0; j < mFieldSize && j < old_size; j++)
				all_gen[num_generation % max_num_gen][i * mFieldSize + j] = field_copy[i * old_size + j];
	}
	
	private void drawGridviewCells() {
		
		for (int i = 0; i < mFieldSize * mFieldSize; i++) {
			
			if (all_gen[num_generation % max_num_gen][i] == 0) 
				gridview.getChildAt(i).setBackgroundColor(Color.WHITE);				
			else
				gridview.getChildAt(i).setBackgroundColor(Color.BLACK);
		}
	}
}
