package TwentyFortyEight;

import processing.core.PApplet;

public class Number {

    private int x;
    private int y;
    private int value;

    public Number(int x, int y, int value) 
    {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public int getValue() 
    {
        return value;
    }

    public void setValue(int newValue) 
    {
        this.value = newValue;
    }

    public void draw(PApplet app, float cellSize, int gridSize, int buffer) 
    {
        if(value <= 4)
            app.fill(0);
        else
            app.fill(255);
        app.textAlign(PApplet.CENTER, PApplet.CENTER);
        app.textSize(24 * 15/gridSize);
        if (value != 0) 
        {
            app.text(value, x * cellSize + (buffer - 2*x) + cellSize / 2 - cellSize/20, 100 + y * cellSize + (buffer - 2*y)+ cellSize / 2 - cellSize/20);
        }
    }
    
}
