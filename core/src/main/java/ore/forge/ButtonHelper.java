package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox.CheckBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

public class ButtonHelper {
    private static Stage hudStage;
    private static Table hudTable;
    private static final TextureAtlas buttonAtlas = new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas"));
    private static Sound hudSound1, hudSound2, hudSound3;
    private static TextButton inventoryButton, shopButton, collectionsButton, settingsButton;
    private static TextButton.TextButtonStyle buttonStyle1, buttonStyle2, buttonStyle3, buttonStyle4, buttonStyle5, buttonStyle6;
    private static final Skin skin2 = new Skin(buttonAtlas);
    private static BitmapFont defaultFont2;
    private static ProgressBar oreLimit;
    private static ProgressBar.ProgressBarStyle progressBarStyle;
    private static ImageButton imageButton;
    private static final String roundEmpty = "128xRoundEmpty";
    private static final String roundFull = "128xRoundFull";
    private static final String roundBold = "128xRoundBold";

    private static final Sound clickSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/UIClick.wav"));
    private static final Sound placeSound = Gdx.audio.newSound(Gdx.files.internal("Sounds/impact.wav"));
    private static final Sound specialPointReward = Gdx.audio.newSound(Gdx.files.internal("Sounds/outbreak_perk.mp3"));


    public ButtonHelper() {

    }



    public static Button createButton() {
//        hudStage = new Stage();
//        hudTable = new Table();


        hudTable = new Table(skin2);
        defaultFont2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));


        hudSound1 = Gdx.audio.newSound(Gdx.files.internal("Sounds/UIClick.wav"));


        hudTable.setBounds(0,0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        buttonStyle1 = new TextButton.TextButtonStyle();
        buttonStyle1.up = new NinePatchDrawable(skin2.getPatch(roundEmpty));
        buttonStyle1.down = new NinePatchDrawable(skin2.getPatch(roundFull));
        buttonStyle1.over = new NinePatchDrawable(skin2.getPatch(roundBold));
//            buttonStyle1.pressedOffsetX = 2f;
        buttonStyle1.pressedOffsetY = -2f;
        buttonStyle1.font = defaultFont2;



        //InventoryButton
        inventoryButton = new TextButton("Inventory", buttonStyle1);
        inventoryButton.setSize(Gdx.graphics.getWidth()/10f, Gdx.graphics.getHeight()/22.5f);
        inventoryButton.setPosition(Gdx.graphics.getWidth()- inventoryButton.getWidth(), (Gdx.graphics.getHeight()/1.6f)- inventoryButton.getHeight());
        inventoryButton.setColor(Color.GOLD);
        inventoryButton.addListener(new ClickListener() {
            @Override
            public  void clicked(InputEvent event, float x, float y) {
                hudSound1.play();
                //handle logic for when button is clicked
            }
        });
        hudTable.addActor(inventoryButton);

        //Shop Button
        shopButton = new TextButton("Shop", buttonStyle1);
        shopButton.setSize(Gdx.graphics.getWidth()/10f, Gdx.graphics.getHeight()/22.5f);
        shopButton.setPosition(Gdx.graphics.getWidth()- shopButton.getWidth(), (Gdx.graphics.getHeight()/1.9f));
        shopButton.setColor(Color.SKY);
        shopButton.addListener(new ClickListener() {
            @Override
            public  void clicked(InputEvent event, float x, float y) {
                hudSound1.play();
                //handle logic for when button is clicked
            }
        });
        hudTable.addActor(shopButton);

        //Collections
        collectionsButton = new TextButton("Collections", buttonStyle1);
        collectionsButton.setSize(Gdx.graphics.getWidth()/10f, Gdx.graphics.getHeight()/22.5f);
        collectionsButton.setPosition(Gdx.graphics.getWidth()- collectionsButton.getWidth(),(Gdx.graphics.getHeight()/2.1f));
        collectionsButton.setColor(Color.CORAL);
        collectionsButton.addListener(new ClickListener() {
            @Override
            public  void clicked(InputEvent event, float x, float y) {
                hudSound1.play();
                //TODO
                //handle logic for when button is clicked
            }
        });
        hudTable.addActor(collectionsButton);



        //oreLimit creation
//            progressBarStyle = new ProgressBar.ProgressBarStyle();
//            progressBarStyle.knobBefore = new NinePatchDrawable(skin2.getPatch("128xVeryRoundBold"));
//            progressBarStyle.knobAfter = new NinePatchDrawable(skin2.getPatch("128xVeryRoundFull"));
//            progressBarStyle.background = new NinePatchDrawable(skin2.getPatch("128xVeryRoundBold"));
//
//
//
//            oreLimit = new ProgressBar(0, 500, 1f, false, progressBarStyle);
//            oreLimit.setSize(Gdx.graphics.getWidth()/12f, Gdx.graphics.getHeight()/26.5f);
////          oreLimit.setSize(Gdx.graphics.getWidth()/10f, Gdx.graphics.getHeight()/22.5f);
//            oreLimit.setPosition(2000, 800);
//            oreLimit.setColor(Color.BLUE);







        placeSound.setVolume(10, 20);

        hudTable.setFillParent(true);
        return inventoryButton;
    }

    //Creates a round TextButton and sets its size and color
    public static TextButton createRoundTextButton(String buttonText, Color buttonColor, float sizeX, float sizeY) {
        TextButton button = new TextButton(buttonText, createRoundButtonStyle());
        button.setSize(sizeX, sizeY);
        button.setColor(buttonColor);

        return button;
    }

    public static TextButton createRoundTextButton(String buttonText, Color buttonColor) {
        TextButton button = new TextButton(buttonText, createRoundButtonStyle());
        button.setColor(buttonColor);

        return button;
    }

    //returns a TextButtonStyle
    private static TextButtonStyle createRoundButtonStyle() {
        TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = getPatch(roundEmpty);
        textButtonStyle.down = getPatch(roundFull);
        textButtonStyle.over = getPatch(roundBold);
        textButtonStyle.pressedOffsetY = -2f;
        textButtonStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        return textButtonStyle;
    }
    private static TextButtonStyle createVeryRoundButtonStyle(){
        TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        return textButtonStyle;
    }
    private static NinePatchDrawable getPatch(String patchName) {
        return new NinePatchDrawable(skin2.getPatch(patchName));
    }

    public static CheckBox createCheckBox(String buttonText, Color buttonColor, float sizeX, float sizeY) {
        CheckBox checkBox = new CheckBox(buttonText, createCheckBoxStyle());
        checkBox.setSize(sizeX, sizeY);
        checkBox.setColor(buttonColor);
        return checkBox;
    }

    private static CheckBoxStyle createCheckBoxStyle(){
        CheckBoxStyle checkBoxStyle = new CheckBox.CheckBoxStyle();
        checkBoxStyle.checkboxOff = getPatch(roundEmpty);
        checkBoxStyle.checkboxOn = getPatch(roundFull);
        checkBoxStyle.font = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
        return checkBoxStyle;
    }

    public static void playFurnaceSellSound() {
        specialPointReward.play();
    }

    public static void playPlaceSound() {
        placeSound.play();
    }

    public static BitmapFont getDefaultFont2() {
        return defaultFont2;
    }

    public static Sound getButtonClickSound() {
        return clickSound;
    }



}
