package kowi2003.core.entity.entities;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.joml.Quaternionf;
import org.joml.Quaternionfc;

import kowi2003.core.contraptions.BlockData;
import kowi2003.core.contraptions.Contraption;
import kowi2003.core.contraptions.ContraptionHelper;
import kowi2003.core.contraptions.ContraptionWrapper;
import kowi2003.core.contraptions.level.IVirtualLevel;
import kowi2003.core.data.IRotatable;
import kowi2003.core.entity.CoreEntitySerializers;
import kowi2003.core.network.PacketHandler;
import kowi2003.core.network.packets.entity.PacketSyncBlocks;
import kowi2003.core.network.packets.entity.PacketSyncContraption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * An entity that represents a contraption in the world
 * This entity is used to render and interact with contraptions in the world
 * 
 * @author KOWI2003
 */
public class ContraptionEntity extends Entity implements IRotatable, ISyncableEntity {

  private static final EntityDataAccessor<Quaternionf> DATA_ROTATION = SynchedEntityData.defineId(ContraptionEntity.class, CoreEntitySerializers.QUATERNION);
  private static final EntityDataAccessor<Contraption> DATA_CONTRAPTION = SynchedEntityData.defineId(ContraptionEntity.class, CoreEntitySerializers.CONTRAPTION);

  private Contraption contraption;
  private Level contraptionLevel;

  @Nonnull
	private Quaternionf rotation = new Quaternionf(0, 0, 0, 1);

  public ContraptionEntity(EntityType<?> entityType, Level level) {
    this(entityType, level, new Contraption());
}

  public ContraptionEntity(EntityType<?> entityType, Level level, @Nonnull Contraption contraption) {
      super(entityType, level);
      this.contraption = contraption;
  }

  @Override
  protected void defineSynchedData() {
    if(contraption == null) contraption = new Contraption();

    this.entityData.define(DATA_ROTATION, new Quaternionf(0, 0, 0, 1));
    this.entityData.define(DATA_CONTRAPTION, contraption);
  }

  @Override
	protected AABB makeBoundingBox() {
    var minX = 0.0;
    var minY = 0.0;
    var minZ = 0.0;
    var maxX = 0.0;
    var maxY = 0.0;
    var maxZ = 0.0;

    for(var blockpos : contraption) {
      minX = Math.min(minX, blockpos.getX());
      minY = Math.min(minY, blockpos.getY());
      minZ = Math.min(minZ, blockpos.getZ());
      
      maxX = Math.max(maxX, blockpos.getX()+1);
      maxY = Math.max(maxY, blockpos.getY()+1);
      maxZ = Math.max(maxZ, blockpos.getZ()+1);
    }
    var result = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
    return result.move(position());
	}
  
	@Override
	protected AABB getBoundingBoxForPose(@Nonnull Pose pose) { return getBoundingBox(); }
	public AABB getBoundingBoxForCulling() { return getBoundingBox(); }

  @Override public boolean causeFallDamage(float p_146828_, float p_146829_, @Nonnull DamageSource source) { return false; }
	@Override protected void checkFallDamage(double fallDistance, boolean p_19912_, @Nonnull BlockState state, @Nonnull BlockPos pos) { this.resetFallDistance(); }
	public boolean isPickable() { return !this.isRemoved(); }
	public boolean canRiderInteract() { return true; }
	public boolean showVehicleHealth() { return false; }
	public boolean canBeCollidedWith() { return true; }
	public boolean isPushable() { return false; }

