package com.jogo.personagens;

import java.util.List;

//NPC que só conversa: dá dicas sobre o jogo. Não faz nada além de
//falar, então interagir() não tem efeito no jogador.
public class NpcInformante extends Npc {

    public NpcInformante(String nome, List<String> falas) {
        super(nome, falas);
    }

    @Override
    public String interagir(Personagem jogador) {
        return ""; //só fala, sem efeito
    }
}
