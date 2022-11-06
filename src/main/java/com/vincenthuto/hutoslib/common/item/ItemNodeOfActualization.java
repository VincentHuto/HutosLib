package com.vincenthuto.hutoslib.common.item;

import com.vincenthuto.hutoslib.common.karma.IKarma;
import com.vincenthuto.hutoslib.common.karma.KarmaProvider;
import com.vincenthuto.hutoslib.common.network.HLPacketHandler;
import com.vincenthuto.hutoslib.common.network.PacketKarmaServer;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class ItemNodeOfActualization extends Item {

	public ItemNodeOfActualization(Properties properties) {
		super(properties.stacksTo(1));

	}

	@Override
	public Rarity getRarity(ItemStack stack) {
		return Rarity.RARE;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
		IKarma karmaCap = playerIn.getCapability(KarmaProvider.KARMA_CAPA).orElseThrow(NullPointerException::new);

		if (!worldIn.isClientSide) {
			if (karmaCap != null) {
				karmaCap.toggleActive();
				HLPacketHandler.MAINCHANNEL.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn),
						new PacketKarmaServer(karmaCap));
				playerIn.displayClientMessage(
						 Component.literal(ChatFormatting.GOLD + "Toggling Karma to :" + karmaCap.isActive()), true);
			}
		}

		return super.use(worldIn, playerIn, handIn);
	}
}