package com.jogo.personagens;

import java.util.List;

//NPC que conversa e também cura o jogador (vida cheia) ao interagir.
//Igual ao FXGL: cada cura custa energia do jogador (custoEnergia) e gasta
//uma das curas da curandeira, então não vira vida infinita. De vida
//cheia, ou sem energia suficiente, nada é gasto.
public class NpcCurandeiro extends Npc {

    public static final int CUSTO_ENERGIA_PADRAO = 20;
    public static final int CURAS_PADRAO = 3;

    private int curasRestantes;
    private final int custoEnergia;

    public NpcCurandeiro(String nome, List<String> falas, int curas) {
        this(nome, falas, curas, CUSTO_ENERGIA_PADRAO);
    }

    public NpcCurandeiro(String nome, List<String> falas, int curas, int custoEnergia) {
        super(nome, falas);
        if (curas < 0) {
            throw new IllegalArgumentException("Número de curas não pode ser negativo: " + curas);
        }
        if (custoEnergia < 0) {
            throw new IllegalArgumentException("Custo de energia não pode ser negativo: " + custoEnergia);
        }
        this.curasRestantes = curas;
        this.custoEnergia = custoEnergia;
    }

    @Override
    public String interagir(Personagem personagem) {
        if (personagem == null) {
            throw new IllegalArgumentException("Jogador não pode ser nulo");
        }
        if (personagem.estaMorto()) {
            return ""; //curar() não revive ninguém
        }
        if (personagem.estaSaudavel()) {
            return "Você está com a vida cheia, não precisa de cura.";
        }
        if (curasRestantes <= 0) {
            return "Não tenho mais curas para dar.";
        }
        //Só o Jogador tem energia pra pagar a cura
        if (!(personagem instanceof Jogador jogador) || !jogador.energiaSuficiente(custoEnergia)) {
            return "Preciso de " + custoEnergia + " de energia sua pra te curar.";
        }
        jogador.consumirEnergia(custoEnergia);
        jogador.curar(jogador.getVidaMaxima()); //curar() já limita na vida máxima
        curasRestantes--;
        return "Vida de " + jogador.getNome() + " restaurada! (-" + custoEnergia
                + " de energia, curas restantes: " + curasRestantes + ")";
    }

    public int getCurasRestantes() {
        return curasRestantes;
    }

    public int getCustoEnergia() {
        return custoEnergia;
    }
}
