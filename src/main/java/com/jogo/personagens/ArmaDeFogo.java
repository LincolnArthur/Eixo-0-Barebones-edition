package com.jogo.personagens;

//Tipo diferente de arma, utilizada em Sniper, por exemplo
//Alem de implementar tudo de arma de fogo, ela calcula o dano
//com base na classe projetil -lincoln
public class ArmaDeFogo extends Arma {
    private int dano;
    private double chanceErrar;

    public ArmaDeFogo(String id, String nome, String descricao, int dano, double chanceErrar, double chanceCritico) {
        super(id, nome, descricao, chanceCritico); // adicionou as informações da arma de fogo
        this.dano = dano;
        this.chanceErrar = chanceErrar;
    }

    @Override 
    protected int calcularDanoBase() {
        Projetil projetil = new Projetil(dano, chanceErrar);
        return projetil.getDano();
    }
}
