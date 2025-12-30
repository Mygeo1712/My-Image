import java.util.HashMap;

public class PlayerData {
    private String name;
    private HashMap<Integer, Long> topTimes;  // Menyimpan waktu terbaik untuk setiap level

    public PlayerData(String name) {
        this.name = name;
        topTimes = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setTopTime(int level, long time) {
        if (!topTimes.containsKey(level) || time < topTimes.get(level)) {
            topTimes.put(level, time);  // Simpan waktu jika lebih baik atau belum ada
        }
    }

    public long getTopTime(int level) {
        return topTimes.getOrDefault(level, 0L);  // Jika belum ada waktu, kembalikan 0
    }
}
