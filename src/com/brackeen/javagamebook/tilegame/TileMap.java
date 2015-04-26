package com.brackeen.javagamebook.tilegame;

import java.awt.Image;
import java.util.LinkedList;
import java.util.Iterator;

import com.brackeen.javagamebook.graphics.Sprite;

/**
 * TileMap
 *
 * It manages the definition of each object of type <code>TileMap</code>
 *
 * The TileMap class contains the data for a tile-based
 * map, including Sprites. Each tile is a reference to an
 * Image. Of course, Images are used multiple times in the tile
 * map.
 *
 * @author Quazar Volume
 *
 */
public class TileMap {

    private Image[][] imaMatTiles; //Matrix of tile images
    private boolean[][] bPlatform; //Matrix that determines wheter the tile is a platform or not
    private LinkedList lklSprites; //Linkedlist of sprites
    private Sprite sprPlayer; //object sprite of player

    /**
     * TileMap
     * 
     * Parameterized Constructor 
     * 
     *  Creates a new TileMap with the specified width and
     *  height (in number of tiles) of the map.
     * 
     * @param iWidth is an object of class <code>Integer</code>
     * @param iHeight is an object of class <code>Integer</code>
     */
    public TileMap(int iWidth, int iHeight) {
        imaMatTiles = new Image[iWidth][iHeight];
        bPlatform = new boolean[iWidth][iHeight];
        // Initalize all tiles as solid tiles.
        for (int iK = 0; iK < iWidth; iK++) {
            for (int iJ = 0; iJ < iHeight; iJ++) {
                bPlatform[iK][iJ] = false;
            }
        }
        lklSprites = new LinkedList();
    }


    /**
     * getWidth
     * 
     * Gets the width of this TileMap (number of tiles across).
     * 
     * @return object of class <code>Image</code>
     */
    public int getWidth() {
        return imaMatTiles.length;
    }

    /**
     * getHeight
     * 
     * Gets the height of this TileMap (number of tiles down).
     * 
     * @return object of class <code>Image</code>
     */
    public int getHeight() {
        return imaMatTiles[0].length;
    }

    /**
     * getTile
     * 
     * Gets the tile at the specified location. Returns null if
     * no tile is at the location or if the location is out of
     * bounds.
     * 
     * @param iX is an object of class <code>Integer</code>
     * @param iY is an object of class <code>Integer</code>
     * @return object of class <code>Image</code>
     */
    public Image getTile(int iX, int iY) {
        if (iX < 0 || iX >= getWidth() ||
            iY < 0 || iY >= getHeight())
        {
            return null;
        }
        else {
            return imaMatTiles[iX][iY];
        }
    }

    /**
     * setTile
     * 
     * Sets the tile at the specified location.
     * 
     * @param iX is an object of class <code>Integer</code>
     * @param iY is an object of class <code>Integer</code>
     * @param imaTile is an object of class <code>Image</code>
     */
    public void setTile(int iX, int iY, Image imaTile, boolean bIsPlatform) {
        imaMatTiles[iX][iY] = imaTile;
        this.bPlatform[iX][iY] = bIsPlatform;
    }
    
    public void setPlatform(int iX, int iY, boolean bIsPlatform) {
        this.bPlatform[iX][iY] = bIsPlatform;
    }
    
    public boolean getPlatform(int iX, int iY) {
        return bPlatform[iX][iY];
    }

    /**
     * getPlayer
     * 
     * Gets the player Sprite.
     * 
     * @return object of class <code>Sprite</code>
     */
    public Sprite getPlayer() {
        return sprPlayer;
    }

    /**
     * setPlayer
     * 
     * Sets the player Sprite.
     * 
     * @param sprPlayer is an object of class <code>Sprite</code>
     */
    public void setPlayer(Sprite sprPlayer) {
        this.sprPlayer = sprPlayer;
    }

    /**
     * addSprite
     * 
     * Adds a Sprite object to this map.
     * 
     * @param sprSprite is an object of class <code>Sprite</code>
     */
    public void addSprite(Sprite sprSprite) {
        lklSprites.add(sprSprite);
    }
    
    /**
     * removeSprite
     * 
     * Removes a Sprite object from this map.
     * 
     * @param sprSprite is an object of class <code>Sprite</code>
     */
    public void removeSprite(Sprite sprSprite) {
        lklSprites.remove(sprSprite);
    }

    /**
     * getSprites
     * 
     * Gets an Iterator of all the Sprites in this map,
     * excluding the player Sprite.
     * 
     * @return object of class <code>LinkedList</code>
     */
    public Iterator getSprites() {
        return lklSprites.iterator();
    }

}
