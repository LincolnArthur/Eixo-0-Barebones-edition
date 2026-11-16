package com.jogo.habilidades;

import com.jogo.combate.Ataque;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

//Classe-mãe de toda habilidade que causa dano num alvo.
//Abstrata: não existe "golpe de energia genérico", só golpes
//concretos (BolaDeFogo e os que vierem). O executar() já fica
//pronto aqui, então cada filho só precisa definir os números.
public abstract class GolpeDeEnergia extends Habilidade {
    private int dano;

    public GolpeDeEnergia(boolean aprendida, double tempoEspera, int custoEnergia, double duracao, int dano) {
        super(aprendida, tempoEspera, custoEnergia, duracao);
        this.dano = dano;
    }

    public int getDano() {
        return dano;
    }

    //Gancho: quantos turnos o alvo fica congelado. Padrão 0 (não
    //congela); o RaioDeGelo sobrescreve.
    public double getCongelamento() {
        return 0;
    }

    @Override
    public void executar(Jogador usuario, Personagem alvo) {
        if (alvo == null) {
            return; //Sem alvo, não tem em quem aplicar dano -lincoln
        }
        Ataque ataque = new Ataque(dano);
        ataque.aplicarEm(alvo);
        if (getCongelamento() > 0 && alvo instanceof Inimigo inimigo && !inimigo.estaMorto()) {
            inimigo.congelar(getCongelamento());
        }
    }
}