  @Override
  public InteractionResult interact(@Nonnull Player player, @Nonnull InteractionHand hand) {
    var result = clip(player);
    if(result != null && result.getType() == HitResult.Type.BLOCK) {
      var stack = player.getItemInHand(hand);
      
      if(!player.isShiftKeyDown()) {
        var state = contraption().getState(result.getBlockPos());

        if(state != null) {
          contraptionLevel().blockEntityChanged(result.getBlockPos());
          var blockInteractionResult = state.use(contraptionLevel(), player, hand, result);
          if(!level().isClientSide()) applyContraptionChanges();
          if(blockInteractionResult != InteractionResult.FAIL && blockInteractionResult != InteractionResult.PASS)
            return blockInteractionResult;
        }
      }
      
      if(!stack.isEmpty()) {
        var interactionResult = player.getItemInHand(hand).useOn(new UseOnContext(contraptionLevel(), player, hand, stack, result));
        if(!level().isClientSide()) applyContraptionChanges();
        return interactionResult;
      }
    }
    return super.interact(player, hand);
  }

  @Override
  public boolean hurt(@Nonnull DamageSource source, float damage) {
    var entity = source.getDirectEntity();
    if(entity instanceof Player player) {
      var result = clip(player);
      if(result != null && result.getType() == HitResult.Type.BLOCK && !level().isClientSide()) {
        // contraption().setState(result.getBlockPos(), Blocks.AIR.defaultBlockState());
        if(contraptionLevel().destroyBlock(result.getBlockPos(), true, player))
          applyContraptionChanges();
        return false;
      }
    }
    return false;
  }

  @Override
  @Nullable
  public ItemStack getPickResult() {
    if(level().isClientSide()) {
      var mc = Minecraft.getInstance();
      var camera = mc.gameRenderer.getMainCamera();
      var player = mc.player;
      if(player == null) return super.getPickResult();

      var result = ContraptionHelper.clip(contraptionWrapper(), camera.getPosition(), new Vec3(camera.getLookVector()), (float)player.getBlockReach(), null);
      if (result != null && result.getType() == HitResult.Type.BLOCK)
        return new ItemStack(contraption().getState(result.getBlockPos()).getBlock());
    }
    return super.getPickResult();
  }

  @Override
  public void tick() {
      super.tick();

      var level = level();
      if(!level.isClientSide && contraptionLevel instanceof ServerLevel serverLevel)
        serverLevel.tick(() -> false);

      if(level.isClientSide)
        handleClientTick();
  }

  @OnlyIn(Dist.CLIENT)
  private void handleClientTick() {
    if(contraptionLevel instanceof ClientLevel clientLevel) {
      var mc = Minecraft.getInstance();
      var player = mc.player;
      if(player != null)
        clientLevel.animateTick(player.getBlockX() - getBlockX(), player.getBlockY() - getBlockY(), player.getBlockZ() - getBlockZ());
    }
  }

  @Override
	public void onSyncedDataUpdated(@Nonnull EntityDataAccessor<?> accessor) {
		super.onSyncedDataUpdated(accessor);
		// if(accessor == DATA_NEED_UPDATE) {
      setContraption(entityData.get(DATA_CONTRAPTION));
			setRotation(entityData.get(DATA_ROTATION));
			// setNeedUpdate(false);
		// }else if(accessor == DATA_MODEL) {
			// model = Model.deserialize(entityData.get(DATA_MODEL));

    // level().getChunk(null).getSections();
    // level().getChunk(null).getPostProcessing();
    // level().getChunk(null).getBlockEntitiesPos()
  }

  @Override
  protected void readAdditionalSaveData(@Nonnull CompoundTag tag) {
    setContraption(Contraption.from(tag.getCompound("contraption")));

    var rotationTag = tag.getCompound("rotation");
    setRotation(new Quaternionf(
      rotationTag.getFloat("x"), 
      rotationTag.getFloat("y"), 
      rotationTag.getFloat("z"), 
      rotationTag.getFloat("w")
    ));
  }

  @Override
  protected void addAdditionalSaveData(@Nonnull CompoundTag tag) {
    tag.put("contraption", contraption.serializeNBT());

    var rotationTag = new CompoundTag();
    rotationTag.putFloat("x", rotation.x);
    rotationTag.putFloat("y", rotation.y);
    rotationTag.putFloat("z", rotation.z);
    rotationTag.putFloat("w", rotation.w);
    tag.put("rotation", rotationTag);
  }

