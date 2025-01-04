package ore.forge.Items.Blocks;

import com.badlogic.gdx.math.Vector2;
import ore.forge.Direction;
import ore.forge.EventSystem.EventManager;
import ore.forge.Items.Item;
import ore.forge.ItemMap;

//@author Nathan Ulmen
public class Block {
    protected final static ItemMap itemMap = ItemMap.getSingleton();
    protected final static EventManager eventManager = EventManager.getSingleton();
    public final Vector2 vector2;

    protected float direction;
    private boolean isProcessBlock, isFull;

    protected String name;
    protected String id;
    protected Item parentItem;

    public Block(float direction, int x, int y) {
        vector2 = new Vector2();
        vector2.set(x,y);
        this.direction = direction;
    }

    public Block(Item parentItem) {
        this.parentItem = parentItem;
        vector2 = new Vector2();
        isProcessBlock = false;
        name = parentItem.getName() + " " + this.getClass().getSimpleName();
        id = parentItem.getID();
        this.direction = parentItem.getDirection();
    }

    public Block setVector2(float X, float Y) {
        vector2.set(X, Y);
        return this;
    }

    public Vector2 getVector() {
        return vector2;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isValid() {
        return !isFull && isProcessBlock;
    }

    public void setFull(boolean state) {
        isFull = state;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public void setProcessBlock(boolean state) {
        isProcessBlock = state;
    }

    public boolean isProcessBlock() {
        return isProcessBlock;
    }

    public Item getParentItem() {
        return parentItem;
    }

    public String toString() {
        return  name + vector2.toString() + direction + "\t";
    }

}
