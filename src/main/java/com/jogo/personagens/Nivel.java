package com.jogo.personagens;

public class Nivel {
    private int nivelAtual;
    private int experienciaAtual;
    private int experienciaParaProximoNivel;

    public Nivel() {
        this.nivelAtual = 1;
        this.experienciaAtual = 0;
        this.experienciaParaProximoNivel = 100;
    }

    //Devolve true se subiu de nivel
    //Quem chama decide oq fazer com essa info -lincoln
    public boolean ganharExperiencia(int quantidade) {
        if (quantidade < 0) {
            throw new IllegalArgumentException("Experiência não pode ser negativa: " + quantidade);
        }
        experienciaAtual += quantidade;
        //while (e não if): muito XP de uma vez pode subir mais de um nível
        boolean subiu = false;
        while (experienciaAtual >= experienciaParaProximoNivel) {
            subirNivel();
            subiu = true;
        }
        return subiu;
    }

    private void subirNivel() {
        nivelAtual++;
        experienciaAtual -= experienciaParaProximoNivel;
        experienciaParaProximoNivel = (int)(experienciaParaProximoNivel * 1.5);
    }

    public int getNivelAtual() {
        return nivelAtual;
    }

    public int getExperienciaAtual() {
        return experienciaAtual;
    }

    public int getExperienciaParaProximoNivel() {
        return experienciaParaProximoNivel;
    }
}
