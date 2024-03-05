package ore.forge.game.Items.Strategies.UpgradeStrategies.game;//The OreRealm is a stack of max Length 500(Ore limit) and when its created it will be full of 500 basic ore objects.
//Droppers will "pop"/pull their ore from the OreRealm rather than making a new ore object each time they drop an ore.
//When a furnace sells an Ore it will reset it and return the ore to the OreRealm.
//This allows for ore objects to be recycled and limits the number of ore objects in that can be present in the tycoon.
//The oreRealm also keeps track of the ore that are active in the tycoon, and is used to set ore state to moveable at the end of each tick.

import java.util.ArrayList;
import java.util.Stack;

//@author Nathan Ulmen
public class OreRealm {
    private static OreRealm oreRealmInstance;
    public final Stack<Ore> stackOfOre, removalStack;
    public final ArrayList<Ore> activeOre;

    public OreRealm() {
        stackOfOre = new Stack<>();
        activeOre = new ArrayList<>(Constants.ORE_LIMIT);
        removalStack = new Stack<>();

        for (int i = 0; i < Constants.ORE_LIMIT; i++) {
            stackOfOre.push(new Ore());
        }
    }

    public static OreRealm getSingleton() {
        if (oreRealmInstance == null) {
            oreRealmInstance = new OreRealm();
        }
        return oreRealmInstance;
    }

    public Ore giveOre() {
        activeOre.add(stackOfOre.peek());
        return stackOfOre.pop();
    }

    public void takeOre(Ore element) {
       removalStack.add(element);
       stackOfOre.push(element);
    }

    public void updateActiveOre() {
        while (!removalStack.isEmpty()) {
            activeOre.remove(removalStack.pop());
        }
    }

    public String toString() {
        StringBuilder result = new StringBuilder();
        for (Ore o : stackOfOre) {
            result.append("\n[").append(o.toString()).append("]");
        }
        return result.toString();
    }

}


