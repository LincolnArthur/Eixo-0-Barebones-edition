package com.jogo.habilidades;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

public class PuloDuplo extends Habilidade {
    //Como ainda não foi usado engine não tem novos parametros pra usar -Raioni

    public PuloDuplo(boolean aprendida, double tempoEspera, int custoEnergia, double duracao) {
        super(aprendida, tempoEspera, custoEnergia, duracao);
    }

    @Override 
    public void executar(Jogador player, Personagem alvo){
        //Precisa das coordenas na engine pra funcionar/executar -Raioni
        //não usa alvo -> pulo age só sobre quem usou -lincoln
    }
}