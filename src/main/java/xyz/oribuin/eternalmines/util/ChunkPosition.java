package xyz.oribuin.eternalmines.util;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

public record ChunkPosition(int chunkX, int chunkZ) {

    /**
     * Create a ChunkPosition from a Chunk
     *
     * @param world The world
     * @return The ChunkPosition
     */
    public Chunk toChunk(World world) {
        return world.getChunkAt(this.chunkX, this.chunkZ);
    }

    /**
     * Create a ChunkPosition from a Chunk
     *
     * @param chunk The chunk
     * @return The ChunkPosition
     */
    public static ChunkPosition fromChunk(Chunk chunk) {
        return new ChunkPosition(chunk.getX(), chunk.getZ());
    }

    /**
     * Create a ChunkPosition from a Location
     *
     * @param location The location
     * @return The ChunkPosition
     */
    public static ChunkPosition fromChunk(Location location) {
        return new ChunkPosition((int) location.getX() >> 4, (int) location.getZ() >> 4);
    }

}

