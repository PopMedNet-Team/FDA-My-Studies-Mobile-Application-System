# LabKey mobileAppStudy Module - Implements the Response Server Functionality of the MyStudies System

This custom module provides the Response Server functionality and services, including:

- Processing and storing all mobile app survey and active task responses
- Issuing enrollment tokens to research organizations
- Enrolling and unenrolling participants
- Creating database schemas that match each study's design and updating them as studies change
- Limited querying of data by the mobile app
- Providing web analytics, querying, reporting, and visualizations through manual and programmatic methods

This module must be deployed within the LabKey Server platform (version 19.2.x). To build a standalone distribution of the Response Server (i.e., a standard LabKey Server distribution plus the mobileAppStudy module), follow these steps:

1. Checkout the LabKey Server 19.2.x public Subversion (SVN) and GitHub repositories: [Enlist in the Version Control Project](https://www.labkey.org/Documentation/Archive/19.2/wiki-page.view?name=svn)
1. Navigate to the root of your SVN working copy (the level right above the "server" directory).
1. In your settings.gradle file, find the commented out line with this text:

    ```
    //include ":server:optionalModules:workflow"
    ```

1. Underneath this line, add these two lines:

   ```
   include ":server:optionalModules:mobileAppStudy"
   include ":server:optionalModules:mobileAppStudy:distributions:fda"
   ```

1. On the command line (again, in the root of your working copy), run one of these commands (use the first command on Linux/OSX and the second on Windows):

    ```
    ./gradlew :server:optionalModules:mobileAppStudy:distributions:fda:dist
    gradlew :server:optionalModules:mobileAppStudy:distributions:fda:dist
    ```

1. Look in the directory "dist/mobileAppStudy" for a file whose name ends with "mobileAppStudy-bin.tar.gz". Install this distribution using the [Install LabKey Manually](https://www.labkey.org/Documentation/wiki-page.view?name=manualInstall) instructions.
