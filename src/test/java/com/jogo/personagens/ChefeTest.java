package com.jogo.personagens;

import com.jogo.combate.Espada;
import com.jogo.habilidades.BuracoNegro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChefeTest {

    private Chefe chefe;
    private Jogador jogador;

    @BeforeEach
    void criar() {
        chefe = new Chefe("NEXUS", 400, 10);
        jogador = new Jogador("J", 500, new Espada("e", "E", "", 1, 0.0), 50);
    }

    @Test
    @DisplayName("Rotação dos 4 ataques: Rajada -> Investida -> Chuva -> Armadilhas -> Rajada")
    void rotacao() {
        assertEquals(AtaqueChefe.INVESTIDA, AtaqueChefe.RAJADA.proximo());
        assertEquals(AtaqueChefe.CHUVA, AtaqueChefe.INVESTIDA.proximo());
        assertEquals(AtaqueChefe.ARMADILHAS, AtaqueChefe.CHUVA.proximo());
        assertEquals(AtaqueChefe.RAJADA, AtaqueChefe.ARMADILHAS.proximo());
    }

    @Test
    @DisplayName("Primeiro turno só avisa (sem dano); o segundo ataca")
    void avisaAntesDeAtacar() {
        chefe.iniciarAtaque(jogador);
        assertTrue(chefe.estaAvisando());
        assertEquals(500, jogador.getVidaAtual());
        assertTrue(chefe.descreverUltimaAcao().contains("Rajada"));

        chefe.iniciarAtaque(jogador); //Rajada: 2 tiros de 10 acertam
        assertFalse(chefe.estaAvisando());
        assertEquals(480, jogador.getVidaAtual());

        chefe.iniciarAtaque(jogador);
        chefe.iniciarAtaque(jogador); //Investida: dano em dobro
        assertEquals(460, jogador.getVidaAtual());
    }

    @Test
    @DisplayName("Fase 2 com metade da vida: mais tiros, blocos, espinhos e a prensa")
    void fases() {
        assertEquals(1, chefe.getFase());
        assertEquals(5, chefe.getQuantidadeTiros());
        assertEquals(3, chefe.getQuantidadeEspinhos());
        assertFalse(chefe.usaPrensa());
        chefe.tomarDano(200);
        assertEquals(2, chefe.getFase());
        assertEquals(7, chefe.getQuantidadeTiros());
        assertEquals(6, chefe.getQuantidadeBlocos());
        assertEquals(5, chefe.getQuantidadeEspinhos());
        assertTrue(chefe.usaPrensa());
    }

    @Test
    @DisplayName("Ataque Armadilhas usa o sistema de armadilhas de verdade")
    void ataqueArmadilhas() {
        for (int i = 0; i < 7; i++) {
            chefe.iniciarAtaque(jogador); //aviso/ataque das 3 primeiras + aviso das armadilhas
        }
        int vidaAntes = jogador.getVidaAtual();
        chefe.iniciarAtaque(jogador);
        assertEquals(AtaqueChefe.ARMADILHAS, chefe.getAtaqueAtual());
        assertEquals(3, chefe.getArmadilhasDoUltimoAtaque().size());
        assertTrue(jogador.getVidaAtual() < vidaAntes, "O espinho embaixo do jogador acerta");
    }

    @Test
    @DisplayName("Espinhos em volta do jogador, um embaixo dele, sem sair da arena")
    void posicoesEspinhos() {
        List<Double> posicoes = chefe.calcularPosicoesEspinhos(640);
        assertEquals(3, posicoes.size());
        assertTrue(posicoes.contains(640 - 32.0));
        for (double x : chefe.calcularPosicoesEspinhos(5)) {
            assertTrue(x >= 0 && x + 64 <= 1280);
        }
    }

    @Test
    @DisplayName("Resistências: gelo vale metade e o Buraco Negro só tira 25%")
    void resistencias() {
        chefe.congelar(4);
        assertEquals(2, chefe.getTurnosCongelado());
        assertEquals(100, chefe.danoDeBuracoNegro());

        BuracoNegro buraco = new BuracoNegro(true, 20);
        buraco.mirar(List.of(chefe));
        assertTrue(buraco.usar(jogador, null));
        assertEquals(300, chefe.getVidaAtual());
        assertEquals(300, chefe.getExperienciaConcedida());
    }

    @Test
    @DisplayName("NEXUS começa como NPC e só desperta depois da conversa inteira")
    void npcChefe() {
        NpcChefe npc = new NpcChefe("NEXUS", List.of("a", "b"), chefe);
        assertThrows(IllegalStateException.class, npc::despertar);
        assertEquals("", npc.interagir(jogador)); //fala "a"
        npc.avancarFala();
        assertTrue(npc.interagir(jogador).contains("desperta")); //fala "b", a última
        assertTrue(npc.conversaCompleta());
        assertSame(chefe, npc.despertar());
        assertThrows(IllegalStateException.class, npc::despertar); //uma vez só
        assertThrows(IllegalArgumentException.class, () -> new NpcChefe("X", List.of("a"), null));
    }
}
