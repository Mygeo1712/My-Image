import greenfoot.*;

public class Button_Next extends Actor {
    private GreenfootImage Original = new GreenfootImage("Button_Next.png");

    public Button_Next() {
        setImage(Original);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (getWorld() instanceof Question1World) {
                Greenfoot.setWorld(new Level2World());
            } else if (getWorld() instanceof Question2World) {
                Greenfoot.setWorld(new Level3World());
            } else if (getWorld() instanceof Question3World) {
                Greenfoot.setWorld(new EndWorld());  // Jika ada world akhir (EndWorld)
            }
        }
    }
}
