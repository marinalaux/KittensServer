package kittensserver;

import java.awt.Point;
import java.awt.Rectangle;
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
public class KittensServer implements Runnable, GameConstants {

    /** Jogadores */
    private ArrayList<Kitty> kittens;
    /** Velocidade do movimento */
    private static final int SPEED = 4;
    /** Mensagem para movimento para direita */
    private static final String MOV_RIGHT = "Right";
    /** Mensagem para movimento para esquerda */
    private static final String MOV_LEFT = "Left";
    /** Mensagem para movimento para cima */
    private static final String MOV_UP = "Up";
    /** Mensagem para movimento para baixo */
    private static final String MOV_DOWN = "Down";
    /** Mensagem para carinho feito */
    private static final String PET = "Pet";
    /** Mensagem para carinho recebido */
    private static final String PETTED = "Petted";
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
                synchronized (kittens) {
                    boolean pettedSomeone = false;
                    for (int i = 0; i < kittens.size(); i++) {
                        if (kittens.get(i).getMessage(MOV_RIGHT)) {
                            kittens.get(i).setPosition(new Point(kittens.get(i).getX() + SPEED, kittens.get(i).getY()));
                            if (collideWallX(kittens.get(i))) {
                                kittens.get(i).setPosition(new Point(WINDOW_WIDTH - Kitty.WIDTH, kittens.get(i).getY()));
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
                                kittens.get(i).setPosition(new Point(kittens.get(i).getX(), WINDOW_HEIGHT - Kitty.HEIGHT));
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
                            if (kittens.size() == 1) {
                                kittens.get(i).setMessage(PET, false);
                            }
                            for (int j = 0; j < kittens.size(); j++) {
                                if (kittens.get(i) != kittens.get(j)) {
                                    if (isPetting(kittens.get(i), kittens.get(j))) {
                                        kittens.get(j).setMessage(PETTED, true);
                                        pettedSomeone = true;
                                    }
                                }
                            }
                            if (pettedSomeone) {
                                kittens.get(i).setPetScore(kittens.get(i).getPetScore() + 1);
                            } else {
                                kittens.get(i).setMessage(PET, false);
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
                                            kittens.get(i).out.println(getMessageNew(kittens.get(k), false));
                                            kittens.get(i).out.println(getMessageMov(k, kittens.get(k)));
                                        }
                                    }
                                }
                                kittens.get(i).out.println(getMessageNew(kittens.get(j), i == j));
                                kittens.get(i).out.println(getMessageMov(j, kittens.get(j)));
                            }
                            if (kittens.get(j).getMessage(MOV_RIGHT) || kittens.get(j).getMessage(MOV_LEFT)
                                    || kittens.get(j).getMessage(MOV_DOWN) || kittens.get(j).getMessage(MOV_UP)) {
                                kittens.get(i).out.println(getMessageMov(j, kittens.get(j)));
                            }
                            if (kittens.get(j).getMessage(PET) || kittens.get(j).getMessage(PETTED)) {
                                kittens.get(i).out.println(getMessagePet(j, kittens.get(j)));
                            }
                            if (kittens.get(j).getMessage(EXIT)) {
                                kittens.get(i).out.println(getMessageExit(j));
                            }
                        }
                    }
                    for (int i = 0; i < kittens.size(); i++) {
                        kittens.get(i).setMessage(NEW, false);
                        kittens.get(i).setMessage(PET, false);
                        kittens.get(i).setMessage(PETTED, false);
                        if (kittens.get(i).getMessage(EXIT)) {
                            exitedKittens.add(kittens.get(i));
                        }
                    }
                    kittens.removeAll(exitedKittens);
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
            Thread checkingConnectionThread = new Thread(this::checkPlayersConnection);
            threadSender.start();
            checkingConnectionThread.start();

            while (true) {
                Socket s = ss.accept();

                Thread threadReceiver = new Thread(() -> {
                    try {
                        Kitty k = new Kitty();
                        kittens.add(k);
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
                            if (command.equals("imAlive")) {
                                k.setTimestamp(System.currentTimeMillis());
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
     * Verifica a conexão dos jogadores
     */
    private void checkPlayersConnection() {
        ArrayList<Kitty> exitedKittens = new ArrayList<>();
        while (true) {
            try {
                synchronized (kittens) {
                    for (int i = 0; i < kittens.size(); i++) {
                        if ((System.currentTimeMillis() - kittens.get(i).getTimestamp()) > 5000) {
                            exitedKittens.add(kittens.get(i));
                        }
                    }
                    kittens.removeAll(exitedKittens);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Verifica se o jogador está colidindo com as paredes laterais
     * 
     * @param k
     * @return Colidiu
     */
    private boolean collideWallX(Kitty k) {
        return (k.getX() + Kitty.WIDTH) >= WINDOW_WIDTH || k.getX() <= 0;
    }
    
    /**
     * Verifica se o jogador está colidindo com as paredes superior e inferior
     * 
     * @param k
     * @return Colidiu
     */
    private boolean collideWallY(Kitty k) {
        return (k.getY() + Kitty.HEIGHT) >= WINDOW_HEIGHT || k.getY() <= 0;
    }
    
    /**
     * Verifica se os jogadores estão sobrepostos
     * 
     * @param k1
     * @param k2
     * @return Estão sobrepostos
     */
    private boolean isPetting(Kitty k1, Kitty k2) {
        Rectangle kitty1 = new Rectangle(k1.getX(), k1.getY(), Kitty.WIDTH, Kitty.HEIGHT);
        Rectangle kitty2 = new Rectangle(k2.getX(), k2.getY(), Kitty.WIDTH, Kitty.HEIGHT);
        return kitty1.intersects(kitty2);
    }
    
    /**
     * Monta mensagem de movimento do jogador
     * 
     * @param j
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
     * @param you
     * @return Mensagem de novo jogador
     */
    private String getMessageNew(Kitty k, boolean you) {
        return "NEW_" + k.getColor() + "_" + you + "_" + k.getPetScore();
    }
    
    /**
     * Monta mensagem de carinho em jogador
     * 
     * @param j
     * @param k
     * @return Mensagem de carinho
     */
    private String getMessagePet(int j, Kitty k) {
        return "PET_" + j + "_" + k.getPetScore();
    }
    
    /**
     * Monta mensagem de saída de jogador
     * 
     * @param j
     * @return Mensagem de saída
     */
    private String getMessageExit(int j) {
        return "EXIT_" + j;
    }
    
}
