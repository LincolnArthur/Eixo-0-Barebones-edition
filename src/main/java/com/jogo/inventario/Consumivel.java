package com.jogo.inventario;
import com.jogo.personagens.Jogador;

public interface Consumivel{
    public void efeito(Jogador jogador);
    // a ideia é que antes de tudo o efeito do consumivel passe
    //pela interface e depois diretamente para o personagem
    // - Raioni

}