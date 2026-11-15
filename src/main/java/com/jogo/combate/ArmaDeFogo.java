package com.jogo.combate;


//Tipo diferente de arma, utilizada em Sniper, por exemplo
//Alem de implementar tudo de arma de fogo, ela calcula o dano
//com base na classe projetil -lincoln
public class ArmaDeFogo extends Arma {
    private int dano;

    public ArmaDeFogo(String id, String nome, String descricao, int dano, double chanceCritico) {
        super(id, nome, descricao, chanceCritico); // adicionou as informações da arma de fogo
        this.dano = dano;
    }

    protected int dispararProjetil() {
        Projetil projetil = new Projetil(dano);
        return projetil.getDano();
    }

    @Override 
    protected int calcularDanoBase() {
        return dispararProjetil();
    }
}
