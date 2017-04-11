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
    /** Indica movimentação do jogador */
    private Map<String, Boolean> movement;
    /** Nome do jogador */
    private String name;
    /** Retornos do servidor para o player */
    public PrintWriter out;
    
    public Kitty() {
        movement = new HashMap<>();
        position = new Point(0, 0);
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
     * Retorna os movimentos do jogador
     * 
     * @param chave
     * @return Movimentos
     */
    public boolean getMovement(String chave) {
        if (movement.get(chave) == null) {
            return false;
        } else {
            return movement.get(chave);
        }
    }

    /**
     * Define movimentação do jogador
     * 
     * @param chave
     * @param valor
     */
    public void setMoviment(String chave, Boolean valor) {
        this.movement.put(chave, valor);
    }
    
}
