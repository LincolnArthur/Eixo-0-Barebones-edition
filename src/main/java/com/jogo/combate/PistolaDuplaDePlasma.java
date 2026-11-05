package com.jogo.combate;

public class PistolaDuplaDePlasma extends ArmaDeFogo {

    public PistolaDuplaDePlasma(String id, String nome, String descricao, int danoPorTiro, double chanceCritico) {
        super(id, nome, descricao, danoPorTiro, chanceCritico);
    }

    @Override
    protected int calcularDanoBase() {
        return dispararProjetil() + dispararProjetil();
    }
}
