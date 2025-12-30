import greenfoot.*;

public class Button_Setting extends Actor {
    public Button_Setting() {
        // Set gambar tombol dengan gambar Setting.png
        setImage("Button_Setting.png");
    }

    public void act() {
        // Deteksi klik mouse pada tombol
        if (Greenfoot.mouseClicked(this)) {
            // Pindah ke SettingWorld
            Greenfoot.setWorld(new SettingWorld());
        }
    }
}
