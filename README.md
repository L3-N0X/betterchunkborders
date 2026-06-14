# Better Chunk Borders

Better Chunk Borders is a Minecraft Fabric mod that improves the visuals of the default chunk debug renderer. It replaces the default chunk border renderer (rather than adding a new one on top), meaning the borders are still toggled using the vanilla `F3` + `G` key combination.

## Features

- **Custom Grid Spacing**: Control the frequency of internal vertical grid lines within the chunk (spacing increments of 1, 2, 4, 8, or 16).
- **Border Customization**: Customize the color, thickness, and corner line colors of the chunk borders.
- **Gradient Walls**: Render semi-transparent boundary walls that fade between custom bottom and top colors.
- **Terrain Heightmap Adaptation**: Option to adapt borders and gradients to the terrain surface height, with optional sloped terrain gradients and height smoothing.
- **Player Height Tracking**: Option to make lines and gradients relative to the player's height.
- **Horizontal Lines**: Add custom horizontal grid lines that can optionally follow the player.
- **In-Game Configuration**: Configure all options in-game via Mod Menu.

## Requirements

Ensure you have the following installed:

- Fabric Loader
- Fabric API
- Fabric Language Kotlin
- YetAnotherConfigLib (YACL)
- Mod Menu

## Building the Mod

To build the mod from source, clone the repository and run the Gradle build task:

```bash
./gradlew build
```

The compiled mod `.jar` will be available in `build/libs/`.
