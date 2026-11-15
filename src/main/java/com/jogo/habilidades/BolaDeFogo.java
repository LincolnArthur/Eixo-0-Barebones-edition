package com.jogo.habilidades;

//Um golpe de energia concreto. Herda o dano e o executar() do
//GolpeDeEnergia; um efeito extra (ex: queimadura) entraria aqui,
//sobrescrevendo o executar() e chamando super.executar().
public class BolaDeFogo extends GolpeDeEnergia {

    public BolaDeFogo(boolean aprendida, double tempoEspera, int custoEnergia, double duracao, int dano) {
        super(aprendida, tempoEspera, custoEnergia, duracao, dano);
    }
}
