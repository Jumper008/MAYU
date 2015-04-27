package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;
import java.util.LinkedList;

/**
 * GameManager
 * 
 * It manages the definition of each object of type <code>GameManager</code>
 * 
 * GameManager manages all parts of the game.
 * 
 * It extends GameCore
 * 
 * @author Quazar Volume
 */
public class GameManager extends GameCore {

    /**
     * main
     * 
     * Runs the game at start
     * 
     * @param sArrArgs is an object of class <code>String</code>
     */
    public static void main(String[] sArrArgs) {
        new GameManager().run();
    }

    // uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
    private static final AudioFormat afPLAYBACK_FORMAT =
        new AudioFormat(44100, 16, 1, true, false);

    private static final int iDRUM_TRACK = 1;

    // Gravity pull
    public static final float fGRAVITY = 0.0013f;

    // Game objects
    private Point pPointCache = new Point();
    private TileMap tmMap;
    private MidiPlayer mpMidiPlayer;
    private SoundManager smSoundManager;
    private ResourceManager rmResourceManager;
    private Sound souPrizeSound;
    private Sound souBoopSound;
    private InputManager imInputManager;
    private TileMapRenderer tmrRenderer;
    private LinkedList<Image> lklBackgrounds;

    private GameAction gaMoveLeft;
    private GameAction gaMoveRight;
    private GameAction gaJump;
    private GameAction gaAttack;
    private GameAction gaExit;
    private GameAction gaJumpRelease;
    private GameAction gaPause;
    private GameAction gaResume;
    private GameAction gaWakeUp;
    private GameAction gaExitGame;
    
    private float fInitialJumpY;    // States from where the character started to jump
    
    private int iLife;
    private int iScore;
    private boolean bPause;
    private boolean bPausaMenu;
    //private Sprite SprLife;
    //private LinkedList<Sprite> lklLife;
    

    /**
     * init
     * 
     * Sets up the game
     */
    public void init() {
        super.init();

        // set up input manager
        initInput();

        // start resource manager
        rmResourceManager = new ResourceManager(
        smScreen.getFullScreenWindow().getGraphicsConfiguration());

        // load resources
        tmrRenderer = new TileMapRenderer();
        //vidas int
        iLife = 2;
        //Score
        iScore = 0;
        
        lklBackgrounds = new LinkedList();
        
        // load map images in order
            // Main menu
        lklBackgrounds.add(rmResourceManager.loadImage("Habitacion.jpg"));
        lklBackgrounds.add(rmResourceManager.loadImage("Escritorio.jpg"));
        lklBackgrounds.add(rmResourceManager.loadImage("Controles.jpg"));
            // First map
        lklBackgrounds.add(rmResourceManager.loadImage("Fondo_Villa.jpg"));
            // Second map
        lklBackgrounds.add(rmResourceManager.loadImage("background.png"));
            // Third map
        lklBackgrounds.add(rmResourceManager.loadImage("background.png"));
            // Fourth map
        lklBackgrounds.add(rmResourceManager.loadImage("first.jpg"));
            //GameOver map
        lklBackgrounds.add(rmResourceManager.loadImage("Game Over.jpg"));
        
        tmrRenderer.setBackground(lklBackgrounds.get(0));
        
            //Vidas
        for(int i=0; i<iLife ; i++){
            
        }
        
        // Controls
        fInitialJumpY = 0;
        
        // load first map
        tmMap = rmResourceManager.loadNextMap();

        // load sounds
        smSoundManager = new SoundManager(afPLAYBACK_FORMAT);
        souPrizeSound = smSoundManager.getSound("sounds/prize.wav");
        souBoopSound = smSoundManager.getSound("sounds/boop2.wav");

        // start music
        mpMidiPlayer = new MidiPlayer();
        Sequence seqSequence =
            mpMidiPlayer.getSequence("sounds/music.midi");
        mpMidiPlayer.play(seqSequence, true);
        toggleDrumPlayback();
        
        //Pause
        bPause = false;
        //Pausamenu
        bPausaMenu = false;
    }
    
