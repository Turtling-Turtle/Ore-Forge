package ore.forge.Strategies.UpgradeStrategies;


import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Ore;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

//@author Nathan Ulmen
//TODO: Add support so that you can evaluate whether or not ore is under the influence of specific effects.
public class ConditionalUPG implements UpgradeStrategy {
    public enum Condition {VALUE, UPGRADE_COUNT, TEMPERATURE, MULTIORE} //Condition to be evaluated
    public enum Comparison {GREATER_THAN, LESS_THAN, EQUAL_TO} //Type of comparison
    private final Condition condition;
    private final Comparison comparison;
    private final UpgradeStrategy ifModifier;
    private final UpgradeStrategy elseModifier;
    private final double threshold;

    public ConditionalUPG(UpgradeStrategy ifMod, UpgradeStrategy elseMod, Condition condition, double threshold, Comparison comparison) {
        ifModifier = ifMod;
        elseModifier = elseMod;
        this.threshold = threshold;
        this.condition = condition;
        this.comparison = comparison;
    }

    public ConditionalUPG(JsonValue jsonValue) {
        try {
            Class<?> aClass= Class.forName(jsonValue.get("ifModifier").getString("type"));
            Constructor<?> constructor = aClass.getConstructor(JsonValue.class);
            ifModifier = (UpgradeStrategy) constructor.newInstance(jsonValue.get("ifModifier"));
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        System.out.println(jsonValue.get("elseModifier").getString("type"));
        try {
            elseModifier = (UpgradeStrategy) Class.forName(jsonValue.get("elseModifier").getString("type")).getConstructor(JsonValue.class).newInstance(jsonValue.get("elseModifier"));
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        this.threshold = jsonValue.getDouble("threshold");
        this.condition = Condition.valueOf(jsonValue.getString("condition"));
        this.comparison = Comparison.valueOf(jsonValue.getString("comparison"));
    }


    @Override
    public void applyTo(Ore ore) {
        switch (condition) {
            case VALUE:
                valueComp(ore);
                break;
            case TEMPERATURE:
                tempComp(ore);
                break;
            case MULTIORE:
                multiOreComp(ore);
                break;
            case UPGRADE_COUNT:
                upgradeCountComp(ore);
                break;
        }
    }

    private void valueComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getOreValue() >=threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
                break;
            case LESS_THAN:
                if (ore.getOreValue()<= threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
                break;
            case EQUAL_TO:
                if (ore.getOreValue()== threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
                break;
        }
    }

    private void tempComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getOreTemp() >=threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
            case LESS_THAN:
                if (ore.getOreTemp()<= threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
            case EQUAL_TO:
                if (ore.getOreTemp()== threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
        }
    }

    private void multiOreComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getMultiOre() >=threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
            case LESS_THAN:
                if (ore.getMultiOre()<= threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
            case EQUAL_TO:
                if (ore.getMultiOre()== threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
        }
    }

    private void upgradeCountComp(Ore ore) {
        switch (comparison) {
            case GREATER_THAN:
                if(ore.getUpgradeCount()>=threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
            case LESS_THAN:
                if (ore.getUpgradeCount()<= threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
            case EQUAL_TO:
                if (ore.getUpgradeCount()== threshold) {
                    ifModifierApply(ore);
                } else {
                    elseModifierApply(ore);
                }
            break;
        }
    }

    public String toString() {
        return getClass().getSimpleName() + "\tCondition: " + condition + "\tComparison: " + comparison +
            "\tThreshold: " + threshold +
            "\n\nifModifier: " + ifModifier.toString() + "\n\nelseModifier: " + elseModifier.toString();
    }

    private boolean isNull(UpgradeStrategy strat) {
        return strat == null;
    }

    private void ifModifierApply(Ore ore) {
        if (!isNull(ifModifier)) {
            ifModifier.applyTo(ore);
        }
    }

    private void elseModifierApply(Ore ore) {
        if (!isNull(elseModifier)) {
            elseModifier.applyTo(ore);
        }
    }

}
