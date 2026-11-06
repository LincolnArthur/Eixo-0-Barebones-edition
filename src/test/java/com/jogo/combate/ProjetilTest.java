package com.jogo.combate;

import com.jogo.personagens.Personagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProjetilTest {

    @Test
    @DisplayName("Projétil comum para no primeiro alvo vivo")
    void comum() {
        Personagem morto = new Personagem("Morto", 5);
        morto.tomarDano(5);
        Personagem a = new Personagem("A", 20);
        Personagem b = new Personagem("B", 20);
        new Projetil(8).atingir(List.of(morto, a, b));
        assertEquals(12, a.getVidaAtual());
        assertEquals(20, b.getVidaAtual());
    }

    @Test
    @DisplayName("Sobrecarga: atinge até a quantidade pedida")
    void quantidadeMaxima() {
        Personagem a = new Personagem("A", 20);
        Personagem b = new Personagem("B", 20);
        Personagem c = new Personagem("C", 20);
        new Projetil(5).atingir(List.of(a, b, c), 3);
        assertEquals(15, a.getVidaAtual());
        assertEquals(15, b.getVidaAtual());
        assertEquals(15, c.getVidaAtual());
    }

    @Test
    @DisplayName("Lista vazia ou só de mortos: não quebra")
    void semAlvos() {
        assertDoesNotThrow(() -> new Projetil(5).atingir(List.of()));
        Personagem morto = new Personagem("Morto", 5);
        morto.tomarDano(5);
        assertDoesNotThrow(() -> new Projetil(5).atingir(List.of(morto), 2));
    }
}
