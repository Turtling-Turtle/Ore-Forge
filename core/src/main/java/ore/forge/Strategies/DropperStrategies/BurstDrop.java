package ore.forge.Strategies.DropperStrategies;

import ore.forge.CoolDown;

public class BurstDrop implements DropStrategy {
    private float currentCooldownInterval, cooldownInterval;
    private float burstCount;
    private float currentBurstDuration, burstDuration;
    private int currentOreInBurst;
    private boolean isDropping;
    private float intervalPerOre, currentIntervalPerOre;

    private final CoolDown CCI, CIPO;

    // oreCooldown, burstCount, ore per minute.
    public BurstDrop(int orePerMinute, float burstCount) {
        this.burstCount = burstCount;
        var burstPerSec = (orePerMinute / burstCount) / 60f;
        cooldownInterval = 1 / burstPerSec;
        var orePerSec = orePerMinute / 60f;
        intervalPerOre = 1 / orePerSec;
        burstDuration = intervalPerOre * burstCount;

        cooldownInterval /= 2;
        currentCooldownInterval = cooldownInterval;
        CCI = new CoolDown(cooldownInterval);
        currentOreInBurst = 0;


        currentBurstDuration = burstDuration;
        intervalPerOre /= 2;
        currentIntervalPerOre = intervalPerOre;
        CIPO = new CoolDown(intervalPerOre);
        isDropping = false;
    }


    @Override
    public boolean drop(float delta) {
        if (!isDropping) {
            if (CCI.update(delta)) {
                isDropping = true;
                currentOreInBurst = 0;
            }
        } else {
            if (CIPO.update(delta) && currentOreInBurst < burstCount) {
                currentOreInBurst++;
                if (currentOreInBurst == burstCount) {
                    CCI.resetCurrentTime();
                    isDropping = false;
                }
                CIPO.resetCurrentTime();
                return true;
            }
        }
        return false;
    }


}
