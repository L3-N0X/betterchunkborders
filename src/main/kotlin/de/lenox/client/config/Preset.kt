package de.lenox.client.config

import java.awt.Color

enum class Preset(
    val id: String,
    val displayName: String,
    val description: String,
    val apply: (ModConfig) -> Unit
) {
    VANILLA(
        "vanilla",
        "Vanilla",
        "Clean version of the vanilla borders.",
        { config ->
            config.gridSpacing = 2
            config.lineColor = Color(0xff, 0xff, 0x00, 0xff)
            config.useSeparateCornerColor = true
            config.cornerLineColor = Color(0xff, 0x00, 0xff, 0xff)
            config.lineWidth = 1.0f
            config.renderGradients = false
            config.gradientTopColor = Color(0xff, 0xee, 0x00, 0x00)
            config.gradientBottomColor = Color(0x9a, 0x9c, 0x1d, 0x75)
            config.gradientMaxHeight = 64.0f
            config.renderSurroundingBorders = false
            config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
            config.useGradientLines = false
            config.lineHeightAbovePlayer = 1.0f
            config.lineHeightBelowPlayer = 64.0f
            config.useTerrainHeightmap = false
            config.slopeTerrainGradients = true
            config.terrainGradientHeight = 1.0f
            config.terrainLineHeight = 12.0f
            config.renderLines = true
            config.terrainAdaptionSteps = 4
            config.gradientUndergroundColor = Color(0xff, 0xf8, 0x00, 0x0f)
            config.smoothTerrainHeight = false
            config.limitLineHeight = false
            config.smoothPlayerHeight = false
            config.playerHeightSmoothingSpeed = 5.0f
            config.renderHorizontalLines = true
            config.horizontalLineColor = Color(0xff, 0xff, 0x00, 0xff)
            config.horizontalLineWidth = 1.0f
            config.horizontalLineSpacing = 2.0f
            config.horizontalLineOffset = 0.0f
            config.horizontalLinesFollowPlayer = false
            config.horizontalLineHeight = 0.0f
            config.smoothLineMovement = false
        }
    ),
    VANILLA_FADED(
        "vanilla_faded",
        "Vanilla Faded",
        "Vanilla borders with 50% opacity and thinner lines.",
        { config ->
            config.gridSpacing = 2
            config.lineColor = Color(0xff, 0xff, 0x00, 0x80)
            config.useSeparateCornerColor = true
            config.cornerLineColor = Color(0xff, 0x00, 0xff, 0x80)
            config.lineWidth = 1.0f
            config.renderGradients = false
            config.gradientTopColor = Color(0xff, 0xee, 0x00, 0x00)
            config.gradientBottomColor = Color(0x9a, 0x9c, 0x1d, 0x75)
            config.gradientMaxHeight = 64.0f
            config.renderSurroundingBorders = false
            config.surroundingBordersColor = Color(0x3b, 0x8e, 0x73, 0x73)
            config.useGradientLines = false
            config.lineHeightAbovePlayer = 1.0f
            config.lineHeightBelowPlayer = 64.0f
            config.useTerrainHeightmap = false
            config.slopeTerrainGradients = true
            config.terrainGradientHeight = 1.0f
            config.terrainLineHeight = 12.0f
            config.renderLines = true
            config.terrainAdaptionSteps = 4
            config.gradientUndergroundColor = Color(0xff, 0xf8, 0x00, 0x0f)
            config.smoothTerrainHeight = false
            config.limitLineHeight = false
            config.smoothPlayerHeight = false
            config.playerHeightSmoothingSpeed = 5.0f
            config.renderHorizontalLines = true
            config.horizontalLineColor = Color(0xff, 0xff, 0x00, 0x80)
            config.horizontalLineWidth = 1.0f
            config.horizontalLineSpacing = 2.0f
            config.horizontalLineOffset = 0.0f
            config.horizontalLinesFollowPlayer = false
            config.horizontalLineHeight = 0.0f
            config.smoothLineMovement = false
        }
    ),
    RED_GRADIENT(
        "red_gradient",
        "Red Gradient",
        "Red gradient walls conforming to terrain and player height.",
        { config ->
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
    ),
    RED_SLOPED(
        "red_sloped",
        "Red Sloped",
        "Tall sloped red walls and lines following terrain.",
        { config ->
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
    ),
    RED_GRID(
        "red_grid",
        "Red Grid",
        "Red terrain-conforming grid with 4-block spacing.",
        { config ->
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
    ),
    GREEN_WALLS(
        "green_walls",
        "Green Walls",
        "Green gradient walls conforming to terrain without vertical lines.",
        { config ->
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
    ),
    WHITE_GRID(
        "white_grid",
        "White Grid",
        "White terrain-conforming grid with 4-block spacing.",
        { config ->
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
            config.gradientUndergroundColor = Color(0xff, 0xff, 0xff, 0x10)
            config.smoothTerrainHeight = false
            config.limitLineHeight = true
            config.smoothPlayerHeight = false
        }
    ),
    LIME_MINIMAL(
        "lime_minimal",
        "Lime Minimal",
        "Subtle lime-green borders following player height.",
        { config ->
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
    ),
    TEAL_LAYERS(
        "teal_layers",
        "Teal Layers",
        "Teal borders with player-relative horizontal layers.",
        { config ->
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
    ),
    GREEN_LAYERS(
        "green_layers",
        "Green Layers",
        "Green borders with fixed horizontal layers.",
        { config ->
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
    );

    fun applyTo(config: ModConfig) {
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

        // Reset corner settings to defaults
        config.useSeparateCornerColor = false
        config.cornerLineColor = Color(255, 0, 255, 255)

        // Apply preset specific settings
        apply(config)
    }
}
