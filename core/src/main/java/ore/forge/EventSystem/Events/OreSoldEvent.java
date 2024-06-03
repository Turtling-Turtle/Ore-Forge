package ore.forge.EventSystem.Events;


import ore.forge.Ore;

public record OreSoldEvent(Ore ore) implements Event<Ore> {

    @Override
    public Ore getSubject() {
        return ore;
    }
}
