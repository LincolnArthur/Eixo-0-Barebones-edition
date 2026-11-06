package com.jogo.mundo;

import com.jogo.excecoes.SalaNaoEncontradaException;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// representa o mapa geral, no caso agrupamento de salas
// guarda o ponto inicial de spawn e centraliza a busca.
public class Mapa {

    private final String nome;
    private final Map<String, Sala> salas;
    //Não podem ser trocados depois
    
    private Sala salaInicial;

    public Mapa(String nome) {
        this.nome = nome;
        this.salas = new LinkedHashMap<>();
        //O id é a chave e a sala é o valor
        //Cada sala é uma consulta direta, como os .json
    }

    public String getNome() {
        return nome;
    }

    // add uma sala ao mapa
    public boolean adicionarSala(Sala sala) {
        if (sala == null || salas.containsKey(sala.getId())) {
            return false;
        }
        salas.put(sala.getId(), sala);

        // Se for a primeira sala adicionada, define-a como ponto de partida inicial
        if (salaInicial == null) {
            this.salaInicial = sala;
        }
        return true;
    }

    // define manualmente a sala de início do jogador
    //Antes só imprimia um aviso no console (o modelo não deve imprimir
    //nada, isso é papel da visão). Agora avisa quem chamou com exceção
    public void definirSalaInicial(String idSala) throws SalaNaoEncontradaException {
        Sala encontrada = salas.get(idSala);
        if (encontrada == null) {
            throw new SalaNaoEncontradaException(idSala);
        }
        this.salaInicial = encontrada;
    }

    public Sala getSalaInicial() {
        return salaInicial;
    }

    // busca uma sala pelo seu ID
    public Sala buscarSala(String idSala) {
        return salas.get(idSala);
    }

    public int totalSalas() {
        return salas.size();
    }

    public Map<String, Sala> getSalas() {
        return Collections.unmodifiableMap(salas);
    }
}
