package ore.forge.Strategies;

import com.badlogic.gdx.utils.JsonValue;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public interface StrategyInitializer<E> {

    @SuppressWarnings("unchecked")
    default E createOrNull(JsonValue jsonValue, String valueToGet, String fieldName) {
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

}
