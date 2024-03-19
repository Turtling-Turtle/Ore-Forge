package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ore.forge.Items.Blocks.Worker;
import ore.forge.Strategies.OreStrategies.BundledEffect;
import ore.forge.Strategies.OreStrategies.OreStrategy;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Stack;

public class Ore {
    protected static Map map = Map.getSingleton();
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    private final BitSet history;
    private final HashMap<String, UpgradeTag> tagMap;
    private final Vector2 position, destination;
    private final Vector2 velocity;
    private final Vector2 acceleration;
    private final Vector2 force;
    private final Texture texture;
    private final ArrayList<OreStrategy> effects;
    private final Stack<OreStrategy> removalStack;
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
        texture = new Texture(Gdx.files.internal("Ruby2.png"));
        direction = Direction.NORTH;
        effects = new ArrayList<>();
        removalStack = new Stack<>();
        history = new BitSet();
        acceleration = new Vector2(1, 1);
        velocity = new Vector2();
        force = new Vector2();
        updateInterval = 0.1f;//effects are updated 10 times every second.
    }

    //position = Vᵢ* Δt + 0.5 * a * Δt^2
    //velocity final = Vᵢ + a * Δt
    // a = F/m
    //momentum = m * v
    public void updatePosition(float deltaTime) {
        acceleration.y = force.y/ mass;
        //Find final velocity in y direction.
        velocity.y = velocity.y + acceleration.y * deltaTime;
        position.y = velocity.y * deltaTime + .5f * acceleration.y * (deltaTime*deltaTime);

        //Find final velocity in x Direction.
        acceleration.x = force.x / mass;
        velocity.x = velocity.x + acceleration.x * deltaTime;
        position.x = velocity.x * deltaTime + .5f * acceleration.x * (deltaTime*deltaTime);

    }

    public void setForce(Vector2 vector2) {
        this.force.x = vector2.x;
        this.force.y = vector2.y;
    }

    public void act(float deltaTime) {
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
            for(OreStrategy strat : effects) {
                if (strat.isEndStepEffect()) {
                    strat.activate(deltaTime, this);
                }
            }
            current = 0f;
        }
        if (isDoomed) {
            oreRealm.takeOre(this);
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
        for (OreStrategy effect : effects) {
            if (!effect.isEndStepEffect()) {
                effect.activate(deltaTime, this);
            }
        }
        while (!removalStack.empty()) {
            effects.remove(removalStack.pop());
        }
    }

    public void applyEffect(OreStrategy strategy) {
        if (strategy instanceof BundledEffect) {
            for (OreStrategy effect : ((BundledEffect) strategy).getStrategies()) {
                if (effect != null) {
                    applyEffect(effect);
                }
            }
        } else if (strategy != null) {
            effects.add(strategy);
        }
    }

    public void setDestination(Vector2 target, float speed, Direction direction) {
        this.destination.set(target);
        this.direction = direction;
        setMoveSpeed(speed);
    }

    public void activateBlock() {
//        Gdx.app.log("Ore" , this.toString());
        if ((map.getBlock((int) position.x, (int) position.y) instanceof Worker)) {
            ((Worker) map.getBlock(position)).handle(this);
        } else {
            isDoomed = true;
            oreRealm.takeOre(this);
        }
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public void setMoveSpeed(float newSpeed) {
        moveSpeed = speedScalar * newSpeed;
    }

    public Ore applyBaseStats(double oreValue, int oreTemp, int multiOre, String oreName, OreStrategy strategy) {
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

    public void setIsDying(boolean state) {
        isDoomed = state;
    }

    public void setSpeedScalar(float newScalar) {
        speedScalar = newScalar;
    }

    public void removeEffect(OreStrategy effectToRemove) {
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
            if (!tag.isResseter()) {
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
            .append("\nEffects: ");
        for (OreStrategy effect : effects) {
            s.append("\n").append(effect.toString());
        }
        return String.valueOf(s);

    }


}
