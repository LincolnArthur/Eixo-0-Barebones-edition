package com.jogo.habilidades;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

//A tradução de parry é paia, mas é assim msm
//O intuito é funcionar como um parry, mas isso é a interface gráfica e a engine que resolve dps -lincoln
public class Apara extends Habilidade{
    public Apara (boolean aprendida, double tempoEspera, int custoEnergia, double duracao){
        super(aprendida,tempoEspera,custoEnergia,duracao);
    }

    @Override 
    public void executar(Jogador player, Personagem alvo){
        //ainda não pensei como usar aparar já que tem que ter a engine - Raioni
        //não usa alvo -> Apara age só sobre quem usou -lincoln
    }
}