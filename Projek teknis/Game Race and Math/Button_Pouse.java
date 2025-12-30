import greenfoot.*;

public class Button_Pouse extends Actor {
    public Button_Pouse() {
        // Set gambar tombol dengan gambar Setting.png
        setImage("Button_Pouse.png");
    }

    public void act() {
        // Deteksi klik mouse pada tombol
        if (Greenfoot.mouseClicked(this)) {
            Greenfoot.setWorld(new PouseWorld(getWorld()));
            if (Greenfoot.mouseClicked(this)) {

            }
        }
    }
}
