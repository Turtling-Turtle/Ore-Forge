package ore.forge.QuestComponents.Rewards;

import com.badlogic.gdx.utils.JsonValue;

public class BundledReward implements Reward{
    private Reward[] rewards;

    public BundledReward(Reward... rewards) {

    }

    public BundledReward(JsonValue jsonValue) {
    }

    @Override
    public void grantReward() {
        for (Reward reward : rewards) {
            reward.grantReward();
        }
    }

}
