package com.jogo.personagens;
import  java.util.ArrayList;
import  java.util.List;
public class Inventario {

    //UMa lista que vai ter todos os itens do jogo ass Raioni
    private final List<Inventariavel> objetos;  

    public Inventario(){
        this.objetos = new ArrayList<>();
        // Como no inicio o inventario não tem nada então
        // o construtor acaba só criando o inventario msm ass Raioni
    }

    //Adiciona um item se for diferente de null e 
    // retorna um true pra deixar claro que deu certo ass RAioni
    public boolean adicionar(Inventariavel objeto){ 
    //Estamos usando o nome "objeto" pq refere-se a coisas mais gerais
    //que podem ser guardadas num inventario ass RAioni
        if(objeto == null){
            return false;
        }
        this.objetos.add(objeto);
        return true;
    }
    //msm logica de adicionar, mas remove toda a referencia
    // não o valor em si, ou seja, se for o msm ID vai apagar
    // so aquele que eu joguei dentro do metodo ass RAIONI
    public boolean remover(Inventariavel objeto){
        if(objeto == null){
            return false;
        }
        return this.objetos.remove(objeto);

    }

    public Inventariavel buscar(String id){
        if(id==null){
            return null;
        }
        for(Inventariavel item : this.objetos){
            if(id.equals(item.getId())){
                return item;
            }
        }
        return null;
    }

    public boolean temItem(String id){
        return buscar(id) != null;
    }
    public int quantidadeItem(){
        return this.objetos.size();
    }
    public boolean inventarioVazio(){
        return this.objetos.isEmpty();
    }

    //Pelo oq eu procurei é melhor mandar uma copia da listade
    // itens ao inves da lista em si pq garante que ninguem
    //vai conseguir fazer add ou clear sem ser no ocalcerto
    // pq a copyOf gera uma copia defensiva ass Raioni
    public List<Inventariavel> getObjetos(){
        return List.copyOf(this.objetos);
    }
}
