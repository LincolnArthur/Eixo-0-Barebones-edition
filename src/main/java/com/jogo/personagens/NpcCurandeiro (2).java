package com.jogo.personagens;

import java.util.List;

//NPC que conversa e também cura o jogador (vida cheia) ao interagir.
//Tem um número limitado de curas pra não virar vida infinita: cada cura
//gasta uma, e só gasta se o jogador realmente estiver ferido.
public class NpcCurandeiro extends Npc {

    private int curasRestantes;

    public NpcCurandeiro(String nome, List<String> falas, int curas) {
        super(nome, falas);
        if (curas < 0) {
            throw new IllegalArgumentException("Número de curas não pode ser negativo: " + curas);
        }
        this.curasRestantes = curas;
    }

    @Override
    public String interagir(Personagem jogador) {
        if (jogador == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo");
        }
        if (jogador.estaMorto()) {
            return ""; //curar() não revive ninguém
        }
        if (jogador.estaSaudavel()) {
            return "Você está com a vida cheia, não precisa de cura.";
        }
        if (curasRestantes <= 0) {
            return "Não tenho mais curas para dar.";
        }
        jogador.curar(jogador.getVidaMaxima()); //curar() já limita na vida máxima
        curasRestantes--;
        return "Vida de " + jogador.getNome() + " restaurada! (curas restantes: " + curasRestantes + ")";
    }

    public int getCurasRestantes() {
        return curasRestantes;
    }
}
