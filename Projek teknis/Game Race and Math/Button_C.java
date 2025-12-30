import greenfoot.*;

public class Button_C extends Actor {
    private GreenfootImage Original = new GreenfootImage("Button_C.png");
    private GreenfootImage Salah = new GreenfootImage("Button_C Wrong.png");
    private World currentWorld;

    public Button_C(World world) {
        this.currentWorld = world;
        setImage(Original);
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
            if (currentWorld instanceof Question1World && !((Question1World) currentWorld).isButtonClicked()) {
                setImage(Salah);
                ((Question1World) currentWorld).setButtonClicked(true);
                ((Question1World) currentWorld).showTryAgainButton();
            } else if (currentWorld instanceof Question2World && !((Question2World) currentWorld).isButtonClicked()) {
                setImage(Salah);
                ((Question2World) currentWorld).setButtonClicked(true);
                ((Question2World) currentWorld).showTryAgainButton();
            } else if (currentWorld instanceof Question3World && !((Question3World) currentWorld).isButtonClicked()) {
                setImage(Salah);
                ((Question3World) currentWorld).setButtonClicked(true);
                ((Question3World) currentWorld).showTryAgainButton();
            }
        }
    }
}
