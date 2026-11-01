package com.jogo.inventario;
import com.jogo.inventario.Categoria;
import com.jogo.personagens.Jogador;

public class ModuloCura extends Item implements Consumivel {
    private final String id;
    private final String nome;
    private final String descricao;
    private final Categoria categoria;
    private int quantidadeDeCura;

    public ModuloCura(String id,String nome,String descricao,Categoria categoria,int quantidadeDeCura){
        super(id,nome,descricao,categoria);
        this.quantidadeDeCura=quantidadeDeCura;

    }
    @Override
    public void efeito(Jogador jogador){      
        if(this.quantidadeDeCura>=0 && jogador.getVidaMaxima()>0){
            int novaVida = jogador.getVidaAtual() + this.quantidadeDeCura;
            jogador.setVidaAtual(novaVida);
        }
    }
}
