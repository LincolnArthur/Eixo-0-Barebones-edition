package com.jogo.personagens;

import java.util.Random;

//Define a classe abstrata Arma
//Nela são criados os parametros e metodos de dano crítico
//E danoBase
//A relação entre causar dano e o dano base é definida aqui, mas o ato de causar dano é concreto e n muda mais (final)
//-lincoln

public abstract class Arma implements Item {

    private double chanceCritico;
    private Random gerador;

    private final String id; //Informações que cada classe considerada um Item vai ter que ter
    private final String nome; //ass raioni
    private final String descricao;

    public Arma(String id, String nome, String descricao, double chanceCritico) {
        this.chanceCritico = chanceCritico;
        this.gerador = new Random();
        this.id = id; //Informações da arma
        this.nome = nome;
        this.descricao = descricao;
    }

    //É final pois é imutavel
    //O calc do danoBase é o mesmo pra TODOS
    //E o critico é igual pra todas as armas
    //-lincoln

    // São as definições de metodos da INterface Item 
    //ONde basicamente dou um nome,ID e descrição pra cada arma criada ass Raioni
    @Override public String getId() { return id; }
    @Override public String getNome() { return nome; }
    @Override public String getDescricao() { return descricao; }
    public final int causarDano() {
        int danoBase = calcularDanoBase();
        boolean critico = gerador.nextDouble() < chanceCritico;
        return critico ? danoBase * 2 : danoBase;
    }


    //Isso sim cada arma decide do jeito que quiser
    protected abstract int calcularDanoBase();
}
