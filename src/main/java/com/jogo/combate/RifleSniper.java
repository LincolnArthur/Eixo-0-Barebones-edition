package com.jogo.combate;

import java.util.List;

import com.jogo.personagens.Personagem;

// Arma de fogo de precisão: um único disparo de dano alto.
// Implementa Perfurante: o tiro atravessa e acerta mais de um alvo
// na linha de tiro (a ordem da lista, já que não tem engine).
public class RifleSniper extends ArmaDeFogo implements Perfurante {
    private int alvosAtravessados;

    public RifleSniper(String id, String nome, String descricao, int dano, double chanceCritico, int alvosAtravessados) {
        super(id, nome, descricao, dano, chanceCritico);
        this.alvosAtravessados = alvosAtravessados;
    }

    @Override
    public int getAlvosAtravessados() {
        return alvosAtravessados;
    }

    // causarDano() já sorteia o crítico (vem de Arma); o mesmo dano
    // vale pra todos os alvos que o tiro atravessar
    public void disparar(List<Personagem> alvos) {
        new Projetil(causarDano()).atingir(alvos, alvosAtravessados);
    }
}