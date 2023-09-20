package com.alphalaneous.Services.GeometryDash;

import jdash.common.DemonDifficulty;
import jdash.common.Difficulty;
import jdash.common.Length;
import jdash.common.entity.GDLevel;
import jdash.common.entity.GDSong;

import java.util.Optional;

public class PseudoLevel implements GDLevel {

    private long ID;

    public PseudoLevel(long ID){
        this.ID = ID;
    }

    @Override
    public long id() {
        return ID;
    }

    @Override
    public String name() {
        return null;
    }

    @Override
    public long creatorPlayerId() {
        return 0;
    }

    @Override
    public String description() {
        return null;
    }

    @Override
    public Difficulty difficulty() {
        return null;
    }

    @Override
    public DemonDifficulty demonDifficulty() {
        return null;
    }

    @Override
    public int stars() {
        return 0;
    }

    @Override
    public int featuredScore() {
        return 0;
    }

    @Override
    public boolean isEpic() {
        return false;
    }

    @Override
    public int downloads() {
        return 0;
    }

    @Override
    public int likes() {
        return 0;
    }

    @Override
    public Length length() {
        return null;
    }

    @Override
    public int coinCount() {
        return 0;
    }

    @Override
    public boolean hasCoinsVerified() {
        return false;
    }

    @Override
    public int levelVersion() {
        return 0;
    }

    @Override
    public int gameVersion() {
        return 0;
    }

    @Override
    public int objectCount() {
        return 0;
    }

    @Override
    public boolean isDemon() {
        return false;
    }

    @Override
    public boolean isAuto() {
        return false;
    }

    @Override
    public Optional<Long> originalLevelId() {
        return Optional.empty();
    }

    @Override
    public int requestedStars() {
        return 0;
    }

    @Override
    public Optional<Long> songId() {
        return Optional.empty();
    }

    @Override
    public Optional<GDSong> song() {
        return Optional.empty();
    }

    @Override
    public Optional<String> creatorName() {
        return Optional.empty();
    }
}
