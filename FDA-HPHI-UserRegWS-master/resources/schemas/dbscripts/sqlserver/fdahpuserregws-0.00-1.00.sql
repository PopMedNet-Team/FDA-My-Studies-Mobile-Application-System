/*
 * Copyright (c) 2015 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- Create schema, tables, indexes, and constraints used for fdahpUserRegWS module here
-- All SQL VIEW definitions should be created in fdahpUserRegWS-create.sql and dropped in fdahpUserRegWS-drop.sql
CREATE SCHEMA fdahpUserRegWS;
GO

/****** Object:  Table [fdahpUserRegWS].[AuthInfo]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [fdahpUserRegWS].[AuthInfo](
	[_ts] [timestamp] NOT NULL,
	[AuthId] [int] IDENTITY(1,1) NOT NULL,
	[ParticipantId] [nvarchar](250) NULL,
	[DeviceToken] [nvarchar](1000) NULL,
	[DeviceType] [nchar](10) NULL,
	[CreatedOn] [datetime] NULL,
	[ModifiedOn] [datetime] NULL,
	[AuthKey] [nvarchar](50) NULL,
	[IosAppVersion] [nvarchar](50) NULL,
	[AndroidAppVersion] [nvarchar](50) NULL,
 CONSTRAINT [PK_AuthInfo] PRIMARY KEY CLUSTERED
(
	[AuthId] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [fdahpUserRegWS].[ParticipantActivities]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [fdahpUserRegWS].[ParticipantActivities](
	[_ts] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ParticipantId] [nvarchar](50) NULL,
	[StudyId] [nvarchar](50) NULL,
	[ActivityId] [nvarchar](50) NULL,
	[ActivityCompleteId] [int] NULL,
	[ActivityType] [nvarchar](50) NULL,
	[Bookmark] [tinyint] NULL,
	[Status] [nvarchar](50) NULL,
	[ActivityVersion] [varchar](max) NULL,
	[ActivityState] [nvarchar](50) NULL,
	[ActivityRunId] [nvarchar](50) NULL,
 CONSTRAINT [PK_ParticipantActivities] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO
/****** Object:  Table [fdahpUserRegWS].[UserDetails]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [fdahpUserRegWS].[UserDetails](
	[_ts] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FirstName] [nvarchar](100) NULL,
	[LastName] [nvarchar](100) NULL,
	[Email] [nvarchar](100) NULL,
	[UsePasscode] [tinyint] NULL,
	[TouchId] [tinyint] NULL,
	[LocalNotificationFlag] [tinyint] NULL,
	[RemoteNotificationFlag] [tinyint] NULL,
	[Status] [int] NULL,
	[Password] [nvarchar](50) NOT NULL,
	[EntityId] [dbo].[ENTITYID] NULL,
	[ReminderTime] [nvarchar](50) NULL,
	[SecurityToken] [nvarchar](50) NULL,
	[UserId] [nvarchar](50) NULL,
	[TempPassword] [tinyint] NULL,
    [Locale] [nvarchar](250) NULL,
 CONSTRAINT [PK_ParticipantDetails] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [fdahpUserRegWS].[ParticipantStudies]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [fdahpUserRegWS].[ParticipantStudies](
	[_ts] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[StudyId] [nvarchar](50) NULL,
	[Status] [varchar](50) NULL,
	[Bookmark] [tinyint] NULL,
	[ConsentStatus] [tinyint] NULL,
	[Consent] [nvarchar](max) NULL,
	[Eligbibility] [tinyint] NULL,
	[ParticipantId] [nvarchar](50) NULL,
	[UserId] [nvarchar](50) NULL,
	[EnrolledDate] [nvarchar](50) NULL,
	[Sharing] [text] NULL,
 CONSTRAINT [PK_ParticipantStudies] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO

CREATE TABLE [fdahpUserRegWS].[StudyConsent]
(
    [_ts] TIMESTAMP NOT NULL,
    [Id] [int] IDENTITY(1,1) NOT NULL,
    [UserId] [int] NULL,
    [StudyId] [nvarchar](50) NULL,
    [Version] [nvarchar](50) NULL,
    [Status] [nvarchar](50) NULL,
    [Pdf]  [nvarchar](max) NULL,
    CONSTRAINT PK_ParticipantStudies PRIMARY KEY CLUSTERED
    (
    [Id] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
     ) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

     GO
     SET ANSI_PADDING OFF
     GO

);

CREATE TABLE [fdahpUserRegWS].[PasswordHistory]
(
    [_ts] TIMESTAMP NOT NULL,
    [Id] [int] IDENTITY(1,1) NOT NULL,
    [UserId] [nvarchar](250) NULL,
    [Password] [nvarchar](250) NULL,
    [Created] [datetime] NULL,
    CONSTRAINT PK_PasswordHistory PRIMARY KEY CLUSTERED
    (
    [Id] ASC
    )WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
     ) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

     GO
     SET ANSI_PADDING OFF
     GO

);