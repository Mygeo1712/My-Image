import greenfoot.*;

public class Label extends Actor {
    private GreenfootImage labelImage;

    public Label(String text, int fontSize) {
        labelImage = new GreenfootImage(text, fontSize, Color.WHITE, Color.BLACK);
        setImage(labelImage);
    }

    public void setText(String text, int fontSize) {
        labelImage = new GreenfootImage(text, fontSize, Color.WHITE, Color.BLACK);
        setImage(labelImage);
    }
}
