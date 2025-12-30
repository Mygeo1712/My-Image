import greenfoot.*;  // (World, Actor, GreenfootImage, Greenfoot and MouseInfo)

/**
 * Button_Back class.
 */
public class Button_Back extends Actor
{
    private World previousWorld; // Menyimpan referensi world sebelumnya

    /**
     * Konstruktor untuk Button_Back.
     * Menerima parameter previousWorld yang akan mengembalikan kita ke world sebelumnya.
     */
    public Button_Back(World previousWorld) {
        this.previousWorld = previousWorld;
        setImage("Button_Back.png"); // Set gambar untuk tombol "Back"
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            // Jika previousWorld tidak null, kembali ke world sebelumnya
            if (previousWorld != null) {
                Greenfoot.setWorld(previousWorld);
            }
        }
    }
}
