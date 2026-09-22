package com.jogo.personagens;

import java.util.Random;

//Define a classe abstrata Arma
//Nela são criados os parametros e metodos de dano crítico
//E danoBase
//A relação entre causar dano e o dano base é definida aqui, mas o ato de causar dano é concreto e n muda mais (final)
//-lincoln

public abstract class Arma {

    private double chanceCritico;
    private Random gerador;

    public Arma(double chanceCritico) {
        this.chanceCritico = chanceCritico;
        this.gerador = new Random();
    }

    //É final pois é imutavel
    //O calc do danoBase é o mesmo pra TODOS
    //E o critico é igual pra todas as armas
    //-lincoln

    public final int causarDano() {
        int danoBase = calcularDanoBase();
        boolean critico = gerador.nextDouble() < chanceCritico;
        return critico ? danoBase * 2 : danoBase;
    }


    //Isso sim cada arma decide do jeito que quiser
    protected abstract int calcularDanoBase();
}
