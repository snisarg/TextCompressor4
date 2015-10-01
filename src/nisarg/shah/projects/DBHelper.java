package nisarg.shah.projects;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

	InputStream fileName;
	public static final String TAG = DBHelper.class.getSimpleName();
	public static final String DB_NAME = "wordlist.db";
	public static final int DB_VERSION = 1;
	public static final String TABLE = "Words";
	public static final String COL_id = "_id";
	public static final String COL_value = "value";
	public static final String COL_touse = "touse";
	
	public DBHelper(Context context, InputStream uri) {
		super(context, DB_NAME, null, DB_VERSION);
		fileName = uri;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		try{
			String sql = "create table Words(_id  TEXT primary key, value TEXT not null, touse INTEGER);";
			db.execSQL(sql);
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// Use the factory to create a builder
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fileName);
			// Get a list of all elements in the document
			NodeList list = doc.getElementsByTagName("key");
			NodeList list2 = doc.getElementsByTagName("value");
			System.out.println("XML Elements: ");
			for (int i=0; i<list.getLength(); i++) {
				// Get element
				Element elementKey = (Element)list.item(i);
				//System.out.print(element.getFirstChild().getNodeValue());
				Element elementValue = (Element)list2.item(i);
				System.out.println("insert into Words values("+elementKey.getFirstChild().getNodeValue()+", "+elementValue.getFirstChild().getNodeValue()+", 1);");
				/*db.execSQL("insert into Words values('"+elementKey.getFirstChild().getNodeValue()+"', '"+elementValue.getFirstChild().getNodeValue()+"', 1);");
				*/
				ContentValues cv = new ContentValues();
				cv.put(COL_id, elementKey.getFirstChild().getNodeValue());
				cv.put(COL_value, elementValue.getFirstChild().getNodeValue());
				cv.put(COL_touse, 1);
				try{
					db.insertOrThrow(TABLE, null, cv);
				}catch(SQLException e){
					System.out.println("Error at insertion");
				}
			}
		}
		//db.execSQL("insert into Words values("+word+", "+value+", 1);");
		catch (ParserConfigurationException e) {
			System.out.println("Exception raised "+e);
			e.printStackTrace();
			db.execSQL("DROP TABLE IF EXISTS "+DB_NAME+"."+TABLE);
		} catch (SAXException e) {
			System.out.println("Exception raised "+e);
			e.printStackTrace();
			db.execSQL("DROP TABLE IF EXISTS "+DB_NAME+"."+TABLE);
		} catch (IOException e) {
			System.out.println("Exception raised "+e);
			e.printStackTrace();
			db.execSQL("DROP TABLE IF EXISTS "+DB_NAME+"."+TABLE);
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		System.out.println("onUpgrade called: "+ oldVersion+ " to "+newVersion);
		//onCreate(db);
		// Repeating the above just to avoid the create table query
		try{
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			// Use the factory to create a builder
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fileName);
			// Get a list of all elements in the document
			NodeList list = doc.getElementsByTagName("key");
			NodeList list2 = doc.getElementsByTagName("value");
			System.out.println("XML Elements: ");
			for (int i=0; i<list.getLength(); i++) {
				// Get element
				Element elementKey = (Element)list.item(i);
				//System.out.print(element.getFirstChild().getNodeValue());
				Element elementValue = (Element)list2.item(i);
				System.out.println("insert into Words values("+elementKey.getFirstChild().getNodeValue()+", "+elementValue.getFirstChild().getNodeValue()+", 1);");
				/*db.execSQL("insert into Words values('"+elementKey.getFirstChild().getNodeValue()+"', '"+elementValue.getFirstChild().getNodeValue()+"', 1);");
				*/
				ContentValues cv = new ContentValues();
				cv.put(COL_id, elementKey.getFirstChild().getNodeValue());
				cv.put(COL_value, elementValue.getFirstChild().getNodeValue());
				cv.put(COL_touse, 1);
				try{
					db.insertOrThrow(TABLE, null, cv);
				}catch(SQLException e){
					System.out.println("Error at insertion");
				}
			}
		}
		//db.execSQL("insert into Words values("+word+", "+value+", 1);");
		catch (ParserConfigurationException e) {
			System.out.println("Exception raised "+e);
			e.printStackTrace();
			db.execSQL("DROP TABLE IF EXISTS "+DB_NAME+"."+TABLE);
		} catch (SAXException e) {
			System.out.println("Exception raised "+e);
			e.printStackTrace();
			db.execSQL("DROP TABLE IF EXISTS "+DB_NAME+"."+TABLE);
		} catch (IOException e) {
			System.out.println("Exception raised "+e);
			e.printStackTrace();
			db.execSQL("DROP TABLE IF EXISTS "+DB_NAME+"."+TABLE);
		}
	}
	
    public String wordConversion(String key) {
    	String returnResult;
    	key = key.replace("'", "");
    	Cursor result = getReadableDatabase().query(DBHelper.TABLE, new String[] {DBHelper.COL_id, DBHelper.COL_value}, DBHelper.COL_id+"='"+key+"' and "+DBHelper.COL_touse+"=1", null, null, null, null);
    	System.out.println("Cursor size : "+result.getCount());
		if(result.moveToFirst()) {
			System.out.println("inside for");
			System.out.println("word found : "+result.getString(result.getColumnIndex(DBHelper.COL_value)));
			returnResult=result.getString(result.getColumnIndex(DBHelper.COL_value));
		}
		else {
			System.out.println("Word not found");
			returnResult="";
		}
		result.close();
		return returnResult;
    }

}
