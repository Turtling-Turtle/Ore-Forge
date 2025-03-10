package ore.forge.Screens.ItemCreator;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import ore.forge.ItemManager;
import ore.forge.OreForge;
import ore.forge.Screens.CustomScreen;
import ore.forge.Screens.ItemCreator.VisualScripting.Forums.DropDownForum;
import ore.forge.UI.UIHelper;

public class ItemCreatorScreen extends CustomScreen {
    private Stage stage;
    private Table canvas;
    private ShapeRenderer shapeRenderer;
    private float lastTouchX, lastTouchY;

    public ItemCreatorScreen(OreForge game, ItemManager itemManager) {
        super(game, itemManager);

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
        listStyle.background = UIHelper.getRoundFull();
        listStyle.selection = UIHelper.getRoundFull().tint(Color.YELLOW);
        listStyle.down = UIHelper.getRoundFull().tint(Color.RED);
        listStyle.over = UIHelper.getRoundFull().tint(Color.GREEN);

        style.listStyle = listStyle;

        ScrollPane.ScrollPaneStyle scrollStyle = new ScrollPane.ScrollPaneStyle();
        scrollStyle.background = UIHelper.getRoundFull().tint(Color.WHITE);


        style.scrollStyle = scrollStyle;

        var dropDown= new DropDownForum(style, "Ore Value", "Ore Temperature", "Multiore", "Speed Scalar");
        dropDown.setSize(300, 300);
        dropDown.showScrollPane();
        canvas.addActor(dropDown);


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

        stage.act(delta);
        stage.draw();
    }


    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }

}
