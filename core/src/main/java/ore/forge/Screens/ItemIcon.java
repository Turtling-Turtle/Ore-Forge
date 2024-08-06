package ore.forge.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import ore.forge.ButtonHelper;
import ore.forge.Input.InventoryMode;
import ore.forge.Items.Item.Tier;
import ore.forge.Player.InventoryNode;


//An Item Icon is a rounded Square with a border and a name below it.
//Inside the square the items icon is held, the border is colored based on the tier of the item, and the name is
//the name of the item.
public class ItemIcon extends Table {
    private final Skin buttonAtlas = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private static final String roundFull = "128xRoundFull";
    private ImageButton button;
    private final InventoryNode node;
    private InventoryMode processor;
    private TextTooltip tooltip;
    private Label storedCount, nameLabel;

    public ItemIcon(InventoryNode node) {
        this.node = node;
        TextureRegionDrawable test = new TextureRegionDrawable(node.getHeldItem().getTexture());
        test.setMinSize(Gdx.graphics.getWidth() * .06f, Gdx.graphics.getHeight() * .105f);
        button = new ImageButton(test);
        Table imageButtonTable = new Table();
        imageButtonTable.add(button).size(Gdx.graphics.getWidth() * 0.04f, Gdx.graphics.getHeight() * 0.075f);

//        button.setDebug(true);
        Table border = new Table();
//        border.setBackground(buttonAtlas.getDrawable(roundFull));
//        border.setColor(Color.BLACK);
        border.setBackground(buttonAtlas.getDrawable(roundFull));
        button.center();
        border.add(imageButtonTable);
        border.center();
//        border.setDebug(true);
        border.setSize(Gdx.graphics.getWidth() * .08f, Gdx.graphics.getHeight() * .15f);


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/ebrimabd.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.genMipMaps = true;
        //Prestige
        //40 size for 4k, 32 for 1440p, 20 for 1080p?
        parameter.size = 40; //for 4k

        parameter.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.borderStraight = true;
        labelStyle.font = generator.generateFont(parameter);


//        labelStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        labelStyle.fontColor = Color.BLACK;

        nameLabel = new Label(node.getName(), labelStyle);
        nameLabel.setFontScale(.8f, .8f);
        nameLabel.setAlignment(Align.center);
        nameLabel.setWrap(true);

//        nameLabel.setDebug(true);
        border.row();


        TextTooltip.TextTooltipStyle style = new TextTooltip.TextTooltipStyle();
        NinePatchDrawable background = new NinePatchDrawable(buttonAtlas.getPatch(roundFull));
        style.background = background;
        style.label = new Label.LabelStyle(labelStyle);

        tooltip = new TextTooltip(node.getHeldItem().getDescription(), style);
        tooltip.setInstant(true);

        border.setColor(determineColor(node));
        setSize(border.getWidth(), border.getHeight());
        this.setTouchable(Touchable.enabled);
        assert this.addListener(tooltip);

        storedCount = new Label("Stored: " + node.getStored(), labelStyle);
        Container<Label> container = new Container<>(storedCount);
        container.setClip(true);

        Container<Label> nameContainer = new Container<>(nameLabel);
        nameContainer.setSize(nameLabel.getWidth(), nameLabel.getHeight());
        nameContainer.setClip(true);

        Stack stack = new Stack();
        stack.setSize(border.getWidth(), border.getHeight());
        stack.add(border);
        container.align(Align.topLeft);
        stack.add(container);
        nameLabel.setAlignment(Align.bottomLeft);
        stack.add(nameLabel);
        nameLabel.setAlignment(Align.bottom);

        this.addActor(stack);
//        this.add(stack).expand().fill();
//        this.setBackground(glowAtlas.getDrawable("glow_square1"));
//        NinePatch patch = glowAtlas.getPatch("glow_square1");
//        NinePatchDrawable drawable = new NinePatchDrawable(patch);
//        this.setBackground(drawable);
//        this.setColor(Color.PINK);
//        this.setSize(stack.getWidth(), stack.getHeight());
//        this.setSize(900,900);
//        border.add(nameContainer).bottom().center();


//        border.add(nameLabel).expand().fill();

//        border.add(storedCount).top().left();

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

    public void updateTopLeftText(String newMessage) {
        storedCount.setText(newMessage);
    }

    public void updateToolTipText(String newMessage) {
        tooltip.getActor().setText(newMessage);
    }

}
