package ch.bildspur.ivat.test

import ch.bildspur.ivat.io.ImageSource
import ch.bildspur.ivat.io.SingleImageSource
import ch.bildspur.ivat.util.ExponentialMovingAverage
import ch.bildspur.ivat.vision.*
import processing.core.PApplet
import processing.core.PFont


class TestSketch : PApplet() {
    private val source : ImageSource = SingleImageSource("data/reference_bw.jpg", "data/original.jpg")
    private val detector : PerspectiveTransformer = SimplePerspectiveTransformer()

    private val fpsAverage = ExponentialMovingAverage(0.1)
    private val imageSize = 600


    private lateinit var font : PFont

    override fun settings() {
        size(imageSize * 3, imageSize, FX2D)
    }

    override fun setup() {
        source.setup(this)

        // setup style
        font = createFont("Helvetica", 20f)
        textFont(font, 18f)
    }

    override fun draw() {
        background(22f)

        // read data
        val queryImage = source.readReference()
        val trainImage = source.readOriginal()

        // resize images
        queryImage.resize(imageSize, 0)
        trainImage.resize(imageSize, 0)

        // create images for opencv
        val queryMat = queryImage.toMat()
        val trainMat = trainImage.toMat()
        val resultMat = trainImage.toMat()

        // do recognition
        val transformMatrix = detector.detectTransformMatrix(queryMat, trainMat)
        detector.transform(resultMat, transformMatrix)

        // draw images
        g.image(queryMat.toPImage(), 0f, 0f)
        g.image(trainMat.toPImage(), imageSize.toFloat(), 0f)
        g.image(resultMat.toPImage(), imageSize * 2f, 0f)

        // show fps
        fpsAverage += frameRate.toDouble()
        surface.setTitle("IVAT | FPS: ${frameRate.format(2)}\tAVG: ${fpsAverage.average.format(2)}\t")
    }

    override fun stop() {
    }

    fun run() {
        runSketch()
    }
}