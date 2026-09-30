package com.jogo;
//Package diz q uma classe pertence a tal grupo lógico
//Namespace: Evita colisão de nome entre classe de mesmo nome
//Organização: agrupa classes relacionadas -lincoln

import com.jogo.personagens.Personagem;

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
            System.out.println(goblin.getNome() + "está saudável.\n");
        }
        
        if (!heroi.estaSaudavel()) {
            System.out.println("\n" + heroi.getNome() + " está morto?\n" + heroi.estaMorto() + "\n");
        } else {
            System.out.println(heroi.getNome() + "está saudável.\n");
        }

        //teste do toString
        System.out.println("Estado dos personagens com o tostring:\n");
        System.out.println(goblin);
        System.out.println(heroi);

        // Demonstração do novo fluxo integrado: Atacante -> Ataque -> Atacavel
        System.out.println("\nTeste de Combate com iniciarAtaque ");
        com.jogo.personagens.Espada espadaHeroi = new com.jogo.personagens.Espada("esp", "Espada de Aço", "Espada forjada", 15, 0.2);
        com.jogo.personagens.Jogador jogador = new com.jogo.personagens.Jogador("Cavaleiro", 80, espadaHeroi, 30);
        com.jogo.personagens.Espada clavaGoblin = new com.jogo.personagens.Espada("clv", "Clava Rústica", "Pedaço de madeira pesado", 8, 0.0);
        com.jogo.personagens.Melee orc = new com.jogo.personagens.Melee("Orc Guerreiro", 40, clavaGoblin);

        System.out.println("Antes do combate:");
        System.out.println(jogador);
        System.out.println(orc);

        System.out.println("\n[Cavaleiro inicia ataque contra Orc Guerreiro]");
        jogador.iniciarAtaque(orc);
        System.out.println("Estado do alvo: " + orc);

        System.out.println("\n[Orc Guerreiro revida contra Cavaleiro]");
        orc.iniciarAtaque(jogador);
        System.out.println("Estado do alvo: " + jogador);
    }
    
}
