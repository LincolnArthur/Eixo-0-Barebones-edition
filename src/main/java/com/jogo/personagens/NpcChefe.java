package com.jogo.personagens;

import java.util.List;

//O chefe antes da luta: NEXUS começa como um NPC (invulnerável, não
//ataca) que conversa com o jogador. Quando a última fala é dita, a
//conversa acaba e o NPC "desperta": despertar() devolve o Chefe de
//verdade, que entra na batalha. Igual ao NpcChefe do FXGL.
public class NpcChefe extends Npc {

    private final Chefe chefe;
    private boolean conversaCompleta = false;
    private boolean despertou = false;

    public NpcChefe(String nome, List<String> falas, Chefe chefe) {
        super(nome, falas);
        if (chefe == null) {
            throw new IllegalArgumentException("O NPC precisa saber em qual chefe vai se transformar");
        }
        this.chefe = chefe;
    }

    //Só fala; quando a fala da vez é a última, a luta está pronta pra começar
    @Override
    public String interagir(Personagem jogador) {
        if (estaNaUltimaFala()) {
            conversaCompleta = true;
            return getNome() + " desperta! A luta começou.";
        }
        return "";
    }

    public boolean conversaCompleta() {
        return conversaCompleta;
    }

    //Troca o NPC pelo chefe (uma vez só, e só depois da conversa inteira)
    public Chefe despertar() {
        if (!conversaCompleta) {
            throw new IllegalStateException(getNome() + " ainda não terminou de falar");
        }
        if (despertou) {
            throw new IllegalStateException(getNome() + " já despertou");
        }
        despertou = true;
        return chefe;
    }
}
