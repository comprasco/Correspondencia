package co.gov.supernotariado.bachue.correspondencia.ejb.util;

import org.apache.log4j.Logger;

import com.google.gson.Gson;

import co.gov.supernotariado.bachue.correspondencia.ejb.entity.Entrada;
import co.gov.supernotariado.bachue.correspondencia.ejb.json.JsonOpcion;

/**
 * Métodos de manejo de campos tipo JSON
 *
 */
public class JsonUtil {
	
	/**
	 * Constructor privado ya que los métodos son estáticos
	 */
	private JsonUtil() {
		
	}
	

	/**
	 * Permite agregar un nuevo campo de tipo opciones a la entrada
	 * @param entrada Entrada a afectar
	 * @param idPadre Identificador opción padre
	 */
	public static void agregarCampoOpciones(Entrada entrada, String idPadre){
    	// Encuentra el maximo id para asignar a la nueva opción
    	int maxid = 0;
    	for(JsonOpcion opcion:entrada.getOpcionesJson()) {
    		if(opcion.getId() > maxid) {
    			maxid = opcion.getId();
    		}
    	}
    		
    	JsonOpcion opcion = new JsonOpcion();
    	opcion.setId(maxid+1);
    	opcion.setActivo(true);
		opcion.setOpcionPadreId(idPadre);
		entrada.getOpcionesJson().add(opcion);
	}

	
	/**
	 * Transforma un json en un objeto pojo
	 * @param objectClass clase en la cual debe mapearse el json
	 * @param json Cadena con el contenido json
	 * @return Objeto pojo parseado
	 */
	public static <T> T parseFromJson(Class<T> objectClass, String json) {
		Logger logger = Logger.getLogger(JsonUtil.class);

		T jsonObject = null;
		try {
			if(json!=null && json.startsWith("{")){
				jsonObject = new Gson().fromJson(json, objectClass);
			} else {
				jsonObject = objectClass.newInstance();
			}
		} catch(Exception e) {
			logger.error(e);
		}
		return jsonObject;
	}
	
	
	/**
	 * Arma un json transformando un objeto pojo
	 * @param object Objeto json a transformar
	 * @return String con el json obtenido
	 */
	public static String transformarAJson(Object object) {
		Logger logger = Logger.getLogger(JsonUtil.class);
		String json = null;
		try {
			json = new Gson().toJson(object);
		} catch(Exception e) {
			logger.error(e);
		}
		return json;
	}
	

}
