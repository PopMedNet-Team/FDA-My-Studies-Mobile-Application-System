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

-- Create schema, tables, indexes, and constraints used for FdahpUserRegWS module here
-- All SQL VIEW definitions should be created in fdahpuserregws-create.sql and dropped in fdahpuserregws-drop.sql
CREATE SCHEMA fdahpuserregws;
GO

/****** Object:  Table [fdahpuserregws].[AuthInfo]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [fdahpuserregws].[AuthInfo](
	[_ts] [timestamp] NOT NULL,
	[AuthId] [int] IDENTITY(1,1) NOT NULL,
	[ParticipantId] [int] NULL,
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
/****** Object:  Table [fdahpuserregws].[ParticipantActivities]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [fdahpuserregws].[ParticipantActivities](
	[_ts] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ParticipantId] [int] NULL,
	[StudyId] [nvarchar](50) NULL,
	[ActivityId] [int] NULL,
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
/****** Object:  Table [fdahpuserregws].[UserDetails]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [fdahpuserregws].[UserDetails](
	[_ts] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[FirstName] [nvarchar](100) NULL,
	[LastName] [nvarchar](100) NULL,
	[Email] [nvarchar](100) NULL,
	[UsePasscode] [tinyint] NULL,
	[TouchId] [tinyint] NULL,
	[LocalNotificationFlag] [tinyint] NULL,
	[RemoteNotificationFlag] [tinyint] NULL,
	[ReminderFlag] [tinyint] NULL,
	[Status] [int] NULL,
	[Password] [nvarchar](50) NOT NULL,
	[Container] [dbo].[ENTITYID] NULL,
	[EntityId] [dbo].[ENTITYID] NULL,
	[ReminderTime] [nvarchar](50) NULL,
	[SecurityToken] [nvarchar](50) NULL,
	[UserId] [nvarchar](50) NULL,
 CONSTRAINT [PK_ParticipantDetails] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]

GO
/****** Object:  Table [fdahpuserregws].[ParticipantStudies]    Script Date: 2/16/2017 12:46:06 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
SET ANSI_PADDING ON
GO
CREATE TABLE [fdahpuserregws].[ParticipantStudies](
	[_ts] [timestamp] NOT NULL,
	[Id] [int] IDENTITY(1,1) NOT NULL,
	[ParticipantId] [int] NULL,
	[StudyId] [nvarchar](50) NULL,
	[Status] [varchar](50) NULL,
	[Bookmark] [tinyint] NULL,
	[ConsentStatus] [tinyint] NULL,
	[Consent] [nvarchar](max) NULL,
	[Eligbibility] [tinyint] NULL,
	[AppToken] [nvarchar](50) NULL,
	[UserId] [int] NULL,
	[EnrolledDate] [nvarchar](50) NULL,
 CONSTRAINT [PK_ParticipantStudies] PRIMARY KEY CLUSTERED
(
	[Id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]

GO
SET ANSI_PADDING OFF
GO

CREATE TABLE [fdahpuserregws].[StudyConsent]
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