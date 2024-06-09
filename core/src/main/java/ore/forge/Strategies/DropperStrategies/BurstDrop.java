package ore.forge.Strategies.DropperStrategies;

public class BurstDrop implements DropStrategy {
    private float currentCooldownInterval, cooldownInterval;
    private float burstCount;
    private float currentBurstDuration, burstDuration;
    private int currentOreInBurst;
    private boolean isDropping;
    private float intervalPerOre, currentIntervalPerOre;

    // oreCooldown, burstCount, ore per minute.
    public BurstDrop(int orePerMinute, float burstCount) {
        this.burstCount = burstCount;
        var burstPerSec = (orePerMinute / burstCount) / 60f;
        cooldownInterval = 1 / burstPerSec;
        var orePerSec = orePerMinute / 60f;
        intervalPerOre = 1 / orePerSec;
        burstDuration = intervalPerOre * burstCount;

        cooldownInterval /=2;
        currentCooldownInterval = cooldownInterval;
        currentOreInBurst = 0;

        currentBurstDuration = burstDuration;
        intervalPerOre /= 2;
        currentIntervalPerOre = intervalPerOre;
        isDropping = false;
    }


    @Override
    public boolean drop(float delta) {
        if (!isDropping) {
            currentCooldownInterval -= delta;
            if (currentCooldownInterval <= 0) {
                isDropping = true;
                currentOreInBurst = 0;
            }
        } else {
            currentIntervalPerOre += delta;
            if (currentIntervalPerOre >= intervalPerOre && currentOreInBurst < burstCount) {
                currentOreInBurst++;
                if(currentOreInBurst == burstCount) {
                    currentCooldownInterval = cooldownInterval;
                    isDropping = false;
                }
                currentIntervalPerOre = 0;
                return true;
            }
        }
        return false;

    }


}
