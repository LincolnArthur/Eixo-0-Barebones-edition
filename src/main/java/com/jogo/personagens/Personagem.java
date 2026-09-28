package com.jogo.personagens;

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