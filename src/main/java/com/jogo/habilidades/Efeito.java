
package com.jogo.habilidades;
import com.jogo.personagens.Personagem; 
/**
 * Interface para efeitos de status que podem ser aplicados a personagens.
 */
public interface Efeito {

    /**
     * Aplica o efeito no alvo (ex: reduz velocidade, inicia dano contínuo).
     */
    void aplicar(Personagem alvo);

    /**
     * Remove o efeito do alvo (ex: restaura velocidade, para dano contínuo).
     */
    void remover(Personagem alvo);

    /**
     * Atualiza o efeito a cada frame. Retorna true se o efeito deve continuar,
     * false se terminou naturalmente e deve ser removido.
     */
    boolean atualizar(Personagem alvo, double tpf);

    /**
     * Se o efeito está ativo no momento.
     */
    boolean estaAtivo();

    /**
     * Nome identificador do efeito (para debug/HUD).
     */
    String getNome();
}