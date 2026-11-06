package com.jogo.personagens;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NivelTest {

    @Test
    @DisplayName("Começa no nível 1 com 0 de XP")
    void estadoInicial() {
        Nivel nivel = new Nivel();
        assertEquals(1, nivel.getNivelAtual());
        assertEquals(0, nivel.getExperienciaAtual());
        assertEquals(100, nivel.getExperienciaParaProximoNivel());
    }

    @Test
    @DisplayName("XP abaixo do necessário não sobe de nível")
    void naoSobe() {
        Nivel nivel = new Nivel();
        assertFalse(nivel.ganharExperiencia(99));
        assertEquals(1, nivel.getNivelAtual());
    }

    @Test
    @DisplayName("Sobe de nível, guarda o que sobrou e aumenta a meta em 50%")
    void sobeGuardandoSobra() {
        Nivel nivel = new Nivel();
        assertTrue(nivel.ganharExperiencia(130));
        assertEquals(2, nivel.getNivelAtual());
        assertEquals(30, nivel.getExperienciaAtual());
        assertEquals(150, nivel.getExperienciaParaProximoNivel());
    }

    @Test
    @DisplayName("Muito XP de uma vez sobe mais de um nível")
    void sobeVariosNiveis() {
        Nivel nivel = new Nivel();
        nivel.ganharExperiencia(250); //100 pro 2, 150 pro 3
        assertEquals(3, nivel.getNivelAtual());
        assertEquals(0, nivel.getExperienciaAtual());
    }

    @Test
    @DisplayName("Falha: XP negativo")
    void xpNegativo() {
        assertThrows(IllegalArgumentException.class, () -> new Nivel().ganharExperiencia(-1));
    }
}
