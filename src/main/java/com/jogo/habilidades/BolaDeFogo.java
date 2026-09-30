package com.jogo.habilidades;

import com.jogo.combate.Ataque;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

public class BolaDeFogo extends Habilidade {
    private int dano;

    public BolaDeFogo(boolean aprendida, double tempoEspera, int custoEnergia, double duracao, int dano) {
        super(aprendida, tempoEspera, custoEnergia, duracao);
        this.dano = dano;
    }

    @Override
    public void executar(Jogador usuario, Personagem alvo) {
        if (alvo == null) {
            return; //Sem alvo, não tem em quem aplicar dano -lincoln
        }
        Ataque ataque = new Ataque(dano);
        ataque.aplicarEm(alvo);
    }
}
