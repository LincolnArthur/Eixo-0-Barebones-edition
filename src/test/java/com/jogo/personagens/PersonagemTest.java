package com.jogo.personagens;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonagemTest {

    @Test
    @DisplayName("Nasce com a vida cheia")
    void nasceComVidaCheia() {
        Personagem p = new Personagem("Herói", 100);
        assertEquals(100, p.getVidaAtual());
        assertEquals(100, p.getVidaMaxima());
        assertTrue(p.estaSaudavel());
        assertFalse(p.estaMorto());
    }

    @Test
    @DisplayName("Falha: nome vazio ou nulo")
    void nomeInvalido() {
        assertThrows(IllegalArgumentException.class, () -> new Personagem("", 10));
        assertThrows(IllegalArgumentException.class, () -> new Personagem("   ", 10));
        assertThrows(IllegalArgumentException.class, () -> new Personagem(null, 10));
    }

    @Test
    @DisplayName("Falha: vida máxima zero ou negativa")
    void vidaInvalida() {
        assertThrows(IllegalArgumentException.class, () -> new Personagem("X", 0));
        assertThrows(IllegalArgumentException.class, () -> new Personagem("X", -5));
    }

    @Test
    @DisplayName("tomarDano desconta a vida")
    void tomarDano() {
        Personagem p = new Personagem("Goblin", 25);
        p.tomarDano(15);
        assertEquals(10, p.getVidaAtual());
        assertFalse(p.estaSaudavel());
    }

    @Test
    @DisplayName("Dano maior que a vida para em 0 e mata")
    void danoMaiorQueVida() {
        Personagem p = new Personagem("Goblin", 25);
        p.tomarDano(999);
        assertEquals(0, p.getVidaAtual());
        assertTrue(p.estaMorto());
    }

    @Test
    @DisplayName("Dano zero ou negativo é ignorado (não cura)")
    void danoInvalidoIgnorado() {
        Personagem p = new Personagem("Goblin", 25);
        p.tomarDano(0);
        p.tomarDano(-10);
        assertEquals(25, p.getVidaAtual());
    }

    @Test
    @DisplayName("curar sobe a vida até o máximo")
    void curar() {
        Personagem p = new Personagem("Herói", 100);
        p.tomarDano(30);
        p.curar(20);
        assertEquals(90, p.getVidaAtual());
        p.curar(500);
        assertEquals(100, p.getVidaAtual());
    }

    @Test
    @DisplayName("Falha: curar não revive morto nem aceita valor negativo")
    void curarInvalido() {
        Personagem p = new Personagem("Herói", 100);
        p.tomarDano(40);
        p.curar(-10);
        assertEquals(60, p.getVidaAtual());

        p.tomarDano(999);
        p.curar(50);
        assertEquals(0, p.getVidaAtual());
        assertTrue(p.estaMorto());
    }

    @Test
    @DisplayName("toString mostra vida e se está morto")
    void toStringMostraEstado() {
        Personagem p = new Personagem("Herói", 100);
        assertEquals("Herói [Vida: 100/100]", p.toString());
        p.tomarDano(100);
        assertTrue(p.toString().contains("(Morto)"));
    }
}
