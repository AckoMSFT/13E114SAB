
CREATE TABLE [Article]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NULL ,
	[Price]              decimal(10,3)  NULL ,
	[ShopId]             integer  NULL ,
	[Count]              integer  NULL 
	CONSTRAINT [DefaultValueZero_1980233043]
		 DEFAULT  0
)
go

CREATE TABLE [Buyer]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL ,
	[Credit]             decimal(10,3)  NOT NULL 
	CONSTRAINT [DefaultValueZero_1086121196]
		 DEFAULT  0,
	[CityId]             integer  NULL 
)
go

CREATE TABLE [City]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Name]               nvarchar(100)  NOT NULL 
)
go

CREATE TABLE [ConnectedCities]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[CityId1]            integer  NULL ,
	[CityId2]            integer  NULL ,
	[Distance]           integer  NOT NULL 
)
go

CREATE TABLE [Item]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[OrderId]            integer  NULL ,
	[ArticleId]          integer  NULL ,
	[Path]               varchar(8000)  NULL ,
	[Count]              integer  NULL 
)
go

CREATE TABLE [Order]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[BuyerId]            integer  NULL ,
	[AssemblyCityId]     integer  NULL ,
	[Path]               varchar(8000)  NULL ,
	[State]              nvarchar(100)  NULL 
	CONSTRAINT [ValidOrderStateConstraint]
		CHECK  ( [State]='created' OR [State]='sent' OR [State]='arrived' ),
	[SentTime]           datetime  NULL ,
	[ReceivedTime]       datetime  NULL 
)
go

CREATE TABLE [Shop]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[CityId]             integer  NULL ,
	[Name]               nvarchar(100)  NULL ,
	[Discount]           integer  NOT NULL 
	CONSTRAINT [DefaultValueZero_2095780820]
		 DEFAULT  0
)
go

CREATE TABLE [Transaction]
( 
	[BuyerId]            integer  NULL ,
	[ExecutionTime]      datetime  NULL ,
	[Id]                 integer  IDENTITY  NOT NULL ,
	[Amount]             decimal(10,3)  NULL ,
	[ShopId]             integer  NULL ,
	[Type]               integer  NULL 
	CONSTRAINT [ValidTransactionType_699847578]
		CHECK  ( [Type]=0 OR [Type]=1 ),
	[OrderId]            integer  NULL 
)
go

CREATE TABLE [Transit]
( 
	[Id]                 integer  IDENTITY  NOT NULL ,
	[OrderId]            integer  NULL ,
	[ItemId]             integer  NULL ,
	[EdgeId]             integer  NULL ,
	[DaysLeft]           integer  NULL ,
	[Type]               integer  NULL 
	CONSTRAINT [ValidTransitType_1088685279]
		CHECK  ( [Type]=0 OR [Type]=1 )
)
go

ALTER TABLE [Article]
	ADD CONSTRAINT [XPKArticle] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [Buyer]
	ADD CONSTRAINT [XPKBuyer] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XPKCity] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [City]
	ADD CONSTRAINT [XAK1CityUniqueName] UNIQUE ([Name]  ASC)
go

ALTER TABLE [ConnectedCities]
	ADD CONSTRAINT [XPKConnectedCities] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [ConnectedCities]
	ADD CONSTRAINT [XAK1ConnectedCitiesUniqueEdge] UNIQUE ([CityId1]  ASC,[CityId2]  ASC)
go

ALTER TABLE [Item]
	ADD CONSTRAINT [XPKItem] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [Order]
	ADD CONSTRAINT [XPKOrder] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XPKShop] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [Shop]
	ADD CONSTRAINT [XAK1ShopUniqueName] UNIQUE ([Name]  ASC)
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [XPKTransaction] PRIMARY KEY  CLUSTERED ([Id] ASC)
go

ALTER TABLE [Transit]
	ADD CONSTRAINT [XPKTransit] PRIMARY KEY  CLUSTERED ([Id] ASC)
go


ALTER TABLE [Article]
	ADD CONSTRAINT [R_6] FOREIGN KEY ([ShopId]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Buyer]
	ADD CONSTRAINT [R_3] FOREIGN KEY ([CityId]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [ConnectedCities]
	ADD CONSTRAINT [R_1] FOREIGN KEY ([CityId1]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [ConnectedCities]
	ADD CONSTRAINT [R_2] FOREIGN KEY ([CityId2]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Item]
	ADD CONSTRAINT [R_8] FOREIGN KEY ([OrderId]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Item]
	ADD CONSTRAINT [R_9] FOREIGN KEY ([ArticleId]) REFERENCES [Article]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Order]
	ADD CONSTRAINT [R_4] FOREIGN KEY ([BuyerId]) REFERENCES [Buyer]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Order]
	ADD CONSTRAINT [R_7] FOREIGN KEY ([AssemblyCityId]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Shop]
	ADD CONSTRAINT [R_5] FOREIGN KEY ([CityId]) REFERENCES [City]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_13] FOREIGN KEY ([BuyerId]) REFERENCES [Buyer]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_16] FOREIGN KEY ([ShopId]) REFERENCES [Shop]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transaction]
	ADD CONSTRAINT [R_20] FOREIGN KEY ([OrderId]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go


ALTER TABLE [Transit]
	ADD CONSTRAINT [R_17] FOREIGN KEY ([OrderId]) REFERENCES [Order]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transit]
	ADD CONSTRAINT [R_18] FOREIGN KEY ([ItemId]) REFERENCES [Item]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go

ALTER TABLE [Transit]
	ADD CONSTRAINT [R_19] FOREIGN KEY ([EdgeId]) REFERENCES [ConnectedCities]([Id])
		ON DELETE NO ACTION
		ON UPDATE NO ACTION
go
