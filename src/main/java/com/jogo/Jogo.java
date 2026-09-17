package com.jogo;
//Package diz q uma classe pertence a tal grupo lógico
//Namespace: Evita colisão de nome entre classe de mesmo nome
//Organização: agrupa classes relacionadas

import com.jogo.personagens.Personagem;

public class Jogo {
    public static void main(String[] args) {
        //Onde são criados os objetos
        Personagem heroi = new Personagem("Herói", 100);
        Personagem goblin = new Personagem("goblin", 25);

        //Uso: chamando um comportamento já incluso na classe criada
        goblin.tomarDano(15);
        heroi.tomarDano(100);

        //Pra enxergar o resultado de algumas comparações (Com concatenação de strings)
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


    }
    
}
