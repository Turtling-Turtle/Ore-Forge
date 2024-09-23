package ore.forge.Screens.Widgets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonType;
import ore.forge.Input.InventoryMode;
import ore.forge.Items.Item.Tier;
import ore.forge.Player.InventoryNode;
import ore.forge.UIHelper;


//An Item Icon is a rounded Square with a border and a name below it.
//Inside the square the items icon is held, the border is colored based on the tier of the item, and the name is
//the name of the item.
public class ItemIcon extends Table {
    private final InventoryNode node;
    private InventoryMode processor;
    private final TextTooltip tooltip;
    private final Label storedCount;

    public ItemIcon(InventoryNode node) {
        this.node = node;
        TextureRegionDrawable test = new TextureRegionDrawable(node.getHeldItem().getTexture());
        test.setMinSize(Gdx.graphics.getWidth() * .06f, Gdx.graphics.getHeight() * .105f);
        ImageButton button = new ImageButton(test);
        Table imageButtonTable = new Table();
        imageButtonTable.add(button).size(Gdx.graphics.getWidth() * 0.04f, Gdx.graphics.getHeight() * 0.075f);

//        button.setDebug(true);
        Table border = new Table();
        border.setBackground(UIHelper.getRoundFull());
        button.center();
        border.add(imageButtonTable).fill().expand();
        border.center();
//        border.setDebug(true);
        border.setSize(Gdx.graphics.getWidth() * .081f, Gdx.graphics.getHeight() * .15f);


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = UIHelper.generateFont(determineFont());


        labelStyle.fontColor = Color.BLACK;

        Label nameLabel = new Label(node.getName(), labelStyle);
        nameLabel.setFontScale(.8f, .8f);
        nameLabel.setAlignment(Align.center);
        nameLabel.setWrap(true);

        border.row();


        TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
        style.background = UIHelper.getRoundFull();
        style.label = new Label.LabelStyle(labelStyle);

        tooltip = new TextTooltip(node.getHeldItem().getDescription(), style);
        tooltip.setInstant(true);

        border.setColor(determineColor(node));
        setSize(border.getWidth(), border.getHeight());
        this.setTouchable(Touchable.enabled);
        assert this.addListener(tooltip);

        storedCount = new Label(" Stored: " + node.getStored(), labelStyle);
        Container<Label> container = new Container<>(storedCount);
        container.setClip(true);

        Stack stack = new Stack();
        stack.setSize(border.getWidth(), border.getHeight());
        stack.add(border);
        container.align(Align.topLeft);
        stack.add(container);
        stack.add(nameLabel);
        nameLabel.setAlignment(Align.bottom);

        this.add(stack).expand().fill().center();
        this.pad(2.5f);

        this.setBackground(UIHelper.getButton(ButtonType.ROUND_BOLD_128));
        this.setColor(Color.BLACK);

//        border.debugAll();
    }

    public String getNodeName() {
        return node.getName();
    }

    public Tier getNodeTier() {
        return node.getHeldItem().getTier();
    }

    public InventoryNode getNode() {
        return node;
    }

    public double getPrice() {
        return node.getHeldItem().getItemValue();
    }

    public void setProcessor(InventoryMode processor) {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notifyProcessor();
            }
        });
    }

    private Color determineColor(InventoryNode node) {
        return switch (node.getHeldItem().getTier()) {
            case PINNACLE -> Color.FIREBRICK;
            case SPECIAL -> Color.CORAL;
            case EXOTIC -> Color.ORANGE;
            case PRESTIGE -> Color.SKY;
            case EPIC -> Color.VIOLET;
            case SUPER_RARE -> Color.BLUE;
            case RARE -> Color.BLUE;
            case UNCOMMON -> Color.GREEN;
            case COMMON -> Color.WHITE;
        };
    }

    public String toString() {
        return this.getNodeName();
    }

    public void notifyProcessor() {
        processor.handleClicked(this);
    }

    public void updateTopLeftText(String newMessage) {
        storedCount.setText(" " + newMessage);
    }

    public void updateToolTipText(String newMessage) {
        tooltip.getActor().setText(newMessage);
    }

    private int determineFont() {
        return switch (Gdx.graphics.getHeight()) {
            case 1080 -> 20;
            case 1440 -> 32;
            case 2160 -> 40;
            default -> 22;
        };
    }
}
