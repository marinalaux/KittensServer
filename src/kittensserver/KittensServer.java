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
    /** Mensagem para movimento para direita */
    private static final String MOV_RIGHT = "Right";
    /** Mensagem para movimento para esquerda */
    private static final String MOV_LEFT = "Left";
    /** Mensagem para movimento para cima */
    private static final String MOV_UP = "Up";
    /** Mensagem para movimento para baixo */
    private static final String MOV_DOWN = "Down";
    /** Mensagem para carinho */
    private static final String PET = "Pet";
    /** Mensagem de jogador novo */
    private static final String NEW = "New";
    /** Mensagem de jogador que saiu */
    private static final String EXIT = "Exit";

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
                    if (kittens.get(i).getMessage(MOV_RIGHT)) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX() + SPEED, kittens.get(i).getY()));
                        if (collideWallX(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(WINDOW_WIDTH - Kitty.width, kittens.get(i).getY()));
                        }
                        kittens.get(i).setIcon("right");
                    }
                    if (kittens.get(i).getMessage(MOV_LEFT)) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX() - SPEED, kittens.get(i).getY()));
                        if (collideWallX(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(0, kittens.get(i).getY()));
                        }
                        kittens.get(i).setIcon("left");
                    }
                    if (kittens.get(i).getMessage(MOV_DOWN)) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX(), kittens.get(i).getY() + SPEED));
                        if (collideWallY(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(kittens.get(i).getX(), WINDOW_HEIGHT - Kitty.height));
                        }
                        kittens.get(i).setIcon("down");
                    }
                    if (kittens.get(i).getMessage(MOV_UP)) {
                        kittens.get(i).setPosition(new Point(kittens.get(i).getX(), kittens.get(i).getY() - SPEED));
                        if (collideWallY(kittens.get(i))) {
                            kittens.get(i).setPosition(new Point(kittens.get(i).getX(), 0));
                        }
                        kittens.get(i).setIcon("up");
                    }
                    if (kittens.get(i).getMessage(PET)) {
                        for (int j = 0; j < kittens.size(); j++) {
                            if (kittens.get(i) != kittens.get(j)) {
                                if (isPetting(kittens.get(i), kittens.get(j))) {
                                    kittens.get(j).setMessage("Pet", true);
                                } else {
                                    kittens.get(i).setMessage("Pet", false);
                                }
                            }
                        }
                    }
                }
                ArrayList<Kitty> exitedKittens = new ArrayList<>();
                for (int i = 0; i < kittens.size(); i++) {
                    for (int j = 0; j < kittens.size(); j++) {
                        if (kittens.get(j).getMessage(NEW)) {
                            if (i == j) {
                                for (int k = 0; k < kittens.size(); k++) {
                                    if (k != j) {
                                        kittens.get(i).out.println(getMessageNew(kittens.get(k)));
                                        kittens.get(i).out.println(getMessageMov(k, kittens.get(k)));
                                    }
                                }
                            }
                            kittens.get(i).out.println(getMessageNew(kittens.get(j)));
                            kittens.get(i).out.println(getMessageMov(j, kittens.get(j)));
                        }
                        if (kittens.get(j).getMessage(MOV_RIGHT) || kittens.get(j).getMessage(MOV_LEFT)
                                || kittens.get(j).getMessage(MOV_DOWN) || kittens.get(j).getMessage(MOV_UP)) {
                            kittens.get(i).out.println(getMessageMov(j, kittens.get(j)));
                        }
                        if (kittens.get(j).getMessage(PET)) {
                            kittens.get(i).out.println("PET_" + j);
                        }
                        if (kittens.get(j).getMessage(EXIT)) {
                            kittens.get(i).out.println("EXI_" + j);
                        }
                    }
                    kittens.get(i).setMessage(NEW, false);
                    if (kittens.get(i).getMessage(EXIT)) {
                        exitedKittens.add(kittens.get(i));
                    }
                }
                kittens.removeAll(exitedKittens);
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
                        
                        k.setMessage(NEW, true);
                        
                        String command = "";
                        while (!(command = in.readLine()).equals("exit")) {
                            if (command.equals("press_right")) {
                                k.setMessage(MOV_RIGHT, true);
                            }
                            if (command.equals("release_right")) {
                                k.setMessage(MOV_RIGHT, false);
                            }
                            if (command.equals("press_left")) {
                                k.setMessage(MOV_LEFT, true);
                            }
                            if (command.equals("release_left")) {
                                k.setMessage(MOV_LEFT, false);
                            }
                            if (command.equals("press_down")) {
                                k.setMessage(MOV_DOWN, true);
                            }
                            if (command.equals("release_down")) {
                                k.setMessage(MOV_DOWN, false);
                            }
                            if (command.equals("press_up")) {
                                k.setMessage(MOV_UP, true);
                            }
                            if (command.equals("release_up")) {
                                k.setMessage(MOV_UP, false);
                            }
                            if (command.equals("pet")) {
                                k.setMessage(PET, true);
                            }
                            if (command.equals("stop_petting")) {
                                k.setMessage(PET, false);
                            }
                            if (command.equals("exiting")) {
                                k.setMessage(EXIT, true);
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
    
    /**
     * Verifica se os jogadores estão sobrepostos
     * 
     * @param k1
     * @param k2
     * @return Estão sobrepostos
     */
    private boolean isPetting(Kitty k1, Kitty k2) {
        return ((k2.getX() >= k1.getX()) && (k2.getX() <= (k1.getX() + Kitty.width)) &&
                (k2.getY() >= k1.getY()) && (k2.getY() <= (k1.getY() + Kitty.height)));
        
        
//        return (position.y + size.height) >= c.getPlayer().getPosition().y &&
//               (position.y + size.height) <= (c.getPlayer().getPosition().y + Player.getTamanho().height) &&
//               (position.x + size.width) >= c.getPlayer().getPosition().x &&
//               (position.x + size.width) <= (c.getPlayer().getPosition().x + Player.getTamanho().width);
    }
    
    /**
     * Monta mensagem de movimento do jogador
     * 
     * @param i
     * @param k
     * @return Mensagem de movimento
     */
    private String getMessageMov(int j, Kitty k) {
        return "MOV_" + j + "_" + k.getX() + "_" + k.getY() + "_" + k.getIcon();
    }
    
    /**
     * Monta mensagem de novo jogador
     * 
     * @param k
     * @return 
     */
    private String getMessageNew(Kitty k) {
        return "NEW_" + k.getName();
    }
}
