package nisarg.shah.projects;

import java.util.ArrayList;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;

public class WordList extends ListActivity {

	DBHelper dBHelper;
	SQLiteDatabase db;
	ArrayList<WordDataClass> data;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dBHelper = new DBHelper(WordList.this, getResources().openRawResource(R.raw.wordlist));
		db = dBHelper.getReadableDatabase();
	}
	
	   @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	    	super.onCreateOptionsMenu(menu);
	    	MenuItem miWordList = menu.add("Add new word");
	    	miWordList.setIcon(android.R.drawable.ic_input_add);
	    	miWordList.setShowAsAction(MenuItem.SHOW_AS_ACTION_WITH_TEXT|MenuItem.SHOW_AS_ACTION_IF_ROOM);
	    	miWordList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
				public boolean onMenuItemClick(MenuItem item) {
					startActivity(new Intent("nisarg.shah.projects.AddNewWord"));
					return false;
				}
			});
	    	return true;
	    }

	@Override
	protected void onResume() {
		Cursor cursor = db.query(DBHelper.TABLE, new String[] {DBHelper.COL_id, DBHelper.COL_value, DBHelper.COL_touse}, null, null, null, null, DBHelper.COL_id);
		
		//data = new WordDataClass[cursor.getCount()];
		data = new ArrayList<WordDataClass>(cursor.getCount());
		int i=0;
		for (boolean hasItem = cursor.moveToFirst(); hasItem; hasItem = cursor.moveToNext()) {
			boolean toUse=true;
			if(cursor.getInt(cursor.getColumnIndex(DBHelper.COL_touse))==0)
				toUse=false;
			//data[i]=new WordDataClass(toUse, cursor.getString(cursor.getColumnIndex(DBHelper.COL_id)),  cursor.getString(cursor.getColumnIndex(DBHelper.COL_value)));
			data.add(i, new WordDataClass(toUse, cursor.getString(cursor.getColumnIndex(DBHelper.COL_id)),  cursor.getString(cursor.getColumnIndex(DBHelper.COL_value))));
		    i++;
		}
		setListAdapter(new WordListViewDataAdapter(this, R.layout.wordlist_row, data));
		getListView().setFastScrollEnabled(true);
		super.onResume();
	}
	
}
