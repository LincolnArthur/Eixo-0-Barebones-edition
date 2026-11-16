package com.jogo.personagens;

import java.util.List;

//NPC (personagem não jogável) amigável. É um Personagem, igual ao
//Inimigo, mas do lado oposto: não ataca, não morre e só interage com o
//jogador (conversar, curar...).
//
//Abstrata pelo mesmo motivo do Inimigo: cada tipo de NPC faz uma coisa
//diferente ao interagir, então interagir() é abstrato e cada filha é
//obrigada a escrever o seu (polimorfismo).
//
//As falas giram em ciclo: a cada conversa o NPC diz a próxima, e depois
//da última volta pra primeira.
public abstract class Npc extends Personagem {

    private final List<String> falas;
    private int indiceFala = 0;

    public Npc(String nome, List<String> falas) {
        //Personagem exige vida > 0, então o NPC tem vida 1. Ele nunca
        //perde essa vida: o tomarDano() abaixo é de propósito sem efeito.
        super(nome, 1);
        if (falas == null || falas.isEmpty()) {
            throw new IllegalArgumentException("NPC precisa de pelo menos uma fala");
        }
        for (String fala : falas) {
            if (fala == null || fala.isBlank()) {
                throw new IllegalArgumentException("NPC não pode ter fala vazia");
            }
        }
        this.falas = List.copyOf(falas); //cópia imutável: ninguém de fora muda as falas
    }

    //O que o NPC faz com o jogador ao interagir. Devolve a descrição do
    //efeito pra visão mostrar ("" se não aconteceu nada): o modelo não
    //imprime, igual ao resto do projeto.
    public abstract String interagir(Personagem jogador);

    //Invulnerável: nada que cause dano (golpe, tiro, armadilha) afeta o NPC
    @Override
    public void tomarDano(int quantidade) {
        //de propósito sem efeito
    }

    public String getFalaAtual() {
        return falas.get(indiceFala);
    }

    //Passa pra próxima fala; depois da última volta pra primeira
    public String avancarFala() {
        indiceFala = (indiceFala + 1) % falas.size();
        return getFalaAtual();
    }

    @Override
    public String toString() {
        return getNome() + " (NPC)";
    }
}
