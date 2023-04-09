import com.badlogic.gdx.ApplicationAdapter; 
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer; 
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.Rectangle; 
import com.badlogic.gdx.math.Circle; 
import com.badlogic.gdx.Input.Keys; 
import com.badlogic.gdx.math.Vector2; 
import com.badlogic.gdx.math.MathUtils; 
import com.badlogic.gdx.math.Intersector; 
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.Texture; 
import com.badlogic.gdx.InputProcessor; 
import com.badlogic.gdx.*; 
import com.badlogic.gdx.utils.Array;  
import java.util.*;

public class GameScreen extends ApplicationAdapter 
{
    private OrthographicCamera camera; //the camera to our world
    private Viewport viewport; //maintains the ratios of your world
    private Vector2 screenCoord;
    private Vector2 worldCoord;
    private SpriteBatch batch; 
    private ShapeRenderer renderer; //used to draw textures and fonts

    // Game instance variables
    private PixelMap drawingScreen;
    private PixelMap displayScreen;
    private Color selectedColor;

    public static final int WORLD_WIDTH = 1200; 
    public static final int WORLD_HEIGHT = 600; 
    public static final float DRAWING_SCREEN_HEIGHT = 500;
    @Override//this is called once when you first run your program
    public void create(){       
        camera = new OrthographicCamera(); 
        viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT, camera); 
        screenCoord = new Vector2();
        worldCoord = new Vector2();
        batch = new SpriteBatch(); 
        renderer = new ShapeRenderer();

        // Construct instance variables
        drawingScreen = new PixelMap(60, 60);
        displayScreen = new PixelMap(60, 60);
        selectedColor = Color.RED;
    }

    @Override//this is called 60 times a second
    public void render(){
        screenCoord.x = Gdx.input.getX();
        screenCoord.y = Gdx.input.getY();
        worldCoord = viewport.unproject(screenCoord);
        viewport.apply(); 
        Gdx.gl.glClearColor(0/255f, 0/255f, 75/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);  

        // Build model
        buildInput(screenCoord.x, screenCoord.y);

        // Start rendering
        renderer.begin(ShapeType.Filled);
        // Draw drawing screen
        for (int r = 0; r < drawingScreen.getMap().length; r++) {
            for (int c = 0; c < drawingScreen.getMap()[0].length; c++) {
                renderer.setColor(drawingScreen.getMap()[r][c] == null ? Color.WHITE : drawingScreen.getMap()[r][c]);
                renderer.rect((DRAWING_SCREEN_HEIGHT/drawingScreen.getMap()[0].length) * c + 15, (DRAWING_SCREEN_HEIGHT/drawingScreen.getMap().length) * r + 15, DRAWING_SCREEN_HEIGHT/drawingScreen.getMap()[0].length, DRAWING_SCREEN_HEIGHT/drawingScreen.getMap().length);
            }
        }

        // Draw display screen
        for (int r = 0; r < displayScreen.getMap().length; r++) {
            for (int c = 0; c < displayScreen.getMap()[0].length; c++) {
                renderer.setColor(displayScreen.getMap()[r][c] == null ? Color.WHITE : displayScreen.getMap()[r][c]);
                renderer.rect((DRAWING_SCREEN_HEIGHT/displayScreen.getMap()[0].length) * c + WORLD_WIDTH - 15 - DRAWING_SCREEN_HEIGHT, (DRAWING_SCREEN_HEIGHT/displayScreen.getMap().length) * r + 15, DRAWING_SCREEN_HEIGHT/displayScreen.getMap()[0].length, DRAWING_SCREEN_HEIGHT/displayScreen.getMap().length);
            }
        }
        renderer.end();
    }

    public void buildInput(float x, float y) {
        if (Gdx.input.isTouched()) {
            drawingScreen.addPixel(15, 15, DRAWING_SCREEN_HEIGHT, DRAWING_SCREEN_HEIGHT, x, y, selectedColor);
        }
        if (Gdx.input.isKeyJustPressed(Keys.C)) {
            drawingScreen.clearMap();
            displayScreen.clearMap();
        }
        if (Gdx.input.isKeyJustPressed(Keys.NUM_1)) {
            selectedColor = Color.RED;
        }
        if (Gdx.input.isKeyJustPressed(Keys.NUM_2)) {
            selectedColor = Color.BLUE;
        }
        if (Gdx.input.isKeyJustPressed(Keys.NUM_3)) {
            selectedColor = Color.GREEN;
        }
        if (Gdx.input.isKeyJustPressed(Keys.T)) {
            System.out.println("Training...");
            
            Predict p = new Predict(convertToXData(drawingScreen), convertToYData(drawingScreen), 90, 60);
            int[][] result = p.predict();
            Color[][] newMap = new Color[60][60];
            for (int r = 0; r < result.length; r++) {
                for (int c = 0; c < result[0].length; c++) {
                    if (result[r][c] == 0)
                        newMap[r][c] = Color.RED;
                    else if (result[r][c] == 1)
                        newMap[r][c] = Color.BLUE;
                    else
                        newMap[r][c] = Color.GREEN;
                }
            }
            displayScreen = new PixelMap(newMap);
        }
    }

    @Override
    public void resize(int width, int height){
        viewport.update(width, height, true); 
    }

    @Override
    public void dispose(){
        batch.dispose();
    }
    
    private float[][] convertToXData(PixelMap x) {
        ArrayList<float[]> ans = new ArrayList<float[]>();
        for (int r = 0; r < x.getMap().length; r++) {
            for (int c = 0; c < x.getMap()[0].length; c++) {
                if (x.getMap()[r][c] != null) {
                    ans.add(new float[]{r, c});
                }
            }
        }
        float[][] stuff = new float[ans.size()][2];
        for (int i = 0; i < ans.size(); i++) {
            stuff[i] = ans.get(i);
        }
        System.out.println("X converted");
        return stuff;
    }
    
    private int[] convertToYData(PixelMap x) {
        ArrayList<Integer> ans = new ArrayList<Integer>();
        for (int r = 0; r < x.getMap().length; r++) {
            for (int c = 0; c < x.getMap()[0].length; c++) {
                if (x.getMap()[r][c] != null) {
                    if (x.getMap()[r][c] == Color.RED)
                        ans.add(0);
                    else if (x.getMap()[r][c] == Color.BLUE)
                        ans.add(1);
                    else
                        ans.add(2);
                }
            }
        }
        int[] stuff = new int[ans.size()];
        for (int i = 0; i < ans.size(); i++) {
            stuff[i] = ans.get(i);
        }
        System.out.println("Y converted");
        return stuff;
    }

}
