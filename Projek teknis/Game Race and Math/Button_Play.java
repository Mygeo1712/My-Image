import greenfoot.*;

public class Button_Play extends Actor {
    public Button_Play() {
        // Set gambar tombol dengan gambar Setting.png
        setImage("Button_Play.png");
    }

    public void act() {
        // Deteksi klik mouse pada tombol
        if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(new Level1World());
        }

    }
}
