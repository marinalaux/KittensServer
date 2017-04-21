package kittensserver;

import java.awt.Point;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Gatinho
 *
 * @author Marina
 */
public class Kitty implements GameConstants {

    /** Posição do jogador na tela */
    private Point position;
    /** Altura do jogador */
    public static final int HEIGHT = 64;
    /** Largura do jogador */
    public static final int WIDTH = 64;
    /** Indica mensagem do jogador */
    private Map<String, Boolean> message;
    /** Ícone do jogador */
    private String icon;
    /** Timestamp da última mensagem de conexão ativa recebida */
    private long timestamp;
    /** Contador de carinhos */
    private int petScore;
    /** Cor do jogador */
    private final int color;
    /** Retornos do servidor para o player */
    public PrintWriter out;

    public Kitty() {
        message = new HashMap<>();
        position = new Point((int) (Math.random() * (WINDOW_WIDTH - WIDTH)),
                (int) (Math.random() * (WINDOW_HEIGHT - HEIGHT)));
        icon = "right";
        timestamp = System.currentTimeMillis();
        petScore = 0;
        color = (int) (Math.random() * 0xFFFFFF);
    }

    /**
     * Retorna a coluna onde o jogador está
     *
     * @return Coluna
     */
    public int getX() {
        return position.x;
    }

    /**
     * Retorna a linha onde o jogador está
     *
     * @return Linha
     */
    public int getY() {
        return position.y;
    }

    /**
     * Define a posição do jogador
     *
     * @param p
     */
    public void setPosition(Point p) {
        position.x = p.x;
        position.y = p.y;
    }

    /**
     * Retorna mensagem do jogador
     *
     * @param chave
     * @return Mensagem
     */
    public boolean getMessage(String chave) {
        if (message.get(chave) == null) {
            return false;
        } else {
            return message.get(chave);
        }
    }

    /**
     * Define mensagem do jogador
     *
     * @param chave
     * @param valor
     */
    public void setMessage(String chave, Boolean valor) {
        this.message.put(chave, valor);
    }

    /**
     * Retorna o ícone do jogador
     *
     * @return Ícone
     */
    public String getIcon() {
        return icon;
    }

    /**
     * Define o ícone do jogador
     *
     * @param icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * Retorna timestamp da última mensagem de conexão ativa recebida
     *
     * @return Timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * Define o timestamp da última mensagem de conexão ativa recebida
     *
     * @param timestamp
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retorna contador de carinho
     *
     * @return Contador
     */
    public int getPetScore() {
        return petScore;
    }

    /**
     * Define contador de carinho
     *
     * @param petScore
     */
    public void setPetScore(int petScore) {
        this.petScore = petScore;
    }

    /**
     * Retorna a cor do jogador
     * 
     * @return Cor
     */
    public int getColor() {
        return color;
    }

}
