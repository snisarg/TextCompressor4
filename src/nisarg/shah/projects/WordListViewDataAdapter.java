package nisarg.shah.projects;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;

public class WordListViewDataAdapter extends ArrayAdapter<WordDataClass>{

	Context context;
	ArrayList<WordDataClass> objects;
	DBHelper dBHelper;
	SQLiteDatabase db;
	ViewHolder holder;

	public WordListViewDataAdapter(Context context, int textViewResourceId,	ArrayList<WordDataClass> objects) {
		super(context, textViewResourceId, objects);
		this.context=context;
		this.objects=objects;
		dBHelper = new DBHelper(context, context.getResources().openRawResource(R.raw.wordlist));
		db = dBHelper.getWritableDatabase();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if(convertView==null) {
			convertView = inflater.inflate(R.layout.wordlist_row, parent, false);
			holder = new ViewHolder();
			holder.word1 = (TextView) convertView.findViewById(R.id.key);
			holder.word2 = (TextView) convertView.findViewById(R.id.value);
			holder.selection = (CheckBox) convertView.findViewById(R.id.checkBox1);
			holder.imgButton = (ImageButton) convertView.findViewById(R.id.deleteButton);
			convertView.setTag(holder);
		}
		else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		//final WordDataClass currentObj = objects[position];
		final WordDataClass currentObj = objects.get(position);
		//final String keyForCheck = objects[position].key;
		final String keyForCheck = objects.get(position).key;
		
		holder.selection.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				System.out.println("Boolean: "+isChecked);
				ContentValues args = new ContentValues();
				if(isChecked) 
					args.put(DBHelper.COL_touse, 1);
				else
					args.put(DBHelper.COL_touse, 0);
			    db.update(DBHelper.TABLE, args, "_id='"+keyForCheck+"'", null);
			}
		});
		holder.imgButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//v.getContext().startActivity(new Intent("nisarg.shah.projects.DeleteDialog").putExtra("keyForCheck", keyForCheck));
				//db.delete(DBHelper.TABLE, "_id=?", new String[]{keyForCheck});
				new AlertDialog.Builder(context)
			    .setTitle("Delete")
			    .setMessage("Are you sure you want to delete '"+keyForCheck+"'?")
			    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
				   		 DBHelper dBHelper = new DBHelper(context, context.getResources().openRawResource(R.raw.wordlist));
						 SQLiteDatabase db = dBHelper.getWritableDatabase();
						 db.delete(DBHelper.TABLE, "_id=?", new String[]{keyForCheck});
						 objects.remove(currentObj);
						 notifyDataSetChanged();
			        }
			     })
			    .setNegativeButton("No", new DialogInterface.OnClickListener() {
			        public void onClick(DialogInterface dialog, int which) { 
			            // do nothing
			        }
			     })
			     .show();
				 
			}
		});
		holder.word1.setText(currentObj.key);
		holder.word2.setText(currentObj.value);
		holder.selection.setChecked(currentObj.toUse);
		return convertView;
	}
	
	static class ViewHolder {
		TextView word1, word2;
		CheckBox selection;
		ImageButton imgButton;
	}
}
