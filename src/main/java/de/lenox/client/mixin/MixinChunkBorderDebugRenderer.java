package de.lenox.client.mixin;

import de.lenox.client.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.ChunkBorderRenderer;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.Vec3;
import net.minecraft.util.debug.DebugValueAccess;
import net.minecraft.client.renderer.culling.Frustum;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.world.level.levelgen.Heightmap;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

@Mixin(ChunkBorderRenderer.class)
public class MixinChunkBorderDebugRenderer {
    private static double smoothedPlayerY = 0.0;
    private static long lastTime = 0;

    @Inject(method = "emitGizmos", at = @At("HEAD"), cancellable = true)
    private void onEmitGizmos(double cameraX, double cameraY, double cameraZ, DebugValueAccess val, Frustum frustum, float delta, CallbackInfo ci) {
        ModConfig config = ModConfig.getINSTANCE();
        if (!config.getModEnabled()) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }

        var entity = mc.gameRenderer.getMainCamera().entity();
        if (entity == null) {
            return;
        }

        SectionPos sectionPos = SectionPos.of(entity.blockPosition());
        double minX = sectionPos.minBlockX();
        double minZ = sectionPos.minBlockZ();
        double maxX = minX + 16.0;
        double maxZ = minZ + 16.0;

        double minY = mc.level.getMinY();
        double maxY = mc.level.getMaxY() + 1.0;

        double targetPlayerY = entity.getY();
        double playerY = targetPlayerY;

        if (config.getSmoothPlayerHeight()) {
            long now = System.currentTimeMillis();
            if (lastTime == 0 || Math.abs(targetPlayerY - smoothedPlayerY) > 10.0) {
                smoothedPlayerY = targetPlayerY;
            } else {
                double dt = (now - lastTime) / 1000.0;
                if (dt > 0.1) dt = 0.1;
                if (dt > 0) {
                    smoothedPlayerY += (targetPlayerY - smoothedPlayerY) * (1.0 - Math.exp(-config.getPlayerHeightSmoothingSpeed() * dt));
                }
            }
            lastTime = now;
            playerY = smoothedPlayerY;
        } else {
            lastTime = 0;
        }

