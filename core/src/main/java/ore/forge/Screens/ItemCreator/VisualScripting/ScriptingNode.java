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
    private String name;
    private ScriptingNode<E> parent; //"input"
    private List<ScriptingNode<E>> leaves; //outputs
    private final ArrayList<Field> fields;

    public ScriptingNode(Field... fields) {
        this.leaves = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
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
        for (ScriptingNode<E> child : leaves) {
            jsonValue.addChild(child.create());
        }
        return jsonValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(Field field) {
        this.fields.add(field);
    }

    public void addChild(ScriptingNode<E> child) {
       this.leaves.add(child);
    }

    public void setChildName(int index, String name) {
        leaves.get(index).setName(name);
    }

    public void onLink(ScriptingNode<E> other) {

    }

    public void onUnlink(ScriptingNode<E> other) {

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
    }

    public static void main(String[] args) {
        System.out.println("Here!");

        ScriptingNode<UpgradeStrategy> condition = new ScriptingNode<>(
            new Field("condition", createForumField("ORE_VALUE > 20", JsonValue.ValueType.stringValue))
        );

        var trueBranch = new ScriptingNode<UpgradeStrategy>(
            new Field("upgradeName", createForumField("ore.forge.Strategies.UpgradeStrategies.BasicUpgrade", JsonValue.ValueType.stringValue)),
            new Field("modifier", createForumField((Double) 20.0, JsonValue.ValueType.doubleValue)),
            new Field("valueToModify", createForumField("ORE_VALUE", JsonValue.ValueType.stringValue)),
            new Field("operator", createForumField("MULTIPLY", JsonValue.ValueType.stringValue))
        );
        condition.addChild(trueBranch);
        condition.setChildName(0, "trueBranch");

        System.out.println(condition.create());
    }

    private static ForumField<?> createForumField(Object value, JsonValue.ValueType type) {
        return new ForumField<>() {
            @Override
            public Object getValue() {
                return value;
            }

            @Override
            public JsonValue.ValueType getType() {
                return type;
            }
        };
    }

}
