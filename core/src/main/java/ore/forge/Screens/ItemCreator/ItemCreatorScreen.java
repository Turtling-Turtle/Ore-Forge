package ore.forge.Screens.ItemCreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ore.forge.Expressions.Condition;
import ore.forge.ItemManager;
import ore.forge.OreForge;
import ore.forge.Screens.CustomScreen;
import ore.forge.Screens.ItemCreator.VisualScripting.Forums.ConstantHiddenForum;
import ore.forge.Screens.ItemCreator.VisualScripting.Forums.DropDownForum;
import ore.forge.Screens.ItemCreator.VisualScripting.Forums.TextInputForum;
import ore.forge.Screens.ItemCreator.VisualScripting.ScriptingNode;
import ore.forge.Strategies.UpgradeStrategies.ConditionalUpgrade;
import ore.forge.Strategies.UpgradeStrategies.UpgradeStrategy;
import ore.forge.UI.UIHelper;

import java.util.ArrayList;

public class ItemCreatorScreen extends CustomScreen {
    private Stage stage;
    private Table canvas;
    private ShapeRenderer shapeRenderer;
    private float lastTouchX, lastTouchY;
    private java.util.List<ScriptingNode<?>> scriptingNodes;

    public ItemCreatorScreen(OreForge game, ItemManager itemManager) {
        super(game, itemManager);
        scriptingNodes = new ArrayList<>();

        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        shapeRenderer = new ShapeRenderer();

        canvas = new Table();
        canvas.setFillParent(true);
        stage.addActor(canvas);
        SelectBox.SelectBoxStyle style = new SelectBox.SelectBoxStyle();
        style.font = UIHelper.generateFont(16);
        style.background = UIHelper.getRoundFull().tint(Color.BLACK);
        style.backgroundOpen = UIHelper.getRoundFull().tint(Color.PURPLE);

        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = UIHelper.generateFont(16);
        listStyle.background = UIHelper.getRoundFull().tint(Color.BLACK);
        listStyle.selection = UIHelper.getRoundFull().tint(Color.PURPLE);
        listStyle.down = UIHelper.getRoundFull().tint(Color.FOREST);
        listStyle.over = UIHelper.getRoundFull().tint(Color.FOREST);

        style.listStyle = listStyle;

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = UIHelper.getRoundFull().tint(Color.BLACK);


        style.scrollStyle = scrollStyle;

        var dropDown= new DropDownForum(style, "Ore Value", "Ore Temperature", "Multiore", "Speed Scalar");
        dropDown.setWidth(300);
        dropDown.showScrollPane();
//        canvas.addActor(dropDown);

        TextInputForum textInput = new TextInputForum("ORE_VALUE > 100") {
            @Override
            public boolean isValid() {
                try {
                    Condition.compile(this.getText());
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }
            @Override
            public String getError() {
                return "Failed to compile condition: " + this.getText();
            }
        };

//        canvas.add(textInput).pad(30);
        textInput.setPosition(300, 300);
        ScriptingNode<UpgradeStrategy> conditionalTestNode = new ScriptingNode<>(new ScriptingNode.Field("upgradeName", new ConstantHiddenForum(ConditionalUpgrade.class.getName())),
            new ScriptingNode.Field("condition", textInput)
            , new ScriptingNode.Field("testIng!", new DropDownForum(style, "Ore Value", "Ore Temperature", "Multiore", "Speed Scalar"))
        );
        conditionalTestNode.setName("upgrade");
//        canvas.add(conditionalTestNode).bottom().right();
        stage.addActor(conditionalTestNode);
        System.out.println(0xB00B1E5);
        conditionalTestNode.debug();
//        stage.setDebugAll(true);

        scriptingNodes.add(conditionalTestNode);
    }

    private void drawGrid() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.3f, 0.3f, 0.3f, 1);
        int gridSize = 50;
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float camX = stage.getCamera().position.x;
        float camY = stage.getCamera().position.y;

        float startX = camX - width / 2 - gridSize;
        float endX = camX + width / 2 + gridSize;
        float startY = camY - height / 2 - gridSize;
        float endY = camY + height / 2 + gridSize;

        for (float x = startX; x < endX; x += gridSize) {
            shapeRenderer.line(x, startY, x, endY);
        }
        for (float y = startY; y < endY; y += gridSize) {
            shapeRenderer.line(startX, y, endX, y);
        }
        shapeRenderer.end();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        shapeRenderer.setProjectionMatrix(stage.getCamera().combined);
        drawGrid();
        if(Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            var result = scriptingNodes.getFirst().validate();
            if (result.isValid()) {
                System.out.println(scriptingNodes.getFirst().create());
            } else {
                for (String err: result.getErrors()) {
                    System.out.println(err);
                }
            }
        }
        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }

    private TextField.TextFieldStyle getTextFieldStyle() {
        TextField.TextFieldStyle style = new TextField.TextFieldStyle();
        style.font = UIHelper.generateFont(16);
        style.background = UIHelper.getRoundFull().tint(Color.WHITE);
        style.cursor = UIHelper.getRoundFull().tint(Color.BLACK);

        return style;
    }

}
