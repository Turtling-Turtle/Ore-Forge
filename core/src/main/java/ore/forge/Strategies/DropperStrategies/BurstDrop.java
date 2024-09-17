package ore.forge.Strategies.DropperStrategies;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.CoolDown;

@SuppressWarnings("unused")
public class BurstDrop implements DropStrategy {
    private final float orePerMinute;
    private final float burstCount;
    private int currentOreInBurst;
    private boolean isDropping;

    //burstCooldown in the CD between bursts.
    //intervalBetween is the time between each ore in the burst
    private final CoolDown burstCooldown, intervalBetween;


    public BurstDrop(JsonValue jsonValue) {
        orePerMinute = jsonValue.getFloat("orePerMinute");
        burstCount = jsonValue.getInt("burstCount");
        var burstPerSec = (orePerMinute / burstCount) / 60f;
        var cooldownInterval = 1 / burstPerSec;
        var orePerSec = orePerMinute / 60f;
        var intervalPerOre = 1 / orePerSec;

        cooldownInterval /= 2;
        burstCooldown = new CoolDown(cooldownInterval);
        currentOreInBurst = 0;


        intervalPerOre /= 2;
        intervalBetween = new CoolDown(intervalPerOre);
        isDropping = false;

    }

    // oreCooldown, burstCount, ore per minute.
    public BurstDrop(float orePerMinute, float burstCount) {
        this.orePerMinute = orePerMinute;
        this.burstCount = burstCount;
        var burstPerSec = (orePerMinute / burstCount) / 60f;
        var cooldownInterval = 1 / burstPerSec;
        var orePerSec = orePerMinute / 60f;
        var intervalPerOre = 1 / orePerSec;

        cooldownInterval /= 2;
        burstCooldown = new CoolDown(cooldownInterval);
        currentOreInBurst = 0;


        intervalPerOre /= 2f;
        intervalBetween = new CoolDown(intervalPerOre);
        isDropping = false;
    }

    public BurstDrop(BurstDrop toBeCloned) {
        this(toBeCloned.orePerMinute, toBeCloned.burstCount);
    }


    @Override
    public boolean drop(float delta) {
        if (!isDropping) {
            if (burstCooldown.update(delta)) {
                isDropping = true;
                currentOreInBurst = 0;
            }
        } else {
            if (intervalBetween.update(delta) && currentOreInBurst < burstCount) {
                currentOreInBurst++;
                if (currentOreInBurst == burstCount) {
                    burstCooldown.resetCurrentTime();
                    isDropping = false;
                }
                intervalBetween.resetCurrentTime();
                return true;
            }
        }
        return false;
    }


}
