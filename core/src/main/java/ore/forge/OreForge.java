/*This class is responsible for starting the game and bringing you to the main menu.
 */

package ore.forge;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.mongodb.client.*;
import ore.forge.Player.Inventory;
import ore.forge.Player.Player;
import ore.forge.Screens.*;
import org.bson.Document;

//@author Nathan Ulmen
public class OreForge extends Game {
	public MainMenu mainMenuScreen;
	private ResourceManager resourceManager;
	public Label fpsCounter;
	public Player player;


//	public TycoonBuilder tycoonBuilder =  TycoonBuilder.getTycoonBuilderInstance();
	public PauseMenu pauseMenu;
	public GameWorld gameWorld;
	public SettingsMenu settingsMenu;
	public UserInterface userInterface;

	private SpriteBatch spriteBatch;

	public void create() {
//        mongoConnect();
		BitmapFont font2 = new BitmapFont(Gdx.files.internal("UIAssets/Blazam.fnt"));
		Label.LabelStyle fpsStyle = new Label.LabelStyle(font2, Color.WHITE);
		fpsCounter = new Label("", fpsStyle);
		fpsCounter.setFontScale(0.6f);
		fpsCounter.setPosition(99, 100);
		fpsCounter.setVisible(false);




        /*
		* Things to Initialize here:
		* AllGameItems
		* Load Save Data
		* Create the Objects for elements like the map and UI.
		*
		*
		* */
		spriteBatch = new SpriteBatch();
		resourceManager = new ResourceManager();
        OreRealm.getSingleton().populate(); //Create all ore.
        Player.getSingleton().inventory = new Inventory(resourceManager);
        Player.getSingleton().loadSaveData();
//        ItemMap.getSingleton().loadState(resourceManager);
		mainMenuScreen = new MainMenu(this, resourceManager);
		settingsMenu = new SettingsMenu(this, resourceManager);
		gameWorld = new GameWorld(this, resourceManager);
		pauseMenu = new PauseMenu(this, resourceManager);



		setScreen(mainMenuScreen);
	}

	public void render() {
		// Clear the screen
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		fpsCounter.setText(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024) + "MB");
//		Gdx.app.log("MB", String.valueOf(((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024/1024)));
//        Gdx.app.log("FPS" , String.valueOf(Gdx.graphics.getFramesPerSecond()));
		// Update and render the current screen
		super.render();
	}

	@Override
	public void dispose() {
		// Dispose of resources when the game is closed
		super.dispose();
	}

	public SpriteBatch getSpriteBatch() {
		return spriteBatch;
	}

	public MainMenu getMainMenuScreen() {
		return mainMenuScreen;
	}

    public void mongoConnect() {
        MongoClient mongoClient = MongoClients.create("mongodb+srv://client:JAaTk8dtGkpSe42u@primarycluster.bonuplz.mongodb.net/");
        MongoDatabase database = mongoClient.getDatabase("OreForge");
        MongoCollection<Document> collection = database.getCollection("Conveyors");
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json);
        FileHandle fileHandle = Gdx.files.local("MongoTEST.json");

        fileHandle.writeString("[\n", false);
        for (Document document : collection.find()) {
            document.remove("_id");
            String jsonString = json.prettyPrint(document.toJson());
            fileHandle.writeString(jsonString +",\n", true);
        }
        fileHandle.writeString("]", true);
        mongoClient.close();
    }



}
