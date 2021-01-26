package de.hglabor.plugins.kitapi.kit.config;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class LastHitInformation {
    private Player lastPlayer;
    private LivingEntity lastEntity;
    private long playerTimeStamp;
    private long entityTimeStamp;

    public LastHitInformation() {
    }

    public LastHitInformation(Player lastHittedPlayer, long lastHittedPlayerTimestamp) {
        this.lastPlayer = lastHittedPlayer;
        this.playerTimeStamp = lastHittedPlayerTimestamp;
    }

    public Player getLastPlayer() {
        return lastPlayer;
    }

    public void setLastPlayer(Player lastPlayer) {
        this.lastPlayer = lastPlayer;
    }

    public LivingEntity getLastEntity() {
        return lastEntity;
    }

    public void setLastEntity(LivingEntity lastEntity) {
        this.lastEntity = lastEntity;
    }

    public long getPlayerTimeStamp() {
        return playerTimeStamp;
    }

    public void setPlayerTimeStamp(long playerTimeStamp) {
        this.playerTimeStamp = playerTimeStamp;
    }

    public long getEntityTimeStamp() {
        return entityTimeStamp;
    }

    public void setEntityTimeStamp(long entityTimeStamp) {
        this.entityTimeStamp = entityTimeStamp;
    }
}
