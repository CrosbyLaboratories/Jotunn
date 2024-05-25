package io.github.racoondog.jotunn.api.explosion;

import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record ExposureRaycastContext(World world, Vec3d startPosition, Vec3d endPosition) { }
