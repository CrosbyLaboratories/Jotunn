package io.github.racoondog.jotunn.impl;

import io.github.racoondog.jotunn.api.Combat;
import io.github.racoondog.jotunn.api.Jotunn;
import io.github.racoondog.jotunn.api.configuration.CombatBehaviour;
import io.github.racoondog.multiversion.MultiVersion;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public final class CombatImpl implements Combat {
    private final CombatBehaviour behaviour;

    public CombatImpl(CombatBehaviour behaviour) {
        this.behaviour = behaviour;
    }

    public float rawAttackDamage(LivingEntity attacker, LivingEntity target, float charge) {
        float itemDamage = (float) Jotunn.attributes().getAttributeValue(attacker, EntityAttributes.GENERIC_ATTACK_DAMAGE);

        // Get enchant damage
        ItemStack stack = attacker.getMainHandStack();
        float enchantDamage = EnchantmentHelper.getAttackDamage(stack, target.getType());

        // Factor charge
        if (attacker instanceof PlayerEntity playerEntity) {
            itemDamage *= 0.2f + charge * charge * 0.8f;
            enchantDamage *= charge;

            if (MultiVersion.getInstance().isAtLeast(MultiVersion.V1_20_5)) {
                itemDamage += stack.getItem().getBonusAttackDamage(playerEntity, itemDamage);
            }

            // Factor critical hit
            if (charge > 0.9f && canCriticalHit(playerEntity)) {
                itemDamage *= 1.5f;
            }
        }

        return itemDamage + enchantDamage;
    }

    @Override
    public float maxAttackDamage(LivingEntity attacker, LivingEntity target) {
        float damage = rawAttackDamage(attacker, target, 1f);

        DamageSource damageSource = attacker instanceof PlayerEntity player ? player.getDamageSources().playerAttack(player) : attacker.getDamageSources().mobAttack(attacker);
        return Jotunn.reductions().reduce(damage, target, damageSource);
    }

    @Override
    public float attackDamage(LivingEntity attacker, LivingEntity target) {
        DamageSource damageSource;
        float charge = 0f;
        if (attacker instanceof PlayerEntity player) {
            damageSource = player.getDamageSources().playerAttack(player);
            charge = player.getAttackCooldownProgress(0.5f);
        } else {
            damageSource = attacker.getDamageSources().mobAttack(attacker);
        }

        float damage = rawAttackDamage(attacker, target, charge);
        return Jotunn.reductions().reduce(damage, target, damageSource);
    }

    @Override
    public float sweepingDamage(LivingEntity attacker, LivingEntity attackTarget, LivingEntity sweepingTarget) {
        // Attacker conditions
        if (!(attacker instanceof PlayerEntity player)
                || player.getAttackCooldownProgress(0.5f) <= 0.9f
                || canCriticalHit(player)
                || player.isSprinting()
                || !player.isOnGround()
                || player.horizontalSpeed - player.prevHorizontalSpeed >= player.getMovementSpeed()) {
            return 0f;
        }

        ItemStack stack = player.getMainHandStack();
        if (!(stack.getItem() instanceof SwordItem)) return 0f;

        // Target conditions
        if (sweepingTarget == attacker
                || sweepingTarget == attackTarget
                || attacker.isTeammate(sweepingTarget)
                || attacker.squaredDistanceTo(sweepingTarget) >= 9d) {
            return 0f;
        }

        float attackDamage = rawAttackDamage(attacker, attackTarget, player.getAttackCooldownProgress(0.5f));
        float sweepingDamage = 1f + EnchantmentHelper.getSweepingMultiplier(attacker) * attackDamage;

        DamageSource damageSource = attacker.getDamageSources().playerAttack(player);
        return Jotunn.reductions().reduce(sweepingDamage, sweepingTarget, damageSource);
    }

    @Override
    public boolean canCriticalHit(PlayerEntity entity) {
        return behaviour.canCriticalHit(entity);
    }
}
