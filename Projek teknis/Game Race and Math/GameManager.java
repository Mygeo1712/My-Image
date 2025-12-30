import java.util.ArrayList;
import java.util.HashMap;

public class GameManager {
    private static GameManager instance;
    private int currentLevel;
    private long[] topScores;  // Array untuk menyimpan top score setiap level (index 0: level 1, index 1: level 2, index 2: level 3)
    private String playerName;
    private ArrayList<PlayerData> players;  // Menyimpan semua data pemain
    private PlayerData currentPlayer; // Menyimpan data pemain saat ini
    
    // Konstruktor privat untuk implementasi Singleton
    private GameManager() {
        currentLevel = 1; // Default ke level 1
         players = new ArrayList<>();
        topScores = new long[3];  // Menyimpan top score untuk 3 level (level 1, 2, dan 3)
        for (int i = 0; i < 3; i++) {
            topScores[i] = Long.MAX_VALUE;  // Inisialisasi dengan nilai besar agar bisa dibandingkan
        }
    }
    
    public void setCurrentPlayer(PlayerData player) {
    this.currentPlayer = player;
}
    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }
    
     public void signIn(String playerName) {
        for (PlayerData player : players) {
            if (player.getName().equals(playerName)) {
                currentPlayer = player;  // Pemain sudah ada, gunakan data yang ada
                return;
            }
        }
        // Jika pemain belum ada, tambahkan pemain baru
        currentPlayer = new PlayerData(playerName);
        players.add(currentPlayer);
    }
    
     public PlayerData getCurrentPlayer() {
        return currentPlayer;
    }
    
    // Mendapatkan semua pemain
    public ArrayList<PlayerData> getPlayers() {
        return players;
    }
    
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    
      public String getPlayerName() {
        return playerName;
    }
    
    // Set level saat ini
    public void setCurrentLevel(int level) {
        this.currentLevel = level;
    }

    // Mendapatkan level saat ini
    public int getCurrentLevel() {
        return this.currentLevel;
    }

    // Menyimpan top score untuk level tertentu jika waktu baru lebih rendah (lebih cepat)
    public void setTopScore(long score, int level) {
        if (score < topScores[level - 1]) {  // Perbandingan waktu (lebih rendah lebih baik)
            topScores[level - 1] = score;  // Simpan score jika lebih cepat
        }
    }

    // Mendapatkan top score untuk level tertentu
    public long getTopScore(int level) {
        return topScores[level - 1];
    }
    
    
}
