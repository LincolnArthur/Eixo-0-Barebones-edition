package com.jogo.visao;

import com.jogo.inventario.Inventariavel;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Npc;
import com.jogo.personagens.Personagem;
import com.jogo.mundo.Sala;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

//VISÃO do MVC: o único lugar do projeto que escreve no console.
//O modelo (personagens, combate, inventario, mundo...) só guarda dados
//e regras; o controle (controle.Batalha) decide o que acontece e manda
//a visão mostrar. Trocar o console por uma tela gráfica seria trocar só
//esta classe.
public class TelaConsole {

    private final PrintStream saida;
    //Teclado (só quando roda num terminal de verdade): Enter passa as falas.
    //null = não espera nada (testes, saída redirecionada)
    private BufferedReader entrada;

    //Padrão: imprime no console de verdade
    public TelaConsole() {
        this(System.out);
    }

    //Sobrecarga: os testes passam um PrintStream próprio pra conferir o
    //que foi impresso
    public TelaConsole(PrintStream saida) {
        this.saida = saida;
    }

    //Sobrecarga com teclado: esperarEnter() passa a esperar o Enter
    public TelaConsole(PrintStream saida, InputStream teclado) {
        this.saida = saida;
        this.entrada = teclado == null ? null : new BufferedReader(new InputStreamReader(teclado));
    }

    //Tela do main: espera o Enter só se estiver num terminal (no teste e
    //com a saída redirecionada não tem console, então não trava)
    public static TelaConsole doTerminal() {
        return System.console() != null ? new TelaConsole(System.out, System.in) : new TelaConsole();
    }

    //Enter passa a fala (igual ao Enter dos NPCs no FXGL)
    public void esperarEnter() {
        if (entrada == null) {
            return;
        }
        saida.print("   [Enter]");
        saida.flush();
        try {
            if (entrada.readLine() == null) {
                entrada = null; //fim da entrada: para de esperar
            }
        } catch (IOException e) {
            entrada = null;
        }
    }

    public void titulo(String texto) {
        saida.println();
        saida.println("===== " + texto.toUpperCase() + " =====");
    }

    public void mostrarMensagem(String texto) {
        saida.println(texto);
    }

    public void mostrarErro(String texto) {
        saida.println("[!] " + texto);
    }

    //List<? extends Personagem>: aceita List<Inimigo>, List<Jogador>...
    //qualquer lista de subclasses de Personagem (generics com curinga)
    public void mostrarPersonagens(List<? extends Personagem> personagens) {
        for (Personagem p : personagens) {
            mostrarPersonagem(p);
        }
    }

    public void mostrarPersonagem(Personagem p) {
        saida.println(p.getNome() + " " + barraDeVida(p) + " " + p.getVidaAtual() + "/" + p.getVidaMaxima()
                + (p.estaMorto() ? " (derrotado)" : ""));
    }

    public void mostrarJogador(Jogador jogador) {
        mostrarPersonagem(jogador);
        saida.println("  Energia: " + jogador.getEnergiaAtual() + "/" + jogador.getEnergiaMaxima()
                + " | Nível " + jogador.getNivel().getNivelAtual()
                + " (XP " + jogador.getNivel().getExperienciaAtual()
                + "/" + jogador.getNivel().getExperienciaParaProximoNivel() + ")"
                + " | Arma: " + jogador.getArma().getNome());
    }

    public void mostrarAtaque(String atacante, Personagem alvo, int dano) {
        saida.println(atacante + " acerta " + alvo.getNome() + " (-" + dano + ")"
                + (alvo.estaMorto() ? " e derrota!" : ""));
    }

    public void mostrarRecompensa(Inimigo inimigo, int xp) {
        saida.println(inimigo.getNome() + " vale " + xp + " de XP");
    }

    public void mostrarSubidaDeNivel(Jogador jogador, int nivelAntes) {
        int nivel = jogador.getNivel().getNivelAtual();
        StringBuilder linha = new StringBuilder(jogador.getNome() + " subiu para o nível " + nivel
                + "! Vida máxima " + jogador.getVidaMaxima() + " (vida cheia).");
        if (nivelAntes < Jogador.NIVEL_GELO && nivel >= Jogador.NIVEL_GELO) {
            linha.append(" Nova magia: Raio de Gelo.");
        }
        if (nivelAntes < Jogador.NIVEL_BURACO_NEGRO && nivel >= Jogador.NIVEL_BURACO_NEGRO) {
            linha.append(" Nova magia: Buraco Negro.");
        }
        if (nivelAntes < Jogador.NIVEL_RECARGA_AUTOMATICA && nivel >= Jogador.NIVEL_RECARGA_AUTOMATICA) {
            linha.append(" A energia agora recarrega sozinha.");
        }
        saida.println(linha);
    }

    //Era o Sala.descrever(): o modelo não imprime mais nada
    public void mostrarSala(Sala sala) {
        saida.println("========== " + sala.getNome().toUpperCase() + " ==========");
        saida.println(sala.getDescricao());
        if (sala.isPontoDeSalvamento()) {
            saida.println("[*] Ponto de Salvamento/Descanso disponível aqui!");
        }
        if (!sala.getItens().isEmpty()) {
            StringBuilder linha = new StringBuilder("Itens na sala: ");
            for (Inventariavel item : sala.getItens()) {
                linha.append("[").append(item.getNome()).append("] ");
            }
            saida.println(linha.toString().trim());
        }
        if (!sala.getNpcs().isEmpty()) {
            StringBuilder linha = new StringBuilder("NPCs: ");
            for (Npc npc : sala.getNpcs()) {
                linha.append("[").append(npc.getNome()).append("] ");
            }
            saida.println(linha.toString().trim());
        }
        if (!sala.getInimigos().isEmpty()) {
            StringBuilder linha = new StringBuilder("Inimigos: ");
            for (Inimigo ini : sala.getInimigos()) {
                linha.append("[").append(ini.getNome()).append(ini.estaMorto() ? " (Derrotado)" : " (Vivo)").append("] ");
            }
            saida.println(linha.toString().trim());
        }
        StringBuilder linha = new StringBuilder("Saídas: ");
        for (Map.Entry<String, Sala> s : sala.getSaidas().entrySet()) {
            linha.append("[").append(s.getKey()).append(" -> ").append(s.getValue().getNome()).append("] ");
        }
        saida.println(linha.toString().trim());
    }

    //[#####-----] com 10 casas, proporcional à vida
    private String barraDeVida(Personagem p) {
        int cheias = (int) Math.round(10.0 * p.getVidaAtual() / p.getVidaMaxima());
        return "[" + "#".repeat(cheias) + "-".repeat(10 - cheias) + "]";
    }
}
