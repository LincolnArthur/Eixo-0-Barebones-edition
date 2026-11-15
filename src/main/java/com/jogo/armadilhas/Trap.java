package com.jogo.armadilhas;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstrata base para todas as armadilhas do jogo.
 *
 * Implementa {@link DamageSource} para seguir o princípio da responsabilidade única (SRP)
 * e o princípio aberto/fechado (OCP), permitindo que novas armadilhas sejam adicionadas
 * sem modificar o loop de jogo ou os sistemas existentes.
 *
 * Arquitetura e Ciclo de Vida:
 * 1. INACTIVE     -> Armadilha armada ou oculta.
 * 2. TRIGGERED    -> Gatilho acionado (por tempo ou presença do jogador na triggerBox).
 * 3. TELEGRAPHING -> Aviso antecipado (Game Juice: tremor e indicador sonoro/visual).
 * 4. ACTIVE       -> Hitbox de dano ativa, infligindo dano a alvos que a tocarem.
 * 5. COOLDOWN     -> Período de desativação / rearme antes de retornar a INACTIVE.
 */
public abstract class Trap implements DamageSource {

    // Identificação e posição básica no mundo
    protected final String name;
    protected double x;
    protected double y;
    protected double width;
    protected double height;

    // Caixas de colisão separadas: Hitbox de DANO vs TriggerBox de ATIVAÇÃO
    protected Hitbox damageHitbox;
    protected Hitbox triggerBox;

    // Estado e temporizadores do ciclo de vida
    protected TrapState state = TrapState.INACTIVE;
    protected double stateTimer = 0.0;

    protected double inactiveDuration = 2.0;  // Para armadilhas com ciclo automático
    protected double telegraphDuration = 0.5; // Duração do aviso antes do dano
    protected double activeDuration = 1.0;    // Duração da fase letal
    protected double cooldownDuration = 2.0;  // Duração da recarga após o ataque

    // Propriedades de combate
    protected int damage = 20;
    protected double knockbackForce = 350.0;
    protected double invulnerabilityGrantDuration = 1.0; // 1.0s de I-Frames concedidos ao jogador após sofrer dano

    // Game Juice: tremor visual gerado durante o estado de TELEGRAPHING
    protected double shakeIntensity = 3.0;
    protected double shakeOffsetX = 0.0;
    protected double shakeOffsetY = 0.0;

    // Lista de ouvintes (Observer pattern) para áudio, partículas e feedback de impacto
    protected final List<TrapListener> listeners = new ArrayList<>();

    // Permite ligar/desligar renderização de hitboxes para depuração
    protected boolean debugHitboxes = false;