        // 1. Draw Gradients if enabled
        if (config.getRenderGradients()) {
            if (config.getUseTerrainHeightmap()) {
                int steps = config.getTerrainAdaptionSteps();

                // --- Wall 1: West (X = minX, Z from minZ to maxZ) ---
                for (int i = 0; i < steps; i++) {
                    double z1 = minZ + i * (16.0 / steps);
                    double z2 = minZ + (i + 1) * (16.0 / steps);
                    double ySurf1 = getTerrainHeight(minX, z1, config, mc, minX, minZ);
                    double ySurf2 = getTerrainHeight(minX, z2, config, mc, minX, minZ);
                    drawWallSegment(minX, z1, ySurf1, minX, z2, ySurf2, minY, config);
                }

                // --- Wall 2: East (X = maxX, Z from minZ to maxZ) ---
                for (int i = 0; i < steps; i++) {
                    double z1 = minZ + i * (16.0 / steps);
                    double z2 = minZ + (i + 1) * (16.0 / steps);
                    double ySurf1 = getTerrainHeight(maxX, z1, config, mc, minX, minZ);
                    double ySurf2 = getTerrainHeight(maxX, z2, config, mc, minX, minZ);
                    drawWallSegment(maxX, z1, ySurf1, maxX, z2, ySurf2, minY, config);
                }

                // --- Wall 3: North (Z = minZ, X from minX to maxX) ---
                for (int i = 0; i < steps; i++) {
                    double x1 = minX + i * (16.0 / steps);
                    double x2 = minX + (i + 1) * (16.0 / steps);
                    double ySurf1 = getTerrainHeight(x1, minZ, config, mc, minX, minZ);
                    double ySurf2 = getTerrainHeight(x2, minZ, config, mc, minX, minZ);
                    drawWallSegment(x1, minZ, ySurf1, x2, minZ, ySurf2, minY, config);
                }

                // --- Wall 4: South (Z = maxZ, X from minX to maxX) ---
                for (int i = 0; i < steps; i++) {
                    double x1 = minX + i * (16.0 / steps);
                    double x2 = minX + (i + 1) * (16.0 / steps);
                    double ySurf1 = getTerrainHeight(x1, maxZ, config, mc, minX, minZ);
                    double ySurf2 = getTerrainHeight(x2, maxZ, config, mc, minX, minZ);
                    drawWallSegment(x1, maxZ, ySurf1, x2, maxZ, ySurf2, minY, config);
                }
            } else {
                double halfHeight = config.getGradientMaxHeight() / 2.0;
                Color topC = config.getGradientTopColor();
                Color botC = config.getGradientBottomColor();

                if (config.getMirrorGradient()) {
                    // Upper half: cameraY to cameraY + halfHeight (interpolates botC -> topC)
                    double upperMinY = Math.max(minY, Math.min(maxY, cameraY));
                    double upperMaxY = Math.max(minY, Math.min(maxY, cameraY + halfHeight));
                    double upperHeight = upperMaxY - upperMinY;
                    if (upperHeight > 0) {
                        int upperSteps = (int) Math.max(4, Math.min(32, upperHeight / 2.0));
                        for (int step = 0; step < upperSteps; step++) {
                            double yStart = upperMinY + step * (upperHeight / upperSteps);
                            double yEnd = upperMinY + (step + 1) * (upperHeight / upperSteps);
                            double yMid = (yStart + yEnd) / 2.0;
                            float t = halfHeight > 0 ? (float) ((yMid - cameraY) / halfHeight) : 0.0f;
                            t = Math.max(0.0f, Math.min(1.0f, t));
                            int packedColor = getInterpolatedPackedColor(botC, topC, t);
                            drawGradientWallsAroundChunk(minX, minZ, maxX, maxZ, yStart, yEnd, packedColor);
                        }
                    }

                    // Lower half: cameraY - halfHeight to cameraY (interpolates topC <- botC as Y increases)
                    // Fades from botC (at cameraY) to topC (at cameraY - halfHeight)
                    double lowerMinY = Math.max(minY, Math.min(maxY, cameraY - halfHeight));
                    double lowerMaxY = Math.max(minY, Math.min(maxY, cameraY));
                    double lowerHeight = lowerMaxY - lowerMinY;
                    if (lowerHeight > 0) {
                        int lowerSteps = (int) Math.max(4, Math.min(32, lowerHeight / 2.0));
                        for (int step = 0; step < lowerSteps; step++) {
                            double yStart = lowerMinY + step * (lowerHeight / lowerSteps);
                            double yEnd = lowerMinY + (step + 1) * (lowerHeight / lowerSteps);
                            double yMid = (yStart + yEnd) / 2.0;
                            float t = halfHeight > 0 ? (float) ((cameraY - yMid) / halfHeight) : 0.0f;
                            t = Math.max(0.0f, Math.min(1.0f, t));
                            int packedColor = getInterpolatedPackedColor(botC, topC, t);
                            drawGradientWallsAroundChunk(minX, minZ, maxX, maxZ, yStart, yEnd, packedColor);
                        }
                    }
                } else {
                    double gradMinY = cameraY - halfHeight;
                    double gradMaxY = cameraY + halfHeight;
                    if (gradMinY < minY) {
                        gradMinY = minY;
                    }
                    if (gradMaxY > maxY) {
                        gradMaxY = maxY;
                    }

                    double height = gradMaxY - gradMinY;
                    if (height > 0) {
                        int steps = (int) Math.max(8, Math.min(64, height / 2)); // Dynamic step size (~2 blocks per step)
                        for (int step = 0; step < steps; step++) {
                            double yStart = gradMinY + step * (height / steps);
                            double yEnd = gradMinY + (step + 1) * (height / steps);
                            float t = (float) (step + 0.5f) / steps;
                            int packedColor = getInterpolatedPackedColor(botC, topC, t);
                            drawGradientWallsAroundChunk(minX, minZ, maxX, maxZ, yStart, yEnd, packedColor);
                        }
                    }
                }
            }
        }

