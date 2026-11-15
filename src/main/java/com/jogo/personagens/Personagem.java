package com.jogo.personagens;

import com.jogo.armadilhas.DamageSource;
import com.jogo.armadilhas.DamageTarget;
import com.jogo.armadilhas.Hitbox;

public class Personagem implements Atacavel, DamageTarget {

    private String nome;
    private int vidaAtual;
    private int vidaMaxima;

    // Posição espacial, hitbox e janela de invulnerabilidade (I-Frames) para interação com armadilhas
    private double x;
    private double y;
    private final Hitbox hitbox;
    private double invulnerabilityTimer = 0.0;
    private double ultimoKnockbackX = 0.0;
    private double ultimoKnockbackY = 0.0;

    public Personagem(String nome, int vidaMaxima) {
        this(nome, vidaMaxima, 0.0, 0.0, 32.0, 48.0);
    }

    public Personagem(String nome, int vidaMaxima, double x, double y) {
        this(nome, vidaMaxima, x, y, 32.0, 48.0);
    }

    public Personagem(String nome, int vidaMaxima, double x, double y, double largura, double altura) {
        this.nome = nome;
        this.vidaAtual = vidaMaxima;
        this.vidaMaxima = vidaMaxima;
        this.x = x;
        this.y = y;
        this.hitbox = new Hitbox(0, 0, largura, altura);
    }

    public String getNome() {
        return nome;
    }

    public int getVidaAtual() {
        return vidaAtual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    @Override
    public void tomarDano(int quantidade) {
        if (quantidade <= 0 || isInvulnerable()) {
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

    public void atualizar(double deltaTime) {
        if (deltaTime > 0 && invulnerabilityTimer > 0) {
            invulnerabilityTimer = Math.max(0.0, invulnerabilityTimer - deltaTime);
        }
    }

    public void moverPara(double novoX, double novoY) {
        this.x = novoX;
        this.y = novoY;
    }

    // === Implementação de DamageTarget (Desacoplamento com o Sistema de Armadilhas) ===

    @Override
    public void takeDamage(int damage, DamageSource source) {
        tomarDano(damage);
    }

    @Override
    public void applyKnockback(double forceX, double forceY) {
        this.ultimoKnockbackX = forceX;
        this.ultimoKnockbackY = forceY;
    }

    @Override
    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public Hitbox getHitbox() {
        return hitbox;
    }

    @Override
    public boolean isAlive() {
        return !estaMorto();
    }

    @Override
    public boolean isInvulnerable() {
        return invulnerabilityTimer > 0;
    }

    @Override
    public void activateInvulnerability(double durationSeconds) {
        if (durationSeconds > 0) {
            this.invulnerabilityTimer = Math.max(this.invulnerabilityTimer, durationSeconds);
        }
    }

    public double getUltimoKnockbackX() {
        return ultimoKnockbackX;
    }

    public double getUltimoKnockbackY() {
        return ultimoKnockbackY;
    }

    @Override
    public String toString() {
        return nome + " [Vida: " + vidaAtual + "/" + vidaMaxima + (estaMorto() ? " (Morto)" : "") + "]";
    }
}