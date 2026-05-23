# Better Chunk Borders

A premium, highly configurable Minecraft Fabric mod designed to enhance the debug chunk border visualizer (normally toggled via `F3` + `G`).

Built with **YetAnotherConfigLib (YACL)** and **Mod Menu**, this mod replaces the plain, cluttering default vanilla chunk boundaries with clean, customizable, and modern visuals.

## TODO

- Anti-aliasing
- per block gradient with higher resolution
- always start gradient at -64 but make it at camera transition
- better gradient config for height and so on

## Features

- 📏 **Custom Grid Spacing**: Control the frequency of internal vertical grid lines within the chunk (spacing increments of 1, 2, 4, 8, or 16).
- 🎨 **Solid Border Customization**: Fully customize the color and thickness of the chunk perimeter lines.
- 🌌 **Translucent Gradient Walls**: Render gorgeous, semi-transparent boundary walls that fade from a custom bottom color to a custom top color.
- 🧱 **Depth Testing (No X-Ray)**: Mod boundaries and gradients respect terrain depth, so they won't render through solid blocks.
- ⚙️ **In-Game Configuration**: Powered by YetAnotherConfigLib (YACL), easily adjust options in-game through the Mod Menu.

## Requirements

Ensure you have the following installed:

- Minecraft `1.21.11`
- Fabric Loader
- Fabric API
- Fabric Language Kotlin
- YetAnotherConfigLib (YACL) v3
- Mod Menu

## Building the Mod

To build the mod from source, clone the repository and run the Gradle build task:

```powershell
.\gradlew.bat build
```

The compiled mod `.jar` will be available in `build/libs/`.
