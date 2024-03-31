package ore.forge.Enums;

public enum Color {
    NONE("\u001B[0m"),
    RED("\u001B[31m"),
    GREEN("\u001B[32m"),
    BLUE("\u001B[34m"),
    YELLOW("\u001B[33m");

    public final String colorId;

    Color(String colorId) {
        this.colorId = colorId;
    }
}
