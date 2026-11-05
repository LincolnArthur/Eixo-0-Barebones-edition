package com.jogo.combate;

public class Sabre extends Arma {
    private int dano;
    private int golpe = 0;

    public Sabre(String nome, String descricao, String tipo, int dano, double cooldown) {
        super(nome, descricao, tipo, cooldown);
        this.dano = dano;
    }
    @Override
    public int calcularDanoBase() {
        golpe++;
        if(golpe%3 == 0){ // a cada 3 golpes, o sabre causa 5 de dano adicional
            return dano +5;
        }
        return dano;
    }
}