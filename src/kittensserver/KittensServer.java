package kittensserver;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * Servidor Kittens
 *
 * @author Marina
 */
public class KittensServer implements Runnable {

    /** Jogadores */
    private ArrayList<Kitty> kittens;
    /** Velocidade do movimento */
    private static final int SPEED = 4;
    /** Largura da janela do cliente */
    private static final int WINDOW_WIDTH = 788;
    /** Altura da janela do cliente */
    private static final int WINDOW_HEIGHT = 560;

    public static void main(String[] args) {

        System.out.println("Starting...");
        KittensServer kittensServer = new KittensServer();
        kittensServer.waitForPlayers();

    }

    @Override
    public void run() {
        try {
            while (true) {
                Thread.sleep(30);
                for (int i = 0; i < kittens.size(); i++) {
                    if (kittens.get(i).getMovement("Right")) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX() + SPEED, kittens.get(i).getY()));
                        if (collideWallX(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(WINDOW_WIDTH - Kitty.width, kittens.get(i).getY()));
                        }
                    }
                    if (kittens.get(i).getMovement("Left")) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX() - SPEED, kittens.get(i).getY()));
                        if (collideWallX(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(0, kittens.get(i).getY()));
                        }
                    }
                    if (kittens.get(i).getMovement("Down")) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX(), kittens.get(i).getY() + SPEED));
                        if (collideWallY(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(kittens.get(i).getX(), WINDOW_HEIGHT - Kitty.height));
                        }
                    }
                    if (kittens.get(i).getMovement("Up")) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX(), kittens.get(i).getY() - SPEED));
                        if (collideWallY(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(kittens.get(i).getX(), 0));
                        }
                    }
                }
                for (int i = 0; i < kittens.size(); i++) {
                    for (int j = 0; j < kittens.size(); j++) {
                        kittens.get(i).out.println(j + "_" + kittens.get(j).getX() + "_" + kittens.get(j).getY());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aguarda os jogadores
     */
    public void waitForPlayers() {
        try {
            kittens = new ArrayList<>();
            ServerSocket ss = new ServerSocket(8880);
            Thread threadSender = new Thread(this);
            threadSender.start();

            while (true) {
                Socket s = ss.accept();

                Thread threadReceiver = new Thread(() -> {
                    try {
                        Kitty k = new Kitty();
                        kittens.add(k);
                        k.setName("Player " + (kittens.size() - 1));
                        BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        k.out = new PrintWriter(s.getOutputStream(), true);

                        String command = "";
                        while (!(command = in.readLine()).equals("exit")) {
                            if (command.equals("press_right")) {
                                k.setMoviment("Right", true);
                            }
                            if (command.equals("release_right")) {
                                k.setMoviment("Right", false);
                            }
                            if (command.equals("press_left")) {
                                k.setMoviment("Left", true);
                            }
                            if (command.equals("release_left")) {
                                k.setMoviment("Left", false);
                            }
                            if (command.equals("press_down")) {
                                k.setMoviment("Down", true);
                            }
                            if (command.equals("release_down")) {
                                k.setMoviment("Down", false);
                            }
                            if (command.equals("press_up")) {
                                k.setMoviment("Up", true);
                            }
                            if (command.equals("release_up")) {
                                k.setMoviment("Up", false);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                threadReceiver.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Verifica se o jogador está colidindo com as paredes laterais
     * 
     * @param k
     * @return Colidiu
     */
    private boolean collideWallX(Kitty k) {
        return (k.getX() + Kitty.width) >= WINDOW_WIDTH || k.getX() <= 0;
    }
    
    /**
     * Verifica se o jogador está colidindo com as paredes superior e inferior
     * 
     * @param k
     * @return Colidiu
     */
    private boolean collideWallY(Kitty k) {
        return (k.getY() + Kitty.height) >= WINDOW_HEIGHT || k.getY() <= 0;
    }
}
