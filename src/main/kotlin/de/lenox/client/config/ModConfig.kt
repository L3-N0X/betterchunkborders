package de.lenox.client.config

import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import net.fabricmc.loader.api.FabricLoader
import java.awt.Color
import java.io.File
import java.io.FileReader
import java.io.FileWriter

data class ModConfig(
    var modEnabled: Boolean = true,
    var gridSpacing: Int = 16,
    var lineColor: Color = Color(255, 0, 0, 255),
    var useSeparateCornerColor: Boolean = false,
    var cornerLineColor: Color = Color(255, 0, 255, 255),
    var lineWidth: Float = 2.0f,
    var renderGradients: Boolean = true,
    var gradientTopColor: Color = Color(255, 0, 0, 0),
    var gradientBottomColor: Color = Color(255, 0, 0, 100),
    var gradientMaxHeight: Float = 64.0f,
    var renderSurroundingBorders: Boolean = false,
    var surroundingBordersColor: Color = Color(0, 255, 0, 255),
    var useGradientLines: Boolean = true,
    var lineHeightAbovePlayer: Float = 64.0f,
    var lineHeightBelowPlayer: Float = 64.0f,
    var useTerrainHeightmap: Boolean = true,
    var slopeTerrainGradients: Boolean = true,
    var terrainGradientHeight: Float = 24.0f,
    var terrainLineHeight: Float = 24.0f,
    var renderLines: Boolean = true,
    var terrainAdaptionSteps: Int = 2,
    var gradientUndergroundColor: Color = Color(255, 0, 0, 30),
    var smoothTerrainHeight: Boolean = true,
    var limitLineHeight: Boolean = true,
    var smoothPlayerHeight: Boolean = true,
    var playerHeightSmoothingSpeed: Float = 5.0f,
    var renderHorizontalLines: Boolean = false,
    var horizontalLineColor: Color = Color(255, 0, 0, 255),
    var horizontalLineWidth: Float = 2.0f,
    var horizontalLineSpacing: Float = 4.0f,
    var horizontalLineOffset: Float = 0.0f,
    var horizontalLinesFollowPlayer: Boolean = false,
    var horizontalLinesAbovePlayer: Int = 1,
    var horizontalLinesBelowPlayer: Int = 0,
    var horizontalLineHeight: Float = 0.0f,
    var smoothLineMovement: Boolean = true,
    var mirrorGradient: Boolean = true
) {
    fun save() {
        save(this)
    }

    companion object {
        private val gson = GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Color::class.java, ColorTypeAdapter())
            .create()

        private val configFile: File
            get() = FabricLoader.getInstance().configDir.resolve("betterchunkborders.json").toFile()

        @JvmStatic
        var INSTANCE: ModConfig = load()

        fun load(): ModConfig {
            val file = configFile
            if (!file.exists()) {
                val config = ModConfig()
                config.save()
                return config
            }
            return try {
                FileReader(file).use { reader ->
                    gson.fromJson(reader, ModConfig::class.java) ?: ModConfig()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                ModConfig()
            }
        }

        fun save(config: ModConfig = INSTANCE) {
            try {
                val file = configFile
                file.parentFile?.mkdirs()
                FileWriter(file).use { writer ->
                    gson.toJson(config, writer)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class ColorTypeAdapter : TypeAdapter<Color>() {
    override fun write(out: JsonWriter, value: Color?) {
        if (value == null) {
            out.nullValue()
        } else {
            out.value(String.format("#%02x%02x%02x%02x", value.red, value.green, value.blue, value.alpha))
        }
    }

    override fun read(reader: JsonReader): Color {
        val str = reader.nextString()
        if (str.startsWith("#")) {
            val hex = str.substring(1)
            if (hex.length == 8) {
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)
                val a = hex.substring(6, 8).toInt(16)
                return Color(r, g, b, a)
            } else if (hex.length == 6) {
                val r = hex.substring(0, 2).toInt(16)
                val g = hex.substring(2, 4).toInt(16)
                val b = hex.substring(4, 6).toInt(16)
                return Color(r, g, b, 255)
            }
        }
        return Color.WHITE
    }
}
