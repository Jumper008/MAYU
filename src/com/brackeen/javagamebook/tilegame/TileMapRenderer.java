package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.Sprite;
import com.brackeen.javagamebook.tilegame.sprites.Creature;
import com.brackeen.javagamebook.tilegame.sprites.Fly;

/**
 * TileMapRenderer
 * 
 * It manages the definition of each object of type <code>TileMapRenderer</code>
 * 
 * The TileMapRenderer class draws a TileMap on the screen. 
 * It draws all tiles, sprites, and an optional background image 
 * centered around the position of the player. 
 * <p>If the width of background image is smaller the width of 
 * the tile map, the background image will appear to move 
 * slowly, creating a parallax background effect.</p> 
 * <p>Also, three static methods are provided to convert pixels 
 * to tile positions, and vice-versa.</p> 
 * <p>This TileMapRender uses a tile size of 64.</p>
 * 
 * @author Quazar Volume
 */
public class TileMapRenderer {

    private static final int iTILE_SIZE = 64;
    // the size in bits of the tile
    // Math.pow(2, TILE_SIZE_BITS) == TILE_SIZE
    private static final int iTILE_SIZE_BITS = 6;

    // Background image object
    private Image imaBackground;
    
    /**
     * pixelsToTiles
     * 
     * Converts a pixel position to a tile position.
     * 
     * @param fPixels is an object of class <code>Float</code>
     * @return object of class <code>Integer</code>
     */
    public static int pixelsToTiles(float fPixels) {
        return pixelsToTiles(Math.round(fPixels));
    }
    
    /**
     * pixelsToTiles
     * 
     * Converts a pixel position to a tile position.
     * 
     * @param fPixels is an object of class <code>Integer</code>
     * @return object of class <code>Integer</code>
     */
    public static int pixelsToTiles(int fPixels) {
        // use shifting to get correct values for negative pixels
        return fPixels >> iTILE_SIZE_BITS;

        // or, for tile sizes that aren't a power of two,
        // use the floor function:
        //return (int)Math.floor((float)pixels / TILE_SIZE);
    }
    
    /**
     * tilesToPixels
     * 
     * Converts a tile position to a pixel position.
     * 
     * @param iNumTiles is an object of class <code>Integer</code>
     * @return object of class <code>Integer</code>
     */
    public static int tilesToPixels(int iNumTiles) {
        // no real reason to use shifting here.
        // it's slighty faster, but doesn't add up to much
        // on modern processors.
        return iNumTiles << iTILE_SIZE_BITS;

        // use this if the tile size isn't a power of 2:
        //return numTiles * TILE_SIZE;
    }
    
    /**
     * setBackground
     * 
     * Sets the background to draw.
     * 
     * @param imaBackground is an object of class <code>Image</code>
     */
    public void setBackground(Image imaBackground) {
        this.imaBackground = imaBackground;
    }
    
    /**
     * draw
     * 
     * Draws the specified TileMap.
     * 
     * @param gra2D_G is an object of class <code>Graphics2D</code>
     * @param tmMap is an object of class <code>TileMap</code>
     * @param iScreenWidth is an object of class <code>Integer</code>
     * @param iScreenHeight is an object of class <code>Integer</code>
     */
    public void draw(Graphics2D gra2D_G, TileMap tmMap,
        int iScreenWidth, int iScreenHeight)
    {
        // Gets player's sprite
        Sprite sprPlayer = tmMap.getPlayer();
        int iMapWidth = tilesToPixels(tmMap.getWidth());
        int iMapHeight = tilesToPixels(tmMap.getHeight());

        // get the scrolling position of the map
        // based on player's position
        int iOffsetX = iScreenWidth / 2 -
            Math.round(sprPlayer.getX()) - iTILE_SIZE;
        iOffsetX = Math.min(iOffsetX, 0);
        iOffsetX = Math.max(iOffsetX, iScreenWidth - iMapWidth);
        
        int iOffsetY = iScreenHeight / 2 -
            Math.round(sprPlayer.getY()) - iTILE_SIZE;
        iOffsetY = Math.min(iOffsetY, 0);
        iOffsetY = Math.max(iOffsetY, iScreenHeight - iMapHeight);

//        // get the y offset to draw all sprites and tiles
//        int iOffsetY = iScreenHeight -
//            tilesToPixels(tmMap.getHeight());

        // draw black background, if needed
        if (imaBackground == null ||
            iScreenHeight > imaBackground.getHeight(null))
        {
            gra2D_G.setColor(Color.black);
            gra2D_G.fillRect(0, 0, iScreenWidth, iScreenHeight);
        }

        // draw parallax background image
        if (imaBackground != null) {
            int x = iOffsetX *
                (iScreenWidth - imaBackground.getWidth(null)) /
                (iScreenWidth - iMapWidth);
            
            int y = iOffsetY *
                (iScreenHeight - imaBackground.getHeight(null)) /
                (iScreenHeight - iMapHeight);
            //int y = iScreenHeight - imaBackground.getHeight(null);

            gra2D_G.drawImage(imaBackground, x, y, null);
        }

        // draw the visible tiles
        int iFirstTileX = pixelsToTiles(-iOffsetX);
        int iLastTileX = iFirstTileX +
            pixelsToTiles(iScreenWidth) + 1;
        for (int iY=0; iY<tmMap.getHeight(); iY++) {
            for (int iX=iFirstTileX; iX <= iLastTileX; iX++) {
                Image imaImage = tmMap.getTile(iX, iY);
                if (imaImage != null) {
                    gra2D_G.drawImage(imaImage,
                        tilesToPixels(iX) + iOffsetX,
                        tilesToPixels(iY) + iOffsetY,
                        null);
                }
            }
        }

        // draw player
        gra2D_G.drawImage(sprPlayer.getImage(),
            Math.round(sprPlayer.getX()) + iOffsetX,
            Math.round(sprPlayer.getY()) + iOffsetY,
            null);

        // draw sprites
        Iterator iteI = tmMap.getSprites();
        while (iteI.hasNext()) {
            Sprite sprSprite = (Sprite)iteI.next();
            int iX = Math.round(sprSprite.getX()) + iOffsetX;
            int iY = Math.round(sprSprite.getY()) + iOffsetY;
            gra2D_G.drawImage(sprSprite.getImage(), iX, iY, null);
            
            if (sprSprite instanceof Fly) {
                System.out.println("Si");
                System.out.println(sprSprite.getImage().getWidth(null));
                System.out.println(sprSprite.getImage().getHeight(null));
            }

            // wake up the creature when it's on screen
            if (sprSprite instanceof Creature &&
                iX >= 0 && iX < iScreenWidth)
            {
                ((Creature)sprSprite).wakeUp();
            }
        }
    }
}