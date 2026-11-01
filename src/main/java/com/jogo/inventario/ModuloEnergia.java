package com.jogo.inventario;
import com.jogo.personagens.Jogador;

public class ModuloEnergia extends Item implements Consumivel{
    private int quantidadeEnergia;
    public ModuloEnergia(String id,String nome,String descricao,Categoria categoria,int quantidadeEnergia){
        super(id,nome,descricao,categoria);
        this.quantidadeEnergia = quantidadeEnergia;

    }
    @Override
    public void efeito(Jogador jogador){
        if(this.quantidadeEnergia>=0 && jogador.getEnergiaAtual()>=0){
            int energiaMax=jogador.getEnergiaMaxima();
            int novaEnergia = this.quantidadeEnergia + jogador.getEnergiaAtual();
            if(energiaMax<=novaEnergia){
                jogador.setEnergiaAtual(energiaMax);
            }else{
                jogador.setEnergiaAtual(novaEnergia);
            }
        }
    }
}
