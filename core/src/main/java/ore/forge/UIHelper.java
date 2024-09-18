package ore.forge;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import java.util.HashMap;

public class UIHelper {
    private final static FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("Fonts/ebrimabd.ttf"));
    private final static HashMap<Integer, BitmapFont> fontLookup = new HashMap<>();

    public static BitmapFont generateFont(int size) {
        if (fontLookup.containsKey(size)) {
            return fontLookup.get(size);
        }
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = size;
        parameter.genMipMaps = true;
        parameter.minFilter = Texture.TextureFilter.MipMapLinearNearest;
        parameter.magFilter = Texture.TextureFilter.MipMapLinearNearest;
        var font = generator.generateFont(parameter);
        fontLookup.put(size, font);
        return font;
    }

    private final static Skin buttonTextures = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/UIButtons.atlas")));
    private final static HashMap<String, NinePatchDrawable> buttonLookup = new HashMap<>();


    public static NinePatchDrawable getRoundFull() {
        if (buttonLookup.containsKey("128xRoundFull")) {
            return buttonLookup.get("128xRoundFull");
        }
        var roundFull = new NinePatchDrawable(buttonTextures.getPatch("128xRoundFull"));
        buttonLookup.put("128xRoundFull", roundFull);
        return roundFull;
    }

    public static NinePatchDrawable getButton(String name) {
        if (buttonLookup.containsKey(name)) {
            return buttonLookup.get(name);
        }
        var newNinePatch = new NinePatchDrawable(buttonTextures.getPatch(name));
        buttonLookup.put(name, newNinePatch);
        return newNinePatch;
    }

    private final static Skin icons = new Skin(new TextureAtlas(Gdx.files.internal("UIAssets/Icons.atlas")));
    private final static HashMap<String, NinePatchDrawable> iconLookup = new HashMap<>();


    public static NinePatchDrawable getIcon(String name) {
        if (iconLookup.containsKey(name)) {
            return iconLookup.get(name);
        }
        var newNinePatch = new NinePatchDrawable(icons.getPatch(name));
        iconLookup.put(name, newNinePatch);
        return newNinePatch;
    }


}
