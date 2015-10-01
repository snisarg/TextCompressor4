package nisarg.shah.projects;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddNewWord extends Activity {
	
	EditText etKey, etValue;
	TextView tvErrorMsg;
	
	  @Override
	  public void onCreate(Bundle savedInstanceState) {
		  super.onCreate(savedInstanceState);
	      setContentView(R.layout.add_new_word_layout);
	      etKey = (EditText)findViewById(R.id.etNewWordKey);
	      etValue = (EditText)findViewById(R.id.etNewWordValue);
	      tvErrorMsg = (TextView)findViewById(R.id.tvErrorMsg);
	  }
	  
	  public void buttonSaveClicked(View view) {
		  DBHelper dBHelper;
		  String key = etKey.getText().toString().toLowerCase().replace("'", "").trim();
		  String value = etValue.getText().toString().toLowerCase().replace("'", "").trim();
		  dBHelper = new DBHelper(AddNewWord.this, getResources().openRawResource(R.raw.wordlist));
		  if(key.length()<2) {
			  tvErrorMsg.setVisibility(View.VISIBLE);
			  tvErrorMsg.setText("ERROR:\nLength of the word has to be atleast 2 characters.");
			  return;
		  }
		  else if(key.length()==0 || value.length()==0){
			  tvErrorMsg.setVisibility(View.VISIBLE);
			  tvErrorMsg.setText("ERROR:\nNone of the fields can be empty.");
			  return;
		  }
		  else if(!dBHelper.wordConversion(key).equals("")){
			  tvErrorMsg.setVisibility(View.VISIBLE);
			  tvErrorMsg.setText("ERROR:\nA conversion for the word already exists. Delete that one to add another conversion.");
			  return;
		  }
		  else{
			  SQLiteDatabase db;
			  db = dBHelper.getWritableDatabase();
			  ContentValues contentValues = new ContentValues();
			  contentValues.put(DBHelper.COL_id, key);
			  contentValues.put(DBHelper.COL_value, value);
			  contentValues.put(DBHelper.COL_touse, 1);
			  db.insert(DBHelper.TABLE, null, contentValues);
			  Toast.makeText(this, "New word added.", Toast.LENGTH_SHORT).show();
			  db.close();
			  finish();
		  }
	  }

}
