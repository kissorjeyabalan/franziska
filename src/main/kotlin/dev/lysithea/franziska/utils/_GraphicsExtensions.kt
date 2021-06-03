@file:Suppress("unused")

package dev.lysithea.franziska.utils

import reactor.util.function.Tuple4
import reactor.util.function.Tuples
import java.awt.Color
import java.awt.Graphics
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.geom.Rectangle2D
import java.awt.image.BufferedImage
import reactor.kotlin.core.util.function.*
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import javax.imageio.ImageIO

/**
 * Graphics related extensions.
 */

fun BufferedImage.getAverageColour(weight: String = "alpha"): Color = this.raster.let { raster ->
    val pixels = raster.getPixels(0, 0, raster.width, raster.height, null as IntArray?)
    var red = 0.0
    var green = 0.0
    var blue = 0.0
    var weights = 0.0
    val getWeight: (Tuple4<Int, Int, Int, Double>) -> Double = when (weight.lowercase()) {
        "alpha" -> { i -> i.t4 }
        "saturation" -> { (r, g, b) -> Color.RGBtoHSB(r, g, b, null)[1].toDouble() }
        "brightness" -> { (r, g, b) -> Color.RGBtoHSB(r, g, b, null)[2].toDouble() }
        else -> throw IllegalStateException("Unknown average color weight key $weight." +
                " Accepted values: alpha, saturation, brightness")
    }
    val hasAlpha = alphaRaster != null
    (pixels.indices).step(if (hasAlpha) 4 else 3).asSequence()
        .map { Tuples.of(pixels[it], pixels[it + 1], pixels[it + 2], if (hasAlpha) pixels[it + 3] / 255.0 else 1.0) }
        .forEach {
            val w = getWeight(it)
            red += it.t1 * w
            green += it.t2 * w
            blue += it.t3 * w
            weights += w
        }
    Color((red / weights).toInt(), (green / weights).toInt(), (blue / weights).toInt())
}

fun Graphics.fillRoundRect(rect: Rectangle2D, radius: Int) {
    fillRoundRect(rect.x.toInt(), rect.y.toInt(), rect.width.toInt(), rect.height.toInt(), radius, radius)
}

fun Graphics.drawStringCentered(text: String, rect: Rectangle2D) {
    val (x, y) = fontMetrics.let {
        rect.x + (rect.width - it.stringWidth(text)) / 2 to rect.y + ((rect.height - it.height) / 2 + it.ascent)
    }

    drawString(text, x.toInt(), y.toInt())
}

fun Graphics2D.enableAntialiasing() {
    setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP)
    setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
}

fun BufferedImage.toInputStream(): InputStream {
    val baos = ByteArrayOutputStream()
    ImageIO.write(this, "png", baos)
    return ByteArrayInputStream(baos.toByteArray())
}