# spock-screen-recorder

Spock Screen Recorder is a simple groovy library, which provides an annotation driven local extension for the [Spock Framework], allowing you to record video of your test execution. 
It is built on top of the [monte-screen-recorder] lib and the Spock extensibility's beauty.

A common use case for this lib is when Spock is glued with [Geb] for an E2E functional testing suit. Here's an [example-geb-spock-screen-recorder] project.

### Example usage

The spock-screen-recorder lib can be found under mavenCentral repository:

```groovy
    repositories {
        mavenCentral()
    }
```

Add the spock-screen-recorder lib to your project dependencies (assuming you're using Gradle as build tool):

```groovy
    // Use this declaration if you don't want to use the spock screen recorder dependencies
    // It avoids affecting your version of Groovy/Spock
    testImplementation('com.github.iskrenyp:spock-screen-recorder:1.0+') 
```

Then create file within the root of you project with name ScreenRecorderConfig.groovy and inside the file add the following line:

```groovy
    defaultRecorder {}
```

This is sufficient enough to use the recorder, either in a Specification level (if you want to record whole specification), or in feature method level (in case you want to screen record test cases):

```groovy
@MakeVideo
class MySpockGebSpec extends GebSpec {}
```
or
```groovy
class MySpockGebSpec extends GebSpec {
    
    @MakeVideo
    def "My recorder test case" () {}
}
```

By providing an empty block for the defaultRecorder, the spock screen recorder will apply all default values for the properties needed to configure the Screen Recorder:

```groovy
    graphicsConfiguration = GraphicsEnvironment
                                    .localGraphicsEnvironment
                                    .defaultScreenDevice
                                    .defaultConfiguration
    captureAre = graphicsConfiguration.bounds
    fileFormat = new Format(MediaTypeKey, FormatKeys.MediaType.FILE, MimeTypeKey, MIME_AVI)
    screenFormat = new Format(
                               MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                               CompressorNameKey, ENCODING_AVI_TECHSMITH_SCREEN_CAPTURE,
                               DepthKey, 24, FrameRateKey, Rational.valueOf(15),
                               QualityKey, 1.0f,
                               KeyFrameIntervalKey, 15 * 60)
    mouseFormat = new Format(MediaTypeKey, FormatKeys.MediaType.VIDEO, EncodingKey, "black", FrameRateKey, Rational.valueOf(30))
    audioFormat = null  // no audio by default
    pathToMovieFolder = 'build/video'
```

In case there's a need to change some of those properties, you can specify them yourself and the rest will be assigned with their default values. You can also define your own, custom recorders, like in the [example]


[Spock Framework]: <http://spockframework.org/spock/docs/1.3/all_in_one.html>
[monte-screen-recorder]: <http://www.randelshofer.ch/monte/>
[Geb]: <https://gebish.org/manual/current/>
[example-geb-spock-screen-recorder]: <https://github.com/iskrenyp/spock-goodies-examples/tree/master/screen-recorder-example>
[example]: <https://github.com/iskrenyp/spock-goodies-examples/blob/master/screen-recorder-example/src/test/resources/ScreenRecorderConfig.groovy#L25-L41>