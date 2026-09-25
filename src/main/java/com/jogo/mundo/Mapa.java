package com.jogo.mundo;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// representa o mapa geral, no caso agrupamento de salas
// guarda o ponto inicial de spawn e centraliza a busca.
public class Mapa {

    private final String nome;
    private final Map<String, Sala> salas;
    private Sala salaInicial;

    public Mapa(String nome) {
        this.nome = nome;
        this.salas = new LinkedHashMap<>();
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
    public void definirSalaInicial(String idSala) {
        Sala encontrada = salas.get(idSala);
        if (encontrada != null) {
            this.salaInicial = encontrada;
        } else {
            System.out.println("Aviso: Sala com ID '" + idSala + "' não encontrada no mapa.");
        }
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
