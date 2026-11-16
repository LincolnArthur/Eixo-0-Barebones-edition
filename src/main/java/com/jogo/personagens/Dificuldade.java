package com.jogo.personagens;

//Dificuldade (no FXGL fica no menu de Opções): muda quanto dano o
//jogador recebe (ver Jogador.tomarDano). Enum com valor próprio em cada
//constante: a regra fica dentro do próprio tipo, sem switch.
public enum Dificuldade {
    FACIL("Fácil", 0.5),
    NORMAL("Normal", 1.0),
    DIFICIL("Difícil", 1.5);

    private final String nome;
    private final double multiplicadorDanoRecebido;

    Dificuldade(String nome, double multiplicadorDanoRecebido) {
        this.nome = nome;
        this.multiplicadorDanoRecebido = multiplicadorDanoRecebido;
    }

    public String getNome() {
        return nome;
    }

    //Dano depois da dificuldade (arredonda, e um golpe nunca vira 0)
    public int aplicar(int dano) {
        if (dano <= 0) {
            return 0;
        }
        return Math.max(1, (int) Math.round(dano * multiplicadorDanoRecebido));
    }
}
