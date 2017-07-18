## Glasses SDK Libraries

This folder contains the glasses SDK libraries.

### evsBase.aar

The main SDK library, contains all main functionality required for developing glasses application.

### evsUICarousel.aar

This library enables opening the menu Carousel control to enable easy interaction with the user via the touch slider.

### Referencing the SDK files

You should add a referece to the libraries in your _build.gradle_ file.

```gradle
repositories {
    mavenCentral()
    flatDir {
        //replace with your Raptor SDK libraries folder
        dirs '../../libs'
    }
}

dependencies {
    compile(name: 'evsBase', ext: 'aar')
    compile(name: 'evsUICarousel', ext: 'aar')
}
```
