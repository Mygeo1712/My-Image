import greenfoot.*;

public class Button_SignIn extends Actor {
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            String playerName = Greenfoot.ask("Masukkan nama pemain:");
            GameManager.getInstance().setPlayerName(playerName);  // Simpan nama pemain di GameManager
            Greenfoot.setWorld(new HomeWorld());  // Pindah ke level 1
        }
    }
}
