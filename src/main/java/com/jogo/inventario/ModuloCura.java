package com.jogo.inventario;
import com.jogo.personagens.Jogador;

public class ModuloCura extends Item implements Consumivel {
    private int quantidadeDeCura;

    public ModuloCura(String id,String nome,String descricao,Categoria categoria,int quantidadeDeCura){
        super(id,nome,descricao,categoria);
        this.quantidadeDeCura=quantidadeDeCura;

    }
    //O módulo só diz quanto cura; o limite da vida máxima e o
    //"não cura morto" ficam no curar() do Personagem
    @Override
    public void efeito(Jogador jogador){
        jogador.curar(this.quantidadeDeCura);
    }
}
