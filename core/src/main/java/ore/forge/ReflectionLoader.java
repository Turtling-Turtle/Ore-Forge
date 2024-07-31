package ore.forge;

import com.badlogic.gdx.utils.JsonValue;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ReflectionLoader {
    private final static HashMap<String, Constructor<?>> cachedResults = new HashMap<>();

    @SuppressWarnings("unchecked")
    public static <E> E createOrNull(JsonValue jsonValue, String valueToGet, String fieldName) {
        Class<?> aClass;
        try {
            try {
                aClass = Class.forName(jsonValue.get(valueToGet).getString(fieldName));
            } catch (NullPointerException e) {
                return null;
            }
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class); //Retrieve the constructor that takes a JsonValue.
            return (E) constructor.newInstance(jsonValue.get(valueToGet));
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException |
                 ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E> E createOrNull(JsonValue jsonValue, String fieldName) {
        try {
            Constructor<?> constructor = cachedResults.get(jsonValue.getString(fieldName));
            try {
                if (constructor != null) {
                    return (E) constructor.newInstance(jsonValue);
                }
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException ignored) {

            }
        } catch (NullPointerException | IllegalArgumentException e) {
            return null;
        }

        Class<?> aClass;
        try {
            try {
                aClass = Class.forName(jsonValue.getString(fieldName));
            } catch (NullPointerException | IllegalArgumentException e) {
                return null;
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
