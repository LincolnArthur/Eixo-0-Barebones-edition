package com.jogo.personagens;

//Os quatro ataques do chefe, sempre na mesma ordem (rotação), iguais aos
//do FXGL. Enum: cada constante tem o nome que aparece na tela.
public enum AtaqueChefe {
    RAJADA("Rajada"),           //leque de tiros na direção do jogador
    INVESTIDA("Investida"),     //mergulha em cima do jogador (dano em dobro)
    CHUVA("Chuva de dados"),    //blocos caindo do teto
    ARMADILHAS("Armadilhas");   //espinhos (e, na fase 2, uma prensa) do pacote armadilhas

    private final String nome;

    AtaqueChefe(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    //Próximo da rotação; depois do último volta pro primeiro
    public AtaqueChefe proximo() {
        AtaqueChefe[] todos = values();
        return todos[(ordinal() + 1) % todos.length];
    }
}
