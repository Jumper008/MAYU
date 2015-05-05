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
import java.util.Calendar;
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
    private GameAction gaEnter;
    private GameAction gaPlay;
    private GameAction gaControls;
    private GameAction gaOptions;
    private GameAction gaReturn;
    
    private float fInitialJumpY;    // States from where the character started to jump
    
    private int iLife;
    private int iScore;
    private boolean bPause;
    private boolean bArrowAvailable;
    private boolean bMenu;
    private Sequence seqSequence1;
    private Sequence seqSequence;
    private Sequence seqSequence2;
    private long lTimer;
    
    // Sounds
    private Sound souPrizeSound;
    private Sound souBoopSound;
    private Sound souPause;
    private Sound souUnpause;
    private Sound souMenuSelect;
    private Sound souPlayerShoot;
    private Sound souPlayerHurt;    // Sin implementar (necesita más tiempo entre el daño que puede recibir un jugador)
    private Sound souArrowHit;
    private Sound souEnemyDeath;
    private Sound souBossImmune;
    
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
        lklBackgrounds.add(rmResourceManager.loadImage("Logo1.jpg"));
        lklBackgrounds.add(rmResourceManager.loadImage("Intento5.jpg"));
        lklBackgrounds.add(rmResourceManager.loadImage("HP_1.jpg"));
        lklBackgrounds.add(rmResourceManager.loadImage("escritorio1.jpg"));
        lklBackgrounds.add(rmResourceManager.loadImage("controles1.jpg"));
            // First map
        lklBackgrounds.add(rmResourceManager.loadImage("Fondo_Villa_2_Negro.jpg"));
            // Second map
        lklBackgrounds.add(rmResourceManager.loadImage("background.png"));
            // Third map
        lklBackgrounds.add(rmResourceManager.loadImage("background.png"));
            // Fourth map
        lklBackgrounds.add(rmResourceManager.loadImage("first.jpg"));
            //GameOver map
        lklBackgrounds.add(rmResourceManager.loadImage("Game Over1.jpg"));
        
        
        tmrRenderer.setBackground(lklBackgrounds.get(0));
        
        // Controls
        fInitialJumpY = 0;
        
        // load first map
        tmMap = rmResourceManager.loadNextMap();

        // load sounds
        smSoundManager = new SoundManager(afPLAYBACK_FORMAT);
        souPrizeSound = smSoundManager.getSound("sounds/prize.wav");
        souBoopSound = smSoundManager.getSound("sounds/boop2.wav");
        souPause = smSoundManager.getSound("sounds/pause_on.wav");
        souUnpause = smSoundManager.getSound("sounds/pause_off.wav");
        souEnemyDeath = smSoundManager.getSound("sounds/enemy_death.wav");
        souPlayerShoot = smSoundManager.getSound("sounds/player_shoot.wav");
        souMenuSelect = smSoundManager.getSound("sounds/menu_select.wav");
        souArrowHit = smSoundManager.getSound("sounds/arrow_hit2.wav");
        souBossImmune = smSoundManager.getSound("sounds/boss_invinsible.wav");

        // start music
        mpMidiPlayer = new MidiPlayer();
        seqSequence =
            mpMidiPlayer.getSequence("sounds/Main_menu1.mid");
        mpMidiPlayer.play(seqSequence, true);
        
        seqSequence1 =
            mpMidiPlayer.getSequence("sounds/Village2.mid");
        
        seqSequence2 =
            mpMidiPlayer.getSequence("sounds/GameOver.mid");
        //Pause
        bPause = false;
        
        //Arrow
        bArrowAvailable = true;
        
        //Timer
        lTimer = 0;
        
        //Menu
        bMenu = false;
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
        gaEnter = new GameAction("Enter", 
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaPlay = new GameAction("Play", 
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaOptions = new GameAction("Options", 
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaControls = new GameAction("Controls", 
            GameAction.iDETECT_INITAL_PRESS_ONLY);
        gaReturn = new GameAction("Return", 
            GameAction.iDETECT_INITAL_PRESS_ONLY);

        imInputManager = new InputManager(
            smScreen.getFullScreenWindow());
        imInputManager.setCursor(InputManager.curINVISIBLE_CURSOR);

        imInputManager.mapToKey(gaMoveLeft, KeyEvent.VK_LEFT);
        imInputManager.mapToKey(gaMoveRight, KeyEvent.VK_RIGHT);
        imInputManager.mapToKey(gaJump, KeyEvent.VK_SPACE);
        imInputManager.mapToKey(gaAttack, KeyEvent.VK_X);
        imInputManager.mapToKey(gaExit, KeyEvent.VK_ESCAPE);
        imInputManager.mapToKey(gaPause, KeyEvent.VK_P);
        imInputManager.mapToKey(gaResume, KeyEvent.VK_R);
        imInputManager.mapToKey(gaWakeUp, KeyEvent.VK_W);
        imInputManager.mapToKey(gaExitGame, KeyEvent.VK_Q);
        imInputManager.mapToKey(gaEnter, KeyEvent.VK_ENTER);
        imInputManager.mapToKey(gaPlay, KeyEvent.VK_J);
        imInputManager.mapToKey(gaOptions, KeyEvent.VK_O);
        imInputManager.mapToKey(gaControls, KeyEvent.VK_C);
        imInputManager.mapToKey(gaReturn, KeyEvent.VK_BACK_SPACE);
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
       
        // Checks if exit within pause menu is pressed
        if(gaExitGame.isPressed() && bPause) {
            smSoundManager.play(souMenuSelect);
            stop();
        }
        
        
        if(iLife == 0){
             mpMidiPlayer.play(seqSequence2, true);
            if (gaEnter.isPressed()) {
                        smSoundManager.play(souMenuSelect);
                        iLife = 2;
                        mpMidiPlayer.play(seqSequence, true);
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
        }
        // Checks if wake up button within pause menu is pressed
        if(gaWakeUp.isPressed() && bPause) {
            smSoundManager.play(souMenuSelect);
            iLife = 2;
            bPause = false;
            rmResourceManager.iCurrentMap = 2;
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
                // Logo preview
                case 0: {
                    lTimer ++;
                    if (lTimer == 500) {
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                // Main menu with game name
                case 1: {
                    if (gaEnter.isPressed()) {
                        smSoundManager.play(souMenuSelect);
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                       
                    }
                    break;
                }
                // Main menu
                case 2: {
                    if (gaPlay.isPressed() && iLife != 0) {
                        smSoundManager.play(souMenuSelect);
                       // mpMidiPlayer.close();
                        mpMidiPlayer.play(seqSequence1, true);
                        rmResourceManager.iCurrentMap = 5;
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    if (gaOptions.isPressed()) {
                        smSoundManager.play(souMenuSelect);
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                // Settings
                case 3: {
                    if (gaControls.isPressed()) {
                        smSoundManager.play(souMenuSelect);
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    if (gaReturn.isPressed()) {
                        rmResourceManager.iCurrentMap = 2;
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                // Controls
                case 4: {
                    if (gaReturn.isPressed()) {
                        rmResourceManager.iCurrentMap = 3;
                        tmrRenderer.setBackground(lklBackgrounds.get
                                (rmResourceManager.getICurrentMap()));
                        tmMap = rmResourceManager.loadNextMap();
                    }
                    break;
                }
                
                case 5: {
                    
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
                    
                    if ( gaAttack.isPressed() ) {
                        if ( !bArrowAvailable ) {   // Check if the player can shoot again after waiting
                            int iTimeToWait = 500;
                            
                            if ( plaPlayer.getShootTime().getTimeInMillis()
                                    + iTimeToWait
                                    < Calendar.getInstance().getTimeInMillis() ) {
                                bArrowAvailable = true;
                            }
                        } 
                        
                        if ( bArrowAvailable ) {
                            // Shoot Arrows
                            smSoundManager.play(souPlayerShoot);

                            bArrowAvailable = false;
                            plaPlayer.updateShootTime();

                            if (plaPlayer.getFacingRight()) { // To the right
                                rmResourceManager.spawnArrow(
                                        plaPlayer.getX() + TileMapRenderer.tilesToPixels(1) + 15f, 
                                        plaPlayer.getY() + TileMapRenderer.tilesToPixels(1), 
                                        1f, -.2f, tmMap);
                            }
                            else {
                                rmResourceManager.spawnArrow( // To the left
                                        plaPlayer.getX() - TileMapRenderer.tilesToPixels(1) - 15f, 
                                        plaPlayer.getY() + TileMapRenderer.tilesToPixels(1), 
                                        -1f, -.2f, tmMap);
                            }
                        } 
                    }
                    
                    // Checks if pause button is pressed
                    if (gaPause.isPressed()) {
                        
                        if(!bPause) {
                            smSoundManager.play(souPause);
                            bPause = true;
                        }
                        
                        else {
                            smSoundManager.play(souUnpause);
                            bPause = false;
                        }
                    }
                    
                    plaPlayer.setVelocityX(velocityX);
                }
                break;
            }
        }
            else {
            // Checks if enter has been pressed within gameover menu
            ///if( rmResourceManager.iCurrentMap == 9 ) {
            if ( gaEnter.isPressed() ) {    
                System.out.println( "HELLO" );
                rmResourceManager.iCurrentMap = 1;
                tmrRenderer.setBackground(
                        lklBackgrounds.get(rmResourceManager.getICurrentMap()));
                tmMap = rmResourceManager.loadNextMap();
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
        
         gra2D_G.setColor(Color.black);
        
        if(bPause) {
            gra2D_G.setColor(Color.decode("#A3A375"));
            gra2D_G.fill3DRect((smScreen.getWidth()/2) - 180, 180, 350, 250, bPause);
            gra2D_G.setColor(Color.black);
            gra2D_G.drawString("Pause", (smScreen.getWidth()/2) - 50, 240);
            gra2D_G.drawString("Resume Game - P", (smScreen.getWidth()/2) - 100, 300);
            gra2D_G.drawString("Wake up - W", (smScreen.getWidth()/2) - 70, 340);
            gra2D_G.drawString("Quit Game - Q", (smScreen.getWidth()/2) - 80, 380);
            gra2D_G.draw3DRect((smScreen.getWidth()/2) - 180, 180, 350, 250, bPause);
            
            
        }
        //play
        if(rmResourceManager.iCurrentMap == 3){
           gra2D_G.setColor(Color.black);
           gra2D_G.drawString("Play - J", (smScreen.getWidth()/2) - 50, 270);
           gra2D_G.drawString("Options - O", (smScreen.getWidth()/2) + 210,(smScreen.getHeight()/2) + 50);
           
        }
        //Desk
        if(rmResourceManager.iCurrentMap == 4){
           gra2D_G.setColor(Color.black);
           gra2D_G.drawString("Controls - C",600, 130);
           gra2D_G.drawString("Return - Back space",30 ,580 );
        }
        //Computer/controles
        if(rmResourceManager.iCurrentMap == 5){
           gra2D_G.setColor(Color.black);
           gra2D_G.drawString("Return - Back space",10 ,30 ); 
        }
        
        
         //Update life and score
        if(rmResourceManager.iCurrentMap > 5){
         gra2D_G.drawString("Score: " + iScore, 10, 20);
         gra2D_G.drawString("Life: " + iLife, 10, 45);
         Player plaPlayer = (Player) tmMap.getPlayer();
         gra2D_G.drawString("Health: " + plaPlayer.getHealth(), 10, 75);
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
                                    TileMapRenderer.tilesToPixels(iY) + 5 
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
        
        if(iLife == 0){     // Send to game over screen
            rmResourceManager.iCurrentMap = 9;
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
                    
                    //Show dying animation if creature has run out of health
                    if (creature.getHealth() == 0) {
                        smSoundManager.play(souEnemyDeath);
                        creature.setState(Creature.iSTATE_DYING);
                        creature.setHealth(-1);
                    }
                    
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
            
            if (fDy > 0) {
                creCreature.setY(TileMapRenderer.tilesToPixels(pTile.y) -
                    creCreature.getHeight());
            }
            else if (fDy < 0) {
                creCreature.setY( TileMapRenderer.tilesToPixels(pTile.y + 1)
                    + ( TileMapRenderer.tilesToPixels(2) - creCreature.getHeight() ) );
            }
            
            creCreature.collideVertical();
        }
        
        //Check for collisions
            //Arrow collides with wall
        if (creCreature instanceof Weapon) {
            
            // Check if arrow collided with an enemy
            Sprite sprCollision = getSpriteCollision( creCreature );
            if ( sprCollision != null ) {
                if (sprCollision instanceof Creature) {
                    smSoundManager.play(souArrowHit);
                    Creature creAux = (Creature)sprCollision;
                    creAux.setHealth(creAux.getHealth() - 1);
                    creCreature.setVelocityX(0f);
                }
                else {
                    if ( !(sprCollision instanceof PowerUp) ) {
                        tmMap.removeSprite(sprCollision);
                    }
                    smSoundManager.play(souBossImmune);
                    creCreature.setVelocityX(0f);
                }
            }
            
            // Check if arrow stopped
            if (creCreature.getVelocityX() == 0 || 
                    getTileCollision(creCreature, 
                            creCreature.getX() + creCreature.getVelocityX(),
                            creCreature.getY() + creCreature.getVelocityY()) != null) { //Eliminate arrows once they have stopped
                creCreature.setState(Weapon.iSTATE_DYING); 
            }
        }
        
        if (creCreature instanceof Player) {
            boolean bCanKill = (fOldY < creCreature.getY());
            checkPlayerCollision((Player)creCreature, bCanKill);
        }
        
        // Check if creature fell to its demise
        if (creCreature.getY() > TileMapRenderer.tilesToPixels( tmMap.getHeight()) 
                && creCreature.getState() == Creature.iSTATE_NORMAL) {
            
            creCreature.setState(Creature.iSTATE_DYING);
            creCreature.setHealth(0);
            
            if ( creCreature instanceof Player ) {
                iLife -= 1;
            }
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
            
            // Restart player shooting when a powerUp is acquired (goal, for instance)
            plaPlayer.updateShootTime();
            bArrowAvailable = true;
        }
        else if (sprCollisionSprite instanceof Creature) {
            Creature creBadguy = (Creature)sprCollisionSprite;
            if (bCanKill) {
//                // Subtract health points from the badguy and make player bounce
//                smSoundManager.play(souBoopSound);
//                creBadguy.setHealth(creBadguy.getHealth() - 1);
//                plaPlayer.setY(creBadguy.getY() - plaPlayer.getHeight());
//                plaPlayer.jump(true);
//                iScore += 10;
            }
            else {
                // player gets hurt!
                if(iLife > 1){
                    plaPlayer.setHealth(plaPlayer.getHealth() - 1);
                    if (plaPlayer.getHealth() < 1) {
                        plaPlayer.setState(Creature.iSTATE_DYING);
                        iLife -= 1;
                    }
                }
                else{
                  plaPlayer.setHealth(plaPlayer.getHealth() - 1);
                    if (plaPlayer.getHealth() < 1) {
                        plaPlayer.setState(Creature.iSTATE_DYING);
                        iLife -= 1;  
                    }
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
        // remove it from the map (not implemented, yet)
        //tmMap.removeSprite(puPowerUp);

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