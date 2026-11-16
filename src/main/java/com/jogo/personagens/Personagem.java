package com.jogo.personagens;

import com.jogo.armadilhas.DamageSource;
import com.jogo.armadilhas.DamageTarget;
import com.jogo.armadilhas.Hitbox;
import com.jogo.combate.Atacavel;

public class Personagem implements Atacavel, DamageTarget {

    //O estado que cada personagem carrega individualmente
    private String nome; // private: Ninguém altera fora a classe -lincoln
    private int vidaAtual;
    private int vidaMaxima;

    // Posição espacial, hitbox e janela de invulnerabilidade (I-Frames) para interação com armadilhas
    private double x;
    private double y;
    private final Hitbox hitbox;
    private double invulnerabilityTimer = 0.0;
    private double ultimoKnockbackX = 0.0;
    private double ultimoKnockbackY = 0.0;

    //Construtor: Roda quando um new Personagem é criado -lincoln
    public Personagem(String nome, int vidaMaxima) {
        this(nome, vidaMaxima, 0.0, 0.0, 32.0, 48.0);
    }

    public Personagem(String nome, int vidaMaxima, double x, double y) {
        this(nome, vidaMaxima, x, y, 32.0, 48.0);
    }

    public Personagem(String nome, int vidaMaxima, double x, double y, double largura, double altura) {
        //Validação: personagem sem nome ou sem vida é erro de quem
        //escreveu o código, não algo que acontece jogando. Por isso
        //IllegalArgumentException (não precisa de try/catch)
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Personagem precisa de um nome");
        }
        if (vidaMaxima <= 0) {
            throw new IllegalArgumentException("Vida máxima deve ser maior que zero: " + vidaMaxima);
        }
        this.nome = nome; //this: separa o parâmetro do construtor do da classe
                          //Os dois tem o mesmo nome, então é necessário
        this.vidaAtual = vidaMaxima; // Cmc com vida cheia -lincoln
        this.vidaMaxima = vidaMaxima;
        this.x = x;
        this.y = y;
        this.hitbox = new Hitbox(0, 0, largura, altura);
    }

    //Usa chamando um new
    //Tipo struct
    //Personagem Herói = new Personagem("Herói", 20)

    //Pega o nome do obj gerado com o new -lincoln
    public String getNome() {
        return nome;
    }

    //Só leitura: a visão (TelaConsole) e os testes precisam ver a vida.
    //Não tem set: a vida só muda por tomarDano() e curar()
    public int getVidaAtual() {
        return vidaAtual;
    }

    public int getVidaMaxima() {
        return vidaMaxima;
    }

    //Comportamentos: Ações que o personagem sabe executar

    @Override
    public void tomarDano(int quantidade) {
        if (quantidade <= 0 || isInvulnerable()) {
            return;
        }
        this.vidaAtual = Math.max(0, this.vidaAtual - quantidade); //Garante que vida n fique negativa -Raioni
    }

    //Substitui o antigo setVidaAtual: a vida só sobe por aqui, e o
    //próprio personagem garante as regras (não passa da máxima, não
    //revive morto). Quem cura (ex: ModuloCura) só diz quanto.
    public void curar(int quantidade) {
        if (quantidade <= 0 || estaMorto()) {
            return;
        }
        this.vidaAtual = Math.min(vidaMaxima, this.vidaAtual + quantidade);
    }

    //Só as filhas (ex.: Jogador ao subir de nível) podem aumentar a vida
    //máxima; quem está fora da família não mexe
    protected void aumentarVidaMaxima(int quantidade) {
        if (quantidade > 0) {
            this.vidaMaxima += quantidade;
        }
    }

    //Verifica se o character está morto -lincoln
    @Override
    public boolean estaMorto() {
        return vidaAtual <= 0;
    }

    //verifica se o character está com vida cheia -lincoln
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

    // sobrescrita do toString() herdado de Object
    // permite exibir informações do personagem de forma legivel, nemilha tinha pedido pra usar isso aqui na aula passada - hiago
    @Override
    public String toString() {
        return nome + " [Vida: " + vidaAtual + "/" + vidaMaxima + (estaMorto() ? " (Morto)" : "") + "]";
    }
}
