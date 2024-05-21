package ore.forge.Items.Blocks;


import com.badlogic.gdx.math.Vector2;
import ore.forge.Direction;
import ore.forge.Items.Item;
import ore.forge.Ore;

//@author Nathan Ulmen
public class ConveyorBlock extends Block implements Worker {
    private float speed;
    private Vector2 force;

    public ConveyorBlock(Direction direction, int x, int y) {
        super(direction, x, y);
    }

    public ConveyorBlock(Item parentItem, float speed, Direction direction) {
        super(parentItem);
        this.speed = speed;
        this.direction = direction;
        setProcessBlock(true);
        setName(parentItem.getName() + " block");
    }

    @Override
    public void handle(Ore ore) {
        Block blockInFront = itemMap.getBlockInFront(vector2, direction);
        if (blockInFront != null && blockInFront.isValid()) {
            ore.setDestination(blockInFront.getVector(), this.speed, this.direction);
            if (!(blockInFront instanceof FurnaceBlock)) {
                blockInFront.setFull(true);
            }
            this.setFull(false); //Set this block to empty because it has now moved the ore.\
       } else {
            int x = (int) vector2.x;
            int y = (int) vector2.y;
            switch (direction) {
                case NORTH:
                    y++;
                    break;
                case SOUTH:
                    y--;
                    break;
                case EAST:
                    x++;
                    break;
                case WEST:
                    x--;
                    break;
            }
//            ore.setForce(force);
            ore.setDestination(new Vector2(x,y), speed, direction);
//            ore.setDestination(blockInFront.getVector().x, blockInFront.getVector().y, speed, direction);
        }
    }

}
