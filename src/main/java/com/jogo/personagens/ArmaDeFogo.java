package com.jogo.personagens;

public class ArmaDeFogo extends Arma {
    private int dano;
    private double chanceErrar;

    public ArmaDeFogo(int dano, double chanceErrar, double chanceCritico) {
        super(chanceCritico);
        this.dano = dano;
        this.chanceErrar = chanceErrar;
    }

    @Override 
    protected int calcularDanoBase() {
        Projetil projetil = new Projetil(dano, chanceErrar);
        return projetil.getDano();
    }
}
