package com.jogo.personagens;

public class PuloDuplo extends Habilidade {
    //Como ainda não foi usado engine não tem novos parametros pra usar -Raioni

    public PuloDuplo(boolean aprendida,double tempoEspera, int custoEnergia,  double duracao) {
        super(aprendida, tempoEspera,custoEnergia,  duracao);
    }
    @Override 
    public void executar(Jogador player){
        //Precisa das coordenas na engine pra funcionar/executar -Raioni
    }
}
