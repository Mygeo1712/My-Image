import greenfoot.*;  // Import Greenfoot classes

    public class Cloud2 extends Actor {
    private int speed;  // Kecepatan gerakan awan
    
    public Cloud2() {
        GreenfootImage image = new GreenfootImage("awan2.png");  // Memuat gambar awan
        setImage(image);  // Menetapkan gambar awan ke objek ini
        speed = Greenfoot.getRandomNumber(3) + 1;  // Menentukan kecepatan acak antara 1 dan 3
    }

    public void act() {
        moveLeft();  // Pindahkan awan ke kiri
    }

    // Metode untuk memindahkan awan ke kiri
    private void moveLeft() {
        move(-speed);  // Gerakkan objek ke kiri sesuai dengan kecepatan
        
        // Jika awan sudah mencapai ujung kiri, posisikan ulang ke kanan
        if (getX() <= 0) {
            setLocation(getWorld().getWidth(), Greenfoot.getRandomNumber(getWorld().getHeight()));  // Posisikan awan secara acak di sisi kanan
        }
    }
}
