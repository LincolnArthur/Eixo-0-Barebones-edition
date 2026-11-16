package com.jogo.habilidades;

import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Personagem;

import java.util.List;

//Magia final, liberada no nível 3. Gasta TODA a energia (só dá pra usar
//com a barra cheia) e atinge todos os inimigos da sala de uma vez: cada
//um toma dano igual à vida que tem, ou seja, morre. Quem quiser resistir
//sobrescreve Inimigo.danoDeBuracoNegro() (o chefe só perde 25%).
public class BuracoNegro extends Habilidade {

    //Quem está na sala no momento do uso (o controle avisa com mirar())
    private List<? extends Personagem> alvos = List.of();

    public BuracoNegro(boolean aprendida, double tempoEspera) {
        super(aprendida, tempoEspera, 0, 0.0);
    }

    //Sobrescreve o custo fixo: custa a energia máxima inteira
    @Override
    public int custoPara(Jogador player) {
        return player.getEnergiaMaxima();
    }

    //Diz quem está na sala antes de usar (ex.: Batalha.usarBuracoNegro)
    public void mirar(List<? extends Personagem> alvosDaSala) {
        this.alvos = alvosDaSala == null ? List.of() : List.copyOf(alvosDaSala);
    }

    @Override
    public void executar(Jogador usuario, Personagem alvo) {
        for (Personagem p : alvos) {
            if (!p.estaMorto()) {
                p.tomarDano(danoPara(p));
            }
        }
        if (alvo != null && !alvos.contains(alvo) && !alvo.estaMorto()) {
            alvo.tomarDano(danoPara(alvo));
        }
        alvos = List.of(); //cada uso precisa mirar de novo
    }

    //Polimorfismo: cada inimigo diz quanto o buraco negro tira dele
    private int danoPara(Personagem p) {
        return p instanceof Inimigo inimigo ? inimigo.danoDeBuracoNegro() : p.getVidaAtual();
    }
}
