package xyz.oribuin.eternalmines.nms.v1_20_R1;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R1.util.CraftMagicNumbers;
import xyz.oribuin.eternalmines.nms.NMSHandler;

import java.util.Map;

public class NMSHandlerImpl implements NMSHandler {

    @Override
    public void update(Map<Location, Material> toUpdate) {
        System.out.println("Updating " + toUpdate.size() + " blocks");
        final Location first = toUpdate.keySet().stream().findFirst().orElse(null);
        if (first == null) return;

        final World bukkitWorld = first.getWorld();
        if (bukkitWorld == null) return;

        final Level world = ((CraftWorld) bukkitWorld).getHandle();
//        final Map<BlockPos, BlockState> states = new HashMap<>();

        // Update the block.
        for (final Map.Entry<Location, Material> entry : toUpdate.entrySet()) {
            final Location location = entry.getKey();
            final Material material = entry.getValue();
            final LevelChunk chunk = world.getChunkSource().getChunk(
                    location.getBlockX() >> 4,
                    location.getBlockZ() >> 4,
                    true
            );
            if (chunk == null) continue;

            final BlockPos blockPos = new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            final BlockState state = CraftMagicNumbers.getBlock(material).defaultBlockState();
            chunk.setBlockState(blockPos, state, false);

//            states.put(blockPos, state);
        }

//        // Send the update to all players.
//        for (final Player player : Bukkit.getOnlinePlayers()) {
//            final Location location = player.getLocation();
//
//            // Not in the same world
//            if (location.getWorld() == null || !location.getWorld().getName().equalsIgnoreCase(bukkitWorld.getName()))
//                return;
//
//        }
    }

}
