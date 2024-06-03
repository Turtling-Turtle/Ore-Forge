package ore.forge;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ore.forge.Expressions.ValueOfInfluence;
import ore.forge.Items.Blocks.Worker;
import ore.forge.Strategies.OreEffects.BundledOreEffect;
import ore.forge.Strategies.OreEffects.OreEffect;
import ore.forge.Strategies.OreEffects.ObserverOreEffect;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Stack;

/**@author Nathan Ulmen
 * */
public class Ore {
    //Ore can be classified by name, id, type. Ores can have multiple types.
    protected static ItemMap itemMap = ItemMap.getSingleton();
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    private final BitSet history;
    private final HashMap<String, UpgradeTag> tagMap;
    private final Vector2 position, destination;
    private Texture texture;
    private final ArrayList<OreEffect> effects;
    private final Stack<OreEffect> removalStack;
    private final ArrayList<ObserverOreEffect> observerEffects;
    private String oreName, id;
    private double oreValue;
    private int upgradeCount, multiOre, oreHistory;
    private float oreTemperature;
    private float moveSpeed, speedScalar;
    private Direction direction;
    private boolean isDoomed;
    private final float updateInterval; //Interval for how often effects are updated.
    private float current;
    private float deltaTime;

    public Ore() {
        this.oreValue = 0;
        this.oreTemperature = 0;
        this.oreName = "";
        this.id = "";
        this.upgradeCount = 0;
        this.multiOre = 1;
        this.speedScalar = 1;
        this.isDoomed = false;
        tagMap = new HashMap<>();
        position = new Vector2();
        destination = new Vector2();
//        texture = new Texture(Gdx.files.internal("Ruby2.png"));
        direction = Direction.NORTH;
        effects = new ArrayList<>();
        removalStack = new Stack<>();
        history = new BitSet();
        observerEffects = new ArrayList<>();
        updateInterval = 0.01f;//effects are updated 100 times every second.
    }

    public void act(float deltaTime) {
        this.deltaTime = deltaTime;
        current += deltaTime;
        if (current >= updateInterval) {
            updateEffects(deltaTime);
        }
        if (position.x != destination.x || position.y != destination.y) {
            move(deltaTime);
        } else {
            activateBlock();
        }
        //End Step effects like invincibility;
        if (current >= updateInterval) {
            updateEndStepEffects(deltaTime);
            current = 0f;
            if (this.isDoomed()) {
                oreRealm.takeOre(this);
            }
        }
    }

    private void updateEffects(float deltaTime) {
        for (OreEffect effect : effects) {
            if (!effect.isEndStepEffect()) {
                effect.activate(deltaTime, this);
            }
        }
        removeOldEffects();
    }

    private void updateEndStepEffects(float deltaTime) {
        for (OreEffect effect : effects) {
            if (effect.isEndStepEffect()) {
                effect.activate(deltaTime, this);
            }
        }
    }

    private void removeOldEffects() {
        while (!removalStack.empty()) {
            effects.remove(removalStack.pop());
        }
    }

    private void move(float deltaTime) {
        switch (direction) {
            case NORTH:
                if (position.y < destination.y) {
                    position.y += moveSpeed * deltaTime;
                }
                if (position.y >= destination.y) {
                    position.y = destination.y;
                    activateBlock();
                }
                break;
            case SOUTH:
                if (position.y > destination.y) {
                    position.y -= moveSpeed * deltaTime;
                }
                if (position.y <= destination.y) {
                    position.y = destination.y;
                    activateBlock();
                }
                break;
            case EAST:
                if (position.x < destination.x) {
                    position.x += moveSpeed * deltaTime;
                }
                if (position.x >= destination.x) {
                    position.x = destination.x;
                    activateBlock();
                }
                break;
            case WEST:
                if (position.x > destination.x) {
                    position.x -= moveSpeed * deltaTime;
                }
                if (position.x <= destination.x) {
                    position.x = destination.x;
                    activateBlock();
                }
                break;
        }
    }

    public void activateBlock() {
//        Gdx.app.log("Ore" , this.toString());
        if ((itemMap.getBlock((int) position.x, (int) position.y) instanceof Worker)) {
            ((Worker) itemMap.getBlock(position)).handle(this);
        } else {
            setIsDoomed(true);
        }
    }

    public void applyEffect(OreEffect strategy) {
        if (strategy == null) {
            return;
        } //Base case
        if (strategy instanceof BundledOreEffect) {//Base case
            for (OreEffect effect : ((BundledOreEffect) strategy).getStrategies()) {
                applyEffect(effect);
            }
        } else if (strategy instanceof ObserverOreEffect) {
            observerEffects.add((ObserverOreEffect) strategy.clone());
        } else {
            effects.add(strategy.clone());
        }
    }

