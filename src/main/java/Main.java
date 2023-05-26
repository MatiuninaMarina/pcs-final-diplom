import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        BooleanSearchEngine engine = new BooleanSearchEngine(new File("pdfs"));
        try (ServerSocket serverSocket = new ServerSocket(8989);) { // стартуем сервер один(!) раз
            ObjectMapper objectMapper = new ObjectMapper();
            while (true) { // в цикле(!) принимаем подключения
                try (
                        Socket socket = serverSocket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        PrintWriter out = new PrintWriter(socket.getOutputStream());
                ) {
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        List<PageEntry> result = engine.search(inputLine);
                        String s = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
                        out.println(s);
                        out.flush();
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Не могу стартовать сервер");
            e.printStackTrace();
        }
    }
}