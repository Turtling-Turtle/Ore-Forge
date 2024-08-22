package ore.forge;

import ore.forge.Strategies.DropperStrategies.BurstDrop;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BurstDropTest {
    @Test
    void testBurstDrop() {
        BurstDrop burstDrop = new BurstDrop(450, 3);
        int dropped = 0;
        for (int i = 0; i < 100_000_000; i++) {
            if (burstDrop.drop(0.000_000_6f)){
                dropped++;
            }
        }
//        assertEquals(450, dropped);
    }
}