    public Trap(String name, double x, double y, double width, double height) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome da armadilha não pode ser nulo ou vazio.");
        }
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        // Por padrão, a hitbox de dano possui uma margem de segurança (offset) de 2px
        this.damageHitbox = new Hitbox(2, 2, Math.max(0, width - 4), Math.max(0, height - 4));

        // Por padrão, a triggerBox cobre a própria área da armadilha
        this.triggerBox = new Hitbox(0, 0, width, height);
    }

    // =========================================================================
    // CICLO DE VIDA (Lifecycle Methods)
    // =========================================================================

    public void update(double deltaTime) {
        if (deltaTime <= 0) {
            return;
        }

        stateTimer += deltaTime;

        switch (state) {
            case INACTIVE -> updateInactive(deltaTime);
            case TRIGGERED -> transitionTo(TrapState.TELEGRAPHING);
            case TELEGRAPHING -> updateTelegraphing(deltaTime);
            case ACTIVE -> updateActive(deltaTime);
            case COOLDOWN -> updateCooldown(deltaTime);
        }

        // Gancho polimórfico para comportamento específico de cada subclasse a cada tick
        updateCustom(deltaTime);
    }

    protected void updateInactive(double deltaTime) {
        if (inactiveDuration > 0 && stateTimer >= inactiveDuration) {
            onTrigger();
        }
    }

    protected void updateTelegraphing(double deltaTime) {
        double frequency = 45.0;
        shakeOffsetX = Math.sin(stateTimer * frequency) * shakeIntensity;
        shakeOffsetY = Math.cos(stateTimer * frequency) * (shakeIntensity * 0.5);

        if (stateTimer >= telegraphDuration) {
            shakeOffsetX = 0.0;
            shakeOffsetY = 0.0;
            transitionTo(TrapState.ACTIVE);
            notifyTriggered();
        }
    }

    protected void updateActive(double deltaTime) {
        if (stateTimer >= activeDuration) {
            transitionTo(TrapState.COOLDOWN);
        }
    }

    protected void updateCooldown(double deltaTime) {
        if (stateTimer >= cooldownDuration) {
            onReset();
        }
    }

    public void onTrigger() {
        if (state == TrapState.INACTIVE) {
            transitionTo(TrapState.TRIGGERED);
            transitionTo(TrapState.TELEGRAPHING);
        }
    }

    public void onReset() {
        shakeOffsetX = 0.0;
        shakeOffsetY = 0.0;
        transitionTo(TrapState.INACTIVE);
        for (TrapListener listener : listeners) {
            listener.onReset(this);
        }
    }

    protected void transitionTo(TrapState newState) {
        TrapState oldState = this.state;
        this.state = newState;
        this.stateTimer = 0.0;

        if (newState == TrapState.TELEGRAPHING) {
            notifyTelegraphStarted(telegraphDuration);
        }

        for (TrapListener listener : listeners) {
            listener.onStateChanged(this, oldState, newState);
        }
    }

    // =========================================================================
    // DETECÇÃO DE GATILHOS E COLISÕES
    // =========================================================================

    public boolean checkTrigger(DamageTarget target) {
        if (target == null || !target.isAlive() || state != TrapState.INACTIVE) {
            return false;
        }

        boolean inside = triggerBox.intersects(x, y, target.getHitbox(), target.getX(), target.getY());
        if (inside) {
            onTrigger();
            return true;
        }
        return false;
    }

    public boolean checkCollision(DamageTarget target) {
        if (target == null || !target.isAlive() || target.isInvulnerable() || state != TrapState.ACTIVE) {
            return false;
        }

        boolean hit = damageHitbox.intersects(x, y, target.getHitbox(), target.getX(), target.getY());
        if (hit) {
            applyDamage(target);
            return true;
        }
        return false;
    }

    public void applyDamage(DamageTarget target) {
        if (target == null || !target.isAlive() || target.isInvulnerable()) {
            return;
        }

        target.takeDamage(damage, this);

        double trapCenterX = x + damageHitbox.getOffsetX() + (damageHitbox.getWidth() / 2.0);
        double targetCenterX = target.getX() + target.getHitbox().getOffsetX() + (target.getHitbox().getWidth() / 2.0);

        double forceX = (targetCenterX >= trapCenterX) ? knockbackForce : -knockbackForce;
        double forceY = -Math.abs(knockbackForce * 0.75);

        target.applyKnockback(forceX, forceY);
        target.activateInvulnerability(invulnerabilityGrantDuration);

        for (TrapListener listener : listeners) {
            listener.onDamageDealt(this, target, damage);
        }
    }

    // =========================================================================
    // RENDERIZAÇÃO AWT (Java Puro) E GAME JUICE
    // =========================================================================

    public void render(Graphics g) {
        double drawX = x + shakeOffsetX;
        double drawY = y + shakeOffsetY;

        renderContent(g, drawX, drawY);

        if (state == TrapState.TELEGRAPHING) {
            renderTelegraphEffect(g, drawX, drawY);
        }

        if (debugHitboxes && g instanceof Graphics2D g2d) {
            renderDebugBounds(g2d);
        }
    }

    protected abstract void renderContent(Graphics g, double drawX, double drawY);

    protected void renderTelegraphEffect(Graphics g, double drawX, double drawY) {
        Color originalColor = g.getColor();
        float pulse = (float) (Math.abs(Math.sin(stateTimer * 10.0)) * 0.4 + 0.2);
        g.setColor(new Color(1.0f, 0.2f, 0.2f, pulse));
        g.fillRect((int) drawX, (int) drawY, (int) width, (int) height);
        g.setColor(originalColor);
    }

    protected void renderDebugBounds(Graphics2D g2d) {
        Color prev = g2d.getColor();

        g2d.setColor(new Color(255, 215, 0, 140));
        g2d.drawRect((int) triggerBox.getMinX(x), (int) triggerBox.getMinY(y),
                (int) triggerBox.getWidth(), (int) triggerBox.getHeight());

        g2d.setColor(state == TrapState.ACTIVE ? new Color(255, 0, 0, 200) : new Color(255, 100, 100, 80));
        g2d.drawRect((int) damageHitbox.getMinX(x), (int) damageHitbox.getMinY(y),
                (int) damageHitbox.getWidth(), (int) damageHitbox.getHeight());

        g2d.setColor(prev);
    }

    protected void updateCustom(double deltaTime) {}

    // =========================================================================
    // OBSERVER / LISTENERS
    // =========================================================================

    public void addListener(TrapListener listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TrapListener listener) {
        listeners.remove(listener);
    }

    protected void notifyTelegraphStarted(double duration) {
        for (TrapListener listener : listeners) {
            listener.onTelegraphStarted(this, duration);
        }
    }

    protected void notifyTriggered() {
        for (TrapListener listener : listeners) {
            listener.onTriggered(this);
        }
    }

    protected void notifyImpact() {
        for (TrapListener listener : listeners) {
            listener.onImpact(this);
        }
    }

    // =========================================================================
    // GETTERS E SETTERS
    // =========================================================================

    public double getInvulnerabilityGrantDuration() {
        return invulnerabilityGrantDuration;
    }

    public void setInvulnerabilityGrantDuration(double invulnerabilityGrantDuration) {
        this.invulnerabilityGrantDuration = Math.max(0.0, invulnerabilityGrantDuration);
    }

    @Override
    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    @Override
    public double getKnockbackForce() {
        return knockbackForce;
    }

    public void setKnockbackForce(double knockbackForce) {
        this.knockbackForce = knockbackForce;
    }

    @Override
    public String getName() {
        return name;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Hitbox getDamageHitbox() {
        return damageHitbox;
    }

    public void setDamageHitbox(Hitbox damageHitbox) {
        this.damageHitbox = damageHitbox;
    }

    public Hitbox getTriggerBox() {
        return triggerBox;
    }

    public void setTriggerBox(Hitbox triggerBox) {
        this.triggerBox = triggerBox;
    }

    public TrapState getState() {
        return state;
    }

    public double getStateTimer() {
        return stateTimer;
    }

    public double getInactiveDuration() {
        return inactiveDuration;
    }

    public void setInactiveDuration(double inactiveDuration) {
        this.inactiveDuration = inactiveDuration;
    }

    public double getTelegraphDuration() {
        return telegraphDuration;
    }

    public void setTelegraphDuration(double telegraphDuration) {
        this.telegraphDuration = telegraphDuration;
    }

    public double getActiveDuration() {
        return activeDuration;
    }

    public void setActiveDuration(double activeDuration) {
        this.activeDuration = activeDuration;
    }

    public double getCooldownDuration() {
        return cooldownDuration;
    }

    public void setCooldownDuration(double cooldownDuration) {
        this.cooldownDuration = cooldownDuration;
    }

    public boolean isDebugHitboxes() {
        return debugHitboxes;
    }

    public void setDebugHitboxes(boolean debugHitboxes) {
        this.debugHitboxes = debugHitboxes;
    }

    public double getShakeOffsetX() {
        return shakeOffsetX;
    }

    public double getShakeOffsetY() {
        return shakeOffsetY;
    }

    @Override
    public String toString() {
        return String.format("%s [pos=(%.0f, %.0f), tam=%.0fx%.0f, estado=%s, dano=%d]",
                name, x, y, width, height, state, damage);
    }
}
