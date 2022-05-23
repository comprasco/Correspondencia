package co.gov.supernotariado.bachue.correspondencia.ejb.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.apache.log4j.Logger;

import co.gov.supernotariado.bachue.correspondencia.ejb.to.AdjuntoTO;
import co.gov.supernotariado.bachue.correspondencia.ejb.to.MensajeCorreoTO;

/**
 * Usada para enviar correos electrónicos
 */
public class GestorCorreo {

	/**
	 * Usuario de autenticación al correo
	 */
	private String usuario = null;
	/**
	 * Clave de autenticación al correo
	 */
	private String clave = null;
	/**
	 * Indica si mostrar información de debug al enviar correo
	 */
	private boolean debug = true;
	/**
	 * Indica si requiere autenticación
	 */
	private boolean auth = true;

	/**
	 * Sesión para envío de correo
	 */
	private Session session;
	
	/** Logger de impresión de mensajes en los logs del servidor */
	private Logger logger = Logger.getLogger(GestorCorreo.class);

	/**
	 * Constructor
	 */
	public GestorCorreo() {

	}

	/**
	 * Constructor
	 * @param user Usuario de autenticación a servidor de correo
	 * @param password Clave de autenticación a servidor de correo
	 */
	public GestorCorreo(String user, String password) {
		this.usuario = user;
		this.clave = password;
	}

