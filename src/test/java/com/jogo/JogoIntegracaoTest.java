package com.jogo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

//Roda o programa inteiro (main) e confere que termina com vitória e com
//as exceções tratadas, sem nenhuma escapar
class JogoIntegracaoTest {

    @Test
    @DisplayName("A demonstração completa roda do começo ao fim")
    void rodaInteiro() {
        PrintStream original = System.out;
        ByteArrayOutputStream capturado = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturado, true, StandardCharsets.UTF_8));
        try {
            assertDoesNotThrow(() -> Jogo.main(new String[0]));
        } finally {
            System.setOut(original);
        }
        String saida = capturado.toString(StandardCharsets.UTF_8);
        assertTrue(saida.contains("Vitória!"));
        assertTrue(saida.contains("[!] Sala não encontrada no mapa"));
        assertTrue(saida.contains("Marreta tirou 14"));
        assertTrue(saida.contains("Shotgun tirou 20"));
    }
}
