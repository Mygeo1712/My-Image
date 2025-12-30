import greenfoot.*;
public class ScorePanel extends Actor {
    private int score; // Variabel untuk menyimpan skor
    private long startTime; // Waktu awal timer
    private int timeAlive;  // Lama waktu hidup dalam detik
    private GreenfootImage scoreImage; // Gambar dasar ScorePanel

    public void setScore(int score) {
        this.score = score;
    }

    public ScorePanel() {
        // Memuat gambar Score Panel dan menambahkan timer di atasnya
        scoreImage = new GreenfootImage("ScorePanel.png");
        setImage(scoreImage);
        timeAlive = 0;
        startTime = System.currentTimeMillis();  // Inisialisasi waktu mulai
        resetTimer(); // Reset waktu saat ScorePanel dibuat
        reset();
    }

    public void updateDisplay() {
        // Salin ulang gambar dasar untuk menghindari penggandaan teks
        GreenfootImage displayImage = new GreenfootImage(scoreImage);

        // Atur warna dan font untuk teks
        displayImage.setColor(Color.WHITE); // Warna teks putih
        displayImage.setFont(new Font("Arial", true, false, 18)); // Font Arial, tebal, ukuran 18

        // Tampilkan teks skor di posisi tertentu
        displayImage.drawString("Score: " + score, 10, 50); // Teks "Score" + nilai

        // Tetapkan gambar baru sebagai gambar objek ini
        setImage(displayImage);
    }

    public void act() {
        // Menyimpan referensi dunia saat ini
        World currentWorld = getWorld();

        // Mengakses objek Plane dari berbagai dunia
        Actor plane = null;

        if (currentWorld instanceof Level1World) {
            Level1World level1World = (Level1World) currentWorld;
            plane = level1World.getObjects(Plane.class).isEmpty() ? null : level1World.getObjects(Plane.class).get(0);

        } else if (currentWorld instanceof Level2World) {
            Level2World level2World = (Level2World) currentWorld;
            plane = level2World.getObjects(Plane2.class).isEmpty() ? null : level2World.getObjects(Plane2.class).get(0);

        } else if (currentWorld instanceof Level3World) {
            Level3World level3World = (Level3World) currentWorld;
            plane = level3World.getObjects(Plane3.class).isEmpty() ? null : level3World.getObjects(Plane3.class).get(0);
        }

        // Memeriksa apakah pesawat ada dan belum mengalami tabrakan
        if (plane != null) {
            boolean isCrashed = false;
            if (plane instanceof Plane) {
                isCrashed = ((Plane) plane).getIsCrashed();
            } else if (plane instanceof Plane2) {
                isCrashed = ((Plane2) plane).getIsCrashed();
            } else if (plane instanceof Plane3) {
                isCrashed = ((Plane3) plane).getIsCrashed();
            }

            if (!isCrashed) {
                updateTimer(); // Perbarui timer jika pesawat tidak mengalami kecelakaan
            }
        }

        // Cek apakah level selesai dan simpan waktu
        if (currentWorld instanceof Level1World || currentWorld instanceof Level2World || currentWorld instanceof Level3World) {
            // Simpan waktu ke GameManager jika pesawat selesai level
            if (plane != null && !plane.isAtEdge()) {
                // Misalnya jika pesawat mencapai titik tertentu, seperti finish line
                saveScore();
            }
        }
    }

    public void saveScore() {
        // Cek level saat ini dan simpan waktu terbaik untuk level yang sesuai
        if (getWorld() instanceof Level1World) {
            // Jika di Level1World
            GameManager.getInstance().setTopScore(timeAlive, 1);  // Simpan waktu terbaik untuk level 1
        } else if (getWorld() instanceof Level2World) {
            // Jika di Level2World
            GameManager.getInstance().setTopScore(timeAlive, 2);  // Simpan waktu terbaik untuk level 2
        } else if (getWorld() instanceof Level3World) {
            // Jika di Level3World
            GameManager.getInstance().setTopScore(timeAlive, 3);  // Simpan waktu terbaik untuk level 3
        }
    }


    public void updateTimer() {
        long elapsedTime = System.currentTimeMillis() - startTime;
        timeAlive = (int) (elapsedTime / 1000); // Konversi ke detik
        displayTime(timeAlive); // Tampilkan waktu ke dalam gambar
    }

    private void displayTime(int time) {
        // Salin ulang gambar dasar untuk menghindari penggandaan teks
        GreenfootImage timerImage = new GreenfootImage(scoreImage);

        // Atur warna dan font untuk teks
        timerImage.setColor(Color.BLACK); // Warna teks putih
        timerImage.setFont(new Font("Arial", true, false, 18)); // Font Arial, tebal, ukuran 18

        // Tampilkan teks waktu di posisi tertentu
        timerImage.drawString("Time: " + time + "s", 10, 30); // Teks "Time" + detik

        // Tetapkan gambar baru sebagai gambar objek ini
        setImage(timerImage);
    }

    public void resetTimer() {
        startTime = System.currentTimeMillis(); // Set ulang waktu mulai
    }

    public void recordTime(int time) {
        timeAlive = time; // Simpan waktu yang telah dicapai
        displayTime(time); // Perbarui tampilan waktu
    }

    public int getTimeAlive() {
        return timeAlive; // Mengembalikan waktu hidup yang tercatat
    }

    public void reset() {
        setScore(0); // Reset skor
        updateDisplay(); // Perbarui tampilan
    }
}
