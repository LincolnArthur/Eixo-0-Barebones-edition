package com.jogo.personagens;

import com.jogo.combate.Atacavel;

public class Personagem implements Atacavel {

    private String nome;
    private int vidaAtual;
    private int vidaMaxima;

    public Personagem(String nome, int vidaMaxima) { 
        this.nome = nome;
        this.vidaAtual = vidaMaxima;
        this.vidaMaxima = vidaMaxima;
    }

    public String getNome() {
        return nome;
    }
    public void setVidaMaxima(int vidaMaxima){
        this.vidaMaxima=vidaMaxima;
    }
    public int getVidaMaxima(){
        return this.vidaMaxima;
    }
    public int getVidaAtual(){
     return this.vidaAtual;   
    }
    public void setVidaAtual(int vidaAtual){
        this.vidaAtual=vidaAtual;
    }

    @Override
    public void tomarDano(int quantidade) {
        if (quantidade <= 0) {
            return;
        }
        this.vidaAtual = Math.max(0, this.vidaAtual - quantidade);
    }

    @Override 
    public boolean estaMorto() {
        return vidaAtual <= 0;
    }

    public boolean estaSaudavel() {
        return vidaAtual == vidaMaxima;
    }

    @Override
    public String toString() {
        return nome + " [Vida: " + vidaAtual + "/" + vidaMaxima + (estaMorto() ? " (Morto)" : "") + "]";
    }
}