    /**
     * stop
     * 
     * Closes any resources used by the GameManager.
     */
    public void stop() {
        super.stop();
        mpMidiPlayer.close();
        smSoundManager.close();
    }

    /**
     * initInput
     * 
     * Sets up certain game input configurations.
     */
    private void initInput() {
        gaMoveLeft = new GameAction("moveLeft");
        gaMoveRight = new GameAction("moveRight");
        gaJump = new GameAction("jump");/*,
            GameAction.iDETECT_INITAL_PRESS_ONLY);*/
        gaJumpRelease = new GameAction("jumpRelease", 
                GameAction.iDETECT_RELEASE_ONLY);
        gaAttack = new GameAction("attack",
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaExit = new GameAction("exit",
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaPause = new GameAction("Pause",
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaResume = new GameAction("Resume",
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaWakeUp = new GameAction("WakeUp",
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaExitGame = new GameAction("ExitGame",
            GameAction.iDETECT_INITAL_PRESS_ONLY);

        imInputManager = new InputManager(
            smScreen.getFullScreenWindow());
        imInputManager.setCursor(InputManager.curINVISIBLE_CURSOR);

        imInputManager.mapToKey(gaMoveLeft, KeyEvent.VK_LEFT);
        imInputManager.mapToKey(gaMoveRight, KeyEvent.VK_RIGHT);
        imInputManager.mapToKey(gaJump, KeyEvent.VK_SPACE);
        imInputManager.mapToKey(gaAttack, KeyEvent.VK_J);
        imInputManager.mapToKey(gaExit, KeyEvent.VK_ESCAPE);
        imInputManager.mapToKey(gaPause, KeyEvent.VK_P);
        imInputManager.mapToKey(gaResume, KeyEvent.VK_R);
        imInputManager.mapToKey(gaWakeUp, KeyEvent.VK_W);
        imInputManager.mapToKey(gaExitGame, KeyEvent.VK_Q);
    }

    /**
     * checkInput
     * 
     * Checks for user input.
     * 
     * @param elapsedTime is an object of class <code>Long</code>
     */
    private void checkInput(long elapsedTime) {
        Player plaPlayer = (Player)tmMap.getPlayer();
        // Checks if exit has been pressed
        if (gaExit.isPressed()) {
            stop();
        }
        if(gaExitGame.isPressed() && bPause){
            stop();
        }
        if (gaPause.isPressed() && rmResourceManager.iCurrentMap == 4 ){
            if(!bPause){
                bPause = true;
                rmResourceManager.spawnMenu(plaPlayer.getX()-160, 500, tmMap, false);
                
            }
            else{
                bPause = false;
                rmResourceManager.spawnMenu(plaPlayer.getX()-160, 500, tmMap, true);
            }
        }
        if(gaWakeUp.isPressed() && bPause){
            bPause = false;
            rmResourceManager.iCurrentMap = 0;
            tmrRenderer.setBackground(lklBackgrounds.get
                     (rmResourceManager.getICurrentMap()));
            tmMap = rmResourceManager.loadNextMap();
        }
        
        if(gaResume.isPressed() && bPause){
            bPause = false;
        }
        
        // Checks for player input
        if (plaPlayer.isAlive()) {
            float velocityX = 0;
            switch(rmResourceManager.getICurrentMap()-1) {
                // Main menu
                case 0: {
                    if (gaMoveLeft.isPressed() && iLife != 0) {
                        rmResourceManager.iCurrentMap = 3;
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    if (gaJump.isPressed()) {
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                // Settings
                case 1: {
                    if (gaJump.isPressed()) {
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                // Controls
                case 2: {
                    if (gaJump.isPressed()) {
                        rmResourceManager.iCurrentMap = 0;
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                case 3:{
                    
                }
                default: {
                    if (gaMoveLeft.isPressed()) {
                        velocityX-=plaPlayer.getMaxSpeed();
                        plaPlayer.setFacingRight(false);
                    }
                    
                    if (gaMoveRight.isPressed()) {
                        velocityX+=plaPlayer.getMaxSpeed();
                        plaPlayer.setFacingRight(true);
                    }
                    
                    if (gaJump.isPressed()) {
                        plaPlayer.jump(false);
                    } 
                    else { // the player released the jump key
                        plaPlayer.setJumpAccelHeightReached(true); // Prevent the player from jumping in mid-air
                    }
                    
                    if (gaAttack.isPressed() && !bPause) {
                        // Shoot Arrows
                        if (plaPlayer.getFacingRight()) { // To the right
                            rmResourceManager.spawnArrow(
                                    plaPlayer.getX() + TileMapRenderer.tilesToPixels(1) + 10f, 
                                    plaPlayer.getY(), 
                                    2f, -.4f, tmMap);
                        }
                        else {
                            rmResourceManager.spawnArrow( // To the left
                                    plaPlayer.getX() - TileMapRenderer.tilesToPixels(1) - 10f, 
                                    plaPlayer.getY(), 
                                    -2f, -.4f, tmMap);
                        }
                    }
                    
                    plaPlayer.setVelocityX(velocityX);
                }
                break;
            }
        }

    }

    /**
     * draw
     * 
     * Invokes renderer in order to draw game content
     * 
     * @param gra2D_G is an object of class <code>Graphics2D</code>
     */
    public void draw(Graphics2D gra2D_G) {
        tmrRenderer.draw(gra2D_G, tmMap,
            smScreen.getWidth(), smScreen.getHeight());
        
         //Update life and score
        if(rmResourceManager.iCurrentMap == 4){
         gra2D_G.drawString("Score: " + iScore, 10, 20);
         gra2D_G.drawString("Life: " + iLife, 10, 45);   
        }
        
        
        //JUST SOME TESTING. ERASE PLEASE.
            // Displays the amount of weapons supposedly on screen as well as the position of the newest weapon on screen
        Iterator itWeapons = tmMap.getSprites();
        int iCantWeapons = 0;
        float fPosX = 0;
        float fPosY = 0;
        while (itWeapons.hasNext()) {
            Sprite sprOtherSprite = (Sprite)itWeapons.next();
            if (sprOtherSprite instanceof Weapon) {
                iCantWeapons++;
                fPosX = sprOtherSprite.getX();
                fPosY = sprOtherSprite.getY();
            }
        }
        if(rmResourceManager.iCurrentMap == 4){
        gra2D_G.drawString("Weapons on screen: " + iCantWeapons, 10, 75);
        gra2D_G.drawString("Weapons X: " + fPosX, 10, 95);
        gra2D_G.drawString("Weapons Y" + fPosY, 10, 115);
        //END OF TESTING. ERASE PLEASE.
        }
    }
    
    /**
     * getMap
     * 
     * Gets the current map.
     * 
     * @return object of class <code>TileMap</code>
     */
    public TileMap getMap() {
        return tmMap;
    }
    
    /**
     * toggleDrumPlayback
     * 
     * Turns on/off drum playback in the midi music (track 1).
     */
    public void toggleDrumPlayback() {
        Sequencer seqSequencer = mpMidiPlayer.getSequencer();
        if (seqSequencer != null) {
            seqSequencer.setTrackMute(iDRUM_TRACK,
                !seqSequencer.getTrackMute(iDRUM_TRACK));
        }
    }
    
    /**
     * getTileCollision
     * 
     * Gets the tile that a Sprites collides with. Only the 
     * Sprite's X or Y should be changed, not both. Returns null 
     * if no collision is detected.
     * 
     * @param sprSprite is an object of class <code>Sprite</code>
     * @param fNewX is an object of class <code>Float</code>
     * @param fNewY is an object of class <code>Float</code>
     * @return object of class <code>Point</code>
     */
    public Point getTileCollision(Sprite sprSprite,
        float fNewX, float fNewY)
    {
        float fFromX = Math.min(sprSprite.getX(), fNewX);
        float fFromY = Math.min(sprSprite.getY(), fNewY);
        float fToX = Math.max(sprSprite.getX(), fNewX);
        float fToY = Math.max(sprSprite.getY(), fNewY);

        // get the tile locations
        int iFromTileX = TileMapRenderer.pixelsToTiles(fFromX);
        int iFromTileY = TileMapRenderer.pixelsToTiles(fFromY);
        int iToTileX = TileMapRenderer.pixelsToTiles(fToX + sprSprite.getWidth() - 1);
        int iToTileY = TileMapRenderer.pixelsToTiles(fToY + sprSprite.getHeight() - 1);

        // check each tile for a collision
        for (int iX = iFromTileX; iX <= iToTileX; iX++) {
            for (int iY = iFromTileY; iY <= iToTileY; iY++) {
                if (iX < 0 || iX >= tmMap.getWidth() ||
                        tmMap.getTile(iX, iY) != null) {

                    // collision found, return the tile
                    if ( iX < 0 || iX >= tmMap.getWidth() ) { // tile out of bounds
                        pPointCache.setLocation(iX, iY);
                        return pPointCache;
                    }
                    else {
                        if (!tmMap.getPlatform( iX, iY )) {  // solid tile
                            pPointCache.setLocation(iX, iY);
                            return pPointCache;
                        }
                        else { // platform (check collision only with top border)
                            if ( fNewY + sprSprite.getHeight() > 
                                    TileMapRenderer.tilesToPixels(iY) 
                                    &&
                                    fNewY + sprSprite.getHeight() < 
                                    TileMapRenderer.tilesToPixels(iY) + 20 
                                    ) {
                                if (sprSprite.getY() < fNewY) {
                                    pPointCache.setLocation(iX, iY); // stop the character on the platform
                                }
                                else {
                                    pPointCache.setLocation(iX,
                                            TileMapRenderer.pixelsToTiles(fNewY) - 1); // Put the player on top of the platform
                                }
                                /*pPointCache.setLocation(iX, 
                                        iY); // put the creature on top of the platform*/
                                return pPointCache;
                            }
                        }
                    }
                }
            }
        }

        // no collision found
        return null;
    }
    
    /**
     * isCollision
     * 
     * Checks if two Sprites collide with one another. Returns 
     * false if the two Sprites are the same. Returns false if 
     * one of the Sprites is a Creature that is not alive.
     * 
     * @param sprS1 is an object of class <code>Sprite</code>
     * @param sprS2 is an object of class <code>Sprite</code>
     * @return object of class <code>Boolean</code>
     */
    public boolean isCollision(Sprite sprS1, Sprite sprS2) {
        // if the Sprites are the same, return false
        if (sprS1 == sprS2) {
            return false;
        }

        // if one of the Sprites is a dead Creature, return false
        if (sprS1 instanceof Creature && !((Creature)sprS1).isAlive()) {
            return false;
        }
        if (sprS2 instanceof Creature && !((Creature)sprS2).isAlive()) {
            return false;
        }

        // get the pixel location of the Sprites
        int iS1x = Math.round(sprS1.getX());
        int iS1y = Math.round(sprS1.getY());
        int iS2x = Math.round(sprS2.getX());
        int iS2y = Math.round(sprS2.getY());

        // check if the two sprites' boundaries intersect
        return (iS1x < iS2x + sprS2.getWidth() &&
            iS2x < iS1x + sprS1.getWidth() &&
            iS1y < iS2y + sprS2.getHeight() &&
            iS2y < iS1y + sprS1.getHeight());
    }
    
    /**
     * getSpriteCollision
     * 
     * Gets the Sprite that collides with the specified Sprite, 
     * or null if no Sprite collides with the specified Sprite.
     * 
     * @param sprSprite is an object of class <code>Sprite</code>
     * @return object of class <code>Sprite</code>
     */
    public Sprite getSpriteCollision(Sprite sprSprite) {

        // run through the list of Sprites
        Iterator iteI = tmMap.getSprites();
        while (iteI.hasNext()) {
            Sprite sprOtherSprite = (Sprite)iteI.next();
            if (isCollision(sprSprite, sprOtherSprite)) {
                // collision found, return the Sprite
                return sprOtherSprite;
            }
        }

        // no collision found
        return null;
    }
    
    /**
     * update
     * 
     * Updates Animation, position, and velocity of all Sprites 
     * in the current map.
     * 
     * @param lElapsedTime is an object of class <code>Long</code>
     */
    public void update(long lElapsedTime) {
        Creature CrePlayer = (Creature)tmMap.getPlayer();

        // player is dead! start map over
        if (CrePlayer.getState() == Creature.iSTATE_DEAD && iLife != 0) {
             tmMap = rmResourceManager.reloadMap();
             return;   
        }
        
        if(iLife == 0){
            rmResourceManager.iCurrentMap = 7;
            tmrRenderer.setBackground(lklBackgrounds.get
                     (rmResourceManager.getICurrentMap()));
            tmMap = rmResourceManager.loadNextMap();
        }
        
        // get keyboard/mouse input
        checkInput(lElapsedTime);
        
        if(!bPause){
        // update player
        updateCreature(CrePlayer, lElapsedTime);
        CrePlayer.update(lElapsedTime);

        // update other sprites
        Iterator iteI = tmMap.getSprites();
        while (iteI.hasNext()) {
            Sprite sprite = (Sprite)iteI.next();
                if (sprite instanceof Creature) {
                    Creature creature = (Creature)sprite;
                    if (creature.getState() == Creature.iSTATE_DEAD) {
                        iteI.remove();
                    }
                    else {
                        updateCreature(creature, lElapsedTime);
                    }
                }
                // normal update
                sprite.update(lElapsedTime);
            }
        }
    }
    
    /**
     * updateCreature
     * 
     * Updates the creature, applying gravity for creatures that 
     * aren't flying, and checks collisions.
     * 
     * @param creCreature is an object of class <code>Creature</code>
     * @param lElapsedTime is an object of class <code>Long</code>
     */
    private void updateCreature(Creature creCreature,
        long lElapsedTime)
    {

        // apply gravity
        if (!creCreature.isFlying()) {
            creCreature.setVelocityY(creCreature.getVelocityY() +
                fGRAVITY * lElapsedTime);
        }

        // change x
        float fDx = creCreature.getVelocityX();
        float fOldX = creCreature.getX();
        float fNewX = fOldX + fDx * lElapsedTime;
        Point pTile =
            getTileCollision(creCreature, fNewX, creCreature.getY());
        if (pTile == null) {
            creCreature.setX(fNewX);
        }
        else {
            // line up with the tile boundary
            if (fDx > 0) {
                creCreature.setX(TileMapRenderer.tilesToPixels(pTile.x) -
                    creCreature.getWidth());
            }
            else if (fDx < 0) {
                creCreature.setX(TileMapRenderer.tilesToPixels(pTile.x + 1));
            }
            creCreature.collideHorizontal();
        }
        if (creCreature instanceof Player) {
            checkPlayerCollision((Player)creCreature, false);
        }

        // change y
        float fDy = creCreature.getVelocityY();
        float fOldY = creCreature.getY();
        float fNewY = fOldY + fDy * lElapsedTime;
        pTile = getTileCollision(creCreature, creCreature.getX(), fNewY);
        if (pTile == null) {
            creCreature.setY(fNewY);
        }
        else {
            // line up with the tile boundary and detect collision with roof
            
            boolean bCollidesTop = false;
            
            if (fDy > 0) {
                creCreature.setY(TileMapRenderer.tilesToPixels(pTile.y) -
                    creCreature.getHeight());
                bCollidesTop = false;
            }
            else if (fDy < 0) {
                creCreature.setY(TileMapRenderer.tilesToPixels(pTile.y + 1));
                bCollidesTop = true;
            }
            
            creCreature.collideVertical();
        }
        
        //Check for collisions
            //Arrow collides with wall
        if (creCreature instanceof Weapon) {
            
            // Check if arrow collided with an enemy
            Sprite sprCollision = getSpriteCollision( creCreature );
            if ( sprCollision != null ) {
                tmMap.removeSprite(sprCollision);
                creCreature.setVelocityX(0f);
            }
            
            // Check if arrow stopped
            if (creCreature.getVelocityX() == 0) { //Eliminate arrows once they have stopped
                creCreature.setState(Weapon.iSTATE_DEAD); 
            }
        }
        
        if (creCreature instanceof Player) {
            boolean bCanKill = (fOldY < creCreature.getY());
            checkPlayerCollision((Player)creCreature, bCanKill);
        }

    }
    
    /**
     * checkPlayerCollision
     * 
     * Checks for Player collision with other Sprites. If canKill is true, 
     * collisions with Creatures will kill them.
     * 
     * @param plaPlayer is an object of class <code>Player</code>
     * @param bCanKill is an object of class <code>Boolean</code>
     */
    public void checkPlayerCollision(Player plaPlayer,
        boolean bCanKill)
    {
        if (!plaPlayer.isAlive()) {
            return;
        }

        // check for player collision with other sprites
        Sprite sprCollisionSprite = getSpriteCollision(plaPlayer);
        if (sprCollisionSprite instanceof PowerUp) {
            acquirePowerUp((PowerUp)sprCollisionSprite);
        }
        else if (sprCollisionSprite instanceof Creature) {
            Creature creBadguy = (Creature)sprCollisionSprite;
            if (bCanKill) {
                // kill the badguy and make player bounce
                smSoundManager.play(souBoopSound);
                creBadguy.setState(Creature.iSTATE_DYING);
                plaPlayer.setY(creBadguy.getY() - plaPlayer.getHeight());
                plaPlayer.jump(true);
                iScore += 10;
            }
            else {
                // player dies!
                //plaPlayer.setState(Creature.iSTATE_DYING);
                if(iLife > 1){
                    iLife -= 1;
                    plaPlayer.setState(Creature.iSTATE_DYING);
                }
                else{
                  iLife -= 1;  
                  plaPlayer.setState(Creature.iSTATE_DYING);
                }
            }
        }
    }
    
    /**
     * acquirePowerUp
     * 
     * Gives the player the specified power up and removes it 
     * from the map.
     * 
     * @param puPowerUp is an object of class <code>PowerUp</code>
     */
    public void acquirePowerUp(PowerUp puPowerUp) {
        // remove it from the map
        tmMap.removeSprite(puPowerUp);

        if (puPowerUp instanceof PowerUp.Star) {
            // do something here, like give the player points
            smSoundManager.play(souPrizeSound);
        }
        else if (puPowerUp instanceof PowerUp.Music) {
            // Change the music
            smSoundManager.play(souPrizeSound);
            toggleDrumPlayback();
        }
        else if (puPowerUp instanceof PowerUp.Goal) {
            // Advance to next map and change background according to map
            smSoundManager.play(souPrizeSound,
                new EchoFilter(2000, .7f), false);
            switch(rmResourceManager.getICurrentMap()) {
                case 4: { // Map 5
                    tmrRenderer.setBackground(lklBackgrounds.get(4));
                    break;
                }
                case 5: { // Map 6
                    tmrRenderer.setBackground(lklBackgrounds.get(5));
                    break;
                }
                case 6: { // Map 7
                    tmrRenderer.setBackground(lklBackgrounds.get(6));
                    break;
                }
                default: {
                    break;
                }
            }
            tmMap = rmResourceManager.loadNextMap();
        }
    }

}