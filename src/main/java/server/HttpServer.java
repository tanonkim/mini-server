package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class HttpServer {
    private int port;
    private volatile boolean running = true;
    private final ExecutorService threadPool;
//    private final RequestHandler requestHandler;

    public HttpServer(int port, int threadPoolSize) {
        this.port = port;
        this.threadPool = Executors.newFixedThreadPool(threadPoolSize);
        // todo : requestHandler
    }

    public void start() {
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("===========================================");
            System.out.println("  HTTP 서버가 시작되었습니다!");
            System.out.println("  포트: " + port);
            System.out.println("  스레드 풀 크기: " + threadPool.toString());
            System.out.println("===========================================");
            System.out.println();
            System.out.println("테스트 방법:");
            System.out.println("  GET  http://localhost:" + port + "/users?name=홍길동&age=25");
            System.out.println("  POST http://localhost:" + port + "/users (JSON body)");
            System.out.println();

            while (running) {
                try {
                    // 클라이언트 연결 대기
                    Socket clientSocket = serverSocket.accept();

                    // 스레드 풀에 요청 처리 위임
                    threadPool.submit(() -> handleClient(clientSocket));

                }
                catch (Exception e) {
                    if (running) {
                        System.err.println("클라이언트 연결 수락 중 오류: " + e.getMessage());
                    }
                }
            }
        }
        catch (Exception e) {
            System.err.println("서버 시작 실패: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            shutdown();
        }
    }

    private void handleClient(Socket clientSocket) {
        try {

        }
        catch (Exception e) {
            System.err.println("요청 처리 중 오류: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void shutdown() {
        running = false;
        threadPool.shutdown();
        System.out.println("서버가 종료.");
    }

    public static void main(String[] args) {
        int port = 8888;
        int threadPoolSize = 10;

        HttpServer server = new HttpServer(port, threadPoolSize);

        Runtime.getRuntime().addShutdownHook(new Thread(server::shutdown));

        server.start();
    }
}
