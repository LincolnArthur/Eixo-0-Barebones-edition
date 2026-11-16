package com.jogo.controle;

import com.jogo.combate.RifleSniper;
import com.jogo.excecoes.JogoException;
import com.jogo.habilidades.Habilidade;
import com.jogo.habilidades.SandevistanMichyle;
import com.jogo.personagens.Inimigo;
import com.jogo.personagens.Jogador;
import com.jogo.visao.TelaConsole;

import java.util.ArrayList;
import java.util.List;

//CONTROLE do MVC: decide a ordem das ações de uma batalha, mexe no
//modelo (Jogador, Inimigo...) e manda a visão (TelaConsole) mostrar o
//resultado. Não imprime nada direto e não guarda regra de combate
//(dano, crítico, XP ficam no modelo).
public class Batalha {

    private final Jogador jogador;
    //Lista do tipo da mãe (Inimigo) com objetos de várias filhas
    //(Melee, Sniper, Voador): coleção polimórfica
    private final List<Inimigo> inimigos;
    private final TelaConsole tela;
    //Sandevistan ligado: os inimigos ficam em câmera lenta. No console,
    //tempo a 25% = os inimigos só agem 1 turno a cada 4
    private SandevistanMichyle sandevistan;
    private int turnosEmCameraLenta = 0;
    private static final double SEGUNDOS_POR_TURNO = 1.0;

    public Batalha(Jogador jogador, List<? extends Inimigo> inimigos, TelaConsole tela) {
        this.jogador = jogador;
        this.inimigos = new ArrayList<>(inimigos);
        this.tela = tela;
    }

    //Jogador ataca um inimigo com a arma equipada. Se derrotar, ganha o XP
    public void jogadorAtaca(Inimigo alvo) {
        if (jogador.estaMorto() || alvo.estaMorto()) {
            return;
        }
        int vidaAntes = alvo.getVidaAtual();
        jogador.iniciarAtaque(alvo);
        tela.mostrarAtaque(jogador.getNome() + " (" + jogador.getArma().getNome() + ")", alvo, vidaAntes - alvo.getVidaAtual());
        if (alvo.estaMorto()) {
            recompensar(alvo);
        }
    }

    //Todos os inimigos vivos atacam o jogador. Mesma chamada
    //(iniciarAtaque) pra todos; o dano depende da arma de cada um
    public void turnoDosInimigos() {
        if (sandevistanLigado()) {
            turnosEmCameraLenta++;
            int turnosPorAcao = (int) Math.round(1 / SandevistanMichyle.ESCALA_TEMPO);
            if (turnosEmCameraLenta % turnosPorAcao != 0) {
                tela.mostrarMensagem("Câmera lenta: os inimigos quase não se mexem.");
                passarTempo();
                return;
            }
        }
        for (Inimigo inimigo : inimigos) {
            if (inimigo.estaMorto() || jogador.estaMorto()) {
                continue;
            }
            int vidaAntes = jogador.getVidaAtual();
            inimigo.iniciarAtaque(jogador);
            tela.mostrarAtaque(inimigo.getNome(), jogador, vidaAntes - jogador.getVidaAtual());
        }
        passarTempo();
    }

    //Liga ou desliga a Sandevistan (a mesma ação faz as duas coisas)
    public boolean alternarSandevistan(SandevistanMichyle habilidade) {
        boolean estavaLigada = habilidade.estaAtivo();
        if (!habilidade.usar(jogador, null)) {
            tela.mostrarErro("Sandevistan indisponível (sem energia ou em cooldown: "
                    + Math.round(habilidade.getCooldownRestante()) + "s)");
            return false;
        }
        sandevistan = habilidade;
        if (estavaLigada) {
            tela.mostrarMensagem("Sandevistan DESLIGADA. Cooldown de "
                    + Math.round(SandevistanMichyle.COOLDOWN_POS_DESATIVAR) + "s.");
        } else {
            turnosEmCameraLenta = 0;
            tela.mostrarMensagem("Sandevistan LIGADA: o tempo dos inimigos cai para 25%.");
            if (habilidade.getCyberpsicose().estaAtivo()) {
                tela.mostrarErro("CYBERPSICOSE! A vida vai caindo até a metade.");
            }
        }
        return true;
    }

