package de.hglabor.plugins.kitapi.pvp;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Optional;

public class LastHitInformation {
    private Player lastPlayer;
    private Player lastDamager;
    private LivingEntity lastEntity;
    private long lastDamagerTimestamp;
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

    public void setLastDamager(Player lastDamager) { this.lastDamager = lastDamager; }

    public void setLastDamagerTimestamp(long lastDamagerTimestamp) {
        this.lastDamagerTimestamp = lastDamagerTimestamp;
    }

    public Optional<Player> getLastDamager() { return Optional.ofNullable(lastDamager); }

    public void setEntityTimeStamp(long entityTimeStamp) {
        this.entityTimeStamp = entityTimeStamp;
    }

    public long getLastDamagerTimestamp() {
        return lastDamagerTimestamp;
    }

    public void clear() {
        lastEntity = null;
        lastPlayer = null;
        lastDamager = null;
        lastDamagerTimestamp = 0;
        playerTimeStamp = 0;
        entityTimeStamp = 0;
    }
}
