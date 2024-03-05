package ore.forge.Items.Strategies.UpgradeStrategies;


import ore.forge.Ore;

//Resets all nonResetterTags on an ore.
public class ResetterUPG implements UpgradeStrategy{

    public ResetterUPG() {}

    @Override
    public void applyTo(Ore ore) {
        ore.resetNonResetterTags();
    }

}