    public boolean sandevistanLigado() {
        return sandevistan != null && sandevistan.estaAtivo();
    }

    //Cada turno conta como 1 segundo pra Sandevistan e pra Cyberpsicose
    private void passarTempo() {
        if (sandevistan == null) {
            return;
        }
        boolean estavaLigada = sandevistan.estaAtivo();
        int vidaAntes = jogador.getVidaAtual();
        sandevistan.atualizarSandevistan(jogador, SEGUNDOS_POR_TURNO);
        int danoDebuff = vidaAntes - jogador.getVidaAtual();
        if (danoDebuff > 0) {
            tela.mostrarMensagem("Cyberpsicose: " + jogador.getNome() + " perdeu " + danoDebuff + " de vida.");
        }
        if (estavaLigada && !sandevistan.estaAtivo()) {
            tela.mostrarErro("Energia esgotada: a Sandevistan desligou sozinha.");
        }
    }

    //Usa uma habilidade. alvo pode ser null (ex: Apara)
    public boolean usarHabilidade(Habilidade habilidade, Inimigo alvo) {
        String nome = habilidade.getClass().getSimpleName();
        int vidaAntes = alvo == null ? 0 : alvo.getVidaAtual();
        boolean usou = habilidade.usar(jogador, alvo);
        if (!usou) {
            tela.mostrarErro("Não deu pra usar " + nome + " agora (cooldown ou energia)");
            return false;
        }
        if (alvo == null) {
            tela.mostrarMensagem(jogador.getNome() + " usou " + nome);
        } else {
            tela.mostrarAtaque(jogador.getNome() + " (" + nome + ")", alvo, vidaAntes - alvo.getVidaAtual());
            if (alvo.estaMorto()) {
                recompensar(alvo);
            }
        }
        return true;
    }

    //Aqui as exceções do modelo são TRATADAS: em vez de derrubar o
    //programa, viram uma mensagem de erro na tela e devolvem false
    public boolean usarItem(String id) {
        try {
            jogador.usarItem(id);
            tela.mostrarMensagem(jogador.getNome() + " usou " + id);
            return true;
        } catch (JogoException e) {
            tela.mostrarErro(e.getMessage());
            return false;
        }
    }

    public boolean equiparArma(String idArma) {
        try {
            jogador.equiparArma(idArma);
            tela.mostrarMensagem(jogador.getNome() + " equipou " + jogador.getArma().getNome());
            return true;
        } catch (JogoException e) {
            tela.mostrarErro(e.getMessage());
            return false;
        }
    }

    //Tiro que atravessa (Perfurante): passa pela fila de inimigos na
    //ordem da lista. Quem morrer com o tiro também dá XP
    public void dispararPerfurante(RifleSniper rifle) {
        List<Integer> vidasAntes = new ArrayList<>();
        for (Inimigo inimigo : inimigos) {
            vidasAntes.add(inimigo.getVidaAtual());
        }
        rifle.disparar(new ArrayList<>(inimigos));
        for (int i = 0; i < inimigos.size(); i++) {
            Inimigo inimigo = inimigos.get(i);
            int dano = vidasAntes.get(i) - inimigo.getVidaAtual();
            if (dano > 0) {
                tela.mostrarAtaque(jogador.getNome() + " (" + rifle.getNome() + ")", inimigo, dano);
                if (inimigo.estaMorto()) {
                    recompensar(inimigo);
                }
            }
        }
    }

    public boolean jogadorVenceu() {
        for (Inimigo inimigo : inimigos) {
            if (!inimigo.estaMorto()) {
                return false;
            }
        }
        return true;
    }

    public boolean jogadorPerdeu() {
        return jogador.estaMorto();
    }

    public List<Inimigo> getInimigos() {
        return List.copyOf(inimigos);
    }

    //getExperienciaConcedida() é polimórfico: cada tipo de inimigo
    //devolve o próprio valor
    private void recompensar(Inimigo derrotado) {
        int xp = derrotado.getExperienciaConcedida();
        jogador.ganharExperiencia(xp);
        tela.mostrarRecompensa(derrotado, xp);
    }
}
