package TwentyFortyEight;

import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import javax.swing.Timer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.*;

public class App extends PApplet {
    // Game configuration constants
    public static int GRID_SIZE = 4;
    public static float CELLSIZE = 100;
    public static int CELL_BUFFER = 8;
    public static int WIDTH = 1000;
    public static int HEIGHT = 1100;
    public static int FPS = 60;
    public static int size;

    // Board state: logic, tile rendering, numbers
    private Cell[][] board;     //logic
    private Tile[][] board_2;   //tile rendering
    private Number[][] board_3; //numbers
    private List<MoveAction> moveQueue = new ArrayList<>();     //holds ongoing animation steps
    private boolean isAnimating = false;        //block input during animation

    // Timer state (top right)
    private int time_seconds_ones = 0, time_seconds_tens = 0, time_minutes_ones = 0, time_minutes_tens = 0;
    private Timer timer;    //timer increments every second

    public static Random random = new Random(); //for tile spawning

    // Inner class that tracks a single animated move for one tile
    public static class MoveAction {
        int fromX, fromY, toX, toY, value;
        float px, py;   //current pixel positions
        float dx, dy;   //movement deltas

        public MoveAction(int fromX, int fromY, int toX, int toY, int value, float cellSize, int buffer) 
        {
            this.fromX = fromX;
            this.fromY = fromY;
            this.toX = toX;
            this.toY = toY;
            this.value = value;

            //initialize pixel location
            this.px = fromX * cellSize + (buffer - 2 * fromX);          //current
            this.py = 100 + fromY * cellSize + (buffer - 2 * fromY);
            float tx = toX * cellSize + (buffer - 2 * toX);             //destination
            float ty = 100 + toY * cellSize + (buffer - 2 * toY);

            //set step per frame (change denominator to change speed)
            this.dx = (tx - px) / 5f;
            this.dy = (ty - py) / 5f;
        }

        public boolean update() 
        {
            px += dx;
            py += dy;
            float tx = toX * CELLSIZE + (CELL_BUFFER - 2 * toX);
            float ty = 100 + toY * CELLSIZE + (CELL_BUFFER - 2 * toY);
            return Math.abs(px - tx) < 1 && Math.abs(py - ty) < 1;
        }

