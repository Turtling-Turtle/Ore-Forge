package ore.forge.EventSystem.Events;

import ore.forge.Currency;
import ore.forge.FontColors;
import ore.forge.Items.Item;

public record FailedPurchaseGameEvent(Item item, Currency currency, int amount) implements GameEvent {

    @Override
    public Class getEventType() {
        return FailedPurchaseGameEvent.class;
    }

    @Override
    public Object getSubject() {
        return null;
    }

    @Override
    public String getBriefInfo() {
        return "Failed to purchase " + item.getName() + ". Not enough " + currency;
    }

    @Override
    public String getInDepthInfo() {
        return "";
    }

    @Override
    public String eventName() {
        return "Purchase Failed";
    }

    @Override
    public FontColors getColor() {
        return FontColors.CRIMSON;
    }
}
