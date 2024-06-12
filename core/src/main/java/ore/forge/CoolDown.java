package ore.forge;

/**@author Nathan Ulmen
*
* */
public class CoolDown {
    private int activationCount;
    private final float finishTime;
    private float currentTime;

    public CoolDown(float finishTime) {
        this.finishTime = finishTime;
        currentTime = 0f;
    }

    public boolean update(float deltaTime) {
        currentTime += deltaTime;
        if (currentTime >= finishTime) {
            activationCount++;
            currentTime = 0f;
            return true;
        }
        return false;
    }

    public void resetCurrentTime() {
        currentTime = 0f;
    }

    public float getCurrentTime() {
        return currentTime;
    }

    public float getFinishTime() {
        return finishTime;
    }

    public int getActivationCount() {
        return activationCount;
    }

    public String toString() {
        return "current time: " + currentTime +  " reset time: " + finishTime + " activation count: " + activationCount;
    }

}
