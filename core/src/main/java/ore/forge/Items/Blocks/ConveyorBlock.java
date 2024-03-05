package ore.forge.Items.Blocks;


import ore.forge.Direction;
import ore.forge.Items.Item;
import ore.forge.Ore;

//@author Nathan Ulmen
public class ConveyorBlock extends Block implements Worker {
    private float speed;

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
        Block blockInFront = map.getBlockInFront(vector2, direction);
        if (blockInFront != null && blockInFront.isValid()) {
            ore.setDestination(blockInFront.getVector(), this.speed, this.direction);
            blockInFront.setFull(true);
            this.setFull(false); //Set this block to empty because it has now moved the ore.\
        }
    }

}
