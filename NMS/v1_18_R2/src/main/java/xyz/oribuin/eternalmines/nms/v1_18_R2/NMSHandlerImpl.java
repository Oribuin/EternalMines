package xyz.oribuin.eternalmines.nms.v1_18_R2;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ThreadedLevelLightEngine;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftMagicNumbers;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import xyz.oribuin.eternalmines.nms.NMSHandler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NMSHandlerImpl implements NMSHandler {

    private final Map<BlockPos, BlockState> modified = new HashMap<>();

    @Override
    public void setBlock(@NotNull Location location, Material type) {
        final BlockPos blockPos = new BlockPos(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );

        modified.put(blockPos, CraftMagicNumbers.getBlock(type).defaultBlockState());
    }

    @Override
    public Material getBlock(Location location) {
        final BlockPos blockPos = new BlockPos(
                location.getBlockX(),
                location.getBlockY(),
                location.getBlockZ()
        );

        final BlockState state = this.modified.get(blockPos);
        if (state != null) {
            return CraftMagicNumbers.getMaterial(state.getBlock());
        }

        return location.getBlock().getType();
    }

    @Override
    public void update(World world) {
        final Level level = ((CraftWorld) world).getHandle();
        final Set<ChunkAccess> chunks = new HashSet<>(); // ArrayLists crash the server :)
        for (Map.Entry<BlockPos, BlockState> entry : modified.entrySet()) {
            final ChunkAccess chunk = level.getChunkSource().getChunk(
                    entry.getKey().getX() >> 4,
                    entry.getKey().getZ() >> 4,
                    false
            );

            if (chunk == null) continue;

            chunks.add(chunk);
            chunk.setBlockState(entry.getKey(), entry.getValue(), false);
        }

        // Update light engine
        final ThreadedLevelLightEngine lightEngine = (ThreadedLevelLightEngine) level.getChunkSource().getLightEngine();
        for (final BlockPos pos : this.modified.keySet()) {
            lightEngine.checkBlock(pos);
        }

        for (final ChunkAccess access : chunks) {
            final LevelChunk levelChunk = (LevelChunk) access;

            final ClientboundForgetLevelChunkPacket unloadChunk = new ClientboundForgetLevelChunkPacket(access.getPos().x, access.getPos().z);
            final ClientboundLevelChunkWithLightPacket chunkWithLight = new ClientboundLevelChunkWithLightPacket(
                    levelChunk,
                    lightEngine,
                    null,
                    null,
                    false
            );

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (!player.getWorld().getName().equalsIgnoreCase(world.getName()))
                    return;

                final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
                serverPlayer.connection.send(unloadChunk);
                serverPlayer.connection.send(chunkWithLight);
            }
        }

        this.modified.clear();
    }

}
