package com.jogo.excecoes;

//Lançada quando o mapa recebe o id de uma sala que não foi cadastrada.
public class SalaNaoEncontradaException extends JogoException {

    public SalaNaoEncontradaException(String idSala) {
        super("Sala não encontrada no mapa: " + idSala);
    }
}
