import greenfoot.*;  // Import Greenfoot classes

public class Rocket extends Actor {
    private GreenfootImage rocketUpImage = new GreenfootImage("Rocket1.png"); // Gambar roket mengarah ke atas
    private GreenfootImage rocketDownImage = new GreenfootImage("Rocket2.png"); // Gambar roket mengarah ke bawah
    private int speed = 2; // Kecepatan roket
    private boolean movingUp = true; // Status arah roket, bergerak ke atas atau ke bawah

    public Rocket() {
        setImage(rocketUpImage); // Set gambar pertama saat roket pertama kali muncul
    }

    public void act() {
        moveRocket();
        checkCollisionWithPlane(); // Tambahkan logika pengecekan tabrakan
    }

    // Fungsi untuk memindahkan roket
    private void moveRocket() {
        if (movingUp) {
            setLocation(getX(), getY() - speed);
            if (getY() <= 35) { 
                setImage(rocketDownImage);
                movingUp = false;
            }
        } else {
            setLocation(getX(), getY() + speed);
            if (getY() >= 580) {
                setImage(rocketUpImage);
                movingUp = true;
            }
        }
    }

    // Logika untuk mengecek tabrakan dengan Plane
    private void checkCollisionWithPlane() {
        Plane plane = (Plane) getOneIntersectingObject(Plane.class); // Cari apakah ada objek Plane yang berinteraksi
        if (plane != null) {
            // Koordinat pusat Plane
            int planeCenterX = plane.getX();
            int planeCenterY = plane.getY();

            // Koordinat pusat Rocket
            int rocketCenterX = getX();
            int rocketCenterY = getY();

            // Hitung radius efektif dari Plane dan Rocket
            int planeRadius = Math.max(plane.getImage().getWidth(), plane.getImage().getHeight()) / 2;
            int rocketRadius = Math.max(getImage().getWidth(), getImage().getHeight()) / 2;

            // Hitung jarak antara pusat Plane dan Rocket
            int deltaX = planeCenterX - rocketCenterX;
            int deltaY = planeCenterY - rocketCenterY;
            double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY);

            // Jika jarak kurang dari atau sama dengan jumlah radius, maka terjadi tabrakan
            if (distance <= planeRadius + rocketRadius) {
                plane.handleDefeat(); // Panggil fungsi defeat dari Plane
            }
        }
    }
}
