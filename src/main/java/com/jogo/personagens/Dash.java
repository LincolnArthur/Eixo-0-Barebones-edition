package com.jogo.personagens;

public class Dash extends Habilidade {

    public Dash(boolean aprendida, double tempoEspera, int custoEnergia, double duracao) {
        super(aprendida, tempoEspera,custoEnergia,  duracao);
    }

    @Override
    public void executar(Jogador dono) {
        // Sem posição, o efeito do dash é a invulnerabilidade temporária.
        // O movimento em si fica pra quando houver mundo/posição.
        //dono.ficarInvulneravel(getDuracao());
    }
}
