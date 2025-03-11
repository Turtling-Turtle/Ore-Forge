package ore.forge.Screens.ItemCreator.VisualScripting;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Expressions.Condition;
import ore.forge.Expressions.Function;
import ore.forge.Expressions.Operands.NumericOreProperties;
import ore.forge.Expressions.Operators.NumericOperator;
import ore.forge.Screens.ItemCreator.VisualScripting.Forums.ConstantHiddenForum;
import ore.forge.Strategies.UpgradeStrategies.BasicUpgrade;
import ore.forge.Strategies.UpgradeStrategies.BundledUpgrade;
import ore.forge.Strategies.UpgradeStrategies.ConditionalUpgrade;
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
 * Fields should be able to validate themselves.
 */
public class ScriptingNode<E> extends Actor {
    private String name, arrayName;
    private ScriptingNode<E> parent; //"input"
    private LinkBehavior<E> behavior; //Link behavior determines # of children, how they are added, and what should happen when added.
    private final List<ScriptingNode<E>> children; //output
    private final ArrayList<Field> fields;
    private final JsonValue.ValueType type;

    public ScriptingNode(Field... fields) {
        this.children = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
        this.type = null;
    }

    public ScriptingNode(List<Field> fields) {
        this(fields.toArray(new Field[0]));
    }

