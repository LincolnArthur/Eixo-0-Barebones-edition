package com.jogo.armadilhas;

import java.util.Map;

/**
 * Padrão Factory (GoF) para criação de armadilhas.
 *
 * Centraliza a instanciação a partir de coordenadas absolutas do mundo,
 * coordenadas em grid de tiles (tileX, tileY) ou dicionários de propriedades.
 */
public final class TrapFactory {

    private TrapFactory() {
    }

    public static SpikesRetrateis createSpikes(double x, double y, double width, double height) {
        return new SpikesRetrateis(x, y, width, height);
    }

    public static SpikesRetrateis createSpikes(double x, double y, double width, double height,
                                              double inactiveDuration, double telegraphDuration,
                                              double activeDuration, double cooldownDuration, int damage) {
        return new SpikesRetrateis(x, y, width, height, inactiveDuration, telegraphDuration, activeDuration, cooldownDuration, damage);
    }

    public static BlocoQueCai createFallingBlock(double x, double y, double width, double height, double fallDistance) {
        return new BlocoQueCai(x, y, width, height, fallDistance, 0.5, 45);
    }

    public static Trap createFromTileCoordinates(String type, int tileX, int tileY, int tileSize, Map<String, Object> properties) {
        double worldX = tileX * tileSize;
        double worldY = tileY * tileSize;
        return createFromTileData(type, worldX, worldY, properties);
    }

    public static Trap createFromTileData(String type, double x, double y, Map<String, Object> properties) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de armadilha não pode ser nulo.");
        }

        double width = getDouble(properties, "width", 64.0);
        double height = getDouble(properties, "height", 32.0);
        int damage = getInt(properties, "damage", 25);

        return switch (type.toLowerCase().trim()) {
            case "spikes", "espinhos", "spikes_retrateis" -> {
                double inactive = getDouble(properties, "inactiveDuration", -1.0);
                double telegraph = getDouble(properties, "telegraphDuration", 2.0);
                double active = getDouble(properties, "activeDuration", 2.0);
                double cooldown = getDouble(properties, "cooldownDuration", 1.5);
                yield new SpikesRetrateis(x, y, width, height, inactive, telegraph, active, cooldown, damage);
            }
            case "falling_block", "bloco_cai", "bloco_esmagador" -> {
                double fallDist = getDouble(properties, "maxFallDistance", 320.0);
                double telegraph = getDouble(properties, "telegraphDuration", 0.5);
                yield new BlocoQueCai(x, y, width, height, fallDist, telegraph, damage);
            }
            default -> throw new IllegalArgumentException("Tipo de armadilha desconhecido: " + type);
        };
    }

    private static double getDouble(Map<String, Object> props, String key, double defaultVal) {
        if (props == null || !props.containsKey(key)) return defaultVal;
        Object val = props.get(key);
        return (val instanceof Number num) ? num.doubleValue() : defaultVal;
    }

    private static int getInt(Map<String, Object> props, String key, int defaultVal) {
        if (props == null || !props.containsKey(key)) return defaultVal;
        Object val = props.get(key);
        return (val instanceof Number num) ? num.intValue() : defaultVal;
    }
}
