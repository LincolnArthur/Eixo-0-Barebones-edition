package com.jogo.personagens;
import java.util.HashMap;
import java.util.Map;

public class GerenciadorHabilidades {
    
    //Cria uma "tabela hash" que liga cada habilidade ao seu tempo de espera
    private final Map<Habilidade,Double> tempoEspera;

    public GerenciadorHabilidades(){
        this.tempoEspera = new HashMap<>();
    }

        /*Atualiza o tempo de espera em cada frame que for tendo */
    public void atualizar(double dt){
        for(Map.Entry<Habilidade,Double> entrada: tempoEspera.entrySet()){
            double restante = entrada.getValue() - dt;
            entrada.setValue(Math.max(0.0, restante));
        } 
    }
    //Indica se a habilidade está fora do tempo de espera
    public boolean estaDisponivel(Habilidade h) {
        return tempoEspera.getOrDefault(h, 0.0) <= 0.0;
    }
    //INdica quanto tempo falata pro tempo de espera acabar e poder usar habilidade
    public double tempoRestante(Habilidade h) {
        return tempoEspera.getOrDefault(h, 0.0);
    }

    public boolean usar(Habilidade h, Jogador player){
        if(!estaDisponivel(h)){
            return false;
        }
        if(!h.podeUsar(player)){
            return false;
        }
        player.consumirEnergia(h.getCustoEnergia());
        h.executar(player);
        tempoEspera.put(h,h.getTempoEspera());
        return true;
    }
}
