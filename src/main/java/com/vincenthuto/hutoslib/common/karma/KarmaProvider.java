package com.vincenthuto.hutoslib.common.karma;

import com.vincenthuto.hutoslib.common.registry.HLAttachmentTypes;

import net.minecraft.world.entity.player.Player;

public class KarmaProvider {

public static float getPlayerKarma(Player player) {
return player.getData(HLAttachmentTypes.KARMA.get()).getKarma();
}

public static IKarma getKarma(Player player) {
return player.getData(HLAttachmentTypes.KARMA.get());
}
}
