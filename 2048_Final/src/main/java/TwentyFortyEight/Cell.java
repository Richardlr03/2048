package TwentyFortyEight;

import processing.core.PApplet;

public class Cell {

    private int x;
    private int y;
    private int value;

    public Cell(int x, int y, int value) 
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
        boolean isHovered = isMouseOver(app, cellSize, gridSize, buffer);
        int colour = 0xFFCDC1B4;
        if(isHovered)
        {
            colour = lightenColour(app, colour, 20);
        }
        app.fill(colour);
        app.rect(x * cellSize + (buffer - 2*x), 100 + y * cellSize + (buffer - 2*y), (cellSize * gridSize - buffer * (gridSize + 1)) / gridSize, (cellSize * gridSize - buffer * (gridSize + 1)) / gridSize, 8);
    }

    /*public void tick()
    {

    }*/

    private boolean isMouseOver(PApplet app, double cellSize, int gridSize, int buffer)
    {
        double x_p = x * cellSize + (buffer - 2*x);
        double y_p = 100 + y * cellSize + (buffer - 2*y);
        double w = (cellSize * gridSize - buffer * (gridSize + 1)) / gridSize;
        double h = (cellSize * gridSize - buffer * (gridSize + 1)) / gridSize;
        return app.mouseX >= x_p && app.mouseX <= x_p + w && app.mouseY >= y_p && app.mouseY <= y_p + h;
    }

    private int lightenColour(PApplet app, int baseColor, int amount)
    {
        int r = (baseColor >> 16) & 0xFF;
        int g = (baseColor >> 8) & 0xFF;
        int b = baseColor & 0xFF;
        return app.color(Math.min(r+amount, 255), Math.min(g+amount, 255), Math.min(b+amount, 255));
    }

}