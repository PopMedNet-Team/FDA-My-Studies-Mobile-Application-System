# My Studies
My Studies is an open-source project to take part in health surveys. ‘My Studies’ is developed using [ResearchStack](https://github.com/ResearchStack/ResearchStack) which is an open-source framework provided by ResearchStack.
My Studies project is FISMA & HIPPA compliant.
# Requirements
My Studies requires Android Studio and can be run on Android versions starting from Kitkat to Pie.
# Backend Server Integration
My Studies fetches all the Studies, Activities, Consent and Resources from the backend, and responses provided by users is stored on the backend.
#### Server
Setup guide for Servers are provided here.
1 - Registration Server stores user information & user’s status for each study and activity.
2 - WCP Server provides the platform to create study, activities, consent, and Resources.
3 - Response Server stores all user’s response to each study activity.
Once you have successfully setup Registration server, WCP server and Response server, replace server URLs in URLs.java
```java
    public static final String BASE_URL_WCP_SERVER = "Your WCP server URL";
    public static final String BASE_URL_REGISTRATION_SERVER = "Your Registration server URL";
    public static final String BASE_URL_RESPONSE_SERVER = "Your Response server URL";
```

# Author
Harvard Pilgrim Health Care Institute (HPHCI)
# License
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.

# Libraries We Used
[EventBus](https://github.com/greenrobot/EventBus)
[butterknife](https://github.com/JakeWharton/butterknife)
[jsoup](https://mvnrepository.com/artifact/org.jsoup/jsoup/1.9.2)
[ResearchStack](https://github.com/ResearchStack/ResearchStack)
[iText](https://github.com/itext/itextpdf/releases/tag/5.5.10)
[AndroidPdfViewer](https://github.com/barteksc/AndroidPdfViewer)
[android-pdfview](https://github.com/JoanZapata/android-pdfview)
[TimePickerWithSeconds](https://github.com/IvanKovac/TimePickerWithSeconds)
[Project Lombok](https://mvnrepository.com/artifact/org.projectlombok/lombok/1.14.8)



