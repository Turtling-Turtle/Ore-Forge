package ore.forge.game.Items.Strategies.UpgradeStrategies.game.Items.Strategies.UpgradeStrategies;


import ore.forge.game.Items.Strategies.UpgradeStrategies.game.Ore;

//Resets all nonResetterTags on an ore.
public class ResetterUPG implements UpgradeStrategy {

    public ResetterUPG() {}

    @Override
    public void applyTo(Ore ore) {
        ore.resetNonResetterTags();
    }

}
