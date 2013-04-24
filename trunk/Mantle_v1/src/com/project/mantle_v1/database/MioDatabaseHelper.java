package com.project.mantle_v1.database;

import java.util.ArrayList;

import com.project.mantle_v1.MantleFile;
import com.project.mantle_v1.User;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MioDatabaseHelper extends SQLiteOpenHelper {

	private static final String DB_NAME = "Mantle";
	private static final int DB_VERSION = 11;
	final SQLiteDatabase db;

	// private String username;
	// private String email;
	// private String password;
	// private int idUser;

	// costruttore della classe
	public MioDatabaseHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
		db = this.getWritableDatabase();
	}

	// Creazione del database

	@Override
	public void onCreate(SQLiteDatabase db) {

		String sql = "";
		sql += "CREATE TABLE User (";
		sql += " idUser INTEGER PRIMARY KEY,";
		sql += " email TEXT,";
		sql += " username TEXT,";
		sql += " name TEXT,";
		sql += " surname TEXT,";
		sql += " key TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE Service (";
		sql += " service TEXT,";
		sql += " useracces TEXT,";
		sql += " passacces TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE Share (";
		sql += " idFile INTEGER,";
		sql += " idUser INTEGER";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE File (";
		sql += " idFile INTEGER PRIMARY KEY,";
		sql += " fileName TEXT,";
		sql += " linkFile TEXT,";
		sql += " linkComment TEXT,";
		sql += " fileKey TEXT";
		sql += ")";
		db.execSQL(sql);

		sql = "";
		sql += "CREATE TABLE History (";
		sql += " idFile INTEGER,";
		sql += " idUser INTEGER,";
		sql += " date INTEGER";
		sql += ")";
		db.execSQL(sql);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}

	// ============== METODI PER LA GESTIONE DEL DATABASE ===============

	public boolean isAlreadyFriend(String email) {

		String[] columns = { "Username" };
		String selection = "email = ?";
		String[] selectionArg = { email };
		Cursor cursor = db.query("User", columns, selection, selectionArg,
				null, null, null);
		Integer i = cursor.getCount();

		if (i < 1) {
			return false;
		} else {
			return true;
		}

	}
	

	// verifica che il servizio mantle esiste
		public boolean serviceMantle() {
			// boolean res = false;
			String[] columns = { "service" };
			String selection = "service = 'mantle'";
			Cursor cursor = db.query("Service", columns, selection, null, null,
					null, null);

			int i = cursor.getCount();

			if (i < 1) {
				return false;
			}

			else {
				return true;
			}

		}

	// verifica se l'utente è registrato e restituisce username e password
	public String[] login() {
		String selection = "service = 'mantle'";
		String[] columns = { "useracces", "passacces" };
		Cursor cursor = db.query("Service", columns, selection, null, null,
				null, null);

		Integer i = cursor.getCount();
		Log.d("MIODATABASEHELPER", "i = " + i.toString());

		// non c'è il servizio mantle l'utente deve registrarsi
		if (i < 1) {
			String[] res = new String[2];
			res[0] = " ";
			res[1] = " ";
			Log.d("MiodatabaseHelper",
					"L'utente non è registrato sto restituendo res[0]= "
							+ res[0] + " res[1] = " + res[1]);
			return res;

		}

		// ho trovato il servizio restituisco anche user e password per
		// effettuare il controllo
		else {
			String[] res = new String[i * 2];
			i = 0;

			// cursor.moveToNext();
			// Log.d("MioDatabaseHelper query result = ",cursor.getString(0)+cursor.getString(1)+cursor.getString(2));

			while (cursor.moveToNext()) {
				res[i] = cursor.getString(0);
				res[i + 1] = cursor.getString(1);
				Log.d("MIO_DATABASE_HELPER", res[i] + " " + res[i + 1]);
				i = i + 2;
			}
			Log.d("MIO_DATABASE_HELPER",
					"L'utente è registrato sto restituendo res[0]= " + res[0]
							+ " res[1] = " + res[1]);
			return res;
		}

	}

	////////////////////////////////////////////////////////////////////////////////
	
	public long insertUser(String email, String username, String name,
			String surname, String key) {
		ContentValues values = new ContentValues();
		values.put("email", email);
		values.put("username", username);
		values.put("name", name);
		values.put("surname", surname);
		values.put("key", key);
		long r = db.insert("User", null, values);
		return r;
	}

	public long insertService(String service, String useracces, String passacces) {
		ContentValues values = new ContentValues();
		values.put("service", service);
		values.put("useracces", useracces);
		values.put("passacces", passacces);
		long r = db.insert("Service", null, values);
		return r;
	}

	public long insertFile(String fileName, String linkFile,
			String linkComment, String fileKey) {
		ContentValues values = new ContentValues();
		values.put("fileName", fileName);
		values.put("linkFile", linkFile);
		values.put("linkComment", linkComment);
		values.put("fileKey", fileKey);
		long r = db.insert("File", null, values);
		return r;

	}

	public long insertShare(int idFile, int idUser) {
		ContentValues values = new ContentValues();
		values.put("idFile", idFile);
		values.put("idUser", idUser);
		long r = db.insert("Share", null, values);
		return r;
	}
	
	public void insertLinkComment(int idFile, String link) {

		ContentValues values = new ContentValues();
		values.put("linkComment", link);
		String whereClause = "idFile = ?";
		String[] whereArgs = { String.valueOf(idFile) };
		int r = db.update("File", values, whereClause, whereArgs);

	}
	
	////////////////////////////////////////////////////////////////////////////////
	
	public void deleteAll() {
		db.delete("User", null, null);
		db.delete("Service", null, null);
		db.delete("File", null, null);
		db.delete("Share", null, null);
		db.delete("History", null, null);

	}

	// elimina l'utente sia dalla tabella friend, sia da contact
	public void deleteFriend(String email) {

		String whereClause = "email = ?";
		String[] whereArgs = { email };

		db.delete("User", whereClause, whereArgs);
		Log.d("MIO_DATABASE_HELPER", "Ho elimnato l'utente richiesto : "
				+ email);

	}

	////////////////////////////////////////////////////////////////////////////////
	
	// Prelevo l'id di un determinato utente dal nome e dal cognome
	public int getId(String name, String surname) {

		// quale campo mi restituisce la query
		String[] columns = { "idUser" };
		// clausola where
		String selection = "name = ? AND surname = ?";
		// cosa devo sostituire al posto dei ?
		String[] selectionArgs = { name, surname };
		// esecuzione della query
		Cursor c = db.query("User", columns, selection, selectionArgs, null,
				null, null);

		c.moveToNext();

		return c.getInt(0);
	}

	public int getId(String email) {
		// quale campo mi restituisce la query
		String[] columns = { "idUser" };
		// clausola where
		String selection = "email = ?";
		// cosa devo sostituire al posto dei ?
		String[] selectionArgs = { email };
		// esecuzione della query
		Cursor c = db.query("User", columns, selection, selectionArgs, null,
				null, null);

		c.moveToNext();

		return c.getInt(0);
	}
	
	public String getEmailFromUrl(String url) {
		// Dalla tabella file ricavo l'id del file
		String[] columns = { "idFile" };
		String selection = "linkFile = ? OR linkComment= ? ";
		String[] selectionArgs = { url, url };
		Cursor c = db.query("File", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		String idFile = c.getString(0);

		Log.d("QUERY PER RICAVARE LA MAIL", "1/3) id del File = " + idFile);

		// Da share con l'idFile ricavo utente proprietario
		String[] columns2 = { "idUser" };
		selection = "idFile = ?";
		String[] selectionArgs2 = { idFile };
		c = db.query("Share", columns2, selection, selectionArgs2, null, null,
				null);
		c.moveToNext();
		String idUser = c.getString(0);

		Log.d("QUERY PER RICAVARE LA MAIL", "2/3) id dell'user = "+ idUser);

		// Dall'id dell'utente ricavo la mail
		String[] columns3 = { "email" };
		selection = "idUser = ?";
		String[] selectionArgs3 = { idFile };
		c = db.query("User", columns3, selection, selectionArgs3, null, null,
				null);
		c.moveToNext();
		String email = c.getString(0);

		Log.d("QUERY PER RICAVARE LA MAIL","3/3) la mail dell'utente = " + email);

		return email;
	}

	public String getLinkfromLinkComment(String linkComment) {
		String[] columns = { "linkFile" };
		String selection = "linkComment = ?";
		String[] selectionArgs = { linkComment };
		Cursor c = db.query("File", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		String linkFile = c.getString(0);
		return linkFile;
	}

	public String[] getFriends() {
		String[] columns = { "name", "surname", "email" };
		String selection = "idUser != 1";
		Cursor c = db.query("User", columns, selection, null, null, null, null);
		int i = c.getCount();
		String[] result = new String[i * 2];
		i = 0;

		while (c.moveToNext()) {
			result[i] = c.getString(0) + " " + c.getString(1);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			result[i + 1] = c.getString(2);
			i = i + 2;
		}

		return result;
	}

	public String getPassword(String email) {

		String[] columns = { "passacces" };
		String selection = "useracces=? AND service=?";
		String[] selectionArgs = { email, "Email" };
		Cursor c = db.query("Service", columns, selection, selectionArgs, null,
				null, null);
		c.moveToNext();
		return c.getString(0);
	}

	public String[] getUser() {
		String selection = "idUser=1";
		Cursor c = db.query("User", null, selection, null, null, null, null);
		String[] result = new String[6];
		c.moveToNext();
		for (int i = 0; i < 6; i++) {
			result[i] = c.getString(i);
		}
		return result;
	}

	public String[] getUser(Integer id) {
		String selection = "idUser=?";
		String[] selectionArgs = { id.toString() };
		Cursor c = db.query("User", null, selection, selectionArgs, null, null,
				null);
		String[] result = new String[6];
		c.moveToNext();
		for (int i = 0; i < 6; i++) {
			result[i] = c.getString(i);
		}
		return result;
	}

	public User getUser(String email) {
		String selection = "email = ?";
		String[] selectionArgs = { email };
		Cursor c = db.query("User", null, selection, selectionArgs, null, null,
				null);
		c.moveToNext();
		User user = new User(c.getString(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5));
		return user;
	}
	
	public int getIdFile(String linkFile){
		String[] columns = { "idFile" };
		String selection = "linkFile=?";
		String[] selectionArgs = { linkFile };
		Cursor c = db.query("File", columns, selection, selectionArgs, null, null,null);
		c.moveToNext();
		int idFile = c.getInt(0);
		return idFile;
	}
	
	
	public String[] getFile(String idFile) {
		String selection = "idFile=?";
		String[] selectionArgs = { idFile };
		Cursor c = db.query("File", null, selection, selectionArgs, null, null,
				null);
		String[] result = new String[5];
		c.moveToNext();
		for (int i = 0; i < 5; i++) {
			result[i] = c.getString(i);
		}
		return result;
	}
	
	public ArrayList <MantleFile> getAllFile(){
		Cursor c = db.query("File", null, null, null, null, null, null);
		ArrayList<MantleFile> arr = new ArrayList<MantleFile>();
		MantleFile mf = new MantleFile();
		
		while(c.moveToNext()){
			mf.setIdFile(c.getString(0));
			mf.setFileName(c.getString(1));
			mf.setLinkFile(c.getString(2));
			mf.setLinkComment(c.getString(3));
			mf.setFileKey(c.getString(4));
			
			arr.add(mf);
		}
		return arr;
	}
	
	// ============== METODI PER LA VISUALIZZAZIONE DEL DATABASE ===============

	public void showAll() {

		int i;

		Cursor cursor = db.query("User", null, null, null, null, null, null);
		i = cursor.getCount();
		String[] result = new String[i];
		i = 0;

		Log.d("MIO_DATABASE_HELPER", "------USER-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2) + " " + cursor.getString(3) + " "
					+ cursor.getString(4) + " " + cursor.getString(5);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("Service", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------SERVICE-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("File", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------FILE-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2) + " " + cursor.getString(3) + " "
					+ cursor.getString(4);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("Share", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------SHARE-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

		cursor = db.query("History", null, null, null, null, null, null);
		i = cursor.getCount();
		result = new String[i];
		i = 0;
		Log.d("MIO_DATABASE_HELPER", "------HISTORY-----");
		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}
	}

	public void showUser() {

		Cursor cursor = db.query("User", null, null, null, null, null, null);
		int i = cursor.getCount();
		String[] result = new String[i];
		i = 0;

		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}
	}

	public void showService() {

		Cursor cursor = db.query("Service", null, null, null, null, null, null);
		int i = cursor.getCount();
		String[] result = new String[i];
		i = 0;

		while (cursor.moveToNext()) {
			result[i] = cursor.getString(0) + " " + cursor.getString(1) + " "
					+ cursor.getString(2);
			Log.d("MIO_DATABASE_HELPER", result[i]);
			i++;
		}

	}


	
}
