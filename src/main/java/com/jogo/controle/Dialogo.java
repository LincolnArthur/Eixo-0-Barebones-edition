package com.jogo.controle;

import com.jogo.personagens.Jogador;
import com.jogo.personagens.Npc;
import com.jogo.visao.TelaConsole;

import java.util.List;

//CONTROLE do MVC pras conversas, igual a Batalha é pro combate: decide a
//ordem (fala, depois efeito, depois próxima fala), mexe no modelo (Npc,
//Jogador) e manda a visão (TelaConsole) mostrar. Não imprime nada direto
//e não guarda regra de NPC (quem cura e quantas curas ficam no modelo).
public class Dialogo {

    private final Jogador jogador;
    private final TelaConsole tela;

    public Dialogo(Jogador jogador, TelaConsole tela) {
        if (jogador == null || tela == null) {
            throw new IllegalArgumentException("Dialogo precisa de um jogador e de uma tela");
        }
        this.jogador = jogador;
        this.tela = tela;
    }

    //Jogador conversa com um NPC: mostra a fala atual, aplica o efeito do
    //NPC (cura, dica...) e deixa a próxima fala pronta pra próxima conversa
    public void conversar(Npc npc) {
        if (npc == null) {
            throw new IllegalArgumentException("NPC não pode ser nulo");
        }
        if (jogador.estaMorto()) {
            tela.mostrarErro(jogador.getNome() + " está derrotado e não pode conversar");
            return;
        }
        tela.mostrarMensagem(npc.getNome() + ": \"" + npc.getFalaAtual() + "\"");
        //interagir() é polimorfico: o Informante não faz nada, o Curandeiro cura
        String efeito = npc.interagir(jogador);
        if (!efeito.isEmpty()) {
            tela.mostrarMensagem("  [" + npc.getNome() + "] " + efeito);
        }
        npc.avancarFala();
    }

    //Conversa com todos os NPCs da lista (ex.: os de uma sala), na ordem.
    //List<? extends Npc> aceita List<Npc>, List<NpcCurandeiro>...
    public void conversarComTodos(List<? extends Npc> npcs) {
        for (Npc npc : npcs) {
            conversar(npc);
        }
    }
}
