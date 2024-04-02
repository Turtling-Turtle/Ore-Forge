package ore.forge;
import ore.forge.Enums.BooleanOperator;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.FunctionalInterfaces.BinomialFunction;
import ore.forge.Items.Item;
import ore.forge.Strategies.OreEffects.ObserverEffect;

import java.util.*;
import java.util.function.Function;

//@author Nathan Ulmen
public class TheShop {
    private final HashMap<String, Item> allItems;
    public static void main(String[] args) {
        //You have the origin of an obejct (which is the position)
        // You have the area of an object
        //When you move you go through all entities and compare origin and position to the area of all other entities.

        int [][] conveyorConfig = {
                {1, 1},
                {1, 1},
                {1, 1, 1, 1, 1, 1},
        };
        int zero = 0;
        int number2 = 2;
        int number3 = 3;
        number3 = number2;
        number2++;
        System.out.println(number2/zero);
        System.out.println(number2);


        enum OreField {
            ORE_VALUE,
            ORE_TEMP,
            UPGRADE_COUNT,
            ORE_MULTIORE,
            ORE_SPEED,
            etc
        }

        class TestOre {
            double oreValue;
            float temperature;
            int multiOre;
            ArrayList<ObserverEffect> listeners;
            Stack<ObserverEffect> expiredListeners;

            public double getOreValue() {
                return oreValue;
            }

            public float getTemperature() {
                return temperature;
            }

            public int getMultiOre() {
                return multiOre;
            }

            public void setOreValue(double newValue) {
                oreValue = newValue;
                notifyListeners(OreField.ORE_VALUE); //Enum
            }

            public void notifyListeners(Object mutatedField) {
                for(ObserverEffect trigger : listeners) {
                    if (trigger.getObservedField().equals(mutatedField)) {
//                        trigger.activate(deltaT, this);
                    }
                }
                clearOldListeners(); //remove listeners that have expired.
            }

            public void addListeners(ObserverEffect listener) {
                listeners.add(listener);
            }

            public void removeListeners(ObserverEffect target) {
                expiredListeners.add(target);
            }

            public void clearOldListeners() {
                while (!expiredListeners.isEmpty()) {
                    listeners.remove(expiredListeners.pop());
                }
            }

            //So if you wanted to see if an ore has been upgraded by a specific Upgrader via tag you would need:
            // UpgradeTag name,
            //
            //List of Conditions to evaluate.
            //Specify Behavior,
            //And Or operators need to be included.

            class Conditions {

                public enum BoolType {AND, OR}
                private Conditions simpleConditionOne;
                private Conditions simpleConditionTwo;
                private BoolType evalType;
                BooleanOperator baseEvalType;
                //A simple condition compares two values/things to one another, things like ore properties, player stats,
                //items on base.
                //A complex Condition can take two "simple" conditions. and evaluates them with a BoolType(AND/OR).
                //A complex Condition can take a "simple" condition (ValueToCompare, Boolean Operator, 2ndValueToCompare)
                //2ndValueToCompare can Either be a preselected threshold or another value/property.
                //When comparing two simple conditions together you must specify either && / ||.


                //Problems? - Cant have multiple && / || comparisons.
                //
                public BinomialFunction<Conditions, Conditions, Boolean> comparator;
                public Function<Ore, Boolean> comp;
                Enum<?> anEnum;

                public Conditions(BoolType type) {
                    comp = switch (evalType) {
                       case AND -> (Ore ore) -> simpleConditionOne.evaluate(ore) && simpleConditionTwo.evaluate(ore);
                       case OR -> (Ore ore) -> simpleConditionOne.evaluate(ore) || simpleConditionTwo.evaluate(ore);
                       default -> (Ore ore) -> simpleConditionOne.evaluate(ore) || simpleConditionTwo.evaluate(ore);
                   };
                }

                public boolean evaluate(Ore ore) {
                    return comp.apply(ore);
                }

            }

            public boolean doTheyMatch(boolean b1, boolean b2) {
                return b1 && b2;
            }

            public boolean or(boolean b1, boolean b2) {
                return b1 || b2;
            }



        }

//        Function<Double, Boolean> comparator;
//        String string = null;
//        comparator = switch (string) {
//            case "Greater Than" -> (x) -> x > threshold;
//            case "Less Than" -> (x) -> x < threshold;
//            case "equal" -> (x) -> x == threshold;
//            default -> throw new IllegalStateException("Unexpected value: " + string);
//        };
//        System.out.println(comparator.apply(value2));
        // Define the size of the list
    }

    public TheShop(ResourceManager resourceManager) {
        allItems = resourceManager.getAllItems();
    }




}
