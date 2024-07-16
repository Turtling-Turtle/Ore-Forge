package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.Constants;
import ore.forge.Input.InventoryMode;
import ore.forge.Items.Item.Tier;
import ore.forge.Player.InventoryNode;


//An Item Icon is a rounded Square with a border and a name below it.
//Inside the square the items icon is held, the border is colored based on the tier of the item, and the name is
//the name of the item.
public class ItemIcon extends WidgetGroup{
    private final static Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    //    private static final String roundEmpty = "128xRoundEmpty";
    private static final String roundFull = "128xRoundFull";
    private ImageButton button;
    private final InventoryNode node;
    private InventoryMode processor;
    private TextTooltip tooltip;
    private Label storedCount, nameLabel;

    public ItemIcon(InventoryNode node) {
        this.node = node;
        var test = new TextureRegionDrawable(node.getHeldItem().getTexture());
        test.setMinSize(Gdx.graphics.getWidth() * .06f, Gdx.graphics.getHeight() * .105f);
        button = new ImageButton(test);
//        button.setSize(Gdx.graphics.getWidth() * .06f, Gdx.graphics.getHeight() * .13f);
        button.setDebug(true);
        Table border = new Table();
        border.setBackground(buttonAtlas.getDrawable(roundFull));
        button.center();
        border.add(button);
//        border.add(button).size(Gdx.graphics.getWidth() *.08f, Gdx.graphics.getHeight() *.15f).center();
        border.center();
//        border.setSize(1.25f * button.getWidth(), 1.25f * button.getHeight());
        border.center();
        border.setDebug(true);
        border.setSize(Gdx.graphics.getWidth() *.08f, Gdx.graphics.getHeight() *.15f);

        Table table = new Table();
        table.setBackground(buttonAtlas.getDrawable(roundFull));
        table.setColor(determineColor(node));

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        labelStyle.fontColor = Color.BLACK;

        nameLabel = new Label(node.getName(), labelStyle);
        nameLabel.setFontScale(.8f, .8f);
        nameLabel.setAlignment(Align.center);
        nameLabel.setWrap(true);
//        nameLabel.setSize(border.getWidth() * .9f, border.getHeight() * .1f);
        border.row();
        border.add(nameLabel).expandX().fillX();


        var style = new TextTooltip.TextTooltipStyle();
        var background = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));
        style.background = background;
        style.label = new Label.LabelStyle(labelStyle);
//        tooltipLabel = new Label(node.getName() + "\nStored: " + node.getStored(), labelStyle);
//        tooltip = new TextTooltip(tooltipLabel, style);
        tooltip = new TextTooltip(node.getName() + "\nStored: " + node.getStored(), style);
        tooltip.setInstant(true);

//        addActor(border);
        border.setColor(determineColor(node));
        setSize(border.getWidth(), border.getHeight());
        this.setTouchable(Touchable.enabled);
        assert this.addListener(tooltip);

        storedCount = new Label( null, labelStyle);
        Container<Label> container = new Container<>(storedCount);
        container.setClip(true);

        Stack stack = new Stack();
        stack.setSize(border.getWidth(), border.getHeight());
        stack.add(border);
        container.align(Align.topLeft);
        stack.add(container);

        this.addActor(stack);


//        border.add(storedCount).top().left();

    }

    @Override
    public void act(float delta) {
        super.act(delta);
//        storedCount.setText("Stored: " + node.getStored());
//        Gdx.app.log("ItemIcon", String.valueOf(storedCount.getText()));

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
        this.processor = processor;
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

    public void updateToolTip(String newMessage) {
        storedCount.setText(newMessage);
        Gdx.app.log("ItemIcon--StoredCountValue", String.valueOf(storedCount.getText()));
    }

    public Label getStoredCountLabel() {
        return storedCount;
    }

}
