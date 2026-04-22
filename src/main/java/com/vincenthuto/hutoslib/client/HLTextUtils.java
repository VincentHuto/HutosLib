package com.vincenthuto.hutoslib.client;

import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.neoforged.fml.loading.StringUtils;

public class HLTextUtils {

public static Rarity AURIC = Rarity.create("Auric", ChatFormatting.GOLD);
public static Rarity SANGUINE = Rarity.create("Sanguine", ChatFormatting.DARK_RED);

public static String getItemRegistryName(Item item) {
return BuiltInRegistries.ITEM.getKey(item).getPath();
}

public static String getBlockRegistryName(Block block) {
return BuiltInRegistries.BLOCK.getKey(block).getPath();
}

public static String getEntityRegistryName(EntityType<?> entity) {
return BuiltInRegistries.ENTITY_TYPE.getKey(entity).getPath();
}

public static String convertInitToLang(String text) {
if (text == null || text.isEmpty()) {
return text;
}

StringBuilder converted = new StringBuilder();
boolean convertNext = true;
text.replace("_trail", "");
for (char ch : text.toCharArray()) {
if (ch == '_') {
ch = ' ';
convertNext = true;
} else if (convertNext) {
ch = Character.toTitleCase(ch);
convertNext = false;
} else {
ch = Character.toLowerCase(ch);
}
converted.append(ch);
}

return converted.toString();
}

public static String stringToBloody(String parString) {
String outputString = "";
outputString = ChatFormatting.ITALIC + parString + ChatFormatting.DARK_RED;
return outputString;
}

public static String stringToBlueObf(String parString, int parShineLocation, boolean parReturnToBlack) {
int stringLength = parString.length();
if (stringLength < 1) return "";
String outputString = "";
for (int i = 0; i < stringLength; i++) {
if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 0) {
outputString = outputString + ChatFormatting.OBFUSCATED + parString.substring(i, i + 1);
} else if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 1) {
outputString = outputString + ChatFormatting.RED + parString.substring(i, i + 1);
} else if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 87) {
outputString = outputString + ChatFormatting.OBFUSCATED + parString.substring(i, i + 1);
} else {
outputString = outputString + ChatFormatting.RED + parString.substring(i, i + 1);
}
}
if (parReturnToBlack) return outputString + ChatFormatting.BLACK;
return outputString + ChatFormatting.WHITE;
}

public static String stringToGolden(String parString, int parShineLocation, boolean parReturnToBlack) {
int stringLength = parString.length();
if (stringLength < 1) return "";
String outputString = "";
for (int i = 0; i < stringLength; i++) {
if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 0) {
outputString = outputString + ChatFormatting.WHITE + parString.substring(i, i + 1);
} else if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 1) {
outputString = outputString + ChatFormatting.GOLD + parString.substring(i, i + 1);
} else if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 87) {
outputString = outputString + ChatFormatting.WHITE + parString.substring(i, i + 1);
} else {
outputString = outputString + ChatFormatting.BLACK + parString.substring(i, i + 1);
}
}
if (parReturnToBlack) return outputString + ChatFormatting.BLACK;
return outputString + ChatFormatting.WHITE;
}

public static String stringToRedObf(String parString, int parShineLocation, boolean parReturnToBlack) {
int stringLength = parString.length();
if (stringLength < 1) return "";
String outputString = "";
for (int i = 0; i < stringLength; i++) {
if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 0) {
outputString = outputString + ChatFormatting.OBFUSCATED + parString.substring(i, i + 1);
} else if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 1) {
outputString = outputString + ChatFormatting.RED + parString.substring(i, i + 1);
} else if ((i + parShineLocation + System.nanoTime() / 20) % 88 == 87) {
outputString = outputString + ChatFormatting.OBFUSCATED + parString.substring(i, i + 1);
} else {
outputString = outputString + ChatFormatting.BLACK + parString.substring(i, i + 1);
}
}
if (parReturnToBlack) return outputString + ChatFormatting.BLACK;
return outputString + ChatFormatting.WHITE;
}

public static String stringToResonant(String parString) {
float stringValue = Float.parseFloat(parString);
int stringLength = parString.length();
if (stringLength < 1) return "";
String outputString = "";
ChatFormatting[] karmicColors = { ChatFormatting.RED, ChatFormatting.DARK_RED, ChatFormatting.BLUE,
ChatFormatting.AQUA };
ChatFormatting[] ManaColors = { ChatFormatting.BLUE, ChatFormatting.AQUA };
if (stringValue > 0) {
for (int i = 0; i < stringLength; i++) {
outputString = ChatFormatting.ITALIC + outputString + ManaColors[i % 2] + parString.substring(i, i + 1);
}
} else if (stringValue < 0) {
for (int i = 0; i < stringLength; i++) {
outputString = ChatFormatting.ITALIC + outputString + karmicColors[i % 2]
+ parString.substring(i, i + 1);
}
} else if (stringValue == 0.0) {
for (int i = 0; i < stringLength; i++) {
outputString = ChatFormatting.WHITE + parString;
}
}
return outputString;
}

public static String toProperCase(String input) {
String newString = "";
String culledString = input.replaceAll("_", " ");
input = culledString;
for (int i = 0; i < input.length(); i++) {
if (i == 0) {
newString = newString + StringUtils.toUpperCase(String.valueOf(input.charAt(i)));
} else {
newString = newString + StringUtils.toLowerCase(String.valueOf(input.charAt(i)));
}
}
return newString;
}
}
