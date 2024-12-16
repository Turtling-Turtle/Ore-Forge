package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ReflectionLoader {
    private final static HashMap<String, Constructor<?>> cachedResults = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static <E> E load(JsonValue jsonValue, String fieldName) throws IllegalArgumentException {
        if (jsonValue == null) {
            throw new IllegalArgumentException("JsonValue with argument: " + fieldName + " is null.");
        }

        Constructor<?> constructor = cachedResults.get(jsonValue.getString(fieldName));
        try {
            if (constructor != null) {
                return (E) constructor.newInstance(jsonValue);
            }
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException e) {
            Gdx.app.log("ReflectionLoader", Color.highlightString("Error loading class: " + constructor.getClass().getSimpleName() + " from JsonValue:\n" + jsonValue , Color.RED));
            throw new RuntimeException(e);
        }

        Class<?> aClass;
        try {
            aClass = Class.forName(jsonValue.getString(fieldName));
            constructor = aClass.getConstructor(JsonValue.class);
            cachedResults.put(jsonValue.getString(fieldName), constructor);
            return (E) constructor.newInstance(jsonValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            Gdx.app.log("ReflectionLoader", Color.highlightString( "Error in:\n" + jsonValue + "\nWhen trying to retrieve value linked to key: " + fieldName, Color.RED));
            throw new RuntimeException(e);
        }
    }

}
