package ore.forge;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ore.forge.Enums.Direction;
import ore.forge.Enums.ValueOfInfluence;
import ore.forge.Items.Blocks.Worker;
import ore.forge.Strategies.OreEffects.BundledEffect;
import ore.forge.Strategies.OreEffects.OreEffect;
import ore.forge.Strategies.OreEffects.ObserverEffect;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Stack;

public class Ore {
    protected static ItemMap itemMap = ItemMap.getSingleton();
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    private final BitSet history;
    private final HashMap<String, UpgradeTag> tagMap;
    private final Vector2 position, destination;
    private final Vector2 velocity;
    private final Vector2 acceleration;
    private final Vector2 force;
    private Texture texture;
    private final ArrayList<OreEffect> effects;
    private final Stack<OreEffect> removalStack;
    private final ArrayList<ObserverEffect> observerEffects;
    private String oreName;
    private double oreValue;
    private int upgradeCount, multiOre, oreHistory;
    private float oreTemperature;
    private float moveSpeed, speedScalar;
    private Direction direction;
    private boolean isDoomed;
    private float mass;
    private final float updateInterval; //Interval for how often effects are updated.
    private float current;
    private float deltaTime;

    public Ore() {
        this.oreValue = 0;
        this.oreTemperature = 0;
        this.oreName = "";
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
        acceleration = new Vector2(1, 1);
        velocity = new Vector2();
        force = new Vector2();
        observerEffects = new ArrayList<>();
        updateInterval = 0.01f;//effects are updated 100 times every second.
    }

    //position = Vᵢ* Δt + 0.5 * a * Δt^2
    //velocity final = Vᵢ + a * Δt
    // a = F/m
    //momentum = m * v
//    public void updatePosition(float deltaTime) {
//        acceleration.y = force.y/ mass;
//        //Find final velocity in y direction.
//        velocity.y = velocity.y + acceleration.y * deltaTime;
//        position.y = velocity.y * deltaTime + .5f * acceleration.y * (deltaTime*deltaTime);
//
//        //Find final velocity in x Direction.
//        acceleration.x = force.x / mass;
//        velocity.x = velocity.x + acceleration.x * deltaTime;
//        position.x = velocity.x * deltaTime + .5f * acceleration.x * (deltaTime*deltaTime);
//
//    }
//
//    public void setForce(Vector2 vector2) {
//        this.force.x = vector2.x;
//        this.force.y = vector2.y;
//    }

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
//            for (int i = 0; i < effects.size(); i++) {
//                if (effects.get(i).isEndStepEffect()) {
//                    effects.get(i).activate(deltaTime, this);
//                }
//            }
            updateEndStepEffects(deltaTime); //Faster to put into own method than doing the below.
//            for(OreEffect strat : effects) {
//                if (strat.isEndStepEffect()) {
//                    strat.activate(deltaTime, this);
//                }
//            }
            current = 0f;
            if (this.isDoomed()) {
                oreRealm.takeOre(this);
            }
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

    private void updateEffects(float deltaTime) {
        for (OreEffect effect : effects) {
            if (!effect.isEndStepEffect()) {
                effect.activate(deltaTime, this);
            }
        }
        removeOldEffects();
    }

    private void removeOldEffects() {
        while (!removalStack.empty()) {
            effects.remove(removalStack.pop());
        }
    }

    private void updateEndStepEffects(float deltaTime) {
        for (OreEffect effect : effects) {
            if (effect.isEndStepEffect()) {
                effect.activate(deltaTime, this);
            }
        }
    }

    public void applyEffect(OreEffect strategy) {
        if (strategy == null) {
            return;
        } //Base case
        if (strategy instanceof BundledEffect) {//Base case
            for (OreEffect effect : ((BundledEffect) strategy).getStrategies()) {
                applyEffect(effect);
            }
        } else if (strategy instanceof ObserverEffect) {
            observerEffects.add((ObserverEffect) strategy.clone());
        } else {
            effects.add(strategy.clone());
        }
    }

    public void notifyObserverEffects(ValueOfInfluence mutatedField) {
        for (ObserverEffect observer : observerEffects) {
            if (observer.getObservedField() == mutatedField) {
                observer.activate(deltaTime, this);
            }
        }
        removeOldEffects();
    }

    public OreEffect getEffect() {
//        return effects.get()
        return null;
    }

    public void setDestination(Vector2 target, float speed, Direction direction) {
        this.destination.set(target);
        this.direction = direction;
        setMoveSpeed(speed);
    }

    public void setDestination(float x, float y, float speed, Direction direction) {
        this.destination.set(x,y);
        this.direction = direction;
        setMoveSpeed(speed);
    }

    public void activateBlock() {
//        Gdx.app.log("Ore" , this.toString());
        if ((itemMap.getBlock((int) position.x, (int) position.y) instanceof Worker)) {
            ((Worker) itemMap.getBlock(position)).handle(this);
        } else {
            setIsDoomed(true);
        }
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float newSpeed) {
        moveSpeed = speedScalar * newSpeed;
    }

    public Ore applyBaseStats(double oreValue, int oreTemp, int multiOre, String oreName, OreEffect strategy) {
        this.oreValue = oreValue;
        this.oreTemperature = oreTemp;
        this.multiOre = multiOre;
        this.oreName = oreName;
        applyEffect(strategy);
        return this;
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

    public UpgradeTag getUpgradeTag(UpgradeTag tag) {
        String tagName = tag.getName();
        if (tagMap.containsKey(tagName)) {
            return tagMap.get(tagName);
        } else {
            UpgradeTag newTag = new UpgradeTag(tag);
            tagMap.put(tagName, newTag);
            return newTag;
        }
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

    public void removeEffect(OreEffect effectToRemove) {
        assert effects.contains(effectToRemove);
        removalStack.add(effectToRemove);
    }

    public void purgeEffects() {
        effects.clear();
    }

    public double getOreValue() {
        return oreValue;
    }

    public Ore setOreValue(double newValue) {
        oreValue = newValue;
        return this;
    }

//    public void setOreValue(double newValue) {
//        this.oreValue = newValue;
//    }

    public float getOreTemp() {
        return oreTemperature;
    }

    public void setTemp(float newTemp) {
        oreTemperature = newTemp;
    }

    public String getName() {
        return this.oreName;
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

    public void incrementTag(UpgradeTag tag) {
        tagMap.get(tag.getName()).incrementCurrentUpgrades();
        upgradeCount++;
    }

    public void resetNonResetterTags() {
        for (UpgradeTag tag : tagMap.values()) {
            if (!tag.isResetter()) {
                tag.reset();
            }
        }
    }

    public void resetAllTags() {
        for (UpgradeTag tag : tagMap.values()) {
            tag.reset();
        }
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
