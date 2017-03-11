import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Jordan
 */
public class Food {
    private int x, y;
    
    public Food(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    
    public Food() {
        randomLocation();
    }
    
    // Returns the food's x coordinate.
    public int getX() {
        return x;
    }
    
    // Returns the food's y coordinate.
    public int getY() {
        return y;
    }
    
    // Places the food at a random location on the grid.
    public void randomLocation() {
        this.x = (int)(Math.random() * (SnakeApp.APP_WIDTH / SnakeApp.BLOCK_SIZE + 1)) * SnakeApp.BLOCK_SIZE;
        this.y = (int)(Math.random() * (SnakeApp.APP_HEIGHT / SnakeApp.BLOCK_SIZE + 1)) * SnakeApp.BLOCK_SIZE;
    }
    
    public void render(GraphicsContext gc) {
        gc.setFill(Color.PINK);
        gc.fillRect(x, y, SnakeApp.BLOCK_SIZE, SnakeApp.BLOCK_SIZE);
    }
}
