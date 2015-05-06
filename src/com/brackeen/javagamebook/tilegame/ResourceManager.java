package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.*;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.tilegame.sprites.*;


/**
 * ResourceManager
 *
 * It manages the definition of each object of type <code>ResourceManager</code>
 *
 * The ResourceManager class loads and manages tile Images and
 * "host" Sprites used in the game. Game Sprites are cloned from
 * "host" Sprites.
 *
 * @author Quazar Volume
 *
 */
public class ResourceManager {

    private ArrayList ArrTiles;
    protected int iCurrentMap;
    private GraphicsConfiguration gcGraphicsConfiguration;

    // host sprites used for cloning
    private Sprite sprPlayerSprite;
    private Sprite sprMusicSprite;
    private Sprite sprCoinSprite;
    private Sprite sprGoalSprite;
    private Sprite sprGrubSprite;
    private Sprite sprFlySprite;
    private Sprite sprArrowSprite;
    private Sprite sprPauseMenu;
    private Sprite sprArcherSprite;
    private Sprite sprAguaSprite;
    private Sprite sprPicosSprite;
    private Sprite sprBossSprite;
    
    public Animation aniDefaultAnim; //Animation meant to be used by other classes when there is no animation available

    /**
     * ResourceManager
     * 
     * Creates a new ResourceManager with the specified
     * GraphicsConfiguration.
     * 
     * @param gcGraphicsConfiguration is an object
     * of class <code>GraphicsConfiguration</code>
     */
    public ResourceManager(GraphicsConfiguration gcGraphicsConfiguration) {
        this.gcGraphicsConfiguration = gcGraphicsConfiguration;
        loadTileImages();
        loadCreatureSprites();
        loadPowerUpSprites();
        aniDefaultAnim = null;
    }
    
    /**
     * getICurrentMap
     * 
     * Gets the current map number
     * 
     * @return object of class <code>Integer</code>
     */
    public int getICurrentMap() {
        return iCurrentMap;
    }

    /**
     * loadImage
     * 
     * Gets an image from the images/ directory.
     * 
     * @param sName is an object of class <code>String</code>
     * @return object of class <code>Image</code>
     */
    public Image loadImage(String sName) {
        String sFilename = "images/" + sName;
        return new ImageIcon(sFilename).getImage();
    }

    /**
     * getMirrorImage
     * 
     * Returns mirror image
     * 
     * @param imaImage is an object of class <code>Image</code>
     * @return object of class <code>Image</code>
     */
    public Image getMirrorImage(Image imaImage) {
        return getScaledImage(imaImage, -1, 1);
    }

    /**
     * getFlippedImage
     * 
     * Returns flipped image
     * 
     * @param imaImage is an object of class <code>Image</code>
     * @return object of class <code>Image</code>
     */
    public Image getFlippedImage(Image imaImage) {
        return getScaledImage(imaImage, 1, -1);
    }

    /**
     * getScaledImage
     * 
     * returns scaled image
     * 
     * @param imaImage is an object of class <code>Image</code>
     * @param fX is an object of class <code>Float</code>
     * @param fY is an object of class <code>Float</code>
     * @return object of class <code>Image</code>
     */
    private Image getScaledImage(Image imaImage, float fX, float fY) {

        // set up the transform
        AffineTransform atTransform = new AffineTransform();
        atTransform.scale(fX, fY);
        atTransform.translate((fX-1) * imaImage.getWidth(null) / 2,
            (fY-1) * imaImage.getHeight(null) / 2);

        // create a transparent (not translucent) image
        Image imaNewImage = gcGraphicsConfiguration.createCompatibleImage(imaImage.getWidth(null),
            imaImage.getHeight(null),
            Transparency.BITMASK);

        // draw the transformed image
        Graphics2D gra2D_G = (Graphics2D)imaNewImage.getGraphics();
        gra2D_G.drawImage(imaImage, atTransform, null);
        gra2D_G.dispose();

        return imaNewImage;
    }

