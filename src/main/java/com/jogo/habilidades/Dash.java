package com.jogo.habilidades;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

public class Dash extends Habilidade {

    public Dash(boolean aprendida, double tempoEspera, int custoEnergia, double duracao) {
        super(aprendida, tempoEspera, custoEnergia, duracao);
    }

    @Override
    public void executar(Jogador dono, Personagem alvo) {
        // Sem posição, o efeito do dash é a invulnerabilidade temporária.
        // O movimento em si fica pra quando houver mundo/posição.
        // não usa alvo -> Dash age só sobre quem usou -lincoln
        //dono.ficarInvulneravel(getDuracao());
    }
}