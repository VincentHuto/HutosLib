package com.vincenthuto.hutoslib.common.effectsource;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

public final class EffectSourceInference {

	private static final StackWalker WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE);
	private static final Map<String, String> PACKAGE_OWNERS = new ConcurrentHashMap<>();

	public static void registerPackageOwner(String packagePrefix, String modId) {
		if (packagePrefix == null || packagePrefix.isBlank() || !packagePrefix.endsWith(".")) {
			throw new IllegalArgumentException("Package prefix must be nonblank and end with '.': " + packagePrefix);
		}
		if (modId == null || modId.isBlank()) {
			throw new IllegalArgumentException("Mod ID must be nonblank");
		}
		PACKAGE_OWNERS.put(packagePrefix, modId);
	}

	public static CapturedCaller capture() {
		return WALKER.walk(stream -> stream
				.map(stackFrame -> new Candidate(new Frame(stackFrame.getClassName(), stackFrame.getMethodName(),
						stackFrame.getFileName(), stackFrame.getLineNumber()), stackFrame.getDeclaringClass()))
				.filter(candidate -> isGameplayFrame(candidate.frame()))
				.findFirst()
				.map(candidate -> new CapturedCaller(candidate.frame(), originFor(candidate.type())))
				.orElse(CapturedCaller.unknown()));
	}

	public static Optional<Frame> selectCaller(List<Frame> frames) {
		return frames.stream().filter(EffectSourceInference::isGameplayFrame).findFirst();
	}

	private static boolean isGameplayFrame(Frame frame) {
		String className = frame.className();
		return !className.startsWith("com.vincenthuto.hutoslib.common.effectsource.")
				&& !className.startsWith("net.neoforged.bus.")
				&& !className.startsWith("net.neoforged.neoforge.eventbus.")
				&& !className.startsWith("java.lang.StackWalker")
				&& !className.startsWith("java.lang.invoke.")
				&& !className.startsWith("java.lang.reflect.")
				&& !className.startsWith("jdk.internal.reflect.")
				&& !(className.equals("net.minecraft.world.entity.LivingEntity") && frame.methodName().equals("addEffect"));
	}

	private static Origin originFor(Class<?> type) {
		String jar = jarName(type);
		Optional<String> registeredModId = registeredModId(type.getName());
		if (registeredModId.isPresent()) {
			String modId = registeredModId.get();
			ModList modList = ModList.get();
			if (modList != null) {
				return modList.getModContainerById(modId)
						.map(container -> new Origin(modId, container.getModInfo().getDisplayName(),
								container.getModInfo().getVersion().toString(), jar))
						.orElse(new Origin(modId, modId, "", jar));
			}
			return new Origin(modId, modId, "", jar);
		}
		if (type.getName().startsWith("net.minecraft.")) {
			return new Origin("minecraft", "Minecraft", "1.21.1", jar);
		}
		ModList modList = ModList.get();
		if (modList != null) {
			if (type.getName().startsWith("com.vincenthuto.hutoslib.")) {
				return modList.getModContainerById("hutoslib")
						.map(container -> new Origin("hutoslib", container.getModInfo().getDisplayName(),
								container.getModInfo().getVersion().toString(), jar))
						.orElse(new Origin("hutoslib", "HutosLib", "", jar));
			}
			for (var file : modList.getModFiles()) {
				if (!file.getFile().getFileName().equals(jar)) {
					continue;
				}
				IModInfo mod = file.getMods().getFirst();
				return new Origin(mod.getModId(), mod.getDisplayName(), mod.getVersion().toString(), jar);
			}
		}
		return new Origin("", "", "", jar);
	}

	private static Optional<String> registeredModId(String className) {
		return PACKAGE_OWNERS.entrySet().stream()
				.filter(entry -> className.startsWith(entry.getKey()))
				.max((left, right) -> Integer.compare(left.getKey().length(), right.getKey().length()))
				.map(Map.Entry::getValue);
	}

	public static String jarName(Class<?> type) {
		try {
			URI location = type.getProtectionDomain().getCodeSource().getLocation().toURI();
			Path path = Path.of(location);
			if (Files.isDirectory(path)) {
				return "development classes";
			}
			Path fileName = path.getFileName();
			return fileName == null ? "development classes" : fileName.toString();
		} catch (Exception exception) {
			return "Unknown";
		}
	}

	private EffectSourceInference() {
	}

	private record Candidate(Frame frame, Class<?> type) {
	}

	public record Frame(String className, String methodName, String fileName, int lineNumber) {
		public Frame {
			fileName = fileName == null ? "Unknown" : fileName;
		}
	}

	public record Origin(String modId, String modName, String modVersion, String jar) {
	}

	public record CapturedCaller(Frame frame, Origin origin) {
		static CapturedCaller unknown() {
			return new CapturedCaller(new Frame("", "", "Unknown", -1), new Origin("", "", "", "Unknown"));
		}
	}
}
