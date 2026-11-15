package com.jogo.visao;

import com.jogo.mundo.Sala;
import com.jogo.personagens.Personagem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

//A TelaConsole recebe um PrintStream: o teste "captura" o que seria
//impresso e confere o texto
class TelaConsoleTest {

    private ByteArrayOutputStream capturado;
    private TelaConsole tela;

    @BeforeEach
    void criar() {
        capturado = new ByteArrayOutputStream();
        tela = new TelaConsole(new PrintStream(capturado, true, StandardCharsets.UTF_8));
    }

    private String saida() {
        return capturado.toString(StandardCharsets.UTF_8);
    }

    @Test
    @DisplayName("Mostra a barra de vida proporcional")
    void barraDeVida() {
        Personagem p = new Personagem("Herói", 100);
        p.tomarDano(50);
        tela.mostrarPersonagem(p);
        assertTrue(saida().contains("Herói [#####-----] 50/100"));
    }

    @Test
    @DisplayName("Erro aparece marcado com [!]")
    void erro() {
        tela.mostrarErro("deu ruim");
        assertEquals("[!] deu ruim" + System.lineSeparator(), saida());
    }

    @Test
    @DisplayName("mostrarSala lista as saídas")
    void sala() {
        Sala a = new Sala("a", "Laboratório", "Escuro");
        a.conectar("norte", new Sala("b", "Corredor", ""));
        tela.mostrarSala(a);
        assertTrue(saida().contains("LABORATÓRIO"));
        assertTrue(saida().contains("[norte -> Corredor]"));
    }
}
