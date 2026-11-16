package com.jogo.personagens;

import com.jogo.combate.Arma;
import com.jogo.combate.Atacante;
import com.jogo.combate.Ataque;
import com.jogo.combate.ConcedeExperiencia;

//Classe herda metodos e estados da superclasse
//Tipo abstrato, por conta do polimorfismo.
//Cada tipo diferente de inimigo vai atacar de uma forma diferente
//attack() é abstrato por isso. -lincoln
//
//getExperienciaConcedida() (da interface ConcedeExperiencia) não é
//escrito aqui: cada filha é obrigada a dizer quanto vale. Antes era um
//campo que nunca recebia valor, então todo inimigo dava 0 de XP.
public abstract class Inimigo extends Personagem implements Atacante, ConcedeExperiencia {

    private final Arma arma;
    //Turnos que ainda faltam congelado (Raio de Gelo). Congelado não ataca.
    private double turnosCongelado = 0;

    public Inimigo(String nome, int vidaMaxima, Arma arma) {
        super(nome, vidaMaxima);
        this.arma = arma;
    }


    // Igual pra QUALQUER inimigo: cria o Ataque a partir da própria arma e aplica -lincoln
    @Override
    public void iniciarAtaque(Personagem vitima) {
        Ataque ataque = new Ataque(arma.causarDano(vitima));
        ataque.aplicarEm(vitima);
    }

    public Arma getArma() {
        return arma;
    }

    //Nome do ataque pra visão mostrar. Padrão: o nome da arma; o Chefe
    //sobrescreve com o nome do ataque da vez (Rajada, Investida...)
    public String getNomeAtaque() {
        return arma.getNome();
    }

    //O que a visão deve mostrar sobre o último turno deste inimigo. Padrão:
    //nada (a Batalha mostra só o dano). O Chefe sobrescreve pra contar o
    //aviso e o ataque da vez
    public String descreverUltimaAcao() {
        return "";
    }

    //=== Raio de Gelo ===

    public void congelar(double turnos) {
        if (turnos <= 0) {
            throw new IllegalArgumentException("Tempo de congelamento precisa ser positivo");
        }
        turnosCongelado = Math.max(turnosCongelado, turnos);
    }

    public boolean estaCongelado() {
        return turnosCongelado > 0;
    }

    //Chamado pelo controle no turno do inimigo: se estava congelado, gasta
    //um turno do gelo e devolve true (ele perde esse turno)
    public boolean passarTurnoCongelado() {
        if (!estaCongelado()) {
            return false;
        }
        turnosCongelado = Math.max(0, turnosCongelado - 1);
        return true;
    }

    public double getTurnosCongelado() {
        return turnosCongelado;
    }

    //=== Buraco Negro ===

    //Quanto o Buraco Negro tira deste inimigo. Padrão: a vida inteira (morre)
    public int danoDeBuracoNegro() {
        return getVidaAtual();
    }
}
