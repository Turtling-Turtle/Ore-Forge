package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import ore.forge.EventSystem.EventListener;
import ore.forge.Input.InventoryModeProcessor;
import ore.forge.Items.Item.Tier;
import ore.forge.Player.InventoryNode;


//An Item Icon is a rounded Square with a border and a name below it.
//Inside the square the items icon is held, the border is colored based on the tier of the item, and the name is
//the name of the item.
public class ItemIcon extends WidgetGroup {
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
//    private static final String roundEmpty = "128xRoundEmpty";
    private static final String roundFull = "128xRoundFull";
    private ImageButton button;
    private final InventoryNode node;
    private InventoryModeProcessor processor;

    public ItemIcon(InventoryNode node) {
        this.node = node;
        button = new ImageButton(new TextureRegionDrawable(node.getHeldItem().getTexture()));
        Table border = new Table();
        border.setBackground(buttonAtlas.getDrawable(roundFull));
        button.center();
        border.add(button).size(100,100).center();
        border.center();
        border.setSize(1.25f * button.getWidth(), 1.25f * button.getHeight());
        border.center();




        Table table = new Table();
        table.setBackground(buttonAtlas.getDrawable(roundFull));
        table.setColor(determineColor(node));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        labelStyle.fontColor = Color.BLACK;

        TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
        var background = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));
        style.background = background;
        style.label = new Label.LabelStyle(labelStyle);
        TextTooltip tooltip = new TextTooltip(node.getName(), style);
        tooltip.setInstant(true);



        addActor(border);
        border.setColor(determineColor(node));
        setSize(border.getWidth(), border.getHeight());
        this.setTouchable(Touchable.enabled);
        this.addListener(tooltip);
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                notifyProcessor();
            }
        });

        if (!node.hasSupply()) {
            setColor(Color.BLACK);
        }


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

    public void setProcessor(InventoryModeProcessor processor) {
        this.processor = processor;
    }

    private Color determineColor(InventoryNode node) {
        return switch (node.getHeldItem().getTier()) {
            case PINNACLE -> Color.FIREBRICK;
            case SPECIAL -> Color.ORANGE;
            case EXOTIC -> Color.GOLD;
            case PRESTIGE -> Color.SKY;
            case EPIC -> Color.VIOLET;
            case SUPER_RARE -> Color.NAVY;
            case RARE -> Color.BLUE;
            case UNCOMMON -> Color.GREEN;
            case COMMON -> Color.DARK_GRAY;
        };
    }

    public String toString() {
        return this.getNodeName();
    }

    public void notifyProcessor() {
        processor.handleClicked(this);
    }

}
