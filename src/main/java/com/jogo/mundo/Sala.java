package com.jogo.mundo;

import com.jogo.inventario.Inventariavel;
import com.jogo.personagens.Inimigo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Representa uma sala ou câmara no mapa do Metroidvania.
// Em vez de fases lineares, o jogo usa salas conectadas em um Grafo.
public class Sala {

    private final String id;
    private final String nome;
    private final String descricao;

    // Conexões com outras salas (ex: "norte", "sul", "leste", "oeste")
    // Usa LinkedHashMap para manter a ordem em que as saídas foram cadastradas
    private final Map<String, Sala> saidas;

    // Entidades e itens contidos nesta sala
    private final List<Inimigo> inimigos;
    private final List<Inventariavel> itens;

    // Se é um ponto de salvamento/descanso (ex: banco de descanso)
    private boolean pontoDeSalvamento;

    public Sala(String id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.saidas = new LinkedHashMap<>();
        this.inimigos = new ArrayList<>();
        this.itens = new ArrayList<>();
        this.pontoDeSalvamento = false;
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public boolean isPontoDeSalvamento() {
        return pontoDeSalvamento;
    }

    public void setPontoDeSalvamento(boolean pontoDeSalvamento) {
        this.pontoDeSalvamento = pontoDeSalvamento;
    }

    // Conecta uma saída desta sala para outra (unidirecional)
    public void conectar(String direcao, Sala destino) {
        if (direcao != null && destino != null) {
            saidas.put(direcao.toLowerCase(), destino);
        }
    }

    // Conecta duas salas em ambos os sentidos (ida e volta)
    public void conectarBidirecional(String direcaoIda, String direcaoVolta, Sala destino) {
        this.conectar(direcaoIda, destino);
        destino.conectar(direcaoVolta, this);
    }

    public Sala getSaida(String direcao) {
        if (direcao == null) return null;
        return saidas.get(direcao.toLowerCase());
    }

    public Map<String, Sala> getSaidas() {
        return Collections.unmodifiableMap(saidas);
    }

    public void adicionarInimigo(Inimigo inimigo) {
        if (inimigo != null) {
            inimigos.add(inimigo);
        }
    }

    public List<Inimigo> getInimigos() {
        return Collections.unmodifiableList(inimigos);
    }

    public void adicionarItem(Inventariavel item) {
        if (item != null) {
            itens.add(item);
        }
    }

    public List<Inventariavel> getItens() {
        return Collections.unmodifiableList(itens);
    }

    //O antigo descrever(), que imprimia a sala, foi pra visão
    //(TelaConsole.mostrarSala): a sala só guarda dados
}
