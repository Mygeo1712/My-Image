import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * PouseWorld class.
 */
public class PouseWorld extends World
{
    private World previousWorld; // Menyimpan referensi world sebelumnya

    public PouseWorld(World previousWorld)
    {    
        super(800, 600, 1); // Ukuran world
         this.previousWorld = previousWorld; // Simpan referensi ke world sebelumnya
           SettingWorld settingWorld = new SettingWorld(this); // Mengirimkan HomeWorld sebagai previousWorld
            
        prepare();
    }

    private void prepare()
    {
        Button_Exit button_Exit = new Button_Exit();
        addObject(button_Exit, 675, 504);

        Button_Back button_Back = new Button_Back(previousWorld);
        addObject(button_Back, 101, 510);

        Button_Setting button_Setting = new Button_Setting();
        addObject(button_Setting, 538, 285);

        Button_Menu button_Menu = new Button_Menu();
        addObject(button_Menu, 268, 285);
    }
}