    public ScriptingNode(JsonValue.ValueType type, String arrayName, Field... fields) {
        this.arrayName = arrayName;
        this.children = new ArrayList<>();
        this.fields = new ArrayList<>();
        this.fields.addAll(Arrays.asList(fields));
        assert type == JsonValue.ValueType.array;
        this.type = type;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        for(Field f : fields) {
            if (f.field.isUiElement() && f.field instanceof Actor actor) {
                actor.draw(batch, parentAlpha);
            }
        }
        for (ScriptingNode<E> child : children) {
            child.draw(batch, parentAlpha);
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        for(Field f : fields) {
            if (f.field.isUiElement() && f.field instanceof Actor actor) {
                actor.act(delta);
            }
        }
        for (ScriptingNode<E> child : children) {
            child.act(delta);
        }
    }

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
        for (ScriptingNode<E> child : children) {
            childAdder.addChild(child.create());
        }
        return jsonValue;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addField(Field field) {
        fields.add(field);
    }

    public void addChild(ScriptingNode<E> child) {
        if (behavior != null) {
            this.behavior.onLink(this, child);
        } else {
            this.children.add(child);
        }
    }

    public void addChild(ScriptingNode<E> child, String childName) {
        child.setName(childName);
        this.addChild(child);

    }

    public void removeChild(ScriptingNode<E> child) {
        if(behavior != null) {
            behavior.onUnlink(this, child);
        } else {
            this.children.remove(child);
        }
    }

    public ValidationResult<E> validate() {
        return validate(new ValidationResult<>());
    }

    private ValidationResult<E> validate(ValidationResult<E> result) {
        result.visit(this);
        for (Field field : fields) {
            if (!field.isValid()) {
                result.addError(field.getError());
                result.recordErrorNode(this);
            }
        }
        for (ScriptingNode<E> child : children) {
            child.validate(result);
        }
        return result;
    }

    public static class ValidationResult<E> {
        private final List<String> errors;
        private final List<ScriptingNode<E>> visitedNodes;
        private final List<ScriptingNode<E>> errorNodes;

        public ValidationResult() {
            errors = new ArrayList<>();
            visitedNodes = new ArrayList<>();
            errorNodes = new ArrayList<>();
        }

        public void addError(String error) {
            errors.add(error);
        }

        public List<String> getErrors() {
            return errors;
        }

        public void recordErrorNode(ScriptingNode<E> errorNode) {
            if (!errorNodes.contains(errorNode)) {
                errorNodes.add(errorNode);
            }
        }

        public void visit(ScriptingNode<E> node) {
            visitedNodes.add(node);
        }

        public boolean isValid() {
            return errors.isEmpty();
        }

        public List<ScriptingNode<E>> getErrorNodes() {
            return errorNodes;
        }

        public List<ScriptingNode<E>> getVisitedNodes() {
            return visitedNodes;
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
            Object value = field.value();
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
        var bundled = createBundledNode();
        var condition = createConditionalNode("TEMPERATURE > 100");
        var trueBranch = createBasicUpgradeNode(10.0, NumericOreProperties.ORE_VALUE.name(), "MULTIPLY");

        condition.addChild(trueBranch, "trueBranch");
        condition.addChild(createBasicUpgradeNode(100.0, NumericOreProperties.TEMPERATURE.name(), "ASSIGNMENT"), "falseBranch");
        bundled.addChild(condition, "upgrades");
        bundled.addChild(trueBranch);
//        bundled.addChild(createBasicUpgradeNode(2.0, NumericOreProperties.ORE_VALUE.name(), "MULTIPLY"));


        bundled.setName("upgrade");
        var result = bundled.validate();
        if (result.isValid()) {
            System.out.println(bundled.create());
        } else {
            for (String error : result.getErrors()) {
                System.out.println(error);
            }
        }

        System.out.println("-------------");
        bundled.removeChild(trueBranch);
        result = bundled.validate();
        if (result.isValid()) {
            System.out.println(bundled.create());
        } else {
            for (String error : result.getErrors()) {
                System.out.println(error);
            }
        }


    }

    private static ForumField<?> createForumField(Object value, JsonValue.ValueType type) {
        return new ForumField<>() {
            @Override
            public Object value() {
                return value;
            }

            @Override
            public String getError() {
                return "";
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public JsonValue.ValueType getType() {
                return type;
            }
        };
    }

    private static ScriptingNode<UpgradeStrategy> createBundledNode() {
        return new ScriptingNode<>(JsonValue.ValueType.array, "upgrades", new Field("upgradeName", new ConstantHiddenForum(BundledUpgrade.class.getName())));
    }

    private static ScriptingNode<UpgradeStrategy> createConditionalNode(String condition) {
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("upgradeName", new ConstantHiddenForum(ConditionalUpgrade.class.getName())));
        fields.add(new Field("condition", new ForumField<String>() {
            private String conditionString = condition;

            @Override
            public String value() {
                return condition;
            }

            @Override
            public String getError() {
                return "Failed to compile condition:" + conditionString;
            }

            @Override
            public boolean isValid() {
                try {
                    Condition.compile(conditionString);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            public JsonValue.ValueType getType() {
                return JsonValue.ValueType.stringValue;
            }
        }));

        return new ScriptingNode<>(fields);
    }

    private static ScriptingNode<UpgradeStrategy> createBasicUpgradeNode(Double modifier, String vtm, String operator) {
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("upgradeName", new ConstantHiddenForum(BasicUpgrade.class.getName())));
        fields.add(new Field("modifier", new ForumField<Double>() {
            @Override
            public Double value() {
                return modifier;
            }

            @Override
            public String getError() {
                return "Invalid modifier";
            }

            @Override
            public boolean isValid() {
                return true;
            }

            @Override
            public JsonValue.ValueType getType() {
                return JsonValue.ValueType.doubleValue;
            }
        }));
        fields.add(new Field("valueToModify", new ForumField<String>() {

            @Override
            public String value() {
                return vtm;
            }

            @Override
            public String getError() {
                return "Invalid vtm";
            }

            @Override
            public boolean isValid() {
                return NumericOreProperties.isProperty(vtm);
            }

            @Override
            public JsonValue.ValueType getType() {
                return JsonValue.ValueType.stringValue;
            }
        }));
        fields.add(new Field("operator", new ForumField<String>() {

            @Override
            public String value() {
                return operator;
            }

            @Override
            public String getError() {
                return "Invalid operator " + operator;
            }

            @Override
            public boolean isValid() {
                if (NumericOperator.isOperator(operator)) {
                    return true;
                }
                try {
                    NumericOperator.valueOf(operator);
                    return true;
                } catch (IllegalArgumentException e) {
                    return false;
                }
            }

            @Override
            public JsonValue.ValueType getType() {
                return JsonValue.ValueType.stringValue;
            }
        }));

        return new ScriptingNode<>(fields);
    }


}
