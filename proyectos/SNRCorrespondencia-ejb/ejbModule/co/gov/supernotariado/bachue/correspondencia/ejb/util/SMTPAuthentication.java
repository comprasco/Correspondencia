package co.gov.supernotariado.bachue.correspondencia.ejb.util;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

/**
 * Usada para la autenticación de una cuenta al enviar un correo electrónico
 */
public class SMTPAuthentication extends Authenticator {
	/**
	 * Nombre de usuario
	 */
	private String userName = "";
	/**
	 * Clave del usuario
	 */
	private String pass = "";

	/**
	 * Constructor 
	 * @param u nombre de usuario
	 * @param p clave de usuario
	 */
	public SMTPAuthentication(String u, String p) {
		this.userName = u;
		this.pass = p;
	}

	@Override
	public PasswordAuthentication getPasswordAuthentication() {
		return new PasswordAuthentication(userName, pass);
	}
}