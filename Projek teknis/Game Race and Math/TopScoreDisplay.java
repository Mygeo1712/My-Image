import greenfoot.*;

public class TopScoreDisplay extends Actor {
    private long topScore;

    public TopScoreDisplay(long topScore) {
        this.topScore = topScore;
        updateImage();
    }

    // Update image untuk menampilkan top score
    public void updateImage() {
        GreenfootImage image = new GreenfootImage(200, 50);  // Membuat area gambar baru
        image.setColor(Color.BLACK);  // Mengisi latar belakang dengan warna hitam
        image.fill();  // Mengisi seluruh area gambar
        image.setColor(Color.WHITE);  // Mengubah warna teks menjadi putih
        image.drawString("Top Score: " + topScore + "s", 10, 25);  // Menulis teks di gambar
        setImage(image);  // Menetapkan gambar pada objek ini
    }

    // Mengubah top score jika diperlukan
    public void setTopScore(long topScore) {
        this.topScore = topScore;
        updateImage();  // Perbarui gambar dengan top score yang baru
    }
}
