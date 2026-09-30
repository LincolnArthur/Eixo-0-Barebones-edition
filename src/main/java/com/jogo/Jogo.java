package com.jogo;
//Package diz q uma classe pertence a tal grupo lógico
//Namespace: Evita colisão de nome entre classe de mesmo nome
//Organização: agrupa classes relacionadas -lincoln

import com.jogo.personagens.Personagem;
import com.jogo.personagens.Jogador;
import com.jogo.combate.Arma;
import com.jogo.combate.Espada;
import com.jogo.habilidades.Apara;
import com.jogo.habilidades.BolaDeFogo;
import com.jogo.personagens.Melee;
import com.jogo.personagens.Sniper;
import com.jogo.personagens.Voador;

public class Jogo {
    public static void main(String[] args) {
        //Onde são criados os objetos -lincoln
        Personagem heroi = new Personagem("Herói", 100);
        Personagem goblin = new Personagem("goblin", 25);

        //Uso: chamando um comportamento já incluso na classe criada -lincoln
        goblin.tomarDano(15);
        heroi.tomarDano(100);

        //Pra enxergar o resultado de algumas comparações (Com concatenação de strings) -lincoln
        if (!goblin.estaSaudavel()) {
        System.out.println("\n" + goblin.getNome() + " está morto?\n" + goblin.estaMorto() + "\n");
        } else {
            System.out.println(goblin.getNome() + " está saudável.\n");
        }

        if (!heroi.estaSaudavel()) {
            System.out.println("\n" + heroi.getNome() + " está morto?\n" + heroi.estaMorto() + "\n");
        } else {
            System.out.println(heroi.getNome() + " está saudável.\n");
        }

        //teste do toString
        System.out.println("Estado dos personagens com o toString:\n");
        System.out.println(goblin);
        System.out.println(heroi);

        //=== Teste de combate: Jogador e Inimigos atacando via iniciarAtaque/Ataque ===
        Arma espadaDoJogador = new Espada("espada-jogador", "Espada Longa", "Arma inicial do jogador", 10, 0.2);
        Jogador jogador = new Jogador("Aventureiro", 100, espadaDoJogador, 50);

        Melee bandido = new Melee("Bandido", 30, 8, 0.1);
        Sniper atirador = new Sniper("Atirador", 40, 12, 0.2, 0.05);
        Voador morcego = new Voador("Morcego", 20, 6, 0.1, 0.3);

        System.out.println("Antes do combate:");
        System.out.println(bandido);
        System.out.println(atirador);
        System.out.println(morcego);

        jogador.iniciarAtaque(bandido);
        bandido.iniciarAtaque(jogador);
        atirador.iniciarAtaque(jogador);
        morcego.iniciarAtaque(jogador);

        System.out.println("\nDepois do combate:");
        System.out.println(bandido);
        System.out.println(atirador);
        System.out.println(morcego);
        System.out.println(jogador);

        //=== Teste de habilidades: uma sem alvo (Apara) e uma com dano (GolpeDeEnergia) ===
        Apara apara = new Apara(true, 1.5, 5, 0.3);
        BolaDeFogo golpe = new BolaDeFogo(true, 2.0, 15, 0.0, 20);

        System.out.println("\nUsando Apara (sem alvo):");
        boolean usouApara = apara.usar(jogador, null);
        System.out.println("Apara usada? " + usouApara);

        System.out.println("\nUsando Golpe de Energia no bandido:");
        boolean usouGolpe = golpe.usar(jogador, bandido);
        System.out.println("Golpe usado? " + usouGolpe);
        System.out.println(bandido);

        System.out.println("\nTentando usar Golpe de Energia de novo, ainda em cooldown:");
        boolean usouDeNovo = golpe.usar(jogador, bandido);
        System.out.println("Golpe usado? " + usouDeNovo); // false esperado: cooldown de 2.0 ainda ativo

        System.out.println("\nPassando 2 segundos (atualizar cooldown):");
        golpe.atualizar(2.0);
        boolean usouTerceiraVez = golpe.usar(jogador, bandido);
        System.out.println("Golpe usado? " + usouTerceiraVez); // true esperado: cooldown zerado
        System.out.println(bandido);
    }
}