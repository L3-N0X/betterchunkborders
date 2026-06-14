package de.lenox.client.renderer;

import de.lenox.client.config.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.SectionPos;
import net.minecraft.gizmos.Gizmos;
import net.minecraft.gizmos.GizmoStyle;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.levelgen.Heightmap;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class ChunkBorderRendererHelper {
    private static double smoothedPlayerY = 0.0;
    private static long lastTime = 0;

    public static boolean onEmitGizmos(double cameraY) {
        ModConfig config = ModConfig.getINSTANCE();
        if (!config.getModEnabled()) {
            return false;
        }

        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        if (level == null) {
            return false;
        }

        var entity = mc.gameRenderer.getMainCamera().entity();
        if (entity == null) {
            return false;
        }

        SectionPos sectionPos = SectionPos.of(entity.blockPosition());
        double minX = sectionPos.minBlockX();
        double minZ = sectionPos.minBlockZ();
        double maxX = minX + 16.0;
        double maxZ = minZ + 16.0;

        double minY = level.getMinY();
        double maxY = level.getMaxY() + 1.0;

        double playerY = getPlayerY(entity.getY(), config);

        if (config.getRenderGradients()) {
            renderGradients(minX, minZ, maxX, maxZ, minY, maxY, cameraY, config, level);
        }

        if (config.getRenderLines()) {
            renderLines(minX, minZ, maxX, maxZ, minY, maxY, playerY, config, level);
        }

        if (config.getRenderHorizontalLines()) {
            renderHorizontalLines(minX, minZ, maxX, maxZ, minY, maxY, playerY, config);
        }

        return true;
    }

    private static double getPlayerY(double targetPlayerY, ModConfig config) {
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
            return smoothedPlayerY;
        } else {
            lastTime = 0;
            return targetPlayerY;
        }
    }

    private static void renderGradients(double minX, double minZ, double maxX, double maxZ, double minY, double maxY, double cameraY, ModConfig config, ClientLevel level) {
        if (config.getUseTerrainHeightmap()) {
            int steps = config.getTerrainAdaptionSteps();
            drawTerrainWall(minX, minZ, minX, maxZ, steps, minY, config, level, minX, minZ); // West
            drawTerrainWall(maxX, minZ, maxX, maxZ, steps, minY, config, level, minX, minZ); // East
            drawTerrainWall(minX, minZ, maxX, minZ, steps, minY, config, level, minX, minZ); // North
            drawTerrainWall(minX, maxZ, maxX, maxZ, steps, minY, config, level, minX, minZ); // South
        } else {
            double halfHeight = config.getGradientMaxHeight() / 2.0;
            Color topC = config.getGradientTopColor();
            Color botC = config.getGradientBottomColor();

            if (config.getMirrorGradient()) {
                // Upper half: cameraY to cameraY + halfHeight (interpolates botC -> topC)
                drawRelativeGradientRange(minX, minZ, maxX, maxZ, cameraY, halfHeight, cameraY, cameraY + halfHeight, minY, maxY, botC, topC);
                // Lower half: cameraY - halfHeight to cameraY (interpolates topC <- botC as Y increases)
                drawRelativeGradientRange(minX, minZ, maxX, maxZ, cameraY, halfHeight, cameraY - halfHeight, cameraY, minY, maxY, botC, topC);
            } else {
                double gradMinY = Math.clamp(cameraY - halfHeight, minY, maxY);
                double gradMaxY = Math.clamp(cameraY + halfHeight, minY, maxY);

                double height = gradMaxY - gradMinY;
                if (height > 0) {
                    int steps = (int) Math.clamp(height / 2.0, 8.0, 64.0); // Dynamic step size (~2 blocks per step)
                    for (int step = 0; step < steps; step++) {
                        double yStart = gradMinY + step * (height / steps);
                        double yEnd = gradMinY + (step + 1) * (height / steps);
                        float t = (step + 0.5f) / steps;
                        int packedColor = getInterpolatedPackedColor(botC, topC, t);
                        drawGradientWallsAroundChunk(minX, minZ, maxX, maxZ, yStart, yEnd, minY, packedColor);
                    }
                }
            }
        }
    }

    private static void renderLines(double minX, double minZ, double maxX, double maxZ, double minY, double maxY, double playerY, ModConfig config, ClientLevel level) {
        Color lineColor = config.getLineColor();
        int packedColor = getPackedColor(lineColor);
        float thickness = config.getLineWidth();

        Color cornerColor = config.getUseSeparateCornerColor() ? config.getCornerLineColor() : lineColor;
        int packedCornerColor = getPackedColor(cornerColor);

        if (config.getUseGradientLines() || config.getLimitLineHeight()) {
            if (config.getUseTerrainHeightmap()) {
                // Draw outer borders (4 corners)
                drawTerrainGradientLine(minX, minZ, minY, cornerColor, thickness, config, level, minX, minZ);
                drawTerrainGradientLine(minX, maxZ, minY, cornerColor, thickness, config, level, minX, minZ);
                drawTerrainGradientLine(maxX, minZ, minY, cornerColor, thickness, config, level, minX, minZ);
                drawTerrainGradientLine(maxX, maxZ, minY, cornerColor, thickness, config, level, minX, minZ);

                // Draw inner grid lines based on Grid Spacing config
                int spacing = config.getGridSpacing();
                if (spacing > 0 && spacing < 16) {
                    for (int xOffset = spacing; xOffset < 16; xOffset += spacing) {
                        double xCoord = minX + xOffset;
                        drawTerrainGradientLine(xCoord, minZ, minY, lineColor, thickness, config, level, minX, minZ);
                        drawTerrainGradientLine(xCoord, maxZ, minY, lineColor, thickness, config, level, minX, minZ);
                    }
                    for (int zOffset = spacing; zOffset < 16; zOffset += spacing) {
                        double zCoord = minZ + zOffset;
                        drawTerrainGradientLine(minX, zCoord, minY, lineColor, thickness, config, level, minX, minZ);
                        drawTerrainGradientLine(maxX, zCoord, minY, lineColor, thickness, config, level, minX, minZ);
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
                                drawTerrainGradientLine(x, z, minY, surrColor, thickness, config, level, chunkMinX, chunkMinZ);
                            }
                        }
                    }
                }
            } else {
                // Draw outer borders (4 corners)
                drawPlayerRelativeGradientLine(minX, minZ, playerY, minY, maxY, cornerColor, thickness, config);
                drawPlayerRelativeGradientLine(minX, maxZ, playerY, minY, maxY, cornerColor, thickness, config);
                drawPlayerRelativeGradientLine(maxX, minZ, playerY, minY, maxY, cornerColor, thickness, config);
                drawPlayerRelativeGradientLine(maxX, maxZ, playerY, minY, maxY, cornerColor, thickness, config);

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
            Gizmos.line(new Vec3(minX, minY, minZ), new Vec3(minX, maxY, minZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(minX, minY, maxZ), new Vec3(minX, maxY, maxZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(maxX, minY, minZ), new Vec3(maxX, maxY, minZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(maxX, minY, maxZ), new Vec3(maxX, maxY, maxZ), packedCornerColor, thickness);

            // Draw horizontal frames (top and bottom boxes)
            // Bottom frame
            Gizmos.line(new Vec3(minX, minY, minZ), new Vec3(maxX, minY, minZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(maxX, minY, minZ), new Vec3(maxX, minY, maxZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(maxX, minY, maxZ), new Vec3(minX, minY, maxZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(minX, minY, maxZ), new Vec3(minX, minY, minZ), packedCornerColor, thickness);
            // Top frame
            Gizmos.line(new Vec3(minX, maxY, minZ), new Vec3(maxX, maxY, minZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(maxX, maxY, minZ), new Vec3(maxX, maxY, maxZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(maxX, maxY, maxZ), new Vec3(minX, maxY, maxZ), packedCornerColor, thickness);
            Gizmos.line(new Vec3(minX, maxY, maxZ), new Vec3(minX, maxY, minZ), packedCornerColor, thickness);

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
                int packedSurrColor = getPackedColor(surrColor);

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

    private static void renderHorizontalLines(double minX, double minZ, double maxX, double maxZ, double minY, double maxY, double playerY, ModConfig config) {
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

    private static void drawTerrainWall(double x1, double z1, double x2, double z2, int steps, double minY, ModConfig config, ClientLevel level, double chunkMinX, double chunkMinZ) {
        for (int i = 0; i < steps; i++) {
            double t1 = (double) i / steps;
            double t2 = (double) (i + 1) / steps;
            double px1 = x1 + t1 * (x2 - x1);
            double pz1 = z1 + t1 * (z2 - z1);
            double px2 = x1 + t2 * (x2 - x1);
            double pz2 = z1 + t2 * (z2 - z1);
            double ySurf1 = getTerrainHeight(px1, pz1, config, level, chunkMinX, chunkMinZ);
            double ySurf2 = getTerrainHeight(px2, pz2, config, level, chunkMinX, chunkMinZ);
            drawWallSegment(px1, pz1, ySurf1, px2, pz2, ySurf2, minY, config);
        }
    }

    private static double getTerrainHeight(double x, double z, ModConfig config, ClientLevel level, double chunkMinX, double chunkMinZ) {
        if (config.getSlopeTerrainGradients()) {
            int blockX = (int) Math.floor(x);
            int blockZ = (int) Math.floor(z);
            int clampedX = Math.clamp(blockX, (int) chunkMinX, (int) chunkMinX + 15);
            int clampedZ = Math.clamp(blockZ, (int) chunkMinZ, (int) chunkMinZ + 15);

            if (config.getSmoothTerrainHeight()) {
                return getSmoothedHeight(level, clampedX, clampedZ, chunkMinX, chunkMinZ);
            } else {
                return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clampedX, clampedZ);
            }
        } else {
            double centerX = chunkMinX + 8.0;
            double centerZ = chunkMinZ + 8.0;
            return level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, (int) Math.floor(centerX), (int) Math.floor(centerZ));
        }
    }

    private static double getSmoothedHeight(ClientLevel level, int blockX, int blockZ, double chunkMinX, double chunkMinZ) {
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
                int clampedX = Math.clamp(nx, minXInt, maxXInt);
                int clampedZ = Math.clamp(nz, minZInt, maxZInt);

                double height = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clampedX, clampedZ);

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

    private static void drawWallSegment(double x1, double z1, double ySurf1, double x2, double z2, double ySurf2, double minY, ModConfig config) {
        Color topC = config.getGradientTopColor();
        Color floorC = config.getGradientBottomColor();
        Color undergroundC = config.getGradientUndergroundColor();
        double wallHeight = config.getTerrainGradientHeight();

        // 1. Above Ground (ySurf to ySurf + wallHeight): Gradient floorC -> topC
        if (wallHeight > 0) {
            int steps = Math.max(16, (int) Math.ceil(wallHeight));
            for (int step = 0; step < steps; step++) {
                float tStart = (float) step / steps;
                float tEnd = (float) (step + 1) / steps;
                float tColor = (step + 0.5f) / steps;

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
            int steps = Math.max(16, (int) Math.ceil(wallHeight));
            for (int step = 0; step < steps; step++) {
                float tStart = (float) step / steps;
                float tEnd = (float) (step + 1) / steps;
                float tColor = (step + 0.5f) / steps;

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

    private static void drawQuad(double x1, double z1, double yStart1, double yEnd1,
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

    private static void drawRelativeGradientRange(double minX, double minZ, double maxX, double maxZ, double cameraY, double halfHeight, double startY, double endY, double minY, double maxY, Color botC, Color topC) {
        double rangeMinY = Math.clamp(startY, minY, maxY);
        double rangeMaxY = Math.clamp(endY, minY, maxY);
        double height = rangeMaxY - rangeMinY;
        if (height <= 0) {
            return;
        }
        int steps = (int) Math.clamp(height / 2.0, 4.0, 32.0);
        for (int step = 0; step < steps; step++) {
            double yStart = rangeMinY + step * (height / steps);
            double yEnd = rangeMinY + (step + 1) * (height / steps);
            double yMid = (yStart + yEnd) / 2.0;
            float t = halfHeight > 0 ? (float) (Math.abs(yMid - cameraY) / halfHeight) : 0.0f;
            t = Math.clamp(t, 0.0f, 1.0f);
            int packedColor = getInterpolatedPackedColor(botC, topC, t);
            drawGradientWallsAroundChunk(minX, minZ, maxX, maxZ, yStart, yEnd, minY, packedColor);
        }
    }

    private static void drawGradientWallsAroundChunk(double minX, double minZ, double maxX, double maxZ, double yStart, double yEnd, double minY, int packedColor) {
        GizmoStyle style = GizmoStyle.fill(packedColor);
        drawQuad(minX, minZ, yStart, yEnd, minX, maxZ, yStart, yEnd, minY, style); // West
        drawQuad(maxX, minZ, yStart, yEnd, maxX, maxZ, yStart, yEnd, minY, style); // East
        drawQuad(minX, minZ, yStart, yEnd, maxX, minZ, yStart, yEnd, minY, style); // North
        drawQuad(minX, maxZ, yStart, yEnd, maxX, maxZ, yStart, yEnd, minY, style); // South
    }

    private static void drawTerrainGradientLine(double x, double z, double minY, Color lineColor, float thickness, ModConfig config, ClientLevel level, double chunkMinX, double chunkMinZ) {
        int surfaceY;
        if (config.getSlopeTerrainGradients()) {
            int blockX = (int) Math.floor(x);
            int blockZ = (int) Math.floor(z);
            int clampedX = Math.clamp(blockX, (int) chunkMinX, (int) chunkMinX + 15);
            int clampedZ = Math.clamp(blockZ, (int) chunkMinZ, (int) chunkMinZ + 15);

            if (config.getSmoothTerrainHeight()) {
                surfaceY = (int) Math.round(getSmoothedHeight(level, clampedX, clampedZ, chunkMinX, chunkMinZ));
            } else {
                surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, clampedX, clampedZ);
            }
        } else {
            int chunkCenterX = (int) chunkMinX + 8;
            int chunkCenterZ = (int) chunkMinZ + 8;
            surfaceY = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, chunkCenterX, chunkCenterZ);
        }

        double halfW = (thickness / 2.0f) * 0.025; // Narrow strip width
        int startAlpha = lineColor.getAlpha();
        double lineHeight = config.getTerrainLineHeight();

        if (config.getUseGradientLines()) {
            // 1. Underground segment: from minY to surfaceY
            if (surfaceY > minY) {
                int packedColor = getPackedColor(lineColor);
                GizmoStyle style = GizmoStyle.fill(packedColor);
                drawGradientLineSegment(x, z, minY, surfaceY, halfW, style);
            }

            // 2. Above-ground segment: from surfaceY to surfaceY + terrainLineHeight
            if (lineHeight > 0) {
                int steps = Math.max(16, (int) Math.ceil(lineHeight));
                for (int step = 0; step < steps; step++) {
                    double yStart = surfaceY + step * (lineHeight / steps);
                    double yEnd = surfaceY + (step + 1) * (lineHeight / steps);
                    double tStart = (double) step / steps;

                    int a = Math.round(startAlpha * (1.0f - (float) tStart));
                    if (a <= 0) continue;

                    int packedColor = getPackedColor(lineColor, a);
                    GizmoStyle style = GizmoStyle.fill(packedColor);
                    drawGradientLineSegment(x, z, yStart, yEnd, halfW, style);
                }
            }
        } else {
            // Draw using actual line primitives when gradients are disabled
            int packedColor = getPackedColor(lineColor);
            double endY = surfaceY;
            if (lineHeight > 0) {
                endY += lineHeight;
            }
            if (endY > minY) {
                Gizmos.line(new Vec3(x, minY, z), new Vec3(x, endY, z), packedColor, thickness);
            }
        }
    }

    private static void drawPlayerRelativeGradientLine(double x, double z, double playerY, double minY, double maxY, Color lineColor, float thickness, ModConfig config) {
        double yMin = Math.max(minY, playerY - config.getLineHeightBelowPlayer());
        double yMax = Math.min(maxY, playerY + config.getLineHeightAbovePlayer());

        double halfW = (thickness / 2.0f) * 0.025; // Narrow strip width
        int startAlpha = lineColor.getAlpha();

        if (config.getUseGradientLines()) {
            // 1. Solid segment below player feet: from yMin to playerY
            if (playerY > yMin) {
                int packedColor = getPackedColor(lineColor);
                GizmoStyle style = GizmoStyle.fill(packedColor);
                drawGradientLineSegment(x, z, yMin, playerY, halfW, style);
            }

            // 2. Fading segment above player feet: from playerY to yMax
            double fadeHeight = yMax - playerY;
            if (fadeHeight > 0) {
                int steps = (int) Math.clamp(fadeHeight / 2.0, 8.0, 64.0);
                for (int step = 0; step < steps; step++) {
                    double yStart = playerY + step * (fadeHeight / steps);
                    double yEnd = playerY + (step + 1) * (fadeHeight / steps);
                    float t = (step + 0.5f) / steps;

                    int a = Math.round(startAlpha * (1.0f - t));
                    if (a <= 0) continue;

                    int packedColor = getPackedColor(lineColor, a);
                    GizmoStyle style = GizmoStyle.fill(packedColor);
                    drawGradientLineSegment(x, z, yStart, yEnd, halfW, style);
                }
            }
        } else {
            // Draw using actual line primitives when gradients are disabled
            int packedColor = getPackedColor(lineColor);
            if (yMax > yMin) {
                Gizmos.line(new Vec3(x, yMin, z), new Vec3(x, yMax, z), packedColor, thickness);
            }
        }
    }

    private static void drawGradientLineSegment(double x, double z, double yStart, double yEnd, double halfW, GizmoStyle style) {
        // Rectangle 1: varying Z, X is constant (facing West/East)
        Gizmos.rect(new Vec3(x, yStart, z - halfW), new Vec3(x, yStart, z + halfW), new Vec3(x, yEnd, z + halfW), new Vec3(x, yEnd, z - halfW), style);
        Gizmos.rect(new Vec3(x, yEnd, z - halfW), new Vec3(x, yEnd, z + halfW), new Vec3(x, yStart, z + halfW), new Vec3(x, yStart, z - halfW), style);

        // Rectangle 2: varying X, Z is constant (facing North/South)
        Gizmos.rect(new Vec3(x - halfW, yStart, z), new Vec3(x + halfW, yStart, z), new Vec3(x + halfW, yEnd, z), new Vec3(x - halfW, yEnd, z), style);
        Gizmos.rect(new Vec3(x - halfW, yEnd, z), new Vec3(x + halfW, yEnd, z), new Vec3(x + halfW, yStart, z), new Vec3(x - halfW, yStart, z), style);
    }

    private static double getChunkMinX(double x, double currentMinX) {
        if (x <= currentMinX) {
            return x;
        } else {
            return x - 16.0;
        }
    }

    private static double getChunkMinZ(double z, double currentMinZ) {
        if (z <= currentMinZ) {
            return z;
        } else {
            return z - 16.0;
        }
    }

    private static int getPackedColor(Color color) {
        return getPackedColor(color, color.getAlpha());
    }

    private static int getPackedColor(Color color, int alpha) {
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        return ((alpha & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }

    private static int getInterpolatedPackedColor(Color c1, Color c2, float t) {
        int r = Math.round(c1.getRed() + t * (c2.getRed() - c1.getRed()));
        int g = Math.round(c1.getGreen() + t * (c2.getGreen() - c1.getGreen()));
        int b = Math.round(c1.getBlue() + t * (c2.getBlue() - c1.getBlue()));
        int a = Math.round(c1.getAlpha() + t * (c2.getAlpha() - c1.getAlpha()));
        return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
    }
}
