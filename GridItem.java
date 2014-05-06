package minesweeper;

import javax.microedition.lcdui.Graphics;

public class GridItem {
    static int size = -1;
    static int fontSize;
    boolean iAmBomb;
    boolean visited = false;
    int bombsAround;
    int posX, posY;
    int color;
    int place;
    boolean visible = false;
    public GridItem(int posX, int posY, int place, int color)
    {
        this.posX = posX;
        this.posY = posY;
        this.color = color;
        this.place = place;
    }
    
    public void drawMe(Graphics g)
    {
        g.setColor(color);
        g.drawRect(posX, posY, size, size);
        drawBombNumber(g);
    }
    public void drawBombNumber(Graphics g)
    {
        if(visible)
        {
            if(!iAmBomb)
            {
                g.setColor(0x666666);
                g.fillRect(posX+1, posY+1, size-1, size-1);
                if (bombsAround!=0)
                {
                    g.setColor(0xcccccc);
                    g.drawString(Integer.toString(bombsAround), posX+5, posY+(size - fontSize)/2, 0);
                }
            }
            if(iAmBomb)
            {
                g.setColor(0xFF1111);
                g.fillRect(posX+1, posY+1, size-1, size-1);
            }
        }
    }
}
