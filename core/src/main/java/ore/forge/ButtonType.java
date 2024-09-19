package ore.forge;

public enum ButtonType {
    CIRCLE_BOLD_128("128xCircleBold", "200,36,64,64", "24,24,24,24"),
    CIRCLE_EMPTY_128("128xCircleEmpty", "662,36,64,64", "24,24,24,24"),
    CIRCLE_FULL_128("128xCircleFull", "68,36,64,64", "24,24,24,24"),
    ROUND_BOLD_128("128xRoundBold", "728,36,64,64", "24,24,24,24"),
    ROUND_EMPTY_128("128xRoundEmpty", "2,36,64,64", "24,24,24,24"),
    ROUND_FULL_128("128xRoundFull", "464,36,64,64", "24,24,24,24"),
    VERY_ROUND_BOLD_128("128xVeryRoundBold", "266,36,64,64", "24,24,24,24"),
    VERY_ROUND_EMPTY_128("128xVeryRoundEmpty", "332,36,64,64", "24,24,24,24"),
    VERY_ROUND_FULL_128("128xVeryRoundFull", "134,36,64,64", "24,24,24,24"),
    CIRCLE_BOLD_64("64xCircleBold", "794,68,32,32", "12,12,12,12"),
    CIRCLE_EMPTY_64("64xCircleEmpty", "36,2,32,32", "12,12,12,12"),
    CIRCLE_FULL_64("64xCircleFull", "2,2,32,32", "12,12,12,12");

    private final String name;
    private final String bounds;
    private final String padding;

    ButtonType(String name, String bounds, String padding) {
        this.name = name;
        this.bounds = bounds;
        this.padding = padding;
    }

    public String getName() {
        return name;
    }

    public String getBounds() {
        return bounds;
    }

    public String getPadding() {
        return padding;
    }
}
