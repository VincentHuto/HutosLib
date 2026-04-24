package com.vincenthuto.hutoslib.common.data.shadow;

import com.vincenthuto.hutoslib.common.data.shadow.PSerializer.PSerializable;

import net.minecraft.resources.Identifier;

/**
 * An interface supporting ID's and Serializers with generic types.
 *
 * @param <V> This
 */
public interface TypeKeyed<V extends TypeKeyed<V>> extends PSerializable<V> {
	/**
	 * Sets the ID of this object.
	 * @param id The object's ID.
	 * @throws UnsupportedOperationException if the ID has already been set.
	 */
	void setId(Identifier id);

	/**
	 * Returns the ID of this object.
	 */
	Identifier getId();

	/**
	 * Intrusive base implementation of {@link TypeKeyed} which implements set/getId
	 *
	 * @param <V> This
	 */
	public static abstract class TypeKeyedBase<V extends TypeKeyed<V>> implements TypeKeyed<V> {
		public Identifier id;

		@Override
		public void setId(Identifier id) {
			if (this.id != null) throw new UnsupportedOperationException();
			this.id = id;
		}

		@Override
		public Identifier getId() {
			return this.id;
		}

	}
}