package TwentyFortyEight;

import processing.core.PApplet;

public class Tile {

    private int x;
    private int y;
    private int value;

    private float px;
    private float py;

    public Tile(int x, int y, int value) 
    {
        this.x = x;
        this.y = y;
        this.value = value;
        this.px = x * 100 + (8 - 2 * x); // initial placeholder assuming default size
        this.py = 100 + y * 100 + (8 - 2 * y);
    }

    public int getValue() 
    {
        return value;
    }

    public void setValue(int newValue) 
    {
        this.value = newValue;
    }

    public void setGridPosition(int x, int y, float cellSize, int buffer) 
    {
        this.x = x;
        this.y = y;
        this.px = x * cellSize + (buffer - 2 * x);
        this.py = 100 + y * cellSize + (buffer - 2 * y);
    }

    public void tick(float cellSize, int buffer) 
    {
        float targetX = x * cellSize + (buffer - 2 * x);
        float targetY = 100 + y * cellSize + (buffer - 2 * y);

        px += (targetX - px) * 0.2;
        py += (targetY - py) * 0.2;
    }

    public void draw(PApplet app, float cellSize, int gridSize, int buffer) 
    {
        if(value != 0)
        {
            int colour = getColor(value);
            app.fill(colour);
            app.rect(x * cellSize + (buffer - 2*x), 100 + y * cellSize + (buffer - 2*y), (cellSize * gridSize - buffer * (gridSize + 1)) / gridSize, (cellSize * gridSize - buffer * (gridSize + 1)) / gridSize, 8);
        }
        
    }

    private int getColor(int value) 
    {
        switch (value) {
            case 0: return 0xFFcdc1b4;
            case 2: return 0xFFeee4da;
            case 4: return 0xFFede0c8;
            case 8: return 0xFFf2b179;
            case 16: return 0xFFf59563;
            case 32: return 0xFFf67c5f;
            case 64: return 0xFFf65e3b;
            case 128: return 0xFFedcf72;
            case 256: return 0xFFedcc61;
            case 512: return 0xFFedc850;
            case 1024: return 0xFFedc53f;
            default: return 0xFFedc22e;
        }
    }
    
}
