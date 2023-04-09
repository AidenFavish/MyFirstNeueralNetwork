import com.badlogic.gdx.graphics.Color;
import java.util.*;
public class PixelMap
{
    private Color[][] map;
    
    public PixelMap() {
        map = new Color[10][10];
    }
    
    public PixelMap(Color[][] map) {
        this.map = map;
    }
    
    public PixelMap(int rows, int columns) {
        map = new Color[rows][columns];
    }
    
    public Color[][] getMap() {
        return map;
    }
    
    public void clearMap() {
        int r = map.length;
        int c = map[0].length;
        map = new Color[r][c];
    }
    
    public void addPixel(float originX, float originY, float width, float height, float clickX, float clickY, Color c) {
        float w = (clickX - originX);
        float h = (clickY - originY);
        float pixelWidth = (width / map[0].length);
        float pixelHeight = (height / map.length);
        //System.out.println("row " + (int)(h/pixelHeight) + "\tcol " + (int)(w/pixelWidth));
        if ((int)(h/pixelHeight) < map.length && (int)(h/pixelHeight) >= 0 && (int)(w/pixelWidth) < map[0].length && (int)(w/pixelWidth) >= 0)
            map[(int)(h/pixelHeight)][(int)(w/pixelWidth)] = c;
    }
    
    public ArrayList<int[]> getPoints() {
        ArrayList<int[]> ans = new ArrayList<int[]>();
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[0].length; c++) {
                if (map[r][c] != null)
                    ans.add(new int[]{r, c});
            }
        }
        return ans;
    }
    
    public ArrayList<Color> getPointColors() {
        ArrayList<Color> ans = new ArrayList<Color>();
        for (int r = 0; r < map.length; r++) {
            for (int c = 0; c < map[0].length; c++) {
                if (map[r][c] != null)
                    ans.add(map[r][c]);
            }
        }
        return ans;
    }
}
