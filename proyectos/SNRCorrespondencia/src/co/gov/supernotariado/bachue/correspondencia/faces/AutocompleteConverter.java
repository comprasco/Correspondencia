package co.gov.supernotariado.bachue.correspondencia.faces;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.WeakHashMap;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

/**
 * Converter de faces que permite trabajar con componentes autocomplete
 */
@FacesConverter(value = "autocompleteConverter")
public class AutocompleteConverter implements Converter {

    /**
     * Entidades que se usan para guardar los componentes que se requieran de la página y su uuid
     */
    private static Map<Object, String> entities = new WeakHashMap<>();

    /**
     * Obtiene el valor plano del valor de un componente UI
     * @param context Contexto de Faces
     * @param component componente sobre el cual evaluar
     * @param entity Objeto al cual mapear
     */
    @Override
    public String getAsString(FacesContext context, UIComponent component, Object entity) {
        synchronized (entities) {
            if (!entities.containsKey(entity)) {
                String uuid = UUID.randomUUID().toString();
                entities.put(entity, uuid);
                return uuid;
            } else {
                return entities.get(entity);
            }
        }
    }

    /**
     * Obtiene el valor de objeto de un componente UI
     * @param context Contexto de Faces
     * @param component componente sobre el cual evaluar
     * @param uuid ID del componente que se quiere mapear a Objeto
     */
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String uuid) {
        for (Entry<Object, String> entry : entities.entrySet()) {
            if (entry.getValue().equals(uuid)) {
                return entry.getKey();
            }
        }
        return null;
    }

}