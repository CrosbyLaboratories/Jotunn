package io.github.racoondog.jotunn.impl;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Environment(EnvType.CLIENT)
public final class Utils {
    private static final Consumer<Object> EMPTY_CONSUMER = o -> {};
    public static final BlockState VOID_AIR = Blocks.VOID_AIR.getDefaultState();
    public static final BlockState AIR = Blocks.AIR.getDefaultState();
    public static final int SIZE_BITS_X = 1 + MathHelper.floorLog2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
    public static final int SIZE_BITS_Z = SIZE_BITS_X;
    public static final int SIZE_BITS_Y = 64 - SIZE_BITS_X - SIZE_BITS_Z;
    public static final long BITS_X = (1L << SIZE_BITS_X) - 1L;
    public static final long BITS_Y = (1L << SIZE_BITS_Y) - 1L;
    public static final long BITS_Z = (1L << SIZE_BITS_Z) - 1L;
    public static final int BIT_SHIFT_Z = SIZE_BITS_Y;
    public static final int BIT_SHIFT_X = SIZE_BITS_Y + SIZE_BITS_Z;

    private Utils() {}

    public static double squaredDistance(double x1, double y1, double z1, double x2, double y2, double z2) {
        double dx = x1 - x2;
        double dy = y1 - y2;
        double dz = z1 - z2;
        return org.joml.Math.fma(dx, dx, org.joml.Math.fma(dy, dy, dz * dz));
    }

    public static double distance(double x1, double y1, double z1, double x2, double y2, double z2) {
        return Math.sqrt(squaredDistance(x1, y1, z1, x2, y2, z2));
    }

    @SuppressWarnings("unchecked")
    public static <T> Consumer<T> emptyConsumer() {
        return (Consumer<T>) EMPTY_CONSUMER;
    }

    /**
     * Since mojang isn't really consistent with how attributes are stored in different objects, this method retrieves
     * them in a somewhat clean functional way.
     *
     * @param attribute the attribute to target
     * @param set the set of objects to extract attributes from
     * @param function a {@link Function} that retrieves the attribute holders for a given object
     * @param filter a {@link Predicate} that filters attribute holders
     * @param extractor a {@link Function} that retrieves the actual attribute modifiers from the attribute holders
     * @param ignoreCollisions whether to skip checking for duplicate modifier uuids
     * @return the attribute value after factoring all attribute modifiers for a given set of objects
     * @param <T> the object type
     * @param <U> the attribute holder type
     * @author Crosby
     */
    public static <T, U> double extractAttributeValue(RegistryEntry<EntityAttribute> attribute, Iterable<T> set,
                                                      Function<T, ? extends Iterable<U>> function, Predicate<U> filter,
                                                      Function<U, EntityAttributeModifier> extractor,
                                                      boolean ignoreCollisions) {
        EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, emptyConsumer());
        for (T object : set) {
            for (U modifierHolder : function.apply(object)) {
                if (filter.test(modifierHolder)) {
                    EntityAttributeModifier modifier = extractor.apply(modifierHolder);
                    if (!ignoreCollisions) entityAttributeInstance.removeModifier(modifier.uuid());
                    entityAttributeInstance.addTemporaryModifier(modifier);
                }
            }
        }
        return entityAttributeInstance.getValue();
    }
    /**
     * Since mojang isn't really consistent with how attributes are stored in different objects, this method retrieves
     * them in a somewhat clean functional way.
     *
     * @param attribute the attribute to target
     * @param set the set of objects to extract attributes from
     * @param filter a {@link Predicate} that filters objects
     * @param function a {@link Function} that retrieves the attribute modifiers for a given object
     * @param ignoreCollisions whether to skip checking for duplicate modifier uuids
     * @return the attribute value after factoring all attribute modifiers for a given set of objects
     * @param <T> the object type
     * @author Crosby
     */
    public static <T> double extractAttributeValue(RegistryEntry<EntityAttribute> attribute, Iterable<T> set,
                                                   Predicate<T> filter, Function<T, EntityAttributeModifier> function,
                                                   boolean ignoreCollisions) {
        EntityAttributeInstance entityAttributeInstance = new EntityAttributeInstance(attribute, emptyConsumer());
        for (T object : set) {
            if (filter.test(object)) {
                EntityAttributeModifier modifier = function.apply(object);
                if (!ignoreCollisions) entityAttributeInstance.removeModifier(modifier.uuid());
                entityAttributeInstance.addTemporaryModifier(modifier);
            }
        }
        return entityAttributeInstance.getValue();
    }

    public static BlockState getBlockState(@Nullable WorldChunk chunk, int x, int y, int z) {
        if (chunk != null) {
            int sectionIndex = chunk.getSectionIndex(y);
            ChunkSection[] sections = chunk.getSectionArray();

            if (sectionIndex >= 0 && sectionIndex < sections.length) {
                ChunkSection section = sections[sectionIndex];
                if (section.isEmpty()) return AIR; // empty section

                return section.getBlockState(
                        x & 15,
                        y & 15,
                        z & 15
                );
            }
        }

        return VOID_AIR; // out of the world
    }
}
