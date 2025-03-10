package ore.forge.Screens.ItemCreator.VisualScripting.Forums;

import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Screens.ItemCreator.VisualScripting.ForumField;

public class TextInputForum extends TextField implements ForumField<String> {

    public TextInputForum() {
        super("Foo", new TextFieldStyle());

    }

    @Override
    public String value() {
        return this.getText();
    }

    @Override
    public String getError() {
        return "";
    }

    @Override
    public boolean isValid() {
        return false;
    }

    @Override
    public JsonValue.ValueType getType() {
        return JsonValue.ValueType.stringValue;
    }
}
