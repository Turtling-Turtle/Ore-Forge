package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import ore.forge.Items.Blocks.Worker;
import ore.forge.Strategies.OreStrategies.OreStrategy;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;

public class Ore {
    protected static Map map = Map.getSingleton();
    protected static OreRealm oreRealm = OreRealm.getSingleton();
    private String oreName;
    private double oreValue;
    private int upgradeCount, multiOre, oreHistory;
    private float oreTemperature;
    private boolean processable;
    private final BitSet history;
    private final HashMap<String, UpgradeTag> tagMap;
    private final Vector2 position, destination;
    private float moveSpeed;
    private Direction direction;
    private final Texture texture;
    private final ArrayList<OreStrategy> effects;

    public Ore() {
        this.oreValue = 0;
        this.oreTemperature = 0;
        this.oreName = "";
        this.upgradeCount = 0;
        this.multiOre = 1;
        this.processable = true;
        tagMap = new HashMap<>();
        position = new Vector2();
        destination = new Vector2();
        texture = new Texture(Gdx.files.internal("Ruby2.png"));
        direction = Direction.NORTH;
        effects = new ArrayList<>();
        history = new BitSet();

    }

    public void move(float deltaTime) {
        for (OreStrategy effect : effects) {
            effect.activate(deltaTime, this);
        }
       if (position.x != destination.x || position.y != destination.y)  {
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
//               default:
//                   activateBlock();
           }
       } else {
           activateBlock();
       }
    }

    public void applyEffect(OreStrategy effect) {
        effects.add(effect);
    }

    public void setDestination(Vector2 target, float speed, Direction direction) {
        this.destination.set(target);
        this.direction = direction;
        this.moveSpeed = speed;
    }

    public void activateBlock() {
        if ((map.getBlock((int) position.x, (int) position.y) instanceof Worker)){
            ((Worker)map.getBlock(position)).handle(this);
        } else {
            oreRealm.takeOre(this);
        }


//        map.getWorker(vector2).handle(this);//Casts block to worker block, very dangerous!!
    }

    public void setMoveSpeed(float newSpeed) {
        moveSpeed = newSpeed;
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public Ore setVector(Vector2 vector) {
        this.position.set(vector);
        return this;
    }

    public Ore applyBaseStats(double oreValue, int oreTemp, int multiOre, String oreName, OreStrategy strategy) {
        this.oreValue = oreValue;
        this.oreTemperature = oreTemp;
        this.multiOre = multiOre;
        this.oreName = oreName;
        if (strategy!= null) {
            effects.add(strategy);
        }
        return this;
    }

    public Vector2 getVector() {
        return position;
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

    public void removeEffect(String effectToRemove) {
        effects.remove(effectToRemove);
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
        this.processable = true;
        effects.clear();
        resetAllTags();
    }

    public void incrementTag(UpgradeTag tag) {
        tagMap.get(tag.getName()).incrementCurrentUpgrades();
        upgradeCount++;
    }

    public void resetNonResetterTags() {
        for (UpgradeTag tag : tagMap.values()) {
            if (!tag.isResseter()){
                tag.reset();
            }
        }
    }

    public void resetAllTags() {
        for (UpgradeTag tag: tagMap.values()) {
            tag.reset();
        }
    }

    public String toString() {
        return "Name: " + oreName + "\tValue: " + oreValue +
                "\tTemp: " + oreTemperature + "\tUpgrade Count: " + upgradeCount +
                "\tMulti-Ore: " + multiOre + "\tPos: " + position;
    }


}
