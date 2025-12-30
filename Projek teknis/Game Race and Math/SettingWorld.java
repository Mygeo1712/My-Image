         import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Write a description of class SettingWorld here.
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */
public class SettingWorld extends World
{
    private World previousWorld; 
     public SettingWorld(World previousWorld)
    {    
        super(800, 600, 1); 
        this.previousWorld = previousWorld; // Menyimpan world sebelumnya
        prepare();
    }
    /**
     * Constructor for objects of class SettingWorld.
     * 
     */
    public SettingWorld()
    {    
        // Create a new world with 600x400 cells with a cell size of 1x1 pixels.
        super(800, 600, 1); 
 
        prepare();
    }
    
    /**
     * Prepare the world for the start of the program.
     * That is: create the initial objects and add them to the world.
     */
    private void prepare()
    {
        Button_Setting button_Setting = new Button_Setting();
        addObject(button_Setting,392,40);

        Button_Music button_Music = new Button_Music();
        addObject(button_Music,590,167);

        Button_BackGround button_BackGround = new Button_BackGround();
        addObject(button_BackGround,207,167);

        Button_Exit button_Exit = new Button_Exit();
        addObject(button_Exit,698,562);
        
        Button_Back button_Back = new Button_Back(previousWorld);
        addObject(button_Back, 101, 562);

        Button_SetMusic button_SetMusic = new Button_SetMusic();
        addObject(button_SetMusic,563,235);

        Button_SetBackground button_SetBackground = new Button_SetBackground();
        addObject(button_SetBackground,176,231);

        Choose_Music choose_Music = new Choose_Music();
        addObject(choose_Music,632,380);

        Choose_BackGround choose_BackGround = new Choose_BackGround();
        addObject(choose_BackGround,249,375);
    }
}
