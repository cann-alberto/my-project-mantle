package com.project.mantle_v1.notification_home;

import java.util.List;

import com.project.mantle_v1.User;

public class Notifica {

	/**  Rappresenta una notifica ottenuta dalle mail. Raccoglie i dati 
	 *     essenziali per andare a creare le notifiche all'interno della 
	 *		home	
	 */
	
	// NOTIFICATION TYPE ID

	public static int NOTE_ID = 1;
	public static int FRIENDSHIP_ID = 2;
	public static int NEW_SHARED_PHOTO_ID = 3;
	
	
	private String data;
	private int NotificationType;
	private User user;
	private List<Note> notes;
	private String title;
	private String username;
	/**
	 * Costruttore da usare nel caso in cui la notifica sia relativa ad 
	 * una richiesta d'amicizia 
	 * @param data: del momento in cui la mail è arrivata
	 * @param who: Utente che desidera stringere l'amicizia
	 */
	
	public Notifica(String data, User user) {
		super();
		this.data = data;
		this.user = user;
		this.NotificationType = FRIENDSHIP_ID;
		this.title = user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")" + ": richiesta d'amicizia";
	}

	/**
	 *  Costruttore per le notifiche di condivisione di immagini o di nuovi
	 *  commenti alle foto
	 *  
	 * @param data: del momento in cui la mail è arrivata
	 * @param notificationType: per indicare con esattezza di che tipo di notifica si tratta.
	 * NEW_SHARED_PHOTO_ID per le nuove condivisioni. NOTE_ID per nuovo commento  
	 * @param who: nome di chi vuole condividere la foto o di chi ha commentato
	 * @param notes: lista dei commenti alla foto
	 */
	
	public Notifica(String data, int notificationType, String who,
			List<Note> notes) {
		super();
		this.data = data;
		NotificationType = notificationType;
		this.username = who;
		this.notes = notes;
		if(notificationType == NOTE_ID) {
			this.title = who + " ha commentato una tua foto";
		}
		else
			this.title = who + " ha condiviso una foto";
	}
	
	public String getData() {
		return data;
	}
	
	public int getNotificationType() {
		return NotificationType;
	}
	
	public String getWho() {
		if(user != null)
			return user.getName() + " " + user.getSurname() + " (" + user.getUsername() + ")";
		else
			return username;
	}
	
	public List<Note> getNotes() {
		return notes;
	}

	public String getTitle() {
		return title;
	}
	
	public User getUser() {
		return user;
	}
}
