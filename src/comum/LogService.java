package comum;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogService {
    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static synchronized void gravar(String arquivo, String msg) {
        try (PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(arquivo, true)))) {
            String log = "[" + dtf.format(LocalDateTime.now()) + "] " + msg;
            out.println(log);
            System.out.println(log);
        } catch (IOException e) {
            System.err.println("Erro log: " + e.getMessage());
        }
    }
}