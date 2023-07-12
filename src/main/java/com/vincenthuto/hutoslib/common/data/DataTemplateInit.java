package com.vincenthuto.hutoslib.common.data;

import java.util.function.Supplier;

import com.vincenthuto.hutoslib.HutosLib;
import com.vincenthuto.hutoslib.common.data.book.BookPageTemplate;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

public class DataTemplateInit {

	public static final ResourceKey<Registry<DataTemplate>> TEMPLATE_KEY = ResourceKey
			.createRegistryKey(HutosLib.rloc("datatemplate"));
	public static final DeferredRegister<DataTemplate> TEMPLATES = DeferredRegister.create(TEMPLATE_KEY,
			HutosLib.MOD_ID);

	public static Supplier<IForgeRegistry<DataTemplate>> DATA_TEMPLATES = TEMPLATES
			.makeRegistry(() -> new RegistryBuilder<DataTemplate>().setMaxID(Integer.MAX_VALUE - 1)
					.setDefaultKey(HutosLib.rloc("datatemplate")));

	public static final RegistryObject<DataTemplate> text_page = TEMPLATES.register("text_page",
			() -> new BookPageTemplate("text_page"));

}
