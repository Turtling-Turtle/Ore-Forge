package ore.forge.Items.Blocks;

import com.badlogic.gdx.math.Vector2;
import ore.forge.Enums.Direction;
import ore.forge.ItemMap;
import ore.forge.OreRealm;
import ore.forge.Strategies.OreEffects.OreEffect;

public class DropSource {
    private static final OreRealm oreRealm = OreRealm.getSingleton();
    private static final ItemMap itemMap = ItemMap.getSingleton();
    private Vector2 vector2;
    private Direction direction;
    private OreEffect strategy;

    public DropSource() {

    }

    @Override
    public String toString() {
        return super.toString();
    }

}
