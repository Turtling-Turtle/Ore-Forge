package ore.forge.QuestComponents.Rewards;

import com.badlogic.gdx.utils.JsonValue;
import ore.forge.Player.Player;

public class CurrencyReward implements Reward{
    private enum CurrencyType {CASH, SPECIAL_POINTS, PRESTIGE_CURRENCY};
    private final Player player = Player.getSingleton();
    private final CurrencyType type;
    private final double rewardAmount;

    public CurrencyReward(double rewardAmount, CurrencyType type) {
        this.rewardAmount = rewardAmount;
        this.type = type;

    }

    public CurrencyReward(JsonValue jsonValue) {
        this.type = CurrencyType.valueOf(jsonValue.getString("currencyType"));
        this.rewardAmount = jsonValue.getDouble("rewardCount");
    }

    @Override
    public void grantReward() {
        switch (type) {
            case CASH -> player.addToWallet(rewardAmount);
            case SPECIAL_POINTS -> player.addSpecialPoints((int) rewardAmount);
            case PRESTIGE_CURRENCY -> player.setPrestigeCurrency((int) (player.getPrestigeCurrency() + rewardAmount));
        }
    }
}
