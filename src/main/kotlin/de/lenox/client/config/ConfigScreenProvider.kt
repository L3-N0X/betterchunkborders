package de.lenox.client.config

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.api.controller.ColorControllerBuilder
import dev.isxander.yacl3.api.controller.IntegerSliderControllerBuilder
import dev.isxander.yacl3.api.controller.FloatSliderControllerBuilder
import dev.isxander.yacl3.api.controller.TickBoxControllerBuilder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import java.awt.Color

object ConfigScreenProvider {
    private val SPACING_VALUES = listOf(1, 2, 4, 8, 16)

    private fun applyPreset(preset: String, config: ModConfig) {
        // Reset horizontal settings to defaults
        config.renderHorizontalLines = false
        config.horizontalLineColor = Color(255, 0, 0, 255)
        config.horizontalLineWidth = 2.0f
        config.horizontalLineSpacing = 4.0f
        config.horizontalLineOffset = 0.0f
        config.horizontalLinesFollowPlayer = false
        config.horizontalLinesAbovePlayer = 1
        config.horizontalLinesBelowPlayer = 0
        config.horizontalLineHeight = 0.0f
        config.smoothLineMovement = true
        config.mirrorGradient = true

        // Reset player height smoothing settings to defaults
        config.smoothPlayerHeight = true
        config.playerHeightSmoothingSpeed = 5.0f

        when (preset) {
            "vanilla_plus" -> {
                config.gridSpacing = 4
                config.lineColor = Color(0xff, 0xff, 0x00, 0xff)
                config.lineWidth = 2.0f
                config.renderGradients = false
                config.gradientTopColor = Color(0xff, 0xee, 0x00, 0x00)
                config.gradientBottomColor = Color(0x9a, 0x9c, 0x1d, 0x75)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
                config.useGradientLines = false
                config.lineHeightAbovePlayer = 1.0f
                config.lineHeightBelowPlayer = 64.0f
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 1.0f
                config.terrainLineHeight = 12.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 4
                config.gradientUndergroundColor = Color(0xff, 0xf8, 0x00, 0x0f)
                config.smoothTerrainHeight = false
                config.limitLineHeight = true
                config.smoothPlayerHeight = false
                config.playerHeightSmoothingSpeed = 5.0f
                config.renderHorizontalLines = true
                config.horizontalLineColor = Color(0xff, 0xff, 0x00, 0xff)
                config.horizontalLineWidth = 2.0f
                config.horizontalLineSpacing = 4.0f
                config.horizontalLineOffset = 0.0f
                config.horizontalLinesFollowPlayer = true
                config.horizontalLinesAbovePlayer = 1
                config.horizontalLinesBelowPlayer = 20
                config.horizontalLineHeight = 0.0f
                config.smoothLineMovement = false
            }
            "red_gradient_adaptive" -> {
                config.modEnabled = true
                config.gridSpacing = 16
                config.lineColor = Color(0xff, 0x00, 0x00, 0xff)
                config.lineWidth = 2.0f
                config.renderGradients = true
                config.gradientTopColor = Color(0xff, 0x00, 0x00, 0x00)
                config.gradientBottomColor = Color(0xff, 0x00, 0x00, 0x64)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0xff, 0x00, 0x00, 0xff)
                config.useGradientLines = true
                config.lineHeightAbovePlayer = 9.0f
                config.lineHeightBelowPlayer = 9.0f
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 3.0f
                config.terrainLineHeight = 3.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 8
                config.gradientUndergroundColor = Color(0xff, 0x00, 0x00, 0x16)
                config.smoothTerrainHeight = true
                config.limitLineHeight = true
                config.smoothPlayerHeight = true
                config.playerHeightSmoothingSpeed = 5.0f
                config.renderHorizontalLines = false
                config.horizontalLineColor = Color(0xff, 0x00, 0x00, 0xff)
                config.horizontalLineWidth = 2.0f
                config.horizontalLineSpacing = 4.0f
                config.horizontalLineOffset = 0.0f
                config.horizontalLinesFollowPlayer = false
                config.horizontalLinesAbovePlayer = 1
                config.horizontalLinesBelowPlayer = 0
                config.horizontalLineHeight = 0.0f
                config.smoothLineMovement = true
            }
            "terrain_adaptive_sloped" -> {
                config.gridSpacing = 16
                config.lineColor = Color(255, 0, 0, 255)
                config.lineWidth = 2.0f
                config.renderGradients = true
                config.gradientTopColor = Color(255, 0, 0, 0)
                config.gradientBottomColor = Color(255, 0, 0, 100)
                config.useGradientLines = true
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 24.0f
                config.terrainLineHeight = 24.0f
                config.renderSurroundingBorders = false
                config.renderLines = true
                config.terrainAdaptionSteps = 16
                config.gradientUndergroundColor = Color(255, 0, 0, 30)
                config.smoothTerrainHeight = true
                config.limitLineHeight = true
            }
            "red_grid_adaptive" -> {
                config.modEnabled = true
                config.gridSpacing = 4
                config.lineColor = Color(0xff, 0x00, 0x00, 0xff)
                config.lineWidth = 2.0f
                config.renderGradients = false
                config.gradientTopColor = Color(0xff, 0x00, 0x00, 0x00)
                config.gradientBottomColor = Color(0xff, 0x00, 0x00, 0x64)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0xff, 0x00, 0x00, 0xff)
                config.useGradientLines = true
                config.lineHeightAbovePlayer = 64.0f
                config.lineHeightBelowPlayer = 64.0f
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 4.0f
                config.terrainLineHeight = 4.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 16
                config.gradientUndergroundColor = Color(0xff, 0x00, 0x00, 0x1e)
                config.smoothTerrainHeight = true
                config.limitLineHeight = true
                config.smoothPlayerHeight = true
                config.playerHeightSmoothingSpeed = 5.0f
                config.renderHorizontalLines = false
                config.horizontalLineColor = Color(0xff, 0x00, 0x00, 0xff)
                config.horizontalLineWidth = 2.0f
                config.horizontalLineSpacing = 4.0f
                config.horizontalLineOffset = 0.0f
                config.horizontalLinesFollowPlayer = false
                config.horizontalLinesAbovePlayer = 1
                config.horizontalLinesBelowPlayer = 0
                config.horizontalLineHeight = 0.0f
                config.smoothLineMovement = true
            }
            "no_lines_green" -> {
                config.gridSpacing = 16
                config.lineColor = Color(0x22, 0x89, 0x42, 0xff)
                config.lineWidth = 1.5f
                config.renderGradients = true
                config.gradientTopColor = Color(0x00, 0xff, 0x8d, 0x00)
                config.gradientBottomColor = Color(0x1d, 0x9c, 0x2b, 0x75)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = true
                config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
                config.useGradientLines = true
                config.lineHeightAbovePlayer = 4.0f
                config.lineHeightBelowPlayer = 64.0f
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 1.5f
                config.terrainLineHeight = 3.0f
                config.renderLines = false
                config.terrainAdaptionSteps = 16
                config.gradientUndergroundColor = Color(0x00, 0xff, 0xc1, 0x0f)
                config.smoothTerrainHeight = true
                config.limitLineHeight = true
            }
            "clean_white_grid" -> {
                config.gridSpacing = 4
                config.lineColor = Color(0xff, 0xff, 0xff, 0xff)
                config.lineWidth = 1.0f
                config.renderGradients = true
                config.gradientTopColor = Color(0xff, 0xff, 0xff, 0x00)
                config.gradientBottomColor = Color(0xff, 0xff, 0xff, 0x34)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
                config.useGradientLines = false
                config.lineHeightAbovePlayer = 2.0f
                config.lineHeightBelowPlayer = 32.0f
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 1.0f
                config.terrainLineHeight = 1.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 4
                config.gradientUndergroundColor = Color(0xff, 0xff, 0xff, 0x05)
                config.smoothTerrainHeight = false
                config.limitLineHeight = true
                config.smoothPlayerHeight = false
            }
            "minimalist" -> {
                config.modEnabled = true
                config.gridSpacing = 16
                config.lineColor = Color(0xb3, 0xd2, 0x69, 0xc8)
                config.lineWidth = 2.0f
                config.renderGradients = false
                config.gradientTopColor = Color(0x80, 0xff, 0x00, 0x00)
                config.gradientBottomColor = Color(0x8a, 0xff, 0x00, 0x64)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0xff, 0xcf, 0x00, 0xff)
                config.useGradientLines = true
                config.lineHeightAbovePlayer = 12.0f
                config.lineHeightBelowPlayer = 12.0f
                config.useTerrainHeightmap = false
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 4.0f
                config.terrainLineHeight = 4.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 16
                config.gradientUndergroundColor = Color(0xa9, 0xff, 0x00, 0x1e)
                config.smoothTerrainHeight = false
                config.limitLineHeight = true
                config.smoothPlayerHeight = true
                config.playerHeightSmoothingSpeed = 5.0f
                config.renderHorizontalLines = false
                config.horizontalLineColor = Color(0xff, 0x00, 0x00, 0xff)
                config.horizontalLineWidth = 2.0f
                config.horizontalLineSpacing = 4.0f
                config.horizontalLineOffset = 0.0f
                config.horizontalLinesFollowPlayer = false
                config.horizontalLinesAbovePlayer = 1
                config.horizontalLinesBelowPlayer = 0
                config.horizontalLineHeight = 0.0f
                config.smoothLineMovement = true
            }
            "teal_layers" -> {
                config.modEnabled = true
                config.gridSpacing = 8
                config.lineColor = Color(0x2f, 0x7f, 0x86, 0xff)
                config.lineWidth = 2.0f
                config.renderGradients = false
                config.gradientTopColor = Color(0x00, 0xff, 0x8d, 0x00)
                config.gradientBottomColor = Color(0x1d, 0x9c, 0x2b, 0x75)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
                config.useGradientLines = false
                config.lineHeightAbovePlayer = 0.4f
                config.lineHeightBelowPlayer = 64.0f
                config.useTerrainHeightmap = false
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 1.5f
                config.terrainLineHeight = 3.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 16
                config.gradientUndergroundColor = Color(0x00, 0xff, 0xc1, 0x0f)
                config.smoothTerrainHeight = false
                config.limitLineHeight = true
                config.smoothPlayerHeight = true
                config.playerHeightSmoothingSpeed = 12.0f
                config.renderHorizontalLines = true
                config.horizontalLineColor = Color(0x60, 0xa7, 0xb1, 0xff)
                config.horizontalLineWidth = 2.0f
                config.horizontalLineSpacing = 2.0f
                config.horizontalLineOffset = 0.20000012f
                config.horizontalLinesFollowPlayer = true
                config.horizontalLinesAbovePlayer = 1
                config.horizontalLinesBelowPlayer = 1
                config.horizontalLineHeight = 0.0f
            }
            "green_layers" -> {
                config.modEnabled = true
                config.gridSpacing = 16
                config.lineColor = Color(0x30, 0x86, 0x2f, 0xff)
                config.lineWidth = 2.0f
                config.renderGradients = false
                config.gradientTopColor = Color(0x00, 0xff, 0x8d, 0x00)
                config.gradientBottomColor = Color(0x1d, 0x9c, 0x2b, 0x75)
                config.gradientMaxHeight = 64.0f
                config.renderSurroundingBorders = false
                config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
                config.useGradientLines = false
                config.lineHeightAbovePlayer = 1.0f
                config.lineHeightBelowPlayer = 64.0f
                config.useTerrainHeightmap = true
                config.slopeTerrainGradients = true
                config.terrainGradientHeight = 1.0f
                config.terrainLineHeight = 5.0f
                config.renderLines = true
                config.terrainAdaptionSteps = 4
                config.gradientUndergroundColor = Color(0x00, 0xff, 0xc1, 0x0f)
                config.smoothTerrainHeight = false
                config.limitLineHeight = true
                config.smoothPlayerHeight = false
                config.playerHeightSmoothingSpeed = 1.0f
                config.renderHorizontalLines = true
                config.horizontalLineColor = Color(0x75, 0xb1, 0x60, 0xff)
                config.horizontalLineWidth = 2.0f
                config.horizontalLineSpacing = 1.0f
                config.horizontalLineOffset = 0.30000013f
                config.horizontalLinesFollowPlayer = true
                config.horizontalLinesAbovePlayer = 1
                config.horizontalLinesBelowPlayer = 1
                config.horizontalLineHeight = 0.0f
                config.smoothLineMovement = false
            }
        }
    }

    fun createScreen(parent: Screen): Screen {
        val config = ModConfig.INSTANCE

        val builder = YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Better Chunk Borders Settings"))
            .save {
                ModConfig.save()
            }

        // 1. Presets Category (Main Tab)
        val presetsCategory = ConfigCategory.createBuilder()
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
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Vanilla Plus"))
                    .description(OptionDescription.of(Component.literal("A premium preset featuring yellow chunk borders and terrain-adaptive lines, paired with yellow grid-locked horizontal layers (smooth movement off).")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("vanilla_plus", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Red Gradient (Adaptive)"))
                    .description(OptionDescription.of(Component.literal("A premium preset featuring red terrain-adaptive gradient walls and vertical lines following the player Y, without horizontal lines.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("red_gradient_adaptive", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Terrain Adaptive (Sloped)"))
                    .description(OptionDescription.of(Component.literal("High-quality sloped walls and lines that follow the terrain surface contour.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("terrain_adaptive_sloped", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Red Grid (Adaptive)"))
                    .description(OptionDescription.of(Component.literal("A premium preset featuring red terrain-adaptive sloped lines with grid spacing 4, without gradient walls or horizontal lines.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("red_grid_adaptive", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("No Lines, Green"))
                    .description(OptionDescription.of(Component.literal("A premium preset featuring green fading overlays conforming to every block with zero vertical lines and surrounding corner borders.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("no_lines_green", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Clean White Grid"))
                    .description(OptionDescription.of(Component.literal("A neat white grid overlay with 4-block spacing and subtle white boundaries conforming to the terrain contours.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("clean_white_grid", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Lime Minimalist"))
                    .description(OptionDescription.of(Component.literal("A clean and subtle preset with semi-transparent lime-green borders and vertical lines following the player Y within a 12-block range, without gradient walls or horizontal lines.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("minimalist", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Teal Layers"))
                    .description(OptionDescription.of(Component.literal("A premium preset featuring teal borders and vertical lines paired with horizontal layers that follow the player Y.")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("teal_layers", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .option(
                ButtonOption.createBuilder()
                    .name(Component.literal("Green Layers"))
                    .description(OptionDescription.of(Component.literal("A premium preset with a green grid and player-relative horizontal layers locked to a fixed grid (smooth movement off).")))
                    .text(Component.literal("Apply"))
                    .action { button ->
                        applyPreset("green_layers", config)
                        ModConfig.save()
                        Minecraft.getInstance().execute {
                            Minecraft.getInstance().setScreen(createScreen(parent))
                        }
                    }
                    .build()
            )
            .build()

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
        renderLinesOption.addListener { _, _ -> updateAvailability() }
        useGradientLinesOption.addListener { _, _ -> updateAvailability() }
        limitLineHeightOption.addListener { _, _ -> updateAvailability() }
        renderGradientsOption.addListener { _, _ -> updateAvailability() }
        useTerrainHeightmapOption.addListener { _, _ -> updateAvailability() }
        smoothPlayerHeightOption.addListener { _, _ -> updateAvailability() }
        renderSurroundingBordersOption.addListener { _, _ -> updateAvailability() }
        renderHorizontalLinesOption.addListener { _, _ -> updateAvailability() }
        horizontalLinesFollowPlayerOption.addListener { _, _ -> updateAvailability() }
        horizontalLineHeightOption.addListener { _, _ -> updateAvailability() }
        mirrorGradientOption.addListener { _, _ -> updateAvailability() }

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
