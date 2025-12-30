import greenfoot.*;

public class Button_TryAgain extends Actor {
    private GreenfootImage Original = new GreenfootImage("Button_TryAgain.png");

    public Button_TryAgain() {
        setImage(Original);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (getWorld() instanceof Question1World) {
                Greenfoot.setWorld(new Question1World());
            } else if (getWorld() instanceof Question2World) {
                Greenfoot.setWorld(new Question2World());
            } else if (getWorld() instanceof Question3World) {
                Greenfoot.setWorld(new Question3World());
            }
        }
    }
}
