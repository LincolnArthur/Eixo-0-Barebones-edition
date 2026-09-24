package com.jogo.personagens;

//Classe Pai para consumíveis, upgrades e chaves.
public abstract class Item implements Inventariavel {

    private final String id;
    private final String nome;
    private final String descricao;

    public Item(String id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    @Override 
    public String getId() { 
        return id; 
    }
    @Override 
    public String getNome() { 
        return nome;
    }
    @Override 
    public String getDescricao() { 
        return descricao; 
    }
    @Override 
    public boolean podeSerEquipado(){ 
        return true; 
    }
}