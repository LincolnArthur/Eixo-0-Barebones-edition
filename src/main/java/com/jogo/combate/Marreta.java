package com.jogo.combate;

import com.jogo.personagens.Personagem;
import com.jogo.personagens.Sniper;

// Arma pesada com dano contundente.
// Possui bônus de dano contra inimigos do tipo Sniper.
public class Marreta extends Arma {
    private static final int BONUS_DANO_CONTRA_SNIPER = 10;

    private final int dano;
//lerda e da mais dano em sniper
    public Marreta(String id, String nome, String descricao, int dano, double chanceCritico, double tempoEspera) {
        super(id, nome, descricao, chanceCritico, tempoEspera);
        this.dano = dano;
    }

    // Por padrão a Marreta é lenta: 1.8s entre golpes (~0.55 golpes/segundo)
    public Marreta(String id, String nome, String descricao, int dano, double chanceCritico) {
        this(id, nome, descricao, dano, chanceCritico, 1.8);
    }

    @Override
    protected int calcularDanoBase() {
        return dano;
    }

    // Sobrescrita polimórfica: se o alvo for um Sniper, aplica o bônus de dano
    @Override
    protected int calcularDanoBase(Personagem alvo) {
        if (alvo instanceof Sniper) {
            return dano + BONUS_DANO_CONTRA_SNIPER;
        }
        return dano;
    }

    public int getBonusDanoContraSniper() {
        return BONUS_DANO_CONTRA_SNIPER;
    }
}
