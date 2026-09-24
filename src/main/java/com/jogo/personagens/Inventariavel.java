package com.jogo.personagens;


//A criação dessa Interface garante que diferentes classes possam 
//se armazenadas no nosso inventario ass RAioni
public interface Inventariavel {
    String getId();
    String getNome();
    String getDescricao();
    boolean podeSerEquipado();
    
} 