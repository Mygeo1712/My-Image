import greenfoot.*;  // Import Greenfoot classes

public class Life extends Actor {
    // Deklarasi gambar life
    private GreenfootImage life3Image = new GreenfootImage("Life3.png");
    private GreenfootImage life2Image = new GreenfootImage("Life2.png");
    private GreenfootImage life1Image = new GreenfootImage("Life1.png");
    private GreenfootImage endLifeImage = new GreenfootImage("EndLife.png");

    private int remainingLives;

    public Life() { 
        remainingLives = 3;  // Set jumlah nyawa awal
        updateLifeImage();   // Tampilkan gambar sesuai jumlah nyawa awal
    }

    public void act() {
        // Tidak ada logika reset di sini
    }

    /**
     * Mengurangi nyawa pemain sebesar 1.
     * Jika nyawa habis, gambar "EndLife" akan ditampilkan.
     */
    public void loseLife() {
        if (remainingLives > 0) {
            remainingLives--;  // Kurangi nyawa
            updateLifeImage(); // Perbarui gambar nyawa
        }
    }

    /**
     * Mengatur ulang nyawa ke jumlah maksimum (3) dan memperbarui gambar.
     */
    public void reset() {
        remainingLives = 3;   // Reset nyawa ke jumlah awal
        updateLifeImage();    // Perbarui gambar ke Life3.png
    }

    /**
     * Perbarui gambar nyawa sesuai jumlah nyawa yang tersisa.
     */
    public void updateLifeImage() {
        switch (remainingLives) {
            case 3:
                setImage(life3Image);
                break;
            case 2:
                    setImage(life2Image);
                break;
            case 1:
                setImage(life1Image);
                break;
            case 0:
                setImage(endLifeImage);
                break;
        }
    }

    /**
     * Getter untuk jumlah nyawa yang tersisa.
     */
    public int getRemainingLives() {
        return remainingLives;
    }
}
