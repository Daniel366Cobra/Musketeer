package com.daniel366cobra.musketeer.item;


import com.daniel366cobra.musketeer.entity.projectile.MusketProjectileEntity;
import com.daniel366cobra.musketeer.init.ModItems;
import com.daniel366cobra.musketeer.init.ModSounds;
import net.fabricmc.fabric.api.item.v1.FabricItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.EntityDamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class MusketItem extends RangedWeaponItem implements FabricItem {
    private static final String LOADED_KEY = "Loaded";
    private static final String BAYONET_ATTACHED_KEY = "BayonetAttached";

    public static final Predicate<ItemStack> MUSKET_PROJECTILES = stack -> stack.isOf(ModItems.MUSKET_BALL);

    public MusketItem(Settings settings) {
        super(settings);
    }

    @Override
    public Predicate<ItemStack> getProjectiles() {
        return MUSKET_PROJECTILES;
    }

    @Override
    public boolean allowNbtUpdateAnimation(PlayerEntity player, Hand hand, ItemStack oldStack, ItemStack newStack) {
        return false;
    }

    @Override
    public int getRange() {
        return 16;
    }

    //LMB - buttstroke attack. Has a chance to briefly stun the target.
    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        if (attacker instanceof PlayerEntity attackerPlayerEntity
                && !attackerPlayerEntity.getItemCooldownManager().isCoolingDown(this)) {

            DamageSource musketMeleeDamageSource = new EntityDamageSource("musket_buttstroke", attackerPlayerEntity);
            target.damage(musketMeleeDamageSource, 2);
            if (attackerPlayerEntity.getRandom().nextBoolean()) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 20), attackerPlayerEntity);
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 20, 4), attackerPlayerEntity);
            }
            target.setVelocity(attackerPlayerEntity.getRotationVector().add(0.0f, 0.5f, 0.0f).multiply(1.25f));
            attackerPlayerEntity.getWorld().playSound(null,
                    attackerPlayerEntity.getX(), attackerPlayerEntity.getY(), attackerPlayerEntity.getZ(),
                    ModSounds.MUSKET_BUTTSTROKE, SoundCategory.PLAYERS, 1.0F, 0.8F);

            if (!attackerPlayerEntity.isCreative())
                stack.damage(3, attackerPlayerEntity, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));

            attackerPlayerEntity.getItemCooldownManager().set(this, 20);
        }
        return true;
    }

    //Sets weapon in use (firing/loading)
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack weaponItemStack = user.getStackInHand(hand);
        if (isLoaded(weaponItemStack)) {

            shoot(world, user, weaponItemStack, user.isSneaking() ? 0.5F : 3.0F);
            setLoaded(weaponItemStack, false);
            return TypedActionResult.consume(weaponItemStack);
        }
        if (!findAmmo(user).isEmpty()) {
            if (!isLoaded(weaponItemStack)) {
                user.setCurrentHand(hand);
            }
            return TypedActionResult.consume(weaponItemStack);
        }
        return TypedActionResult.fail(weaponItemStack);
    }

    public static boolean hasAmmo(PlayerEntity user) {
        return findAmmo(user).isEmpty();
    }

    public static ItemStack findAmmo(PlayerEntity user) {
        boolean userCreative = user.isCreative();
        ItemStack ammoStack;
        if (isAmmo(user.getMainHandStack())) {
            ammoStack = user.getMainHandStack();
            return ammoStack;
        } else if (isAmmo(user.getOffHandStack())) {
            ammoStack = user.getOffHandStack();
            return ammoStack;
        } else {
            for (int iter = 0; iter < user.getInventory().size(); iter++) {
                if (isAmmo(user.getInventory().getStack(iter))) {
                    ammoStack = user.getInventory().getStack(iter);
                    return ammoStack;
                }
            }
            ammoStack = ItemStack.EMPTY;
        }
        if (ammoStack.isEmpty() && userCreative) {
            ammoStack = new ItemStack(ModItems.MUSKET_BALL);
        }
        return ammoStack;
    }


    public static boolean isAmmo(ItemStack stack) {
        return stack.getItem() instanceof MusketBallItem;
    }

    private static boolean checkAndConsumeAmmo(LivingEntity user) {
        if (user instanceof PlayerEntity playerEntity) {
            boolean userIsCreative = playerEntity.isCreative();
            ItemStack ammoStack = findAmmo(playerEntity);

            if (ammoStack.isEmpty() && userIsCreative) {
                ammoStack = new ItemStack(ModItems.MUSKET_BALL);
            }
            return attemptConsumeAmmo(ammoStack, userIsCreative);
        } else {
            return true;
        }
    }

    private static boolean attemptConsumeAmmo(ItemStack ammoStack, boolean creative) {
        if (creative) return true;

        if (ammoStack.isEmpty()) return false;

        ammoStack.decrement(1);
        return true;
    }


    @Override
    public void onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        int timeLoading = this.getMaxUseTime(stack) - remainingUseTicks;
        float loadProgress = getLoadProgress(timeLoading, stack);

        if (loadProgress >= 1.0f && !isLoaded(stack) && checkAndConsumeAmmo(user)) {
            setLoaded(stack, true);
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_COCK_FULL, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        } else {
            world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_MISFIRE, SoundCategory.PLAYERS, 1.0f, 1.0f / (world.getRandom().nextFloat() * 0.5f + 1.0f) + 0.2f);
        }
    }

    public static boolean isLoaded(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean(LOADED_KEY);
    }

    public static void setLoaded(ItemStack stack, boolean loaded) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean(LOADED_KEY, loaded);
    }

    public static boolean isBayonetAttached(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.getBoolean(BAYONET_ATTACHED_KEY);
    }

    public static void setBayonetAttached(ItemStack stack, boolean bayonetAttached) {
        NbtCompound nbtCompound = stack.getOrCreateNbt();
        nbtCompound.putBoolean(BAYONET_ATTACHED_KEY, bayonetAttached);
    }

    public static void shoot(World world, LivingEntity shooter, ItemStack weaponStack, float divergence) {
        if (world.isClient()) {
            return;
        }

        if (!shooter.isWet()) {

            MusketProjectileEntity bullet = MusketItem.createBullet(world, shooter);

            boolean creative = ((PlayerEntity) shooter).isCreative();

            //Shot sound
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.MUSKET_SHOT, SoundCategory.PLAYERS, 2.0F, 0.5F);
            bullet.setVelocity(shooter, shooter.getPitch(), shooter.getYaw(), 6F, divergence);

            //Degrade the durability
            if (!creative) {
                weaponStack.damage(1, shooter, (entity) -> entity.sendToolBreakStatus(shooter.getActiveHand()));

                //additionally have a chance to explode if durability is very low
                DamageSource gunExplosionDamage;
                int remainingLife = weaponStack.getMaxDamage() - weaponStack.getDamage();
                if (remainingLife <= 10) {
                    if (shooter.getRandom().nextFloat() < (1.0F / (remainingLife + 1))) {
                        ((PlayerEntity) shooter).getInventory().removeOne(weaponStack);
                        gunExplosionDamage = new EntityDamageSource("musket_explosion", shooter);
                        world.createExplosion(shooter, shooter.getX(), shooter.getY() + shooter.getEyeHeight(shooter.getPose()) - 0.1D, shooter.getZ(), 1.5F, false, Explosion.DestructionType.NONE);
                        shooter.damage(gunExplosionDamage, 18.0F);

                    }
                }
            }

            //Create and spawn the bullet
            world.spawnEntity(bullet);


        } else {
            world.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), ModSounds.MUSKET_MISFIRE, SoundCategory.PLAYERS, 2.0F, 1.0F);
        }

    }

    private static MusketProjectileEntity createBullet(World world, LivingEntity shooter) {
        MusketProjectileEntity bullet = new MusketProjectileEntity(world, shooter, 2.0d, false);
        bullet.setSound(ModSounds.BULLET_HIT);
        return bullet;
    }

    @Override
    public void usageTick(World world, LivingEntity user, ItemStack stack, int remainingUseTicks) {
        if (!world.isClient) {
            int totalLoadTicks = this.getMaxUseTime(stack);
            int currentTick = totalLoadTicks - remainingUseTicks;

            int cockedTick = (int) (totalLoadTicks * 0.2f);
            int loadedTick = (int) (totalLoadTicks * 0.5f);
            int powderLoadedTick = (int) (totalLoadTicks * 0.7f);

            if (currentTick == cockedTick) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_COCK_START, SoundCategory.PLAYERS, 0.5f, 1.0f);
            } else if (currentTick == loadedTick) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_COCK_HALF, SoundCategory.PLAYERS, 0.5f, 1.0f);
            } else if (currentTick == powderLoadedTick) {
                world.playSound(null, user.getX(), user.getY(), user.getZ(), ModSounds.MUSKET_POWDER_POUR, SoundCategory.PLAYERS, 0.5f, 1.0f);
            }
        }
    }

    @Override
    public int getMaxUseTime(ItemStack stack) {
        return MusketItem.getLoadTime(stack) + 5;
    }

    public static int getLoadTime(ItemStack stack) {
        return 25;
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BOW;
    }

    private static float getLoadProgress(int useTicks, ItemStack stack) {
        float f = (float) useTicks / (float) MusketItem.getLoadTime(stack);
        if (f > 1.0f) {
            f = 1.0f;
        }
        return f;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        if (MusketItem.isLoaded(stack)) {
            tooltip.add(Text.translatable("item.musketeer.musket.loaded"));
        }
        tooltip.add(Text.translatable("item.musketeer.musket.shift_info_hint"));
        if (Screen.hasShiftDown()) {
            tooltip.add(Text.translatable("item.musketeer.musket.shift_info_1"));
            tooltip.add(Text.translatable("item.musketeer.musket.shift_info_2"));
            tooltip.add(Text.translatable("item.musketeer.musket.shift_info_3"));
        }
    }

    @Override
    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

}