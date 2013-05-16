package com.project.mantle_v1.notification_home;

import java.io.Serializable;

import android.util.Log;

import com.project.mantle_v1.database.User;
import com.project.mantle_v1.fileNavigator.MantleFile;
import com.project.mantle_v1.parser.MantleMessage;

public class Notifica implements Serializable {

	/**
	 * Rappresenta una notifica ottenuta dalle mail. Raccoglie i dati essenziali
	 * per andare a creare le notifiche all'interno della home
	 */

	private static final long serialVersionUID = 3324254621470042381L;

	private String data;
	private String NotificationType;
	private User user;
	private String title;
	private String body;
	private MantleFile mFile;
	private Note note;
	
	/**
	 * Costruttore da usare nel caso in cui la notifica sia relativa ad una
	 * richiesta d'amicizia o all'accettazione della stessa
	 * 
	 * @param data
	 *            del momento in cui la mail è arrivata
	 * @param who
	 *            Utente che desidera stringere l'amicizia
	 */

	public Notifica(String data, User user, String code) {
		super();
		this.data = data;
		this.user = user;
		this.NotificationType = code;
		this.title = user.getName() + " " + user.getSurname() + " ("
				+ user.getUsername() + ")" + ": richiesta d'amicizia";

		if (code.compareTo(MantleMessage.FRIENDSHIP_REQUEST) == 0)
			this.body = user.getName() + " " + user.getSurname() + " ("
					+ user.getUsername() + ")"
					+ " vuole stringere amicizia con te.";

		if (code.compareTo(MantleMessage.FRIENDSHIP_ACCEPTED) == 0)
			this.body = user.getName() + " " + user.getSurname() + " ("
					+ user.getUsername() + ")"
					+ " ha accettato la tua richiesta di amicizia.";

	}

	/**
	 * Costruttore da utilizzare in caso di notifiche di sistema
	 * 
	 * @param date
	 *            relativa alla creazione (invio) della mail
	 * @param body
	 *            corpo della notifica
	 */

	public Notifica(String date, String body, String code) {
		super();
		this.data = date;
		this.NotificationType = code;

		if (NotificationType.equals(MantleMessage.FRIENDSHIP_DENIED))
			this.title = "Richiesta di amicizia rifiutata";
		else
			this.title = "Nuovo commento";

		this.body = body;
	}

	/**
	 * Crea la notifica relativa alla condivisione di un file 
	 * 
	 * @param mFile 
	 * 					file condiviso
	 */

	public Notifica(MantleFile mFile) {
		super();
		this.mFile = mFile;
		this.data = mFile.getDate();
		this.NotificationType = MantleMessage.SHARING_FILE;
		
	}

	/**
	 * Creata a seguito di un aggiunta di un commento da
	 * parte di un utente ad un file
	 * 
	 * @param note Commento inserito
	 */
	
	public Notifica(Note note) {
		super();
		this.data = note.getDate();
		this.NotificationType = MantleMessage.NOTE;
		this.note = note;
		this.title = note.getUser() + " ha commentato una foto";
	}

	public String getData() {
		return data;
	}

	public String getNotificationType() {
		return NotificationType;
	}

	public String getWho() {
		if (user != null)
			return user.getName() + " " + user.getSurname() + " ("
					+ user.getUsername() + ")";
		else if (note != null)
			return note.getUser();
		else if (mFile != null)
			return mFile.getUsername();
		else
			return null;
	}

	public String getTitle() {
		return title;
	}

	public User getUser() {
		return user;
	}

	public String getNotificationBody() {
		return body;
	}

	public MantleFile getmFile() {
		return mFile;
	}

	public void setmFile(MantleFile mFile) {
		this.mFile = mFile;
	}

	public Note getNote() {
		return note;
	}

	public void setNote(Note note) {
		this.note = note;
	}

}