	/**
	 * Envía SMS
	 * @param mensaje Contenido del mensaje
	 * @return true si se envió correctamente
	 */
	public final boolean enviarSMS(MensajeCorreoTO mensaje) {
		List<String> errores = new ArrayList<>();
		boolean retorno = true;
		try {
			errores.add("No hay servicio configurado: "+mensaje.getAsunto());
			if(!errores.isEmpty()){
				retorno = false;
				for(String error:errores){
					logger.error(error);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			retorno = false;
		}
		return retorno;
	}

	
	/**
	 * Envía Correo
	 * @param mensaje Contenido del mensaje
	 * @return true si se envió correctamente
	 */
	public final boolean enviarCorreo(MensajeCorreoTO mensaje) {
		List<String> errores = new ArrayList<>();
		boolean retorno = true;
		try {
			String mail = "";
			if(mensaje==null){
				errores.add("MensajeCorreo viene nulo");
			} else{
				if (mensaje.getAsunto() == null || mensaje.getAsunto().isEmpty()){
					errores.add("MensajeCorreo asunto viene nulo");
				}
				if (mensaje.getRemitente() == null || mensaje.getRemitente().isEmpty()){
					errores.add("MensajeCorreo remitente viene nulo");
				}
				if (mensaje.getDestinatario() == null || mensaje.getDestinatario().isEmpty()){
					errores.add("MensajeCorreo destinatario viene nulo");
				}
				if (mensaje.getContenido() == null || mensaje.getContenido().isEmpty()){
					errores.add("MensajeCorreo contenido viene nulo");
				}
			}

			if(errores.isEmpty()){
				if (session == null) {
					establecerPropiedadesSesion();
				}
				if(session!=null && mensaje!=null){
					Message msg = new MimeMessage(session);
					
					msg.setSubject(mensaje.getAsunto());
					
					InternetAddress from = new InternetAddress(mensaje.getRemitente());
					msg.setFrom(from);

					InternetAddress[] mails = new InternetAddress[mensaje.getDestinatario().size()];
					int i = 0;
					for(String m:mensaje.getDestinatario()){
						mails[i++] = new InternetAddress(m);
					}
					msg.setRecipients(Message.RecipientType.TO, mails);

					if (mensaje.getCopia()!=null && !mensaje.getCopia().toString().isEmpty()) {
						mails = new InternetAddress[mensaje.getCopia().size()];
						i = 0;
						for(String m:mensaje.getCopia()){
							mails[i++] = new InternetAddress(m);
						}
						msg.setRecipients(Message.RecipientType.CC, mails);
					}
					if (mensaje.getCopiaOculta()!=null && !mensaje.getCopiaOculta().toString().isEmpty()) {
						mails = new InternetAddress[mensaje.getCopiaOculta().size()];
						i = 0;
						for(String m:mensaje.getCopiaOculta()){
							mails[i++] = new InternetAddress(m);
						}
						msg.setRecipients(Message.RecipientType.BCC, mails);
					}
					
					if(mensaje.getAdjuntos().isEmpty()){
						if (mensaje.isFormatoHTML()) {
							msg.setContent(mensaje.getContenido(), "text/html; charset=UTF-8");
						} else {
							msg.setText(mensaje.getContenido());
						}
					} else{
				         BodyPart mensajeBodyPart = new MimeBodyPart();
				         if (mensaje.isFormatoHTML()) {
				        	 mensajeBodyPart.setContent(mensaje.getContenido(), "text/html; charset=UTF-8");
				         } else {
				        	 mensajeBodyPart.setText(mensaje.getContenido());
				         }
				         Multipart multipart = new MimeMultipart();
				         multipart.addBodyPart(mensajeBodyPart);
				         for(AdjuntoTO attachment:mensaje.getAdjuntos()){
					         mensajeBodyPart = new MimeBodyPart();
					         DataSource source = new ByteArrayDataSource(attachment.getArchivo(), "application/octet-stream");
					         mensajeBodyPart.setDataHandler(new DataHandler(source));
					         mensajeBodyPart.setFileName(attachment.getNombre());
					         multipart.addBodyPart(mensajeBodyPart);
				         }
				         msg.setContent(multipart);
					}
					
					msg.setSentDate(new Date());
					logger.info("Enviando correo a "+mail + " asunto "+mensaje.getAsunto());
					Transport.send(msg);
				} else{
					errores.add("Correo Session viene nulo");
				}
			} else{
				retorno = false;
				for(String error:errores){
					logger.error(error);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			retorno = false;
		}
		return retorno;
	}
	
	
	/**
	 * 	Establece las propiedades de sesion del correo 
	 */
	private void establecerPropiedadesSesion() {
		try {
			Properties props = System.getProperties();
			props.put("mail.smtp.host", "smtp.gmail.com");
		    props.put("mail.smtp.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.starttls.enable", true);
			props.put("mail.smtp.user", this.usuario);
			props.put("mail.smtp.pass", this.clave);
			props.put("mail.smtp.ssl.checkserveridentity", true);
			SMTPAuthentication smptAuth = null;

			if(this.auth){
				props.put("mail.smtp.auth", true);
				smptAuth = new SMTPAuthentication(this.usuario, this.clave);
			}

			if (this.debug) {
				props.put("mail.debug", true);
			}
			session = Session.getInstance(props, smptAuth);
			session.setDebug(debug);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	/**
	 * Obtiene el valor del atributo usuario
	 * @return El valor del atributo usuario
	 */
	public String getUsuario() {
		return usuario;
	}

	/**
	 * Establece el valor del atributo usuario
	 * @param usuario con el valor del atributo usuario a establecer
	 */
	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	/**
	 * Obtiene el valor del atributo clave
	 * @return El valor del atributo clave
	 */
	public String getClave() {
		return clave;
	}

	/**
	 * Establece el valor del atributo clave
	 * @param clave con el valor del atributo clave a establecer
	 */
	public void setClave(String clave) {
		this.clave = clave;
	}

	/**
	 * Obtiene el valor del atributo debug
	 * @return El valor del atributo debug
	 */
	public boolean isDebug() {
		return debug;
	}

	/**
	 * Establece el valor del atributo debug
	 * @param debug con el valor del atributo debug a establecer
	 */
	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	/**
	 * Obtiene el valor del atributo auth
	 * @return El valor del atributo auth
	 */
	public boolean isAuth() {
		return auth;
	}

	/**
	 * Establece el valor del atributo auth
	 * @param auth con el valor del atributo auth a establecer
	 */
	public void setAuth(boolean auth) {
		this.auth = auth;
	}

	/**
	 * Obtiene el valor del atributo session
	 * @return El valor del atributo session
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Establece el valor del atributo session
	 * @param session con el valor del atributo session a establecer
	 */
	public void setSession(Session session) {
		this.session = session;
	}

	/**
	 * Obtiene el valor del atributo logger
	 * @return El valor del atributo logger
	 */
	public Logger getLogger() {
		return logger;
	}

	/**
	 * Establece el valor del atributo logger
	 * @param logger con el valor del atributo logger a establecer
	 */
	public void setLogger(Logger logger) {
		this.logger = logger;
	}


}
