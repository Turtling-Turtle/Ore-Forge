package ore.forge.Screens.ItemCreator.VisualScripting;

import com.badlogic.gdx.utils.JsonValue;

public interface ForumField<E> {
    E getValue();

    String getError();

    boolean isValid();

    JsonValue.ValueType getType();

}
