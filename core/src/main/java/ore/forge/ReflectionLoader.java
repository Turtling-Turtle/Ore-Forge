package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ReflectionLoader {
    private final static HashMap<String, Constructor<?>> cachedResults = new HashMap<>();


    @SuppressWarnings("unchecked")
    public static <E> E create(JsonValue jsonValue, String fieldName) {

        try {
            Constructor<?> constructor = cachedResults.get(jsonValue.getString(fieldName));
            if (constructor != null) {
                return (E) constructor.newInstance(jsonValue);
            }
        } catch (IllegalArgumentException | InvocationTargetException | InstantiationException |
                 IllegalAccessException e) {
            Gdx.app.log("Reflection Loader", Color.highlightString(fieldName + " not found in: " + jsonValue, Color.YELLOW));
        }

        Class<?> aClass;
        try {
            try {
                aClass = Class.forName(jsonValue.getString(fieldName));
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e + "\nJson value:" + jsonValue.toString() + "\t" + fieldName);
            }
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            cachedResults.put(jsonValue.getString(fieldName), constructor);
            return (E) constructor.newInstance(jsonValue);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e + "\nJson value:" + jsonValue.toString() + "\t" + fieldName);
        }
    }

}
