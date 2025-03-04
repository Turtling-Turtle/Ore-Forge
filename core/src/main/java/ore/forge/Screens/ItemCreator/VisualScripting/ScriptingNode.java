package ore.forge.Screens.ItemCreator.VisualScripting;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Expressions.Condition;
import ore.forge.Expressions.Function;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Nathan Ulmen
 * A scripting node is a tree like structure.
 * A scripting node will be able to create what it models.
 * So for example if you have a tree of ScriptingNodes of type UpgradeStrategy it will return an
 * UpgradeStrategy by calling create() recursively on itself
 * <p>
 * ScriptingNodes should be able to add fields
 * Field's are key value pairs that correspond to a property of the Object the ScriptingNode is modeling.
 * Fields can take the form of TextInputFields, Drop DownMenus, toggles/checkboxes Or they can accept/require
 * other ScriptingNodes
 * Thus Fields will have a visual aspect to them.
 * <p>
 * Fields should be easily extendable to enable features for things like {@link Condition} and
 * {@link Function}
 */
public class ScriptingNode<E> {
    private String name, arrayName;
    private ScriptingNode<E> parent; //"input"
    private List<ScriptingNode<E>> leaves; //outputs
    private final ArrayList<Field> fields;
    private final JsonValue.ValueType type;

    public ScriptingNode(Field... fields) {
        this.leaves = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
        this.type = null;
    }

    public ScriptingNode(JsonValue.ValueType type, String arrayName, Field... fields) {
        this.arrayName = arrayName;
        this.leaves = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
        this.type = type;
    }

//    @Override
//    public void draw(Batch batch, float parentAlpha) {
//        super.draw(batch, parentAlpha);
//        for (ScriptingNode<E> child : leaves) {
//            //TODO: draw line to each child
//        }
//    }

    public JsonValue create() {
        JsonValue jsonValue = new JsonValue(JsonValue.ValueType.object);
        jsonValue.name = this.name;
        for (Field field : fields) {
            jsonValue.addChild(field.getValue());
        }
        JsonValue childAdder;
        if (type == JsonValue.ValueType.array) {
            childAdder = new JsonValue(JsonValue.ValueType.array);
            assert arrayName != null && !arrayName.isEmpty();
            childAdder.name = arrayName;
            jsonValue.addChild(childAdder);
        } else {
            childAdder = jsonValue;
        }
        for (ScriptingNode<E> child : leaves) {
            childAdder.addChild(child.create());
        }
        return jsonValue;
    }

    public ValidationResult validate(ValidationResult result) {
        for (Field field: fields) {
            if (!field.isValid()) {
                result.addError(field.getError());
            }
        }
        for (ScriptingNode<E> child : leaves) {
            child.validate(result);
        }
        return result;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public void addChild(ScriptingNode<E> child) {
        this.leaves.add(child);
        this.onLink(child);
    }

    public void removeChild(ScriptingNode<E> child) {
        this.leaves.remove(child);
        this.onUnlink(child);
    }

    public void setChildName(int index, String name) {
        leaves.get(index).setName(name);
    }

    public void onLink(ScriptingNode<E> other) {

    }

    public void onUnlink(ScriptingNode<E> other) {

    }

    public static class ValidationResult {
        private boolean valid;
        private final List<String> errors;

        public ValidationResult() {
            valid = true;
            errors = new ArrayList<>();
        }

        public void addError(String error) {
            valid = false;
            errors.add(error);
        }

        public List<String> getErrors() {
            return errors;
        }

        public boolean isValid() {
            return valid;
        }

    }

    public static class Field {
        private final String key;
        private ForumField<?> field; //InputField for the ValueType, actual UI element

        public Field(String key, ForumField<?> field) {
            this.field = field;
            this.key = key;
        }

        public JsonValue getValue() {
            JsonValue jsonValue = new JsonValue(field.getType());
            jsonValue.name = key;
            Object value = field.getValue();
            switch (value) {
                case String s -> jsonValue.set(s);
                case Double d -> jsonValue.set(d, String.valueOf(d));
                case Long l -> jsonValue.set(l, String.valueOf(l));
                case Boolean b -> jsonValue.set(b);
                default -> throw new IllegalStateException("Unexpected value: " + value);
            }
            return jsonValue;
        }

        public boolean isValid() {
            return field.isValid();
        }

        public String getError() {
            return field.getError();
        }
    }

    public static void main(String[] args) {

        ScriptingNode<UpgradeStrategy> condition = new ScriptingNode<>(
            new Field("condition", createForumField("ORE_VALUE > 20", JsonValue.ValueType.stringValue))
        );

        var trueBranch = new ScriptingNode<UpgradeStrategy>(
            new Field("upgradeName", createForumField("ore.forge.Strategies.UpgradeStrategies.BasicUpgrade", JsonValue.ValueType.stringValue)),
            new Field("modifier", createForumField((Double) 20.0, JsonValue.ValueType.doubleValue)),
            new Field("valueToModify", createForumField("ORE_VALUE", JsonValue.ValueType.stringValue)),
            new Field("operator", createForumField("MULTIPLY", JsonValue.ValueType.stringValue))
        );
        condition.setName("upgradeName");
        condition.addChild(trueBranch);
        condition.setChildName(0, "trueBranch");


        ScriptingNode<UpgradeStrategy> bundledUpgrade = new ScriptingNode<>(JsonValue.ValueType.array, "upgrades",
            new Field("upgradeName", createForumField("ore.forge.Strategies.UpgradeStrategies.BundledUpgrade", JsonValue.ValueType.stringValue))
        );
        bundledUpgrade.addChild(condition);
        bundledUpgrade.addChild(trueBranch);
        bundledUpgrade.setName("upgrade");
        System.out.println(bundledUpgrade.create());

    }

    private static ForumField<?> createForumField(Object value, JsonValue.ValueType type) {
        return new ForumField<>() {
            @Override
            public Object getValue() {
                return value;
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
                return type;
            }
        };
    }

}