  /**
   * Applies the contraption changes to the entity
   */
  public void applyContraptionChanges() {
    setContraption(this.contraption);
    sync();
  }

  public void setContraption(Contraption contraption) {
    var hasChanged = this.contraption != contraption;
    
    for (var tile : new ContraptionWrapper(contraption, level()).getBlockEntities())
      tile.setLevel(contraptionLevel());

    this.entityData.set(DATA_CONTRAPTION, contraption);
    this.contraption = contraption;

    if(hasChanged)
      setBoundingBox(makeBoundingBox()); 
  }

  @Nonnull
  public Contraption contraption() {
    return this.contraption;
  }

  @Nonnull
  public ContraptionWrapper contraptionWrapper() {
    var wrapper = new ContraptionWrapper(contraption, level());
    wrapper.setPosition(position());
    wrapper.setRotation(getRotation());

    if(contraptionLevel instanceof IVirtualLevel virtualLevel)
      virtualLevel.updateData(wrapper, level());
    
    return wrapper;
  }

  private Level contraptionLevel() {
    if(contraptionLevel == null) {
      contraptionLevel = contraptionWrapper().contraptionLevel(level(), w -> {
        setContraption(w.contraption());
        syncContraption();
      }, (wrapper, position) -> syncBlock(position));
    }
    return contraptionLevel;
  }

  /**
   * Sets the rotation of the contraption, and updates this to the server if the entity is controlled by the local instance
   * @param rotation the new rotation
   */
  public void setRotation(Quaternionfc rotation) {
		this.rotation = new Quaternionf(rotation);
		this.entityData.set(DATA_ROTATION, this.rotation);
		// if(level().isClientSide && isControlledByLocalInstance())
			// sync(rotation);
	}

  /**
   * Gets the rotation of the contraption
   * @return the rotation
   */
  @SuppressWarnings("resource")
  public Quaternionf getRotation() {
		var rotation = level().isClientSide && isControlledByLocalInstance() ? this.rotation : entityData.get(DATA_ROTATION);
    if(rotation.w == 0)
      setRotation(rotation = new Quaternionf(0, 0, 0, 1));

    return rotation;
	}

  /**
   * Clips the contraption with the given player
   * @param player the player to clip with
   * @return the block hit result
   */
  public BlockHitResult clip(Player player) {
    var cameraPosition = player.getEyePosition();
    return ContraptionHelper.clip(contraptionWrapper(), cameraPosition, player.getLookAngle(), (float)player.getBlockReach(), null);
  }

  public void syncContraption() {
    var level = level();
    if(level.isClientSide)
      PacketHandler.sendToServer(new PacketSyncContraption(getId(), getUUID(), contraption()));
    else
      PacketHandler.sendToAllClients(new PacketSyncContraption(getId(), getUUID(), contraption()), level);
  }
  
  public void syncBlock(BlockPos position) { syncBlocks(Set.of(position)); }
  public void syncBlock(BlockData position) { syncBlocks(Set.of(position)); }

  public void syncBlocks(Collection<BlockPos> positions) {
    var blocks = new HashSet<BlockData>(
        positions.stream()
        .map(pos -> new BlockData(pos, contraption().getState(pos), contraption().getBlockEntity(pos)))
        .filter(blockdata -> blockdata.state() != null)
        .toList()
    );

    var level = level();
    if(level.isClientSide)
      PacketHandler.sendToServer(new PacketSyncBlocks(getId(), getUUID(), blocks));
    else
      PacketHandler.sendToAllClients(new PacketSyncBlocks(getId(), getUUID(), blocks), level);
  }

  public void syncBlocks(Set<BlockData> blocks) {
    var level = level();
    if(level.isClientSide)
      PacketHandler.sendToServer(new PacketSyncBlocks(getId(), getUUID(), blocks));
    else
      PacketHandler.sendToAllClients(new PacketSyncBlocks(getId(), getUUID(), blocks), level);
  }
}