    public void removeEffect(OreEffect effectToRemove) {
        assert effects.contains(effectToRemove);
        removalStack.add(effectToRemove);
    }

    private void notifyObserverEffects(ValueOfInfluence mutatedField) {
        for (ObserverOreEffect observer : observerEffects) {
            if (observer.getObservedField() == mutatedField) {
                observer.activate(deltaTime, this);
            }
        }
        removeOldEffects();
    }

    public Ore applyBaseStats(double oreValue, int oreTemp, int multiOre, String oreName, String id, OreEffect strategy) {
        this.oreValue = oreValue;
        this.id = id;
        this.oreTemperature = oreTemp;
        this.multiOre = multiOre;
        this.oreName = oreName;
        applyEffect(strategy);
        return this;
    }

    public Ore setDestination(Vector2 target, float speed, Direction direction) {
        this.destination.set(target);
        this.direction = direction;
        setMoveSpeed(speed);
        return this;
    }

    public void setDestination(float x, float y, float speed, Direction direction) {
        this.destination.set(x, y);
        this.direction = direction;
        setMoveSpeed(speed);
    }

    public void reset() {
//        if (map.getBlock(position)!= null) {
//            map.getBlock(position).setFull(false);
//        }
        this.oreValue = 0;
        this.oreTemperature = 0;
        this.oreName = "";
        this.upgradeCount = 0;
        this.multiOre = 1;
        this.speedScalar = 1;
        effects.clear();
        removalStack.clear();
        isDoomed = false;
        current = 0f;
        resetAllTags();
    }

    public void resetNonResetterTags() {
        for (UpgradeTag tag : tagMap.values()) {
            if (!tag.isResetter()) {
                tag.reset();
            }
        }
    }

    public void incrementTag(UpgradeTag tag) {
        tagMap.get(tag.getID()).incrementCurrentUpgrades();
        upgradeCount++;
    }

    public UpgradeTag getUpgradeTag(UpgradeTag tag) {
        String tagName = tag.getID();
        if (tagMap.containsKey(tagName)) {
            return tagMap.get(tagName);
        } else {
            UpgradeTag newTag = new UpgradeTag(tag);
            tagMap.put(tagName, newTag);
            return newTag;
        }
    }

    public void resetAllTags() {
        for (UpgradeTag tag : tagMap.values()) {
            tag.reset();
        }
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float newSpeed) {
        moveSpeed = speedScalar * newSpeed;
    }

    public Vector2 getVector() {
        return position;
    }

    public Ore setVector(Vector2 vector) {
        this.position.set(vector);
        return this;
    }

    public Texture getTexture() {
        return texture;
    }

    public boolean isDoomed() {
        return isDoomed;
    }

    public void setIsDoomed(boolean state) {
        isDoomed = state;
//        Gdx.app.log("State changed to: ", String.valueOf(isDoomed));
    }

    public void setSpeedScalar(float newScalar) {
        speedScalar = newScalar;
    }

    public float getSpeedScalar() {
        return speedScalar;
    }

    public void purgeEffects() {
        effects.clear();
    }

    public double getOreValue() {
        return oreValue;
    }

    public void setOreValue(double newValue) {
        oreValue = newValue;
    }

    public float getOreTemp() {
        return oreTemperature;
    }

    public void setTemp(float newTemp) {
        oreTemperature = newTemp;
    }

    public String getName() {
        return this.oreName;
    }

    public void setOreName(String oreName) {
        this.oreName = oreName;
    }

    public String getID() {
        return this.id;
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    public int getUpgradeCount() {
        return upgradeCount;
    }

    public int getMultiOre() {
        return multiOre;
    }

    public void setMultiOre(int newMultiOre) {
        this.multiOre = newMultiOre;
    }

    public int getOreHistory() {
        return oreHistory;
    }

    public void setOreHistory(int oreHistory) {
        this.oreHistory = oreHistory;
    }

    public void setUpgradeCount(int upgradeCount) {
        this.upgradeCount = upgradeCount;
    }

    public String toString() {
        //Name, Value, Temp, Multi-Ore, Upgrade Count, Position, Active Effects.
        StringBuilder s = new StringBuilder();
        s.append("Name: ")
            .append(oreName)
            .append("\tValue: ").append(oreValue)
            .append("\tTemp: ").append(oreTemperature)
            .append("\tMulti-Ore: ").append(multiOre)
            .append("\tPos: ").append(position)
            .append("\tSpeed: ").append(moveSpeed)
            .append("\tisDoomed: ").append(isDoomed)
            .append("\nEffects: ");
        for (OreEffect effect : effects) {
            s.append("\n").append(effect.toString());
        }
        return String.valueOf(s);
    }

}


