import greenfoot.*;

public class Button_QuitGame extends Actor {
    public Button_QuitGame() {
        // Mengatur gambar tombol dengan "Quit Game.png"
        setImage("Button_QuitGame.png");
    }

    public void act() {
        // Deteksi jika tombol diklik oleh mouse
        if (Greenfoot.mouseClicked(this)) {

            // Menghentikan permainan
            Greenfoot.stop();

        }
    }
}
