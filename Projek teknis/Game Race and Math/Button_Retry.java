import greenfoot.*;

public class Button_Retry extends Actor {
    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            GameManager manager = GameManager.getInstance();
            int lastLevel = manager.getCurrentLevel();

            // Reset skor dan nyawa
            

            // Pindah ke world yang sesuai
            switch (lastLevel) {
                case 1:
                    Greenfoot.setWorld(new Level1World());
                    break;
                case 2:
                    Greenfoot.setWorld(new Level2World());
                    break;
                case 3:
                    Greenfoot.setWorld(new Level3World());
                    break;
                default:
                    Greenfoot.setWorld(new Level1World()); // Default ke Level 1
                    break;
            }
        }
    }
   
}
