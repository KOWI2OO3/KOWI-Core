package kowi2003.core.contraptions.level;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import com.google.common.base.Preconditions;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelDataManager;

@OnlyIn(Dist.CLIENT)
public class VirtualModelDataManager extends ModelDataManager {

    private final BlockAndTintGetter level;

    private final Map<ChunkPos, Set<BlockPos>> needModelDataRefresh = new ConcurrentHashMap<>();
    private final Map<ChunkPos, Map<BlockPos, ModelData>> modelDataCache = new ConcurrentHashMap<>();

    public VirtualModelDataManager(BlockAndTintGetter level) {
        super(null);
        this.level = level;
    }
 
    public void requestRefresh(@NotNull BlockEntity blockEntity)
    {
        Preconditions.checkNotNull(blockEntity, "Block entity must not be null");
        needModelDataRefresh.computeIfAbsent(new ChunkPos(blockEntity.getBlockPos()), $ -> Collections.synchronizedSet(new HashSet<>()))
                            .add(blockEntity.getBlockPos());
    }

    private void refreshAt(ChunkPos chunk)
    {
        Set<BlockPos> needUpdate = needModelDataRefresh.remove(chunk);

        if (needUpdate != null)
        {
            Map<BlockPos, ModelData> data = modelDataCache.computeIfAbsent(chunk, $ -> new ConcurrentHashMap<>());
            for (BlockPos pos : needUpdate)
            {
                BlockEntity toUpdate = level.getBlockEntity(pos);
                if (toUpdate != null && !toUpdate.isRemoved())
                {
                    data.put(pos, toUpdate.getModelData());
                }
                else
                {
                    data.remove(pos);
                }
            }
        }
    }

    public @Nullable ModelData getAt(BlockPos pos)
    {
        return getAt(new ChunkPos(pos)).get(pos);
    }

    public Map<BlockPos, ModelData> getAt(ChunkPos pos)
    {
        Preconditions.checkArgument(true, "Cannot request model data for server level");
        refreshAt(pos);
        return modelDataCache.getOrDefault(pos, Collections.emptyMap());
    }
}
