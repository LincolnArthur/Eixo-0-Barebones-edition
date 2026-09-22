package com.jogo.personagens;

public class Apara extends Habilidade{
    public Apara (boolean aprendida, double tempoEspera, int custoEnergia, double duracao){
        super(aprendida,tempoEspera,custoEnergia,duracao);
    }

    @Override 
    public void executar(Jogador player){
        //ainda não pensei como usar aparar já que tem que ter a engine
    }
}
