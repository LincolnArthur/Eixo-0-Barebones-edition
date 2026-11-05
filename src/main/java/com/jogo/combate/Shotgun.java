package com.jogo.combate;

public class Shotgun extends ArmaDeFogo {
    private int quantidadeBalins;

    public Shotgun(String id, String nome, String descricao, int danoPorBalim, int quantidadeBalins, double chanceCritico) {
        super(id, nome, descricao, danoPorBalim, chanceCritico);
        this.quantidadeBalins = quantidadeBalins;
    }

    @Override
    protected int calcularDanoBase() {
        int total = 0;
        for (int i = 0; i < quantidadeBalins; i++) {
            total += dispararProjetil();
        }
        return total;
    }

}
