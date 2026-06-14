package de.lenox.client.config

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import java.awt.Color

object ConfigScreenProvider {
    private val SPACING_VALUES = listOf(1, 2, 4, 8, 16)

    fun createScreen(parent: Screen): Screen {
        val config = ModConfig.INSTANCE

        val builder = YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Better Chunk Borders Settings"))
            .save {
                ModConfig.save()
            }

        // 1. Presets Category (Main Tab)
        val presetsCategoryBuilder = ConfigCategory.createBuilder()
            .name(Component.literal("Presets"))
            .option(
                Option.createBuilder<Boolean>()
                    .name(Component.literal("Enable Mod"))
                    .description(OptionDescription.of(Component.literal("Toggles whether the custom chunk borders mod is active. If disabled, vanilla chunk borders will render instead.")))
                    .binding(
                        true,
                        { config.modEnabled },
                        { config.modEnabled = it }
                    )
                    .controller { opt ->
                        TickBoxControllerBuilder.create(opt)
                    }
                    .build()
            )

        for (preset in Preset.entries) {
            presetsCategoryBuilder.option(
                ButtonOption.createBuilder()
                    .name(Component.literal(preset.displayName))
                    .description(
                        OptionDescription.createBuilder()
                            .text(Component.literal(preset.description))
                            .webpImage(Identifier.fromNamespaceAndPath("betterchunkborders", "config/previews/${preset.id}.webp"))
                            .build()
                    )
                    .text(Component.literal("Apply"))
                    .action { _, _ ->
                        preset.applyTo(config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
        }
        val presetsCategory = presetsCategoryBuilder.build()

        // 2. Vertical Lines Category (Second Tab)
        val customCategory = ConfigCategory.createBuilder()
            .name(Component.literal("Vertical Lines"))

        // --- Declare Vertical Options ---
        val renderLinesOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Render Border Lines"))
            .description(OptionDescription.of(Component.literal("Toggles rendering the chunk border lines entirely.")))
            .binding(
                true,
                { config.renderLines },
                { config.renderLines = it }
            )
            .controller { opt -> TickBoxControllerBuilder.create(opt) }
            .build()

        val gridSpacingOption = Option.createBuilder<Int>()
            .name(Component.literal("Grid Spacing"))
            .description(OptionDescription.of(Component.literal("Controls the vertical line frequency inside the chunk. Vanilla default is 16.")))
            .binding(
                4,
                {
                    val idx = SPACING_VALUES.indexOf(config.gridSpacing)
                    if (idx == -1) 4 else idx
                },
                { idx ->
                    config.gridSpacing = SPACING_VALUES[idx.coerceIn(0, 4)]
                }
            )
            .controller { opt ->
                IntegerSliderControllerBuilder.create(opt)
                    .range(0, 4)
                    .step(1)
                    .formatValue { idx ->
                        Component.literal(SPACING_VALUES[idx].toString())
                    }
            }
            .build()

        val lineColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Border Line Color"))
            .description(OptionDescription.of(Component.literal("The solid color of the main chunk border lines (outer edges and corners).")))
            .binding(
                Color(255, 0, 0, 255),
                { config.lineColor },
                { config.lineColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        val useSeparateCornerColorOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Use Separate Corner Color"))
            .description(OptionDescription.of(Component.literal("When enabled, the 4 main corner lines can have a different color than the inner grid lines.")))
            .binding(
                false,
                { config.useSeparateCornerColor },
                { config.useSeparateCornerColor = it }
            )
            .controller { opt -> TickBoxControllerBuilder.create(opt) }
            .build()

        val cornerLineColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Corner Line Color"))
            .description(OptionDescription.of(Component.literal("The color of the 4 main corner lines when 'Use Separate Corner Color' is enabled.")))
            .binding(
                Color(255, 0, 255, 255),
                { config.cornerLineColor },
                { config.cornerLineColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        val lineWidthOption = Option.createBuilder<Float>()
            .name(Component.literal("Line Width / Thickness"))
            .description(OptionDescription.of(Component.literal("Controls the width/thickness of the rendered border lines.")))
            .binding(
                2.0f,
                { config.lineWidth },
                { config.lineWidth = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.5f, 10.0f)
                    .step(0.5f)
            }
            .build()

        val useGradientLinesOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Use Gradient Lines"))
            .description(OptionDescription.of(Component.literal("Renders vertical lines with a fade-out gradient above the player, and solid color below.")))
            .binding(
                true,
                { config.useGradientLines },
                { config.useGradientLines = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val limitLineHeightOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Limit Line Height"))
            .description(OptionDescription.of(Component.literal("When enabled, vertical lines do not extend to the world build limits and instead respect player/terrain height limits even if they are not using gradients.")))
            .binding(
                true,
                { config.limitLineHeight },
                { config.limitLineHeight = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        // --- Declare Gradient Wall Options ---
        val renderGradientsOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Render Gradient Overlay"))
            .description(OptionDescription.of(Component.literal("Toggles rendering the semi-transparent gradient walls on chunk boundaries.")))
            .binding(
                true,
                { config.renderGradients },
                { config.renderGradients = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val gradientTopColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Gradient Top Color"))
            .description(OptionDescription.of(Component.literal("Color at the top of the translucent overlay (usually transparent).")))
            .binding(
                Color(255, 0, 0, 0),
                { config.gradientTopColor },
                { config.gradientTopColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        val gradientBottomColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Gradient Bottom Color"))
            .description(OptionDescription.of(Component.literal("Color at the bottom of the translucent overlay.")))
            .binding(
                Color(255, 0, 0, 100),
                { config.gradientBottomColor },
                { config.gradientBottomColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        val gradientUndergroundColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Gradient Underground Color"))
            .description(OptionDescription.of(Component.literal("Color used deep underground to prevent the border from being too opaque in caves.")))
            .binding(
                Color(255, 0, 0, 30),
                { config.gradientUndergroundColor },
                { config.gradientUndergroundColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        val gradientMaxHeightOption = Option.createBuilder<Float>()
            .name(Component.literal("Gradient Wall Height"))
            .description(OptionDescription.of(Component.literal("Controls the total vertical height of the gradient walls centered around the player Y level. Only active when 'Adapt to Terrain' is disabled and 'Render Gradient Overlay' is enabled.")))
            .binding(
                64.0f,
                { config.gradientMaxHeight },
                { config.gradientMaxHeight = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(1.0f, 128.0f)
                    .step(0.5f)
            }
            .build()

        val mirrorGradientOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Mirror Gradient"))
            .description(OptionDescription.of(Component.literal("If enabled, the gradient is mirrored downwards below the player's Y level. Only active when 'Adapt to Terrain' is disabled and 'Render Gradient Overlay' is enabled.")))
            .binding(
                true,
                { config.mirrorGradient },
                { config.mirrorGradient = it }
            )
            .controller { opt -> TickBoxControllerBuilder.create(opt) }
            .build()

        // --- Declare Player-Relative Options ---
        val lineHeightAbovePlayerOption = Option.createBuilder<Float>()
            .name(Component.literal("Line Height Above Player"))
            .description(OptionDescription.of(Component.literal("How many blocks vertical lines extend and fade out above the player's feet. Only active when 'Adapt to Terrain' is disabled.")))
            .binding(
                64.0f,
                { config.lineHeightAbovePlayer },
                { config.lineHeightAbovePlayer = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.0f, 64.0f)
                    .step(0.2f)
            }
            .build()

        val lineHeightBelowPlayerOption = Option.createBuilder<Float>()
            .name(Component.literal("Line Height Below Player"))
            .description(OptionDescription.of(Component.literal("How many blocks vertical lines extend below the player's feet. Only active when 'Adapt to Terrain' is disabled.")))
            .binding(
                64.0f,
                { config.lineHeightBelowPlayer },
                { config.lineHeightBelowPlayer = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.0f, 64.0f)
                    .step(0.2f)
            }
            .build()

        val smoothPlayerHeightOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Smooth Player Height"))
            .description(OptionDescription.of(Component.literal("Smooths the line movement when following the player Y to avoid jittering. Only active when 'Adapt to Terrain' is disabled.")))
            .binding(
                true,
                { config.smoothPlayerHeight },
                { config.smoothPlayerHeight = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val playerHeightSmoothingSpeedOption = Option.createBuilder<Float>()
            .name(Component.literal("Player Height Smoothing Speed"))
            .description(OptionDescription.of(Component.literal("How fast vertical lines catch up to your height. Only active when 'Smooth Player Height' is enabled and 'Adapt to Terrain' is disabled.")))
            .binding(
                5.0f,
                { config.playerHeightSmoothingSpeed },
                { config.playerHeightSmoothingSpeed = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(1.0f, 20.0f)
                    .step(0.5f)
            }
            .build()

        // --- Declare Terrain-Adapting Options ---
        val useTerrainHeightmapOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Adapt to Terrain"))
            .description(OptionDescription.of(Component.literal("Conforms the vertical lines and gradient walls to the local terrain height instead of using player-relative or world-fixed heights.")))
            .binding(
                true,
                { config.useTerrainHeightmap },
                { config.useTerrainHeightmap = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val slopeTerrainGradientsOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Slope Terrain Gradients"))
            .description(OptionDescription.of(Component.literal("Slopes the boundary lines and walls to match terrain changes. Only active when 'Adapt to Terrain' is enabled.")))
            .binding(
                true,
                { config.slopeTerrainGradients },
                { config.slopeTerrainGradients = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val smoothTerrainHeightOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Smooth Height Jumps"))
            .description(OptionDescription.of(Component.literal("Applies a smoothing filter on terrain heights to round off steep block cliffs or deep holes. Only active when 'Adapt to Terrain' is enabled.")))
            .binding(
                true,
                { config.smoothTerrainHeight },
                { config.smoothTerrainHeight = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val terrainAdaptionStepsOption = Option.createBuilder<Int>()
            .name(Component.literal("Terrain Adaption Steps"))
            .description(OptionDescription.of(Component.literal("Subdivisions calculated along the chunk edge (1 = flat corner-to-corner, 16 = detailed block-by-block). Only active when 'Adapt to Terrain' is enabled.")))
            .binding(
                1,
                {
                    when (config.terrainAdaptionSteps) {
                        1 -> 0
                        2 -> 1
                        4 -> 2
                        8 -> 3
                        16 -> 4
                        else -> 1
                    }
                },
                { idx ->
                    config.terrainAdaptionSteps = when (idx) {
                        0 -> 1
                        1 -> 2
                        2 -> 4
                        3 -> 8
                        4 -> 16
                        else -> 2
                    }
                }
            )
            .controller { opt ->
                IntegerSliderControllerBuilder.create(opt)
                    .range(0, 4)
                    .step(1)
                    .formatValue { idx ->
                        val steps = when (idx) {
                            0 -> 1
                            1 -> 2
                            2 -> 4
                            3 -> 8
                            4 -> 16
                            else -> 2
                        }
                        Component.literal("$steps steps")
                    }
            }
            .build()

        val terrainGradientHeightOption = Option.createBuilder<Float>()
            .name(Component.literal("Terrain Gradient Height"))
            .description(OptionDescription.of(Component.literal("Height of the gradient overlay above the terrain. Only active when 'Adapt to Terrain' and 'Render Gradient Overlay' are enabled.")))
            .binding(
                24.0f,
                { config.terrainGradientHeight },
                { config.terrainGradientHeight = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.0f, 64.0f)
                    .step(0.5f)
            }
            .build()

        val terrainLineHeightOption = Option.createBuilder<Float>()
            .name(Component.literal("Terrain Line Height"))
            .description(OptionDescription.of(Component.literal("Height of the vertical lines above the terrain. Only active when 'Adapt to Terrain' and 'Render Border Lines' are enabled.")))
            .binding(
                24.0f,
                { config.terrainLineHeight },
                { config.terrainLineHeight = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.0f, 64.0f)
                    .step(0.5f)
            }
            .build()

        // --- Declare Surrounding Chunks Options ---
        val renderSurroundingBordersOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Render Surrounding Chunk Corners"))
            .description(OptionDescription.of(Component.literal("Show only the 4 vertical corner lines for the 8 chunks surrounding the current one.")))
            .binding(
                false,
                { config.renderSurroundingBorders },
                { config.renderSurroundingBorders = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val surroundingBordersColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Surrounding Corner Color"))
            .description(OptionDescription.of(Component.literal("The color of the corner lines of the 8 surrounding chunks.")))
            .binding(
                Color(0, 255, 0, 255),
                { config.surroundingBordersColor },
                { config.surroundingBordersColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        // --- Declare Horizontal Options ---
        val renderHorizontalLinesOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Render Horizontal Lines"))
            .description(OptionDescription.of(Component.literal("Toggles rendering the horizontal chunk boundary lines or rectangles.")))
            .binding(
                false,
                { config.renderHorizontalLines },
                { config.renderHorizontalLines = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val horizontalLineColorOption = Option.createBuilder<Color>()
            .name(Component.literal("Line Color"))
            .description(OptionDescription.of(Component.literal("The color of the horizontal chunk border lines or filled rectangles.")))
            .binding(
                Color(255, 0, 0, 255),
                { config.horizontalLineColor },
                { config.horizontalLineColor = it }
            )
            .controller { opt ->
                ColorControllerBuilder.create(opt)
                    .allowAlpha(true)
            }
            .build()

        val horizontalLineWidthOption = Option.createBuilder<Float>()
            .name(Component.literal("Line Width / Thickness"))
            .description(OptionDescription.of(Component.literal("Thickness of the horizontal lines. Only active when 'Rectangle Height' is 0.0.")))
            .binding(
                2.0f,
                { config.horizontalLineWidth },
                { config.horizontalLineWidth = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.5f, 10.0f)
                    .step(0.5f)
            }
            .build()

        val horizontalLineSpacingOption = Option.createBuilder<Float>()
            .name(Component.literal("Line Spacing"))
            .description(OptionDescription.of(Component.literal("Vertical distance between consecutive horizontal lines/rectangles.")))
            .binding(
                4.0f,
                { config.horizontalLineSpacing },
                { config.horizontalLineSpacing = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.1f, 32.0f)
                    .step(0.1f)
            }
            .build()

        val horizontalLineOffsetOption = Option.createBuilder<Float>()
            .name(Component.literal("Line Offset"))
            .description(OptionDescription.of(Component.literal("Vertical offset relative to the world bottom or the player's feet.")))
            .binding(
                0.0f,
                { config.horizontalLineOffset },
                { config.horizontalLineOffset = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(-8.0f, 8.0f)
                    .step(0.1f)
            }
            .build()

        val horizontalLinesFollowPlayerOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Follow Player's Height"))
            .description(OptionDescription.of(Component.literal("If enabled, spacing and offset are calculated relative to the player's current vertical position instead of the world bottom.")))
            .binding(
                false,
                { config.horizontalLinesFollowPlayer },
                { config.horizontalLinesFollowPlayer = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val smoothLineMovementOption = Option.createBuilder<Boolean>()
            .name(Component.literal("Smooth Line Movement"))
            .description(OptionDescription.of(Component.literal("When disabled, lines stay locked to the fixed world grid and activate/deactivate as you move, instead of moving smoothly with you. Only active when 'Follow Player's Height' is enabled.")))
            .binding(
                true,
                { config.smoothLineMovement },
                { config.smoothLineMovement = it }
            )
            .controller { opt ->
                TickBoxControllerBuilder.create(opt)
            }
            .build()

        val horizontalLinesAbovePlayerOption = Option.createBuilder<Int>()
            .name(Component.literal("Lines Above Player"))
            .description(OptionDescription.of(Component.literal("Number of horizontal lines to show at or above your Y position. Only active when 'Follow Player's Height' is enabled.")))
            .binding(
                1,
                { config.horizontalLinesAbovePlayer },
                { config.horizontalLinesAbovePlayer = it }
            )
            .controller { opt ->
                IntegerSliderControllerBuilder.create(opt)
                    .range(0, 20)
                    .step(1)
            }
            .build()

        val horizontalLinesBelowPlayerOption = Option.createBuilder<Int>()
            .name(Component.literal("Lines Below Player"))
            .description(OptionDescription.of(Component.literal("Number of horizontal lines to show below your Y position. Only active when 'Follow Player's Height' is enabled.")))
            .binding(
                0,
                { config.horizontalLinesBelowPlayer },
                { config.horizontalLinesBelowPlayer = it }
            )
            .controller { opt ->
                IntegerSliderControllerBuilder.create(opt)
                    .range(0, 20)
                    .step(1)
            }
            .build()

        val horizontalLineHeightOption = Option.createBuilder<Float>()
            .name(Component.literal("Rectangle Height (0 = Lines)"))
            .description(OptionDescription.of(Component.literal("The height of the horizontal rectangles spanning around the chunk. Setting to 0.0 uses normal line rendering instead.")))
            .binding(
                0.0f,
                { config.horizontalLineHeight },
                { config.horizontalLineHeight = it }
            )
            .controller { opt ->
                FloatSliderControllerBuilder.create(opt)
                    .range(0.0f, 3.0f)
                    .step(0.1f)
            }
            .build()

        // --- Build Groups ---
        val lineGroup = OptionGroup.createBuilder()
            .name(Component.literal("Line Settings"))
            .option(renderLinesOption)
            .option(gridSpacingOption)
            .option(lineColorOption)
            .option(useSeparateCornerColorOption)
            .option(cornerLineColorOption)
            .option(lineWidthOption)
            .option(useGradientLinesOption)
            .option(limitLineHeightOption)
            .build()

        val gradientGroup = OptionGroup.createBuilder()
            .name(Component.literal("Gradient Wall Settings"))
            .option(renderGradientsOption)
            .option(gradientTopColorOption)
            .option(gradientBottomColorOption)
            .option(gradientUndergroundColorOption)
            .option(gradientMaxHeightOption)
            .option(mirrorGradientOption)
            .build()

        val playerRelativeGroup = OptionGroup.createBuilder()
            .name(Component.literal("Player-Relative Line Settings"))
            .option(lineHeightAbovePlayerOption)
            .option(lineHeightBelowPlayerOption)
            .option(smoothPlayerHeightOption)
            .option(playerHeightSmoothingSpeedOption)
            .build()

        val terrainGroup = OptionGroup.createBuilder()
            .name(Component.literal("Terrain-Adapting Settings"))
            .option(useTerrainHeightmapOption)
            .option(slopeTerrainGradientsOption)
            .option(smoothTerrainHeightOption)
            .option(terrainAdaptionStepsOption)
            .option(terrainGradientHeightOption)
            .option(terrainLineHeightOption)
            .build()

        val surroundingGroup = OptionGroup.createBuilder()
            .name(Component.literal("Surrounding Chunks"))
            .option(renderSurroundingBordersOption)
            .option(surroundingBordersColorOption)
            .build()

        customCategory.group(lineGroup)
        customCategory.group(gradientGroup)
        customCategory.group(playerRelativeGroup)
        customCategory.group(terrainGroup)
        customCategory.group(surroundingGroup)

        // 3. Horizontal Lines Category
        val horizontalCategory = ConfigCategory.createBuilder()
            .name(Component.literal("Horizontal Lines"))

        val horizontalLineSettingsGroup = OptionGroup.createBuilder()
            .name(Component.literal("Horizontal Line Settings"))
            .option(renderHorizontalLinesOption)
            .option(horizontalLineColorOption)
            .option(horizontalLineWidthOption)
            .option(horizontalLineSpacingOption)
            .option(horizontalLineOffsetOption)
            .option(horizontalLinesFollowPlayerOption)
            .option(smoothLineMovementOption)
            .option(horizontalLinesAbovePlayerOption)
            .option(horizontalLinesBelowPlayerOption)
            .option(horizontalLineHeightOption)
            .build()

        horizontalCategory.group(horizontalLineSettingsGroup)

        // --- Reactive Availability Functions ---
        fun updateAvailability() {
            val linesActive = renderLinesOption.pendingValue()
            val gradientsActive = renderGradientsOption.pendingValue()
            val terrainActive = useTerrainHeightmapOption.pendingValue()
            val gradientLinesActive = useGradientLinesOption.pendingValue()
            val limitHeightActive = limitLineHeightOption.pendingValue()

            // 1. Line Settings
            gridSpacingOption.setAvailable(linesActive)
            lineColorOption.setAvailable(linesActive)
            useSeparateCornerColorOption.setAvailable(linesActive)
            cornerLineColorOption.setAvailable(linesActive && useSeparateCornerColorOption.pendingValue())
            lineWidthOption.setAvailable(linesActive)
            useGradientLinesOption.setAvailable(linesActive)
            limitLineHeightOption.setAvailable(linesActive)

            // 2. Gradient Wall Settings
            gradientTopColorOption.setAvailable(gradientsActive)
            gradientBottomColorOption.setAvailable(gradientsActive)
            gradientUndergroundColorOption.setAvailable(gradientsActive)
            gradientMaxHeightOption.setAvailable(gradientsActive && !terrainActive)
            mirrorGradientOption.setAvailable(gradientsActive && !terrainActive)

            // 3. Player-Relative Settings
            val playerRelativeActive = linesActive && !terrainActive && (gradientLinesActive || limitHeightActive)
            lineHeightAbovePlayerOption.setAvailable(playerRelativeActive)
            lineHeightBelowPlayerOption.setAvailable(playerRelativeActive)
            smoothPlayerHeightOption.setAvailable(playerRelativeActive)
            playerHeightSmoothingSpeedOption.setAvailable(playerRelativeActive && smoothPlayerHeightOption.pendingValue())

            // 4. Terrain-Adapting Settings
            val terrainSettingsActive = terrainActive && (linesActive || gradientsActive)
            slopeTerrainGradientsOption.setAvailable(terrainSettingsActive)
            smoothTerrainHeightOption.setAvailable(terrainSettingsActive)
            terrainAdaptionStepsOption.setAvailable(terrainSettingsActive)
            terrainGradientHeightOption.setAvailable(terrainSettingsActive && gradientsActive)
            terrainLineHeightOption.setAvailable(terrainSettingsActive && linesActive)

            // 5. Surrounding borders
            surroundingBordersColorOption.setAvailable(renderSurroundingBordersOption.pendingValue())

            // 6. Horizontal Lines
            val horizActive = renderHorizontalLinesOption.pendingValue()
            horizontalLineColorOption.setAvailable(horizActive)
            horizontalLineWidthOption.setAvailable(horizActive && horizontalLineHeightOption.pendingValue() == 0.0f)
            horizontalLineSpacingOption.setAvailable(horizActive)
            horizontalLineOffsetOption.setAvailable(horizActive)
            horizontalLinesFollowPlayerOption.setAvailable(horizActive)

            val horizFollowPlayer = horizActive && horizontalLinesFollowPlayerOption.pendingValue()
            smoothLineMovementOption.setAvailable(horizFollowPlayer)
            horizontalLinesAbovePlayerOption.setAvailable(horizFollowPlayer)
            horizontalLinesBelowPlayerOption.setAvailable(horizFollowPlayer)
            horizontalLineHeightOption.setAvailable(horizActive)
        }

        // Add listeners to reactively refresh availability on value change
        renderLinesOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        useGradientLinesOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        limitLineHeightOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        renderGradientsOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        useTerrainHeightmapOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        smoothPlayerHeightOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        renderSurroundingBordersOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        renderHorizontalLinesOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        horizontalLinesFollowPlayerOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        horizontalLineHeightOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        mirrorGradientOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }
        useSeparateCornerColorOption.addEventListener { _, event -> if (event == OptionEventListener.Event.STATE_CHANGE) updateAvailability() }

        // Set initial availability state
        updateAvailability()

        return builder
            .category(presetsCategory)
            .category(customCategory.build())
            .category(horizontalCategory.build())
            .build()
            .generateScreen(parent)
    }
}
