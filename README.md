# Epson Connect Java SDK
The Epson Connect Java SDK provides a comprehensive interface to the Epson Connect API. With this SDK, Java developers can seamlessly control Epson printers and scanners through the Epson cloud service.

## Getting Started
### Installation
To include the SDK in your Gradle project, add the following in your `build.gradle`:

```
repositories {
    maven { url 'https://jitpack.io' }
}
dependencies {
    implementation 'com.github.arafatkatze:epson-connect-java:v1.0.1'
}
```
## Prerequisites
Make sure you have the required credentials:

- Printer Email
- Client ID
- Client Secret

These can be obtained from the Epson Connect API registration portal.

## Usage

You can initialize the client using direct parameters:

```
import com.epsonconnectjava.Client;
import com.epsonconnectjava.Scanner;

import java.io.IOException;

public class EpsonPrinter {
  public static void main(String[] args) throws IOException {
	Client client = new Client("printer_email", "client_id", "client_secret");
	Scanner scanme = client.getScanner();
	Printer printer = client.getPrinter();
	System.out.println(scanme.list());
	System.out.println(printer.deviceId());
  }
}
```

## Printing
```
Printer printFile = client.getPrinter();
printFile.print("file_path.pdf");
``` 
For printing the following file extensions are supported 
```
doc
docx
xls
xlsx
ppt
pptx
pdf
jpeg
jpg
bmp
gif
png
tiff
```

## Scanning 
```
Scanner scanme = client.getScanner();
System.out.println(scanme.list());
```

## Testing the library

```
git clone git@github.com:arafatkatze/epson-connect-java.git
./gradelw build
./gradlew test

```

## Local Build
```
git clone git@github.com:arafatkatze/epson-connect-java.git
./gradlew build
```

To publish to JitPack:
Ensure you have the required configurations set up in your build.gradle and then create a new release on your GitHub repository. JitPack will automatically detect and build your library.

