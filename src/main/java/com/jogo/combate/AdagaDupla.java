package com.jogo.combate;
// veloz e da mais dano no 5 hit
public class AdagaDupla extends Arma {

    private static final int GOLPES_PARA_COMBO = 5;
    private static final int BONUS_QUINTO_HIT = 15;

    private final int dano;
    private int contadorGolpes;

    // Construtor completo: permite especificar tempo de espera customizado
    public AdagaDupla(String id, String nome, String descricao, int dano, double chanceCritico, double tempoEspera) {
        super(id, nome, descricao, chanceCritico, tempoEspera);
        this.dano = dano;
        this.contadorGolpes = 0;
    }

    // Sobrecarga com tempo de espera padrão rápido (0.25s = 4 golpes por segundo)
    public AdagaDupla(String id, String nome, String descricao, int dano, double chanceCritico) {
        this(id, nome, descricao, dano, chanceCritico, 0.25);
    }

    @Override
    protected int calcularDanoBase() {
        contadorGolpes++;

        // Ao atingir o 5 hit, aplica o golpe finalizador e reseta o combo
        if (contadorGolpes >= GOLPES_PARA_COMBO) {
            contadorGolpes = 0;
            return dano + BONUS_QUINTO_HIT;
        }
        return dano;
    }
    public void reiniciarCombo() {
        this.contadorGolpes = 0;
    }
}

