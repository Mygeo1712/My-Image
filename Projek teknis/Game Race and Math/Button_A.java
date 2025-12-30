import greenfoot.*;

public class Button_A extends Actor {
    private GreenfootImage Original = new GreenfootImage("Button_A.png");
    private GreenfootImage Salah = new GreenfootImage("Button_A Wrong.png");
    private World currentWorld;

    public Button_A(World world) {
        this.currentWorld = world;
        setImage(Original);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this) && currentWorld instanceof Question1World && !((Question1World) currentWorld).isButtonClicked()) {
            setImage(Salah);
            ((Question1World) currentWorld).setButtonClicked(true);
            ((Question1World) currentWorld).showTryAgainButton();
        } else if (Greenfoot.mouseClicked(this) && currentWorld instanceof Question2World && !((Question2World) currentWorld).isButtonClicked()) {
            setImage(Salah);
            ((Question2World) currentWorld).setButtonClicked(true);
            ((Question2World) currentWorld).showTryAgainButton();
        } else if (Greenfoot.mouseClicked(this) && currentWorld instanceof Question3World && !((Question3World) currentWorld).isButtonClicked()) {
            setImage(Salah);
            ((Question3World) currentWorld).setButtonClicked(true);
            ((Question3World) currentWorld).showTryAgainButton();
        }
    }
}
