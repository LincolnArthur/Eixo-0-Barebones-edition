package com.jogo.mundo;

import com.jogo.excecoes.SalaNaoEncontradaException;
import com.jogo.personagens.Melee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MapaSalaTest {

    @Test
    @DisplayName("A primeira sala adicionada vira a inicial")
    void salaInicial() {
        Mapa mapa = new Mapa("Mapa");
        Sala a = new Sala("a", "A", "");
        Sala b = new Sala("b", "B", "");
        assertTrue(mapa.adicionarSala(a));
        assertTrue(mapa.adicionarSala(b));
        assertSame(a, mapa.getSalaInicial());
        assertEquals(2, mapa.totalSalas());
    }

    @Test
    @DisplayName("Falha: sala nula ou com id repetido")
    void salaInvalida() {
        Mapa mapa = new Mapa("Mapa");
        mapa.adicionarSala(new Sala("a", "A", ""));
        assertFalse(mapa.adicionarSala(null));
        assertFalse(mapa.adicionarSala(new Sala("a", "Outra", "")));
    }

    @Test
    @DisplayName("Troca a sala inicial por id")
    void trocaInicial() throws Exception {
        Mapa mapa = new Mapa("Mapa");
        mapa.adicionarSala(new Sala("a", "A", ""));
        Sala b = new Sala("b", "B", "");
        mapa.adicionarSala(b);
        mapa.definirSalaInicial("b");
        assertSame(b, mapa.getSalaInicial());
        assertSame(b, mapa.buscarSala("b"));
    }

    @Test
    @DisplayName("Exceção: sala inicial com id que não existe")
    void inicialInexistente() {
        Mapa mapa = new Mapa("Mapa");
        Sala a = new Sala("a", "A", "");
        mapa.adicionarSala(a);
        assertThrows(SalaNaoEncontradaException.class, () -> mapa.definirSalaInicial("z"));
        assertSame(a, mapa.getSalaInicial()); //não mudou
    }

    @Test
    @DisplayName("Conexão de ida e volta, sem diferenciar maiúscula")
    void conexoes() {
        Sala a = new Sala("a", "A", "");
        Sala b = new Sala("b", "B", "");
        a.conectarBidirecional("Norte", "SUL", b);
        assertSame(b, a.getSaida("norte"));
        assertSame(a, b.getSaida("Sul"));
        assertNull(a.getSaida("leste"));
        assertNull(a.getSaida(null));
    }

    @Test
    @DisplayName("Listas da sala são só leitura por fora")
    void listasImutaveis() {
        Sala a = new Sala("a", "A", "");
        a.adicionarInimigo(new Melee("M", 10, 1, 0.0));
        a.adicionarInimigo(null);
        assertEquals(1, a.getInimigos().size());
        assertThrows(UnsupportedOperationException.class, () -> a.getInimigos().clear());
        assertThrows(UnsupportedOperationException.class, () -> a.getSaidas().clear());
    }
}