        public void draw(PApplet app) 
        {
            app.fill(getColor(value));
            float size = (CELLSIZE * GRID_SIZE - CELL_BUFFER * (GRID_SIZE + 1)) / GRID_SIZE;
            app.rect(px, py, size, size, 8);
            app.fill(value <= 4 ? 0 : 255);
            app.textAlign(CENTER, CENTER);
            app.textSize(24 * 15 / GRID_SIZE);
            app.text(value, px + size / 2 - CELLSIZE / 20, py + size / 2 - CELLSIZE / 20);
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

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    @Override
    // Called once program start, initialize board, tiles and timer
    public void setup() {
        frameRate(FPS);
        GRID_SIZE = size;
        CELLSIZE = (1000 - 8) / (float) GRID_SIZE;

        //Game board components
        board = new Cell[GRID_SIZE][GRID_SIZE];
        board_2 = new Tile[GRID_SIZE][GRID_SIZE];
        board_3 = new Number[GRID_SIZE][GRID_SIZE];

        for (int i=0; i<GRID_SIZE; i++) 
        {
            for (int j=0; j<GRID_SIZE; j++) 
            {
                board[i][j] = new Cell(i, j, 0);        //logical
                board_2[i][j] = new Tile(i, j, 0);      //visual tile
                board_3[i][j] = new Number(i, j, 0);    //numbers
            }
        }

        addRandomTile();
        addRandomTile();

        //Setup timer
        timer = new Timer(1000, new ActionListener() 
        {
            public void actionPerformed(ActionEvent e) 
            {
                if (!gameEnd()) 
                {
                    time_seconds_ones++;
                    if (time_seconds_ones == 10) 
                    {
                        time_seconds_tens++;
                        time_seconds_ones = 0;
                    }
                    if (time_seconds_tens == 6 && time_seconds_ones == 0) 
                    {
                        time_minutes_ones++;
                        time_seconds_tens = 0;
                        time_seconds_ones = 0;
                    }
                    if (time_minutes_ones == 10) 
                    {
                        time_minutes_tens++;
                        time_minutes_ones = 0;
                    }
                }
            }
        });
        timer.start();
    }

    @Override
    //full screen rendering logic
    public void draw() {
        //Timer display
        background(0xBBADA0);
        fill(0);
        rect(700, 25, 200, 50, 10);
        fill(255);
        textAlign(CENTER, CENTER);
        textSize(30);
        String elapsedTime = time_minutes_tens + "" + time_minutes_ones + ":" + time_seconds_tens + time_seconds_ones;
        text(elapsedTime, 800, 50);

        //Draw static cells
        for (int i=0; i<GRID_SIZE; i++) 
        {
            for (int j=0; j<GRID_SIZE; j++) 
            {
                board[i][j].draw(this, CELLSIZE, GRID_SIZE, CELL_BUFFER);
            }
        }

        //Tile movement animation or full time rendering
        if (!moveQueue.isEmpty()) 
        {
            //Animate all moving tiles
            isAnimating = true;
            Set<String> moving = new HashSet<>();               //set containing all the moving cells
            Iterator<MoveAction> it = moveQueue.iterator();     //iterate through movequeue
            while (it.hasNext()) 
            {
                MoveAction action = it.next();
                moving.add(action.fromX + "," + action.fromY);
                if (action.update()) 
                {
                    it.remove();    //remove moveaction from movequeue after the movement
                }
                action.draw(this);  //draw after each currentX and Y are updated after each frame
            }

            //Draw static tiles
            for (int i = 0; i < GRID_SIZE; i++) 
            {
                for (int j = 0; j < GRID_SIZE; j++) 
                {
                    if (!moving.contains(i + "," + j) && board[i][j].getValue() != 0)   //not in moving and not 0
                    {
                        board_2[i][j].draw(this, CELLSIZE, GRID_SIZE, CELL_BUFFER);
                        board_3[i][j].draw(this, CELLSIZE, GRID_SIZE, CELL_BUFFER);
                    }
                }
            }
        } 
        else 
        {
            //When animation complete, reset flag and spawn new tile
            if (isAnimating) 
            {
                isAnimating = false;
                addRandomTile();
            }

            //Draw all tiles when no animation is happening
            for (int i = 0; i < GRID_SIZE; i++) 
            {
                for (int j = 0; j < GRID_SIZE; j++) 
                {
                    if (board[i][j].getValue() != 0) 
                    {
                        board_2[i][j].draw(this, CELLSIZE, GRID_SIZE, CELL_BUFFER);
                        board_3[i][j].draw(this, CELLSIZE, GRID_SIZE, CELL_BUFFER);
                    }
                }
            }
        }

        //Display game over
        if (gameEnd()) 
        {
            fill(211, 211, 211, 127);
            rect(0, 0, WIDTH, HEIGHT);
            fill(0);
            textSize(40);
            textAlign(CENTER, CENTER);
            text("GAME OVER", WIDTH / 2, HEIGHT / 2);
        }
    }

    @Override
    //Handles keyboard input
    public void keyPressed(KeyEvent event) {
        if (!moveQueue.isEmpty()) return; // Ignore input during animation
        switch (event.getKeyCode()) 
        {
            case 38: generateMoveQueue("up"); break;    //UP
            case 40: generateMoveQueue("down"); break;  //DOWN
            case 37: generateMoveQueue("left"); break;  //LEFT
            case 39: generateMoveQueue("right"); break; //RIGHT
            case 82: reset(); break;                        //'R'
        }
    }

    @Override
    //Click cell to spawn 2 or 4
    public void mousePressed(MouseEvent e) 
    {
        int col = (int)(e.getX() / (CELLSIZE));
        int row = (int)((e.getY() - 100 - CELL_BUFFER) / (CELLSIZE));

        if (col >= 0 && col < GRID_SIZE && row >= 0 && row < GRID_SIZE) 
        {
            if (board[col][row].getValue() == 0) 
            {
                int value = random.nextInt(2) < 1 ? 2 : 4;
                board[col][row].setValue(value);
                board_2[col][row].setValue(value);
                board_3[col][row].setValue(value);
            }
        }
    }

    //Clear game board and reset timer
    private void reset() {
        time_seconds_ones = time_seconds_tens = time_minutes_ones = time_minutes_tens = 0;
        for (int i=0; i<GRID_SIZE; i++) 
        {
            for (int j=0; j<GRID_SIZE; j++) 
            {
                board[i][j].setValue(0);
                board_2[i][j].setValue(0);
                board_3[i][j].setValue(0);
            }
        }
        addRandomTile();
        addRandomTile();
    }

    // Spawn random '2' or '4' in an empty cell
    private void addRandomTile() {
        List<int[]> empty = new ArrayList<>();
        for (int i=0; i<GRID_SIZE; i++) 
        {
            for (int j=0; j<GRID_SIZE; j++) 
            {
                if (board[i][j].getValue() == 0) 
                {
                    empty.add(new int[]{i, j});     //add coordinates of empty cell
                }
            }
        }
        if (empty.isEmpty()) return;        //No empty cells -> cannot add random tile
        int[] pos = empty.get(random.nextInt(empty.size()));    //Get random position of empty cells
        int value = random.nextInt(2) < 1 ? 2 : 4;        //Get random value between 1 and 2
        board[pos[0]][pos[1]].setValue(value);
        board_2[pos[0]][pos[1]].setValue(value);
        board_3[pos[0]][pos[1]].setValue(value);
    }

    //Construct movement and merge actions in a direction
    private void generateMoveQueue(String dir) 
    {
        //Handle vertical movement
        if (dir.equals("up") || dir.equals("down")) 
        {
            for (int col=0; col<GRID_SIZE; col++) 
            {
                int[] newCol = new int[GRID_SIZE];      //new empty array to store final values after movement
                Arrays.fill(newCol, 0);
                int lastValue = 0;                      //track last value seen to know when to merge
                int target = dir.equals("up") ? 0 : GRID_SIZE - 1;      //used as index of new array (target position)
                int step = dir.equals("up") ? 1 : -1;

                for (int i = dir.equals("up") ? 0 : GRID_SIZE - 1; dir.equals("up") ? i < GRID_SIZE : i >= 0; i += step) 
                {
                    int val = board[col][i].getValue();
                    if (val == 0) continue;         //skip 0 (nothing to move)
                    
                    // current value same as last value -> merge
                    if (lastValue == val && ((dir.equals("up") && target - step >= 0) || (dir.equals("down") && target - step < GRID_SIZE))) 
                    {
                        int mergedVal = val * 2;
                        newCol[target - step] = mergedVal;  //target-step = position of merged cell
                        moveQueue.add(new MoveAction(col, i, col, target - step, val, CELLSIZE, CELL_BUFFER));
                        lastValue = 0;
                    } 
                    else 
                    {
                        newCol[target] = val;
                        if (target != i) 
                        {
                            moveQueue.add(new MoveAction(col, i, col, target, val, CELLSIZE, CELL_BUFFER));
                        }
                        lastValue = val;    //update last value
                        target += step;     //update target position
                    }
                }

                for (int i=0; i<GRID_SIZE; i++) 
                {
                    board[col][i].setValue(newCol[i]);
                    board_2[col][i].setValue(newCol[i]);
                    board_3[col][i].setValue(newCol[i]);
                }
            }
        } 

        //Handle horizontal movement
        else if (dir.equals("left") || dir.equals("right")) 
        {
            for (int row = 0; row < GRID_SIZE; row++) 
            {
                int[] newRow = new int[GRID_SIZE];
                Arrays.fill(newRow, 0);
                int lastValue = 0;
                int target = dir.equals("left") ? 0 : GRID_SIZE - 1;
                int step = dir.equals("left") ? 1 : -1;

                for (int i = dir.equals("left") ? 0 : GRID_SIZE - 1; dir.equals("left") ? i < GRID_SIZE : i >= 0; i += step) 
                {
                    int val = board[i][row].getValue();
                    if (val == 0) continue;

                    if (lastValue == val && ((dir.equals("left") && target - step >= 0) || (dir.equals("right") && target - step < GRID_SIZE))) 
                    {
                        int mergedVal = val * 2;
                        newRow[target - step] = mergedVal;
                        moveQueue.add(new MoveAction(i, row, target - step, row, val, CELLSIZE, CELL_BUFFER));  //add moveaction in moveQueue indicating process sliding
                        lastValue = 0;
                    } 
                    else 
                    {
                        newRow[target] = val;
                        if (target != i) 
                        {
                            moveQueue.add(new MoveAction(i, row, target, row, val, CELLSIZE, CELL_BUFFER));
                        }
                        lastValue = val;
                        target += step;
                    }
                }

                for (int i=0; i<GRID_SIZE; i++) 
                {
                    board[i][row].setValue(newRow[i]);
                    board_2[i][row].setValue(newRow[i]);
                    board_3[i][row].setValue(newRow[i]);
                }
            }
        }
    } 
        

    private boolean gameEnd() {
        for (int i=0; i<GRID_SIZE; i++) 
        {
            for (int j=0; j<GRID_SIZE; j++) 
            {
                int val = board[i][j].getValue();
                if (val == 0) return false;

                // Check right
                if (i < GRID_SIZE - 1 && val == board[i + 1][j].getValue()) return false;
                // Check down
                if (j < GRID_SIZE - 1 && val == board[i][j + 1].getValue()) return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        if (args.length == 0) size = 4;
        else size = Integer.parseInt(args[0]);
        PApplet.main("TwentyFortyEight.App");
    }
}