    /**
     * loadNextMap
     * 
     * returns next map
     * 
     * @return object of class <code>TileMap</code>
     */
    public TileMap loadNextMap() {
        TileMap tmMap = null;
        while (tmMap == null) {
            iCurrentMap++;
            try {
                tmMap = loadMap("maps/map" + iCurrentMap + ".txt");
            }
            catch (IOException ex) {
                if (iCurrentMap == 1) {
                    // no maps to load!
                    return null;
                }
                iCurrentMap = 0;
                tmMap = null;
            }
        }

        return tmMap;
    }

    /**
     * reloadMap
     * 
     * Returns load map
     * 
     * @return object of class <code>TileMap</code>
     */
    public TileMap reloadMap() {
        try {
            return loadMap("maps/map" + iCurrentMap + ".txt");
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * loadMap
     * 
     * load map by file name
     * 
     * @param sFilename is an object of class <code>String</code>
     * @return object of class <code>TileMap</code>
     * @throws IOException 
     */
    private TileMap loadMap(String sFilename)
        throws IOException
    {
        ArrayList ArrLines = new ArrayList();
        int iWidth = 0;
        int iHeight = 0;

        // read every line in the text file into the list
        BufferedReader brReader = new BufferedReader(
            new FileReader(sFilename));
        while (true) {
            String sLine = brReader.readLine();
            // no more lines to read
            if (sLine == null) {
                brReader.close();
                break;
            }

            // add every line except for comments
            if (!sLine.startsWith("#")) {
                ArrLines.add(sLine);
                iWidth = Math.max(iWidth, sLine.length());
            }
        }

        // parse the lines to create a TileEngine
        iHeight = ArrLines.size();
        TileMap tmNewMap = new TileMap(iWidth, iHeight);
        for (int iY=0; iY<iHeight; iY++) {
            String sLine = (String)ArrLines.get(iY);
            for (int iX=0; iX<sLine.length(); iX++) {
                char cChar = sLine.charAt(iX);

                // check if the char represents tile A, B, C etc.
                int iTile = cChar - 'A';
                if (iTile >= 0 && iTile < ArrTiles.size()) {
                    tmNewMap.setTile(iX, iY, (Image)ArrTiles.get(iTile), false);
                    if (cChar == 'L') { //Transform platform tiles into platforms
                        tmNewMap.setPlatform(iX, iY, true);
                    }
                }

                // check if the char represents a sprite
                else if (cChar == 'o') {
                    addSprite(tmNewMap, sprAguaSprite, iX, iY);
                }
                else if (cChar == '!') {
                    addSprite(tmNewMap, sprMusicSprite, iX, iY);
                }
                else if (cChar == '*') {
                    addSprite(tmNewMap, sprGoalSprite, iX, iY);
                }
                else if (cChar == '1') {
                    addSprite(tmNewMap, sprGrubSprite, iX, iY);
                }
                else if (cChar == '2') {
                    addSprite(tmNewMap, sprFlySprite, iX, iY);
                }
                else if (cChar == '3') {
                    addSprite(tmNewMap, sprArcherSprite, iX, iY);
                }
                else if (cChar == '7') {
                    addSprite( tmNewMap, sprArrowSprite, iX, iY);
                }
                else if (cChar == '8') {
                    addSprite( tmNewMap, sprAguaSprite, iX, iY);
                }
                else if (cChar == '9') {
                    addSprite( tmNewMap, sprPicosSprite, iX, iY);
                }
                else if (cChar == '0') {
                    addSprite( tmNewMap, sprBossSprite, iX, iY);
                }
            }
        }

        // add the player to the map
        Creature player = (Creature)sprPlayerSprite.clone();
        player.setHealth(1000);
        switch(iCurrentMap-1) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4: { // Main Menus
                player.setX(-TileMapRenderer.tilesToPixels(3));
                player.setY(0);
                break;
            }
            case 5: { // Map 6
                player.setX(TileMapRenderer.tilesToPixels(3));
                player.setY(TileMapRenderer.tilesToPixels(14));
                break;
            }
            case 6: { // Map 7
                player.setX(TileMapRenderer.tilesToPixels(100));
                player.setY(TileMapRenderer.tilesToPixels(30));
                break;
            }
            case 7: { // Map 8
                player.setX(TileMapRenderer.tilesToPixels(31));
                player.setY(TileMapRenderer.tilesToPixels(40));
                break;
            }
            case 8: { // Map 9
                player.setX(TileMapRenderer.tilesToPixels(3));
                player.setY(TileMapRenderer.tilesToPixels(14));
                break;
            }
        }
        tmNewMap.setPlayer(player);

        return tmNewMap;
    }

    /**
     * addSprite
     * 
     * Adds a sprite
     * 
     * @param tmMap is an object of class <code>TileMap</code>
     * @param sprHostSprite is an object of class <code>Sprite</code>
     * @param iTileX is an object of class <code>Integer</code>
     * @param iTileY is an object of class <code>Integer</code>
     */
    private void addSprite(TileMap tmMap,
        Sprite sprHostSprite, int iTileX, int iTileY)
    {
        if (sprHostSprite != null) {
            // clone the sprite from the "host"
            Sprite sprite = (Sprite)sprHostSprite.clone();

            // center the sprite
            sprite.setX(TileMapRenderer.tilesToPixels(iTileX) +
                (TileMapRenderer.tilesToPixels(1) -
                sprite.getWidth()) / 2);

            // bottom-justify the sprite
            sprite.setY(TileMapRenderer.tilesToPixels(iTileY + 1) -
                sprite.getHeight());

            // add it to the map
            tmMap.addSprite(sprite);
        }
    }


    // -----------------------------------------------------------
    // code for loading sprites and images
    // -----------------------------------------------------------

    /**
     * loadTileImages
     * 
     * Loads and array of tiles
     */
    public void loadTileImages() {
        // keep looking for tile A,B,C, etc. this makes it
        // easy to drop new tiles in the images/ directory
        ArrTiles = new ArrayList();
        char cChar = 'A';
        while (true) {
            String sName = "tile_" + cChar + ".png";
            File filFile = new File("images/" + sName);
            if (!filFile.exists()) {
                break;
            }
            ArrTiles.add(loadImage(sName));
            cChar++;
        }
    }

    /**
     * loadCreatureSprites
     * 
     * loads the sprites of the creatures used in the map
     */
    public void loadCreatureSprites() {
        
        // Loads the default animation in case no animation of a creature is used
        aniDefaultAnim = new Animation();
        aniDefaultAnim.addFrame(loadImage("PC_1.png"), 1000);

        Image[][] imaMatImages = new Image[4][];
        
        int iHeight = TileMapRenderer.tilesToPixels(2);
        
        // load left-facing images
        imaMatImages[0] = new Image[] {
            getMirrorImage(loadImage("PC_1.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PC_2.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PC_3.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PC_4.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PC_5.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PC_6.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PP_1.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PP_2.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PP_3.png")),//.getScaledInstance(-1, iHeight, 0),
            getMirrorImage(loadImage("PP_4.png")),//.getScaledInstance(-1, iHeight, 0),
            loadImage("Murcielago1.png"),
            loadImage("Murcielago2.png"),
            loadImage("Murcielago1.png"),
            loadImage("Malo_espada_caminando_1.png"),
            loadImage("Malo_espada_caminando_2.png"),
            loadImage("Malo_espada_caminando_3.png"),
            loadImage("Malo_espada_caminando_4.png"),
            loadImage("Malo_espada_caminando_5.png"),
            loadImage("Malo_espada_caminando_6.png"),
            loadImage("MA_1.png"),
            loadImage("MA_2.png"),
            loadImage("MA_3.png"),
            loadImage("MA_4.png"),
            loadImage("MA_5.png"),
            loadImage("MA_6.png"),
            getMirrorImage(loadImage("flecha.png")),
            loadImage("flecha.png"),
            loadImage("flecha.png"),
            loadImage("rey_caminando_1.png"),
            loadImage("rey_caminando_2.png"),
            loadImage("rey_caminando_3.png"),
            loadImage("rey_caminando_4.png"),
            loadImage("rey_caminando_5.png"),
            loadImage("rey_caminando_6.png"),
        };

        imaMatImages[1] = new Image[imaMatImages[0].length];
        imaMatImages[2] = new Image[imaMatImages[0].length];
        imaMatImages[3] = new Image[imaMatImages[0].length];
        for (int i=0; i<imaMatImages[0].length; i++) {
            // right-facing images
            imaMatImages[1][i] = getMirrorImage(imaMatImages[0][i]);
            // left-facing "dead" images
            imaMatImages[2][i] = getFlippedImage(imaMatImages[0][i]);
            // right-facing "dead" images
            imaMatImages[3][i] = getFlippedImage(imaMatImages[1][i]);
        }

        // create creature animations
        Animation[] aniArrPlayerCaminAnim = new Animation[4];
        Animation[] aniArrPlayerIdleAnim = new Animation[4];
        Animation[] aniArrFlyAnim = new Animation[4];
        Animation[] aniArrGrubAnim = new Animation[4];
        Animation[] aniArrArrowAnim = new Animation[4];
        Animation[] aniArrArcherAnim = new Animation[4];
        Animation[] aniArrBossAnim = new Animation[4];
        
       for (int iI=0; iI<4; iI++) {
            aniArrPlayerCaminAnim[iI] = createPlayerAnim(imaMatImages[iI][0], 
                    imaMatImages[iI][1], imaMatImages[iI][2],imaMatImages[iI][3] ,
                    imaMatImages[iI][4],imaMatImages[iI][5]);
            aniArrPlayerIdleAnim[iI] = createPlayerAnim(imaMatImages[iI][6], 
                    imaMatImages[iI][7], imaMatImages[iI][8],imaMatImages[iI][9] ,
                    imaMatImages[iI][6],imaMatImages[iI][7]);
            aniArrFlyAnim[iI] = createFlyAnim(imaMatImages[iI][10], 
                    imaMatImages[iI][11], imaMatImages[iI][12]);
            aniArrGrubAnim[iI] = createGrubAnim(imaMatImages[iI][13], 
                    imaMatImages[iI][14],imaMatImages[iI][15],
                    imaMatImages[iI][16],imaMatImages[iI][17],
                    imaMatImages[iI][18]);
            aniArrArcherAnim[iI] = createGrubAnim(imaMatImages[iI][19], 
                    imaMatImages[iI][20],imaMatImages[iI][21],
                    imaMatImages[iI][22],imaMatImages[iI][23],
                    imaMatImages[iI][24]);
            aniArrArrowAnim[iI] = createWeaponAnim(imaMatImages[iI][25], 
                    imaMatImages[iI][25], imaMatImages[iI][25]);
            aniArrBossAnim[iI] = createGrubAnim(imaMatImages[iI][29], 
                    imaMatImages[iI][30],imaMatImages[iI][31],
                    imaMatImages[iI][32],imaMatImages[iI][33],
                    imaMatImages[iI][34]);
        }

        // create creature sprites
        sprPlayerSprite = new Player(aniArrPlayerCaminAnim[0], aniArrPlayerCaminAnim[1],
                aniArrPlayerCaminAnim[2], aniArrPlayerCaminAnim[3],
                aniArrPlayerIdleAnim[0], aniArrPlayerIdleAnim[1]);
        sprFlySprite = new Fly(aniArrFlyAnim[0], aniArrFlyAnim[1],
                aniArrFlyAnim[2], aniArrFlyAnim[3], aniArrFlyAnim[0], 
                aniArrFlyAnim[1]);
        sprGrubSprite = new Grub(aniArrGrubAnim[0], aniArrGrubAnim[1],
                aniArrGrubAnim[2], aniArrGrubAnim[3], aniArrGrubAnim[0], 
                aniArrGrubAnim[1]);
        sprArcherSprite = new Archer(aniArrArcherAnim[0], aniArrArcherAnim[1],
                aniArrArcherAnim[2], aniArrArcherAnim[3], aniArrArcherAnim[0], 
                aniArrArcherAnim[1]);
        sprArrowSprite = new Weapon(aniArrArrowAnim[0], aniArrArrowAnim[1], 
                aniArrArrowAnim[2], aniArrArrowAnim[0], aniArrArrowAnim[0], 
                aniArrArrowAnim[1]);
        sprBossSprite = new Boss(aniArrBossAnim[0], aniArrBossAnim[1],
                aniArrBossAnim[2], aniArrBossAnim[3], aniArrBossAnim[0], 
                aniArrBossAnim[1]);

    }

    /**
     * createPlayerAnim
     * 
     * Creates the player animation
     * 
     * @param imaPlayer1 is an object of class <code>Image</code>
     * @param imaPlayer2 is an object of class <code>Image</code>
     * @param imaPlayer3 is an object of class <code>Image</code>
     * @return object of class <code>Animation</code>
     */
    private Animation createPlayerAnim(Image imaPlayer1,
        Image imaPlayer2, Image imaPlayer3,Image imaPlayer4,Image imaPlayer5, Image imaPlayer6)
    {
        Animation aniAnim = new Animation();
        aniAnim.addFrame(imaPlayer1, 250);
        aniAnim.addFrame(imaPlayer2, 150);
        aniAnim.addFrame(imaPlayer3, 150);
        aniAnim.addFrame(imaPlayer4, 150);
        aniAnim.addFrame(imaPlayer5, 200);
        aniAnim.addFrame(imaPlayer6, 150);
        return aniAnim;
    }
    
     /**
     * createMenuAnim
     *
     * Creates the Menu animation
     *
     * @param imaImg1 is an object of class <code>Image</code>
     * @return object of class <code>Animation</code>
     */
    private Animation createMenuAnim(Image imaImg1) {
        Animation aniAnim = new Animation();
        aniAnim.addFrame(imaImg1, 50);
        return aniAnim;
    }
    
    /**
     * createWeaponAnim
     *
     * Creates the weapon animation
     *
     * @param imaImg1 is an object of class <code>Image</code>
     * @param imaImg2 is an object of class <code>Image</code>
     * @param imaImg3 is an object of class <code>Image</code>
     * @return object of class <code>Animation</code>
     */
    private Animation createWeaponAnim(Image imaImg1, Image imaImg2,
                                       Image imaImg3) {
        Animation aniAnim = new Animation();
        aniAnim.addFrame(imaImg1, 50);
        aniAnim.addFrame(imaImg2, 50);
        aniAnim.addFrame(imaImg3, 50);
        aniAnim.addFrame(imaImg2, 50);
        return aniAnim;
    }

    /**
     * createFlyAnim
     * 
     * Creates the animation for creatures that fly
     * 
     * @param imaImg1 is an object of class <code>Image</code>
     * @param imaImg2 is an object of class <code>Image</code>
     * @param imaImg3 is an object of class <code>Image</code>
     * @return object of class <code>Animation</code>
     */
    private Animation createFlyAnim(Image imaImg1, Image imaImg2,
        Image imaImg3)
    {
        Animation aniAnim = new Animation();
        aniAnim.addFrame(imaImg1, 50);
        aniAnim.addFrame(imaImg2, 50);
        aniAnim.addFrame(imaImg3, 50);
        aniAnim.addFrame(imaImg2, 50);
        return aniAnim;
    }

    /**
     * createGrubAnim
     * 
     * Creates the animation for creatures that are on the ground
     * 
     * @param imaImg1 is an object of class <code>Image</code>
     * @param imaImg2 is an object of class <code>Image</code>
     * @param imaImg3 is an object of class <code>Image</code>
     * @return object of class <code>Animation</code>
     */
    private Animation createGrubAnim(Image imaImg1, Image imaImg2, 
            Image imaImg3,Image imaImg4,Image imaImg5,Image imaImg6) {
        Animation aniAnim = new Animation();
        aniAnim.addFrame(imaImg1, 250);
        aniAnim.addFrame(imaImg2, 150);
        aniAnim.addFrame(imaImg3, 150);
        aniAnim.addFrame(imaImg4, 150);
        aniAnim.addFrame(imaImg5, 200);
        aniAnim.addFrame(imaImg6, 150);
        return aniAnim;
    }

    /**
     * loadPowerUpSprites
     * 
     * Loads the power up
     */
    private void loadPowerUpSprites() {
        // create "goal" sprite
        Animation aniAnim = new Animation();
        aniAnim.addFrame(loadImage("heart1.png"), 150);
        aniAnim.addFrame(loadImage("heart2.png"), 150);
        aniAnim.addFrame(loadImage("heart3.png"), 150);
        aniAnim.addFrame(loadImage("heart2.png"), 150);
        sprGoalSprite = new PowerUp.Goal(aniAnim);
        
        aniAnim = new Animation();
        aniAnim.addFrame(loadImage("Agua1.png"), 150);
        aniAnim.addFrame(loadImage("Agua2.png"), 150);
        aniAnim.addFrame(loadImage("Agua3.png"), 150);
        aniAnim.addFrame(loadImage("Agua2.png"), 150);
        sprAguaSprite = new PowerUp.Goal(aniAnim);

        // create "star" sprite
        aniAnim = new Animation();
        aniAnim.addFrame(loadImage("star1.png"), 100);
        aniAnim.addFrame(loadImage("star2.png"), 100);
        aniAnim.addFrame(loadImage("star3.png"), 100);
        aniAnim.addFrame(loadImage("star4.png"), 100);
        sprCoinSprite = new PowerUp.Star(aniAnim);
        
        aniAnim = new Animation();
        aniAnim.addFrame(loadImage("Picos_2.png"), 100);
        aniAnim.addFrame(loadImage("Picos_2.png"), 100);
        aniAnim.addFrame(loadImage("Picos_2.png"), 100);
        aniAnim.addFrame(loadImage("Picos_2.png"), 100);
        sprPicosSprite = new PowerUp.Star(aniAnim);

        // create "music" sprite
        aniAnim = new Animation();
        aniAnim.addFrame(loadImage("music1.png"), 150);
        aniAnim.addFrame(loadImage("music2.png"), 150);
        aniAnim.addFrame(loadImage("music3.png"), 150);
        aniAnim.addFrame(loadImage("music2.png"), 150);
        sprMusicSprite = new PowerUp.Music(aniAnim);
    }
    
    /**
     * spawnArrow
     * 
     * Spawns an arrow in a Map with a specific position and velocity.
     * 
     * @param fPosX is an object of class <code>float</code> that represents the position of the arrow in the X axis
     * @param fPosY is an object of class <code>float</code> that represents the position of the arrow in the Y axis
     * @param fVelX is an object of class <code>float</code> that represents the velocity of the arrow in the X axis
     * @param fVelY is an object of class <code>float</code> that represents the velocity of the arrow in the Y axis
     */
    public void spawnArrow(float fPosX, float fPosY, float fVelX, float fVelY, TileMap tmMap) {
        Weapon weaSpawnSprite;
        weaSpawnSprite = (Weapon)sprArrowSprite.clone();
        
        weaSpawnSprite.setX(fPosX);
        weaSpawnSprite.setY(fPosY);
        weaSpawnSprite.setVelocityX(fVelX);
        weaSpawnSprite.setVelocityY(fVelY);
        
        tmMap.addSprite(weaSpawnSprite);
    }
    /**
     * spawnArrow
     * 
     * Spawns a menu in a Map with a specific position.
     * 
     * @param fPosX is an object of class <code>float</code> that represents the position of the arrow in the X axis
     * @param fPosY is an object of class <code>float</code> that represents the position of the arrow in the Y axis
     */
      public void spawnMenu(float fPosX, float fPosY, TileMap tmMap, boolean bPausaMenu) {
        
        if(!bPausaMenu) {
        
        sprPauseMenu.setX(fPosX);
        sprPauseMenu.setY(fPosY);
       
        
        tmMap.addSprite(sprPauseMenu);
         
        }
        else{
            tmMap.removeSprite(sprPauseMenu);
        }
    }

}
