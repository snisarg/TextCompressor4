package nisarg.shah.projects;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class TxtCmprsorActivity extends Activity {

	int oi = 0; // Used for conversions indexing to find last ' ' char
	EditText actualMessageET;
	TextView messageLengthTV;
	DBHelper dBHelper;
	SQLiteDatabase db;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        actualMessageET = (EditText)findViewById(R.id.editText1);
        messageLengthTV = (TextView)findViewById(R.id.messageLength);
        actualMessageET.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			public void afterTextChanged(Editable s) {
				changeWord();
			}
		});
		dBHelper = new DBHelper(TxtCmprsorActivity.this, getResources().openRawResource(R.raw.wordlist));
		db = dBHelper.getReadableDatabase();
    }
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuItem send = menu.add("Send");
    	send.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
    	send.setIcon(android.R.drawable.ic_menu_send);
    	send.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				//MyMsg.setText("button pressed for sure");
		    	Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);  
		    	shareIntent.setType("text/plain");   
		    	//shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Insert Subject Here");  
		    	//String shareMessage = "Insert message body here.";  
		    	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, actualMessageET.getText().toString());  
		    	startActivity(Intent.createChooser(shareIntent, "Share message via"));
				return false;
			}
		});
    	MenuItem miWordList = menu.add("See words");
    	miWordList.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	miWordList.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent("nisarg.shah.projects.WordList"));
				return false;
			}
		});
    	MenuItem miAbout = menu.add("About");
    	miAbout.setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
    	miAbout.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			public boolean onMenuItemClick(MenuItem item) {
				startActivity(new Intent("nisarg.shah.projects.About"));
				return false;
			}
		});
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// TODO Auto-generated method stub
    	return super.onOptionsItemSelected(item);
    }
    
    void changeWord(){
		int cursorPosition = actualMessageET.getSelectionStart();
		// Appostrophe filter part
		/*if(actualMessageET.toString().contains("'")) {		// necessary to prevent infinite textChanged looping
			int originalMessageLength = actualMessageET.length();
			String withoutApos = actualMessageET.toString().replace("'", "");
			actualMessageET.setText(withoutApos);
			cursorPosition = cursorPosition - originalMessageLength + withoutApos.length();
			actualMessageET.setSelection(cursorPosition);
		}*/
		// Conversion part
		CharSequence textBeforeCursor = actualMessageET.getText().subSequence(0, cursorPosition);
		try{
                //if (textBeforeCursor.charAt(textBeforeCursor.length() - 1) == ' ' || textBeforeCursor.charAt(textBeforeCursor.length() - 1) == '.' || textBeforeCursor.charAt(textBeforeCursor.length() - 1) == '?' || textBeforeCursor.charAt(textBeforeCursor.length() - 1) == '!' || textBeforeCursor.charAt(textBeforeCursor.length() - 1) == ',' || textBeforeCursor.charAt(textBeforeCursor.length() - 1) == '\r') // Decides when to invoke the function. In WP, only /r is used for NEWLINE
                if(limitingCharCheck(textBeforeCursor.charAt(textBeforeCursor.length() - 1)))
                {
                	CharSequence textAfterCursor;
                	try{
                		textAfterCursor= actualMessageET.getText().subSequence(cursorPosition,actualMessageET.length()-1);
                	} catch (Exception e) {
                		textAfterCursor = "";
                	}
                	char conditionalEndChar = textBeforeCursor.charAt(textBeforeCursor.length()-1);
                	for (oi = textBeforeCursor.length() - 2; oi >= 0 && !limitingCharCheck(textBeforeCursor.charAt(oi)); oi--) ; //start from end looking for second last ' ' char
                	oi++;
                    if (oi >= 0 && oi != (textBeforeCursor.length() - 2)){
                    	CharSequence lastWord = textBeforeCursor.subSequence(oi, textBeforeCursor.length()-1);
                    	textBeforeCursor = textBeforeCursor.subSequence(0, oi);
                    	System.out.println("Passing: "+lastWord.toString());
                    	if(true)		// Apostrophe filtering
                    		lastWord = lastWord.toString().replace("'", "");
                    	CharSequence newWord = dBHelper.wordConversion(lastWord.toString());
                    	System.out.println("Receiving: "+newWord.toString());
                    	if(newWord.length()!=0){	// There is a new word replacement for the current word in consideration.
	                    	actualMessageET.setText(textBeforeCursor.toString() + newWord.toString() + conditionalEndChar + textAfterCursor.toString());
	                    	actualMessageET.setSelection(textBeforeCursor.length() + newWord.length() + 1);
                    	}
                    	else{
                    		actualMessageET.setText(textBeforeCursor.toString() + lastWord.toString() + conditionalEndChar + textAfterCursor.toString());
	                    	actualMessageET.setSelection(textBeforeCursor.length() + lastWord.length() + 1);
                    	}
                    }
                }
		}catch(IndexOutOfBoundsException e){
			//messageLength.setText(e.getLocalizedMessage());
		};
		showMessageLength();
    }
    
    static final boolean limitingCharCheck(char c){
    	if(c == ' ' || c == '.' || c == '?' ||c == '!' || c == ',' || c == '\n' || c == '\r')
    		return true;
    	else
    		return false;
    }


	@Override
	protected void onDestroy() {
		if(db!=null)
			db.close();
		super.onDestroy();
	}
	
	public void showMessageLength(){
    	int length = actualMessageET.getText().length();
    	if(length<120)
    		messageLengthTV.setVisibility(View.GONE);
    	else if(length<141){
    		messageLengthTV.setVisibility(View.VISIBLE);
    		messageLengthTV.setText("Twitter limit, "+length+"/140");
    	}
    	else if(length<161){
    		messageLengthTV.setVisibility(View.VISIBLE);
    		messageLengthTV.setText("SMS, "+length+"/160");
    	}
    	else if(length<305){
    		messageLengthTV.setVisibility(View.VISIBLE);
    		messageLengthTV.setText("2 messages, "+length+"/304");
    	}
    	else if(length<457){
    		messageLengthTV.setVisibility(View.VISIBLE);
    		messageLengthTV.setText("3 messages, "+length+"/456");
    	}
    	else {
    		messageLengthTV.setVisibility(View.VISIBLE);
    		messageLengthTV.setText("Really long!, "+length);
    	}
    }
    
    /*
    public void copyToClipboard(View view) {
    	android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setText(actualMessageET.getText().toString());
    }
    
    public void intentLaunch(View view){
    	//MyMsg.setText("button pressed for sure");
    	Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);  
    	shareIntent.setType("text/plain");   
    	//shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Insert Subject Here");  
    	//String shareMessage = "Insert message body here.";  
    	shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, actualMessageET.getText().toString());  
    	startActivity(Intent.createChooser(shareIntent, "Share message via"));  
    }*/
}