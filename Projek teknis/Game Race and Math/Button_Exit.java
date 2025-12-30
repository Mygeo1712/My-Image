import greenfoot.*;

public class Button_Exit extends Actor {
    public Button_Exit() {
        // Set gambar tombol dengan gambar Setting.png
        setImage("Button_Exit.png");
    }

    public void act() {
        // Deteksi klik mouse pada tombol
        if (Greenfoot.mouseClicked(this)) {
            // Pindah ke SettingWorld
            Greenfoot.setWorld(new HomeWorld());
        }
    }
}