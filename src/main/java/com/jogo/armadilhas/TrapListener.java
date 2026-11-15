package com.jogo.armadilhas;

/**
 * Interface ouvinte (Observer pattern) para eventos do ciclo de vida das armadilhas.
 *
 * Permite desacoplar a lógica de jogo (física/temporizadores) dos subsistemas
 * de efeitos sonoros (SFX), partículas, animações visuais e feedback de impacto.
 */
public interface TrapListener {

    /**
     * Disparado sempre que o estado da armadilha for modificado.
     */
    default void onStateChanged(Trap trap, TrapState oldState, TrapState newState) {}

    /**
     * Disparado quando a armadilha inicia a fase de aviso antecipado (telegraphing).
     *
     * @param trap Armadilha emissora.
     * @param duration Segundos até a ativação do dano.
     */
    default void onTelegraphStarted(Trap trap, double duration) {}

    /**
     * Disparado no exato instante em que a armadilha se torna letal.
     */
    default void onTriggered(Trap trap) {}

    /**
     * Disparado quando a armadilha acerta um alvo com sucesso.
     */
    default void onDamageDealt(Trap trap, DamageTarget target, int damage) {}

    /**
     * Disparado quando uma armadilha de impacto (ex: {@link BlocoQueCai}) atinge o chão.
     */
    default void onImpact(Trap trap) {}

    /**
     * Disparado quando a armadilha conclui o ciclo e é restaurada ao estado inicial.
     */
    default void onReset(Trap trap) {}
}
