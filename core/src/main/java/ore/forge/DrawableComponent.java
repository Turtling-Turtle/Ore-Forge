package ore.forge;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class DrawableComponent {
    private final Vector3 componentColor;
    private final Vector2 position;
    private final Texture texture;
//    private TextureRegion textureRegion;
    private final int drawablePrecedence;

    public DrawableComponent(Texture texture, int drawablePrecedence) {
        componentColor = new Vector3();
        position = new Vector2();
        this.texture = texture;
        this.drawablePrecedence = drawablePrecedence;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, position.x, position.y);
    }

}
