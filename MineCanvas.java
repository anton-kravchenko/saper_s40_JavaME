package minesweeper;

import java.util.Random;
import java.util.Vector;
import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

public class MineCanvas extends GameCanvas implements Runnable{

    Graphics graphics;
    int width, height;
    int gridSize = 10;
    int bombsAmount = 10;
    int freeze = 5;
    int[] c = new int[]{
    Colors.color2,  Colors.color4,  
    Colors.color8,   Colors.color16, 
    Colors.color32, Colors.color64, 
    Colors.color128, Colors.color256,
    Colors.color512, Colors.color1024};
    
    Font f;
    GridItem[] items;
    GridItem active;
    
    protected MineCanvas()
    {
        super(true);
        graphics = getGraphics();
        width = getWidth();
        height = getHeight();

        items = new GridItem[gridSize * gridSize];
        f = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        GridItem.fontSize = f.getHeight();
        GridItem.size = width / gridSize;
        graphics.setFont(f);
        System.out.println("grid item size "+Integer.toString(GridItem.size));
        System.out.println("font size "+Integer.toString(GridItem.fontSize));
        System.out.println(Integer.toString((GridItem.size - GridItem.fontSize)/2));
        
        //adding bombs
        
        initItems();
        setActive();
        generateBombs();
        calcBombsAround();
        
        drawGrid();
    }
    public void openSignal()
    {
        try {
            openZero(active);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }
    public void clearSignal()
    {
        clearGrid();
    }
    private void clearGrid()
    {
        initItems();
        setActive();
        generateBombs();
        calcBombsAround();
        drawGrid();
    }
    private void setActive()
    {
        int n = gridSize * gridSize/2;
        if(gridSize % 2==0) n += gridSize/2;
        active = items[n];
    }
    private void initItems()
    {
        for(int i = 0; i < gridSize*gridSize; i++)
            items[i] = new GridItem((i%gridSize) * (width / gridSize), (i/gridSize) * (width / gridSize), i, c[i%10]);
    }
    private void generateBombs()
    {
        Vector temp = new Vector();
        for(int i = 0; i < gridSize*gridSize; i++)
        {
            temp.addElement(items[i]);
        }
        Random r = new Random();
        for(int i = 0; i < bombsAmount; i++)
        {
            int rand = r.nextInt(temp.size()-1);
            GridItem bomb = (GridItem) temp.elementAt(rand);
            bomb.iAmBomb = true;
            temp.removeElementAt(rand);
        }
        temp.removeAllElements();
    }
    private void calcBombsAround()
    {
        for(int i =0; i< gridSize * gridSize; i++)
        {
            GridItem t = items[i];
            if(t.place % gridSize < gridSize-1) //right
            {
                if(items[i+1].iAmBomb) t.bombsAround++;
                if(t.place / gridSize > 0) if(items[i-gridSize+1].iAmBomb) t.bombsAround++; //right top
                if(t.place / gridSize < gridSize-1) if(items[i+gridSize+1].iAmBomb) t.bombsAround++; //right bottom
            } 
            
            if(t.place % gridSize > 0) //left
            {
                if(items[i-1].iAmBomb) t.bombsAround++;
                if(t.place / gridSize > 0) if(items[i-gridSize-1].iAmBomb) t.bombsAround++; //left top
                if(t.place / gridSize < gridSize-1) if(items[i+gridSize-1].iAmBomb) t.bombsAround++; //left bottom 
            } 
            
            if(t.place / gridSize > 0) if(items[i-gridSize].iAmBomb) t.bombsAround++; // top
            if(t.place / gridSize < gridSize-1) if(items[i+gridSize].iAmBomb) t.bombsAround++; // bottom
            
        }
    }
    private void drawActive()
    {
        graphics.setColor(Colors.MAGENTA);
        graphics.drawRect(active.posX - 1, active.posY - 1, GridItem.size+2, GridItem.size+2);
        graphics.drawRect(active.posX - 2, active.posY - 2, GridItem.size+4, GridItem.size+4);
        flushGraphics();
    }
    int keyStates = 0;
    int prevKey = 0;
    
    private void move(int dir)
    {
        
        if(dir == Canvas.RIGHT)
            if(active.place % gridSize < gridSize-1) //right
            {
                active = items[active.place+1];
            }else active = items[active.place/gridSize *gridSize];
        
        if(dir == Canvas.LEFT)
            if(active.place % gridSize > 0) //left
            {
                active = items[active.place-1];
            }else active = items[active.place+gridSize-1];
        
        if(dir == Canvas.UP)
            if(active.place / gridSize > 0) // top
            {
                active = items[active.place-gridSize];
            } else active = items[gridSize*gridSize - gridSize+ (active.place%gridSize)];
        
        if(dir == Canvas.DOWN) 
            if(active.place / gridSize < gridSize-1) // bottom
            {
                active = items[active.place+gridSize];
            } else active = items[active.place%gridSize];

        drawActive();
    }
    private void handleActiveElement() 
    {
        prevKey = keyStates;
        keyStates = getKeyStates();
        if(prevKey != keyStates)
        if ((keyStates & LEFT_PRESSED) != 0) {
            move(Canvas.LEFT);
        } else if ((keyStates & RIGHT_PRESSED) != 0) {
            move(Canvas.RIGHT);
        } else if ((keyStates & UP_PRESSED) != 0) {
            move(Canvas.UP);
        } else if ((keyStates & DOWN_PRESSED) != 0) {
            move(Canvas.DOWN);
        } else if((keyStates & Canvas.KEY_STAR)!=0) {
     
    }
        drawGrid();
    }
    
    private void drawGrid()
    {
        graphics.setColor(0x333333);
        graphics.fillRect(0, 0, width, height);
        for(int i =0; i<gridSize*gridSize; i++)
            items[i].drawMe(graphics);
        drawActive();
    }
    public Graphics getGraphics()
    {
        return super.getGraphics();
    }
    public void start()
    {  
        flushGraphics();
        Thread t = new Thread(this);
        t.start();
    }
    public void run()
    {
        while(true)
        {
            handleActiveElement();
            try {
                Thread.sleep(5);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
    private void openZero(GridItem t) throws InterruptedException
    {
        Thread.sleep(freeze);
        t.visited = true;
        t.visible = true;
        t.drawBombNumber(graphics);
        if(t.place % gridSize < gridSize-1) 
            if (!items[t.place+1].visited) 
                if(t.bombsAround==0)openZero(items[t.place+1]); // right
        if(t.place % gridSize > 0 ) 
            if (!items[t.place-1].visited) 
                if(t.bombsAround==0)openZero(items[t.place-1]);//left
        if(t.place / gridSize > 0 ) 
            if (!items[t.place-gridSize].visited)
                if(t.bombsAround==0)openZero(items[t.place-gridSize]); // top
        if(t.place / gridSize < gridSize-1) 
            if (!items[t.place+gridSize].visited) 
                if(t.bombsAround==0)openZero(items[t.place+gridSize]);// bottom
        
        if(t.place % gridSize < gridSize-1) if(t.place / gridSize > 0) // right top 
            if(!items[t.place-gridSize+1].visited) 
                if(t.bombsAround==0)openZero(items[t.place-gridSize+1]);
        
        if(t.place % gridSize > 0) if(t.place / gridSize > 0) // left top 
            if(!items[t.place-gridSize-1].visited) 
                if(t.bombsAround==0)openZero(items[t.place-gridSize-1]);
        
        
        if(t.place % gridSize < gridSize-1) if(t.place / gridSize < gridSize-1) // right bottom
            if(!items[t.place+gridSize+1].visited) 
                if(t.bombsAround==0)openZero(items[t.place+gridSize+1]);
        
        if(t.place % gridSize > 0) if(t.place / gridSize < gridSize-1) // left bottom
            if(!items[t.place+gridSize-1].visited) 
                if(t.bombsAround==0)openZero(items[t.place+gridSize-1]);
        
    }

}
