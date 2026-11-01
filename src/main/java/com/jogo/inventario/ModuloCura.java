package com.jogo.inventario;
import com.jogo.personagens.Jogador;

public class ModuloCura extends Item implements Consumivel {
    private int quantidadeDeCura;

    public ModuloCura(String id,String nome,String descricao,Categoria categoria,int quantidadeDeCura){
        super(id,nome,descricao,categoria);
        this.quantidadeDeCura=quantidadeDeCura;

    }
    @Override
    public void efeito(Jogador jogador){      
        if(this.quantidadeDeCura>=0 && jogador.getVidaAtual()>0){
            int novaVida = jogador.getVidaAtual() + this.quantidadeDeCura;
            int vidaMaxima = jogador.getVidaMaxima();
            if(vidaMaxima<= novaVida){
                jogador.setVidaAtual(vidaMaxima);
            }else{
                jogador.setVidaAtual(novaVida);}
        }
    }
}
