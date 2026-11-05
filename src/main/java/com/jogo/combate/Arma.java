package com.jogo.combate;

import java.util.Random;

import com.jogo.inventario.Categoria;
import com.jogo.inventario.Inventariavel;
import com.jogo.personagens.Personagem;

//Define a classe abstrata Arma
//Nela são criados os parametros e metodos de dano crítico
//E danoBase
//A relação entre causar dano e o dano base é definida aqui, mas o ato de causar dano é concreto e n muda mais (final)
//-lincoln

public abstract class Arma implements Inventariavel{

    private static int contadorInstancias = 0;

    private double chanceCritico;
    private Random gerador;

    private final String id; //Informações de cada classe que pode ir pro inventario
    private final String nome; //ass raioni
    private final String descricao;
    private final double tempoEspera; // Tempo de espera entre golpes em segundos 

    // Construtor completo com tempoEspera (velocidade de uso da arma)
    public Arma(String idBase, String nome, String descricao, double chanceCritico, double tempoEspera) {
        this.chanceCritico = chanceCritico;
        this.gerador = new Random();
        this.id = idBase + "-" + (++contadorInstancias); //Informações da arma
        this.nome = nome;
        this.descricao = descricao;
        this.tempoEspera = Math.max(0.01, tempoEspera);
    }

    // Sobrecarga de construtor: assume 1.0 segundo por padrão para armas antigas
    public Arma(String idBase, String nome, String descricao, double chanceCritico) {
        this(idBase, nome, descricao, chanceCritico, 1.0);
    }

    //É final pois é imutavel
    //O calc do danoBase é o mesmo pra TODOS
    //E o critico é igual pra todas as armas
    //-lincoln

    // São as definições de metodos da INterface Inventariavel
    //ONde basicamente dou um nome,ID e descrição pra cada arma criada ass Raioni
    @Override public String getId() { 
        return id; 
    }
    @Override 
    public String getNome() { 
        return nome; 
    }
    @Override
    public String getDescricao() { 
        return descricao; 
    }

    @Override
    public Categoria getCategoria() {
        return Categoria.ARMA;
    }

    // Retorna o tempo de espera em segundos entre ataques
    public double getTempoEspera() {
        return tempoEspera;
    }

    // Retorna a velocidade de ataque em golpes por segundo (1 / tempoEspera)
    public double getVelocidadeAtaque() {
        return 1.0 / tempoEspera;
    }

    public final int causarDano() {
        return causarDano(null); // aq nulo pq n tem alvo, mas o metodo com alvo é chamado
    }

    // Calcula o dano considerando quem é o alvo (permite bônus situacionais)
    public int causarDano(Personagem alvo) {
        int danoBase = calcularDanoBase(alvo);
        boolean critico = gerador.nextDouble() < chanceCritico;
        return critico ? danoBase * 2 : danoBase;
    }

    // Sobrecarga com alvo: por padrão, delega para o cálculo base sem alvo
    protected int calcularDanoBase(Personagem alvo) {
        return calcularDanoBase();
    }

    //Isso sim cada arma decide do jeito que quiser
    protected abstract int calcularDanoBase();
}
