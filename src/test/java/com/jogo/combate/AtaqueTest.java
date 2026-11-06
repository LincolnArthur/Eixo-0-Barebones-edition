package com.jogo.combate;

import com.jogo.personagens.Personagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AtaqueTest {

    @Test
    @DisplayName("Aplica o dano no alvo")
    void aplica() {
        Personagem alvo = new Personagem("Alvo", 30);
        new Ataque(12).aplicarEm(alvo);
        assertEquals(18, alvo.getVidaAtual());
    }

    @Test
    @DisplayName("Alvo nulo ou morto: não faz nada nem quebra")
    void alvoInvalido() {
        assertDoesNotThrow(() -> new Ataque(5).aplicarEm(null));
        Personagem morto = new Personagem("Morto", 10);
        morto.tomarDano(10);
        new Ataque(5).aplicarEm(morto);
        assertEquals(0, morto.getVidaAtual());
    }

    @Test
    @DisplayName("Falha: dano negativo")
    void danoNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Ataque(-1));
    }
}