        // 2. Draw Lines
        if (config.getRenderLines()) {
            Color lineColor = config.getLineColor();
            int r = lineColor.getRed();
            int g = lineColor.getGreen();
            int b = lineColor.getBlue();
            int a = lineColor.getAlpha();
            int packedColor = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
            float thickness = config.getLineWidth();

            if (config.getUseGradientLines() || config.getLimitLineHeight()) {
                if (config.getUseTerrainHeightmap()) {
                    // Draw outer borders (4 corners)
                    drawTerrainGradientLine(minX, minZ, minY, lineColor, thickness, config, mc, minX, minZ);
                    drawTerrainGradientLine(minX, maxZ, minY, lineColor, thickness, config, mc, minX, minZ);
                    drawTerrainGradientLine(maxX, minZ, minY, lineColor, thickness, config, mc, minX, minZ);
                    drawTerrainGradientLine(maxX, maxZ, minY, lineColor, thickness, config, mc, minX, minZ);

                    // Draw inner grid lines based on Grid Spacing config
                    int spacing = config.getGridSpacing();
                    if (spacing > 0 && spacing < 16) {
                        for (int xOffset = spacing; xOffset < 16; xOffset += spacing) {
                            double xCoord = minX + xOffset;
                            drawTerrainGradientLine(xCoord, minZ, minY, lineColor, thickness, config, mc, minX, minZ);
                            drawTerrainGradientLine(xCoord, maxZ, minY, lineColor, thickness, config, mc, minX, minZ);
                        }
                        for (int zOffset = spacing; zOffset < 16; zOffset += spacing) {
                            double zCoord = minZ + zOffset;
                            drawTerrainGradientLine(minX, zCoord, minY, lineColor, thickness, config, mc, minX, minZ);
                            drawTerrainGradientLine(maxX, zCoord, minY, lineColor, thickness, config, mc, minX, minZ);
                        }
                    }

                    // Draw surrounding chunk corner lines if enabled
                    if (config.getRenderSurroundingBorders()) {
                        Color surrColor = config.getSurroundingBordersColor();
                        double[] xCoords = { minX - 16.0, minX, maxX, maxX + 16.0 };
                        double[] zCoords = { minZ - 16.0, minZ, maxZ, maxZ + 16.0 };

                        for (double x : xCoords) {
                            for (double z : zCoords) {
                                boolean isCurrentChunkCorner = (Math.abs(x - minX) < 0.01 || Math.abs(x - maxX) < 0.01)
                                        && (Math.abs(z - minZ) < 0.01 || Math.abs(z - maxZ) < 0.01);
                                
                                if (!isCurrentChunkCorner) {
                                    double chunkMinX = getChunkMinX(x, minX);
                                    double chunkMinZ = getChunkMinZ(z, minZ);
                                    drawTerrainGradientLine(x, z, minY, surrColor, thickness, config, mc, chunkMinX, chunkMinZ);
                                }
                            }
                        }
                    }
                } else {


                    // Draw outer borders (4 corners)
                    drawPlayerRelativeGradientLine(minX, minZ, playerY, minY, maxY, lineColor, thickness, config);
                    drawPlayerRelativeGradientLine(minX, maxZ, playerY, minY, maxY, lineColor, thickness, config);
                    drawPlayerRelativeGradientLine(maxX, minZ, playerY, minY, maxY, lineColor, thickness, config);
                    drawPlayerRelativeGradientLine(maxX, maxZ, playerY, minY, maxY, lineColor, thickness, config);

                    // Draw inner grid lines based on Grid Spacing config
                    int spacing = config.getGridSpacing();
                    if (spacing > 0 && spacing < 16) {
                        for (int xOffset = spacing; xOffset < 16; xOffset += spacing) {
                            double xCoord = minX + xOffset;
                            drawPlayerRelativeGradientLine(xCoord, minZ, playerY, minY, maxY, lineColor, thickness, config);
                            drawPlayerRelativeGradientLine(xCoord, maxZ, playerY, minY, maxY, lineColor, thickness, config);
                        }
                        for (int zOffset = spacing; zOffset < 16; zOffset += spacing) {
                            double zCoord = minZ + zOffset;
                            drawPlayerRelativeGradientLine(minX, zCoord, playerY, minY, maxY, lineColor, thickness, config);
                            drawPlayerRelativeGradientLine(maxX, zCoord, playerY, minY, maxY, lineColor, thickness, config);
                        }
                    }

                    // Draw surrounding chunk corner lines if enabled
                    if (config.getRenderSurroundingBorders()) {
                        Color surrColor = config.getSurroundingBordersColor();
                        double[] xCoords = { minX - 16.0, minX, maxX, maxX + 16.0 };
                        double[] zCoords = { minZ - 16.0, minZ, maxZ, maxZ + 16.0 };

                        for (double x : xCoords) {
                            for (double z : zCoords) {
                                boolean isCurrentChunkCorner = (Math.abs(x - minX) < 0.01 || Math.abs(x - maxX) < 0.01)
                                        && (Math.abs(z - minZ) < 0.01 || Math.abs(z - maxZ) < 0.01);
                                
                                if (!isCurrentChunkCorner) {
                                    drawPlayerRelativeGradientLine(x, z, playerY, minY, maxY, surrColor, thickness, config);
                                }
                            }
                        }
                    }
                }
            } else {
                // Draw outer borders (4 corners from bottom to top)
                Gizmos.line(new Vec3(minX, minY, minZ), new Vec3(minX, maxY, minZ), packedColor, thickness);
                Gizmos.line(new Vec3(minX, minY, maxZ), new Vec3(minX, maxY, maxZ), packedColor, thickness);
                Gizmos.line(new Vec3(maxX, minY, minZ), new Vec3(maxX, maxY, minZ), packedColor, thickness);
                Gizmos.line(new Vec3(maxX, minY, maxZ), new Vec3(maxX, maxY, maxZ), packedColor, thickness);

                // Draw horizontal frames (top and bottom boxes)
                // Bottom frame
                Gizmos.line(new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ), packedColor, thickness);
                Gizmos.line(new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ), packedColor, thickness);
                Gizmos.line(new Vec3(maxX, minY, maxZ), new Vec3(minX, minY, maxZ), packedColor, thickness);
                Gizmos.line(new Vec3(minX, minY, maxZ), new Vec3(minX, minY, minZ), packedColor, thickness);
                // Top frame
                Gizmos.line(new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ), packedColor, thickness);
                Gizmos.line(new Vec3(maxX, maxY, minZ), new Vec3(maxX, maxY, maxZ), packedColor, thickness);
                Gizmos.line(new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ), packedColor, thickness);
                Gizmos.line(new Vec3(minX, maxY, maxZ), new Vec3(minX, maxY, minZ), packedColor, thickness);

                // Draw inner grid lines based on Grid Spacing config
                int spacing = config.getGridSpacing();
                if (spacing > 0 && spacing < 16) {
                    // Vertical lines along X boundaries (varying X, Z is constant minZ or maxZ)
                    for (int xOffset = spacing; xOffset < 16; xOffset += spacing) {
                        double xCoord = minX + xOffset;
                        Gizmos.line(new Vec3(xCoord, minY, minZ), new Vec3(xCoord, maxY, minZ), packedColor, thickness);
                        Gizmos.line(new Vec3(xCoord, minY, maxZ), new Vec3(xCoord, maxY, maxZ), packedColor, thickness);
                    }
                    // Vertical lines along Z boundaries (varying Z, X is constant minX or maxX)
                    for (int zOffset = spacing; zOffset < 16; zOffset += spacing) {
                        double zCoord = minZ + zOffset;
                        Gizmos.line(new Vec3(minX, minY, zCoord), new Vec3(minX, maxY, zCoord), packedColor, thickness);
                        Gizmos.line(new Vec3(maxX, minY, zCoord), new Vec3(maxX, maxY, zCoord), packedColor, thickness);
                    }
                }

                // 3. Draw surrounding chunk corner lines if enabled
                if (config.getRenderSurroundingBorders()) {
                    Color surrColor = config.getSurroundingBordersColor();
                    int sR = surrColor.getRed();
                    int sG = surrColor.getGreen();
                    int sB = surrColor.getBlue();
                    int sA = surrColor.getAlpha();
                    int packedSurrColor = ((sA & 0xFF) << 24) | ((sR & 0xFF) << 16) | ((sG & 0xFF) << 8) | (sB & 0xFF);

                    double[] xCoords = { minX - 16.0, minX, maxX, maxX + 16.0 };
                    double[] zCoords = { minZ - 16.0, minZ, maxZ, maxZ + 16.0 };

                    for (double x : xCoords) {
                        for (double z : zCoords) {
                            boolean isCurrentChunkCorner = (Math.abs(x - minX) < 0.01 || Math.abs(x - maxX) < 0.01)
                                    && (Math.abs(z - minZ) < 0.01 || Math.abs(z - maxZ) < 0.01);
                            
                            if (!isCurrentChunkCorner) {
                                Gizmos.line(new Vec3(x, minY, z), new Vec3(x, maxY, z), packedSurrColor, thickness);
                            }
                        }
                    }
                }
            }
        }

        // 3. Draw Horizontal Lines / Rectangles
        if (config.getRenderHorizontalLines()) {
            Color horizColor = config.getHorizontalLineColor();
            int packedColor = getPackedColor(horizColor);
            float thickness = config.getHorizontalLineWidth();
            float height = config.getHorizontalLineHeight();

            List<Double> yLevels = new ArrayList<>();

            if (config.getHorizontalLinesFollowPlayer()) {
                double spacing = config.getHorizontalLineSpacing();
                double offset = config.getHorizontalLineOffset();
                int aboveCount = config.getHorizontalLinesAbovePlayer();
                int belowCount = config.getHorizontalLinesBelowPlayer();

                if (spacing > 0.0f) {
                    if (config.getSmoothLineMovement()) {
                        double base = playerY + offset;
                        // Add lines above (including base if aboveCount >= 1)
                        for (int i = 0; i < aboveCount; i++) {
                            double y = base + i * spacing;
                            if (y >= minY && y <= maxY) {
                                yLevels.add(y);
                            }
                        }

                        // Add lines below (not including base)
                        for (int j = 1; j <= belowCount; j++) {
                            double y = base - j * spacing;
                            if (y >= minY && y <= maxY) {
                                yLevels.add(y);
                            }
                        }
                    } else {
                        // Locked to the fixed world grid: (minY + offset) + k * spacing
                        double kBaseDouble = Math.ceil((playerY - minY - offset) / spacing);
                        long kBase = (long) kBaseDouble;

                        // Add lines above (including base index)
                        for (int i = 0; i < aboveCount; i++) {
                            double y = (minY + offset) + (kBase + i) * spacing;
                            if (y >= minY && y <= maxY) {
                                yLevels.add(y);
                            }
                        }

                        // Add lines below (not including base index)
                        for (int j = 1; j <= belowCount; j++) {
                            double y = (minY + offset) + (kBase - j) * spacing;
                            if (y >= minY && y <= maxY) {
                                yLevels.add(y);
                            }
                        }
                    }
                }
            } else {
                double base = minY + config.getHorizontalLineOffset();
                double spacing = config.getHorizontalLineSpacing();
                if (spacing > 0.0f) {
                    double diff = minY - base;
                    double k = Math.ceil(diff / spacing);
                    double startY = base + k * spacing;
                    for (double y = startY; y <= maxY; y += spacing) {
                        yLevels.add(y);
                    }
                }
            }

            if (height > 0.0f) {
                GizmoStyle style = GizmoStyle.fill(packedColor);
                for (double y : yLevels) {
                    double yStart = y - height / 2.0;
                    double yEnd = y + height / 2.0;
                    double yStartClamped = Math.max(minY, yStart);
                    double yEndClamped = Math.min(maxY, yEnd);
                    if (yStartClamped < yEndClamped) {
                        drawQuad(minX, minZ, yStartClamped, yEndClamped, minX, maxZ, yStartClamped, yEndClamped, minY, style);
                        drawQuad(maxX, minZ, yStartClamped, yEndClamped, maxX, maxZ, yStartClamped, yEndClamped, minY, style);
                        drawQuad(minX, minZ, yStartClamped, yEndClamped, maxX, minZ, yStartClamped, yEndClamped, minY, style);
                        drawQuad(minX, maxZ, yStartClamped, yEndClamped, maxX, maxZ, yStartClamped, yEndClamped, minY, style);
                    }
                }
            } else {
                for (double y : yLevels) {
                    Gizmos.line(new Vec3(minX, y, minZ), new Vec3(maxX, y, minZ), packedColor, thickness);
                    Gizmos.line(new Vec3(maxX, y, minZ), new Vec3(maxX, y, maxZ), packedColor, thickness);
                    Gizmos.line(new Vec3(maxX, y, maxZ), new Vec3(minX, y, maxZ), packedColor, thickness);
                    Gizmos.line(new Vec3(minX, y, maxZ), new Vec3(minX, y, minZ), packedColor, thickness);
                }
            }
        }

        ci.cancel();
    }

    private void drawTerrainGradientLine(double x, double z, double minY, Color lineColor, float thickness, ModConfig config, Minecraft mc, double chunkMinX, double chunkMinZ) {
        int surfaceY;
        if (config.getSlopeTerrainGradients()) {
            int blockX = (int) Math.floor(x);
            int blockZ = (int) Math.floor(z);
            int clampedX = Math.max((int) chunkMinX, Math.min((int) chunkMinX + 15, blockX));
            int clampedZ = Math.max((int) chunkMinZ, Math.min((int) chunkMinZ + 15, blockZ));

            if (config.getSmoothTerrainHeight()) {
                surfaceY = (int) Math.round(getSmoothedHeight(mc, clampedX, clampedZ, chunkMinX, chunkMinZ));
            } else {
                surfaceY = mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clampedX, clampedZ);
            }
        } else {
            int chunkCenterX = (int) chunkMinX + 8;
            int chunkCenterZ = (int) chunkMinZ + 8;
            surfaceY = mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, chunkCenterX, chunkCenterZ);
        }

        double halfW = (thickness / 2.0f) * 0.025; // Narrow strip width

        int r = lineColor.getRed();
        int g = lineColor.getGreen();
        int b = lineColor.getBlue();
        int startAlpha = lineColor.getAlpha();

        double lineHeight = config.getTerrainLineHeight();

        if (config.getUseGradientLines()) {
            // 1. Underground segment: from minY to surfaceY
            if (surfaceY > minY) {
                int packedColor = ((startAlpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                GizmoStyle style = GizmoStyle.fill(packedColor);

                // Rectangle 1: varying Z, X is constant (facing West/East)
                Gizmos.rect(new Vec3(x, minY, z - halfW), new Vec3(x, minY, z + halfW), new Vec3(x, surfaceY, z + halfW), new Vec3(x, surfaceY, z - halfW), style);
                Gizmos.rect(new Vec3(x, surfaceY, z - halfW), new Vec3(x, surfaceY, z + halfW), new Vec3(x, minY, z + halfW), new Vec3(x, minY, z - halfW), style);

                // Rectangle 2: varying X, Z is constant (facing North/South)
                Gizmos.rect(new Vec3(x - halfW, minY, z), new Vec3(x + halfW, minY, z), new Vec3(x + halfW, surfaceY, z), new Vec3(x - halfW, surfaceY, z), style);
                Gizmos.rect(new Vec3(x - halfW, surfaceY, z), new Vec3(x + halfW, surfaceY, z), new Vec3(x + halfW, minY, z), new Vec3(x - halfW, minY, z), style);
            }

            // 2. Above-ground segment: from surfaceY to surfaceY + terrainLineHeight
            if (lineHeight > 0) {
                int steps = (int) Math.max(16, (int) Math.ceil(lineHeight));
                for (int step = 0; step < steps; step++) {
                    double yStart = surfaceY + step * (lineHeight / steps);
                    double yEnd = surfaceY + (step + 1) * (lineHeight / steps);
                    double tStart = (double) step / steps;

                    int a = Math.round(startAlpha * (1.0f - (float) tStart));
                    if (a <= 0) continue;

                    int packedColor = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                    GizmoStyle style = GizmoStyle.fill(packedColor);

                    // Rectangle 1: varying Z, X is constant (facing West/East)
                    Gizmos.rect(new Vec3(x, yStart, z - halfW), new Vec3(x, yStart, z + halfW), new Vec3(x, yEnd, z + halfW), new Vec3(x, yEnd, z - halfW), style);
                    Gizmos.rect(new Vec3(x, yEnd, z - halfW), new Vec3(x, yEnd, z + halfW), new Vec3(x, yStart, z + halfW), new Vec3(x, yStart, z - halfW), style);

                    // Rectangle 2: varying X, Z is constant (facing North/South)
                    Gizmos.rect(new Vec3(x - halfW, yStart, z), new Vec3(x + halfW, yStart, z), new Vec3(x + halfW, yEnd, z), new Vec3(x - halfW, yEnd, z), style);
                    Gizmos.rect(new Vec3(x - halfW, yEnd, z), new Vec3(x + halfW, yEnd, z), new Vec3(x + halfW, yStart, z), new Vec3(x - halfW, yStart, z), style);
                }
            }
        } else {
            // Draw using actual line primitives when gradients are disabled
            int packedColor = ((startAlpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
            double endY = surfaceY;
            if (lineHeight > 0) {
                endY += lineHeight;
            }
            if (endY > minY) {
                Gizmos.line(new Vec3(x, minY, z), new Vec3(x, endY, z), packedColor, thickness);
            }
        }
    }

    private void drawPlayerRelativeGradientLine(double x, double z, double playerY, double minY, double maxY, Color lineColor, float thickness, ModConfig config) {
        double yMin = Math.max(minY, playerY - config.getLineHeightBelowPlayer());
        double yMax = Math.min(maxY, playerY + config.getLineHeightAbovePlayer());

        double halfW = (thickness / 2.0f) * 0.025; // Narrow strip width

        int r = lineColor.getRed();
        int g = lineColor.getGreen();
        int b = lineColor.getBlue();
        int startAlpha = lineColor.getAlpha();

        if (config.getUseGradientLines()) {
            // 1. Solid segment below player feet: from yMin to playerY
            if (playerY > yMin) {
                int packedColor = ((startAlpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                GizmoStyle style = GizmoStyle.fill(packedColor);

                // Rectangle 1: varying Z, X is constant (facing West/East)
                Gizmos.rect(new Vec3(x, yMin, z - halfW), new Vec3(x, yMin, z + halfW), new Vec3(x, playerY, z + halfW), new Vec3(x, playerY, z - halfW), style);
                Gizmos.rect(new Vec3(x, playerY, z - halfW), new Vec3(x, playerY, z + halfW), new Vec3(x, yMin, z + halfW), new Vec3(x, yMin, z - halfW), style);

                // Rectangle 2: varying X, Z is constant (facing North/South)
                Gizmos.rect(new Vec3(x - halfW, yMin, z), new Vec3(x + halfW, yMin, z), new Vec3(x + halfW, playerY, z), new Vec3(x - halfW, playerY, z), style);
                Gizmos.rect(new Vec3(x - halfW, playerY, z), new Vec3(x + halfW, playerY, z), new Vec3(x + halfW, yMin, z), new Vec3(x - halfW, yMin, z), style);
            }

            // 2. Fading segment above player feet: from playerY to yMax
            double fadeHeight = yMax - playerY;
            if (fadeHeight > 0) {
                int steps = (int) Math.max(8, Math.min(64, fadeHeight / 2.0));
                for (int step = 0; step < steps; step++) {
                    double yStart = playerY + step * (fadeHeight / steps);
                    double yEnd = playerY + (step + 1) * (fadeHeight / steps);
                    float t = (float) (step + 0.5f) / steps;

                    int a = Math.round(startAlpha * (1.0f - t));
                    if (a <= 0) continue;

                    int packedColor = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
                    GizmoStyle style = GizmoStyle.fill(packedColor);

                    // Rectangle 1: varying Z, X is constant (facing West/East)
                    Gizmos.rect(new Vec3(x, yStart, z - halfW), new Vec3(x, yStart, z + halfW), new Vec3(x, yEnd, z + halfW), new Vec3(x, yEnd, z - halfW), style);
                    Gizmos.rect(new Vec3(x, yEnd, z - halfW), new Vec3(x, yEnd, z + halfW), new Vec3(x, yStart, z + halfW), new Vec3(x, yStart, z - halfW), style);

                    // Rectangle 2: varying X, Z is constant (facing North/South)
                    Gizmos.rect(new Vec3(x - halfW, yStart, z), new Vec3(x + halfW, yStart, z), new Vec3(x + halfW, yEnd, z), new Vec3(x - halfW, yEnd, z), style);
                    Gizmos.rect(new Vec3(x - halfW, yEnd, z), new Vec3(x + halfW, yEnd, z), new Vec3(x + halfW, yStart, z), new Vec3(x - halfW, yStart, z), style);
                }
            }
        } else {
            // Draw using actual line primitives when gradients are disabled
            int packedColor = ((startAlpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
            if (yMax > yMin) {
                Gizmos.line(new Vec3(x, yMin, z), new Vec3(x, yMax, z), packedColor, thickness);
            }
        }
    }

    private void drawWallSegment(double x1, double z1, double ySurf1, double x2, double z2, double ySurf2, double minY, ModConfig config) {
        Color topC = config.getGradientTopColor();
        Color floorC = config.getGradientBottomColor();
        Color undergroundC = config.getGradientUndergroundColor();
        double wallHeight = config.getTerrainGradientHeight();

        // 1. Above Ground (ySurf to ySurf + wallHeight): Gradient floorC -> topC
        if (wallHeight > 0) {
            int steps = (int) Math.max(16, (int) Math.ceil(wallHeight));
            for (int step = 0; step < steps; step++) {
                float tStart = (float) step / steps;
                float tEnd = (float) (step + 1) / steps;
                float tColor = (float) (step + 0.5f) / steps;

                int packedColor = getInterpolatedPackedColor(floorC, topC, tColor);
                GizmoStyle style = GizmoStyle.fill(packedColor);

                double yStart1 = ySurf1 + tStart * wallHeight;
                double yStart2 = ySurf2 + tStart * wallHeight;
                double yEnd1 = ySurf1 + tEnd * wallHeight;
                double yEnd2 = ySurf2 + tEnd * wallHeight;

                drawQuad(x1, z1, yStart1, yEnd1, x2, z2, yStart2, yEnd2, minY, style);
            }
        }

        // 2. Floor Block (ySurf - 1.0 to ySurf): Solid floorC
        int floorPacked = getPackedColor(floorC);
        GizmoStyle floorStyle = GizmoStyle.fill(floorPacked);
        drawQuad(x1, z1, ySurf1 - 1.0, ySurf1, x2, z2, ySurf2 - 1.0, ySurf2, minY, floorStyle);

        // 3. Underground Gradient (ySurf - 1.0 - wallHeight to ySurf - 1.0): Gradient undergroundC -> floorC
        if (wallHeight > 0) {
            int steps = (int) Math.max(16, (int) Math.ceil(wallHeight));
            for (int step = 0; step < steps; step++) {
                float tStart = (float) step / steps;
                float tEnd = (float) (step + 1) / steps;
                float tColor = (float) (step + 0.5f) / steps;

                int packedColor = getInterpolatedPackedColor(undergroundC, floorC, tColor);
                GizmoStyle style = GizmoStyle.fill(packedColor);

                double yStart1 = (ySurf1 - 1.0 - wallHeight) + tStart * wallHeight;
                double yStart2 = (ySurf2 - 1.0 - wallHeight) + tStart * wallHeight;
                double yEnd1 = (ySurf1 - 1.0 - wallHeight) + tEnd * wallHeight;
                double yEnd2 = (ySurf2 - 1.0 - wallHeight) + tEnd * wallHeight;

                drawQuad(x1, z1, yStart1, yEnd1, x2, z2, yStart2, yEnd2, minY, style);
            }
        }

        // 4. Deep Underground (minY to ySurf - 1.0 - wallHeight): Solid undergroundC
        int undergroundPacked = getPackedColor(undergroundC);
        GizmoStyle undergroundStyle = GizmoStyle.fill(undergroundPacked);
        drawQuad(x1, z1, minY, ySurf1 - 1.0 - wallHeight, x2, z2, minY, ySurf2 - 1.0 - wallHeight, minY, undergroundStyle);
    }

    private void drawQuad(double x1, double z1, double yStart1, double yEnd1,
                          double x2, double z2, double yStart2, double yEnd2,
                          double minY, GizmoStyle style) {
        double ys1 = Math.max(minY, yStart1);
        double ye1 = Math.max(minY, yEnd1);
        double ys2 = Math.max(minY, yStart2);
        double ye2 = Math.max(minY, yEnd2);

        if (ys1 >= ye1 && ys2 >= ye2) {
            return;
        }

        Gizmos.rect(new Vec3(x1, ys1, z1), new Vec3(x2, ys2, z2), new Vec3(x2, ye2, z2), new Vec3(x1, ye1, z1), style);
        Gizmos.rect(new Vec3(x1, ye1, z1), new Vec3(x2, ye2, z2), new Vec3(x2, ys2, z2), new Vec3(x1, ys1, z1), style);
    }

    private int getPackedColor(Color color) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int a = color.getAlpha();
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private int getInterpolatedPackedColor(Color c1, Color c2, float t) {
        int r = Math.round(c1.getRed() + t * (c2.getRed() - c1.getRed()));
        int g = Math.round(c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = Math.round(c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
        int a = Math.round(c1.getAlpha() + t * (c2.getAlpha() - c1.getAlpha()));
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private double getChunkMinX(double x, double currentMinX) {
        if (x <= currentMinX) {
            return x;
        } else {
            return x - 16.0;
        }
    }

    private double getChunkMinZ(double z, double currentMinZ) {
        if (z <= currentMinZ) {
            return z;
        } else {
            return z - 16.0;
        }
    }

    private double getSmoothedHeight(Minecraft mc, int blockX, int blockZ, double chunkMinX, double chunkMinZ) {
        double totalHeight = 0.0;
        int minXInt = (int) chunkMinX;
        int minZInt = (int) chunkMinZ;
        int maxXInt = minXInt + 15;
        int maxZInt = minZInt + 15;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                int nx = blockX + dx;
                int nz = blockZ + dz;

                // Clamp to chunk bounds to prevent leaking next chunk height
                int clampedX = Math.max(minXInt, Math.min(maxXInt, nx));
                int clampedZ = Math.max(minZInt, Math.min(maxZInt, nz));

                double height = mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clampedX, clampedZ);

                double weight;
                if (dx == 0 && dz == 0) {
                    weight = 4.0 / 16.0;
                } else if (dx == 0 || dz == 0) {
                    weight = 2.0 / 16.0;
                } else {
                    weight = 1.0 / 16.0;
                }

                totalHeight += height * weight;
            }
        }
        return totalHeight;
    }

    private double getTerrainHeight(double x, double z, ModConfig config, Minecraft mc, double chunkMinX, double chunkMinZ) {
        if (config.getSlopeTerrainGradients()) {
            int blockX = (int) Math.floor(x);
            int blockZ = (int) Math.floor(z);
            int clampedX = Math.max((int) chunkMinX, Math.min((int) chunkMinX + 15, blockX));
            int clampedZ = Math.max((int) chunkMinZ, Math.min((int) chunkMinZ + 15, blockZ));

            if (config.getSmoothTerrainHeight()) {
                return getSmoothedHeight(mc, clampedX, clampedZ, chunkMinX, chunkMinZ);
            } else {
                return mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clampedX, clampedZ);
            }
        } else {
            double centerX = chunkMinX + 8.0;
            double centerZ = chunkMinZ + 8.0;
            return mc.level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(centerX), (int) Math.floor(centerZ));
        }
    }

    private void drawGradientWallsAroundChunk(double minX, double minZ, double maxX, double maxZ, double yStart, double yEnd, int packedColor) {
        GizmoStyle style = GizmoStyle.fill(packedColor);
        // Wall 1: West
        Gizmos.rect(new Vec3(minX, yStart, minZ), new Vec3(minX, yStart, maxZ), new Vec3(minX, yEnd, maxZ), new Vec3(minX, yEnd, minZ), style);
        Gizmos.rect(new Vec3(minX, yEnd, minZ), new Vec3(minX, yEnd, maxZ), new Vec3(minX, yStart, maxZ), new Vec3(minX, yStart, minZ), style);
        // Wall 2: East
        Gizmos.rect(new Vec3(maxX, yStart, minZ), new Vec3(maxX, yStart, maxZ), new Vec3(maxX, yEnd, maxZ), new Vec3(maxX, yEnd, minZ), style);
        Gizmos.rect(new Vec3(maxX, yEnd, minZ), new Vec3(maxX, yEnd, maxZ), new Vec3(maxX, yStart, maxZ), new Vec3(maxX, yStart, minZ), style);
        // Wall 3: North
        Gizmos.rect(new Vec3(minX, yStart, minZ), new Vec3(maxX, yStart, minZ), new Vec3(maxX, yEnd, minZ), new Vec3(minX, yEnd, minZ), style);
        Gizmos.rect(new Vec3(minX, yEnd, minZ), new Vec3(maxX, yEnd, minZ), new Vec3(maxX, yStart, minZ), new Vec3(minX, yStart, minZ), style);
        // Wall 4: South
        Gizmos.rect(new Vec3(minX, yStart, maxZ), new Vec3(maxX, yStart, maxZ), new Vec3(maxX, yEnd, maxZ), new Vec3(minX, yEnd, maxZ), style);
        Gizmos.rect(new Vec3(minX, yEnd, maxZ), new Vec3(maxX, yEnd, maxZ), new Vec3(maxX, yStart, maxZ), new Vec3(minX, yStart, maxZ), style);
    }
}
