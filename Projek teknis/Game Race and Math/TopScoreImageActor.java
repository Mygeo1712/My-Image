import greenfoot.*;

public class TopScoreImageActor extends Actor {
    private GreenfootImage topScoreImage;

    public TopScoreImageActor(GreenfootImage image) {
        this.topScoreImage = image;
        setImage(topScoreImage);
    }

    // Jika perlu, Anda bisa menambahkan metode untuk mengubah gambar
    public void setTopScoreImage(GreenfootImage newImage) {
        this.topScoreImage = newImage;
        setImage(topScoreImage);
    }
}
