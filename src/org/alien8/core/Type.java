package org.alien8.core;
/**
 * This enum class provides a list of possible entity types. All entities must have a valid type.
 * This determines how or if the entity is rendered, how it is updated, etc.
 */
public enum Type {
	PLAYER(),
	AI(),
	TERRAIN(),
	PROJECTILE();
}
