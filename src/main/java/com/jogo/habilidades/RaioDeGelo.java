package com.jogo.habilidades;

//Segundo golpe de energia, liberado no nível 2. Usa o mesmo executar()
//do GolpeDeEnergia (dano no alvo); só sobrescreve o gancho
//getCongelamento(): quem for atingido fica congelado e perde os
//próximos turnos (no FXGL são segundos; aqui, 1 turno = 1 segundo).
public class RaioDeGelo extends GolpeDeEnergia {

    private final double turnosCongelado;

    public RaioDeGelo(boolean aprendida, double tempoEspera, int custoEnergia, int dano, double turnosCongelado) {
        super(aprendida, tempoEspera, custoEnergia, 0.0, dano);
        if (turnosCongelado <= 0) {
            throw new IllegalArgumentException("Congelamento precisa durar algum tempo");
        }
        this.turnosCongelado = turnosCongelado;
    }

    @Override
    public double getCongelamento() {
        return turnosCongelado;
    }
}
