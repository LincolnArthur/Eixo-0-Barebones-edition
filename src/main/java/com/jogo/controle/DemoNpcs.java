package com.jogo;

import com.jogo.combate.Espada;
import com.jogo.controle.Dialogo;
import com.jogo.mundo.Sala;
import com.jogo.personagens.Jogador;
import com.jogo.personagens.Npc;
import com.jogo.personagens.NpcCurandeiro;
import com.jogo.personagens.NpcInformante;
import com.jogo.visao.TelaConsole;

import java.util.List;

//Demonstração dos NPCs, separada do Jogo.main (que não foi alterado).
//Rodar com:
//   mvn clean compile exec:java -Dexec.mainClass=com.jogo.DemoNpcs
//
//Mesma divisão do resto do projeto: o main só MONTA as peças (modelo,
//visão, controle). Quem conversa é o Dialogo; quem imprime é a TelaConsole.
public class DemoNpcs {
    public static void main(String[] args) {
        TelaConsole tela = new TelaConsole();

        Sala laboratorio = new Sala("lab", "Laboratório", "Tubos de criogenia quebrados por todo lado.");
        laboratorio.setPontoDeSalvamento(true);

        Jogador jogador = new Jogador("Aventureiro", 100,
                new Espada("espada", "Espada Longa", "Arma inicial do jogador", 10, 0.0), 50);

        //Lista do tipo da mãe (Npc) com objetos de duas filhas: coleção polimórfica
        NpcInformante ze = new NpcInformante("Zé", List.of(
                "Olá, viajante! Há armas e módulos espalhados pelas salas.",
                "Use a Apara na hora certa pra não tomar dano.",
                "No fim do caminho existe um chefe. Chegue com a vida cheia!"));
        NpcCurandeiro vega = new NpcCurandeiro("Dra. Vega", List.of(
                "Você parece ferido. Fique perto que eu cuido disso.",
                "Só tenho uma cura por sala, então use com sabedoria."), 1);
        List<Npc> npcs = List.of(ze, vega);

        Dialogo dialogo = new Dialogo(jogador, tela);

        tela.titulo("Sala");
        tela.mostrarSala(laboratorio);
        tela.mostrarMensagem("NPCs aqui: " + npcs);
        tela.mostrarJogador(jogador);

        tela.titulo("Conversa com a vida cheia (a cura não é gasta)");
        dialogo.conversarComTodos(npcs);

        tela.titulo("Depois de ser ferido");
        jogador.tomarDano(60);
        tela.mostrarJogador(jogador);
        dialogo.conversarComTodos(npcs);
        tela.mostrarJogador(jogador);

        tela.titulo("Sem curas restantes");
        jogador.tomarDano(30);
        dialogo.conversar(vega);

        tela.titulo("NPCs são invulneráveis");
        ze.tomarDano(999);
        tela.mostrarMensagem(ze.getNome() + " tomou 999 de dano e " + (ze.estaMorto() ? "morreu" : "continua de pé"));
    }
}
