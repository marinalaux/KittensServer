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
public class Kitty {
    
    /** Posição do jogador na tela */
    private Point position;
    /** Altura do jogador */
    public static final int height = 64;
    /** Largura do jogador */
    public static final int width = 64;
    /** Indica mensagem do jogador */
    private Map<String, Boolean> message;
    /** Nome do jogador */
    private String name;
    /** Ícone do jogador */
    private String icon;
    /** Retornos do servidor para o player */
    public PrintWriter out;
    
    public Kitty() {
        message = new HashMap<>();
        position = new Point(0, 0);
        icon = "right";
    }
    
    /**
     * Retorna a linha onde o jogador está
     * 
     * @return Linha
     */
    public int getX() {
        return position.x;
    }
    
    /**
     * Retorna a coluna onde o jogador está
     * 
     * @return Coluna
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
     * Retorna o nome do jogador
     * 
     * @return Nome
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome do jogador
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
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

}
