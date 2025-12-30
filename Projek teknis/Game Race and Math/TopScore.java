import greenfoot.*;

public class TopScore extends Actor {
    public TopScore() {
        // Set gambar tombol dengan gambar Setting.png
        setImage("Button_TopScore.png");
    }

    public void act() {
        if (Greenfoot.mouseClicked(this)) {
    Greenfoot.setWorld(new TopScoreWorld());
}

    }
}
