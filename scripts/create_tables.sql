DECLARE @Sql NVARCHAR(500) DECLARE @Cursor CURSOR

SET @Cursor = CURSOR FAST_FORWARD FOR
    SELECT DISTINCT sql = 'ALTER TABLE [' + tc2.TABLE_SCHEMA + '].[' +  tc2.TABLE_NAME + '] DROP [' + rc1.CONSTRAINT_NAME + '];'
    FROM INFORMATION_SCHEMA.REFERENTIAL_CONSTRAINTS rc1
             LEFT JOIN INFORMATION_SCHEMA.TABLE_CONSTRAINTS tc2 ON tc2.CONSTRAINT_NAME =rc1.CONSTRAINT_NAME

OPEN @Cursor FETCH NEXT FROM @Cursor INTO @Sql

WHILE (@@FETCH_STATUS = 0)
    BEGIN
        Exec sp_executesql @Sql
        FETCH NEXT FROM @Cursor INTO @Sql
    END

CLOSE @Cursor DEALLOCATE @Cursor
GO

EXEC sp_MSforeachtable 'DROP TABLE ?'
GO
CREATE TABLE [Address]
(
 [id]         int NOT NULL IDENTITY (1,1),
 [country]    varchar(120) NOT NULL ,
 [region]     varchar(120) NOT NULL ,
 [zipcode]    varchar(12) NOT NULL ,
 [city]       varchar(120) NOT NULL ,
 [street]     varchar(120) NOT NULL ,
 [number]     varchar(12) NOT NULL ,
 [supplement] varchar(12) NOT NULL ,


 CONSTRAINT [PK_Address] PRIMARY KEY CLUSTERED ([id] ASC)
);
GO

CREATE TABLE [Provider]
(
 [id]        int NOT NULL IDENTITY (1,1),
 [name]      varchar(120) NOT NULL ,
 [email]     varchar(256) NOT NULL ,
 [available] bit NOT NULL ,
 [addressID] int NOT NULL ,


 CONSTRAINT [PK_Provider] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_37] FOREIGN KEY ([addressID])  REFERENCES [Address]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [Store]
(
 [id]        int NOT NULL IDENTITY (1,1),
 [name]      varchar(120) NOT NULL UNIQUE ,
 [email]     varchar(256) NOT NULL UNIQUE ,
 [password]  varchar(256) NOT NULL ,
 [addressID] int NOT NULL ,


 CONSTRAINT [PK_Store] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_122] FOREIGN KEY ([addressID])  REFERENCES [Address]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [Warehouse]
(
 [id]        int NOT NULL IDENTITY (1,1),
 [name]      varchar(120) NOT NULL UNIQUE ,
 [email]     varchar(256) NOT NULL UNIQUE ,
 [password]  varchar(256) NOT NULL ,
 [addressID] int NOT NULL ,


 CONSTRAINT [PK_Warehouse] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_48] FOREIGN KEY ([addressID])  REFERENCES [Address]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [Product]
(
 [id]     int NOT NULL IDENTITY (1,1),
 [name]   varchar(120) NOT NULL ,
 [weight] real NOT NULL ,
 [price]  money NOT NULL ,


 CONSTRAINT [PK_Product] PRIMARY KEY CLUSTERED ([id] ASC)
);
GO

CREATE TABLE [Stock]
(
 [warehouseID] int NOT NULL ,
 [productID]   int NOT NULL ,
 [quantity]    int NOT NULL ,
 [minQuantity] int NOT NULL ,


 CONSTRAINT [PK_Stock] PRIMARY KEY CLUSTERED ([warehouseID] ASC, [productID] ASC),
 CONSTRAINT [FK_108] FOREIGN KEY ([warehouseID])  REFERENCES [Warehouse]([id]) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT [FK_111] FOREIGN KEY ([productID])  REFERENCES [Product]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [Order]
(
 [id]          int NOT NULL IDENTITY (1,1),
 [storeID]     int NOT NULL ,
 [warehouseID] int NOT NULL ,
 [status]      varchar(32) not null ,
 [date]        datetime2(7) NOT NULL ,


 CONSTRAINT [PK_Order] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_128] FOREIGN KEY ([storeID])  REFERENCES [Store]([id]) ON UPDATE CASCADE,
 CONSTRAINT [FK_131] FOREIGN KEY ([warehouseID])  REFERENCES [Warehouse]([id]) ON UPDATE NO ACTION
);
GO

CREATE TABLE [OrderRevision]
(
 [id]      int NOT NULL IDENTITY (1,1),
 [orderID] int NOT NULL ,
 [status]  varchar(32) not null ,
 [date]    datetime2(7) NOT NULL ,
 [note]    text NOT NULL ,


 CONSTRAINT [PK_OrderStatus] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_137] FOREIGN KEY ([orderID])  REFERENCES [Order]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [OrderItem]
(
    [orderID]   int null ,
    [orderRevisionID] int null ,
    [quantity]  int NOT NULL ,
    [unitPrice] money not null ,
    [productID] int NOT NULL ,


    CONSTRAINT [FK_145] FOREIGN KEY ([orderID])  REFERENCES [Order]([id]) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT [FK_149] FOREIGN KEY ([productID])  REFERENCES [Product]([id]) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT [FK_150] foreign key ([orderRevisionID]) references [OrderRevision]([id]) on DELETE no action on UPDATE no action
);
GO

CREATE TABLE [StoreStockReport]
(
 [id]      int NOT NULL IDENTITY (1,1),
 [storeID] int NOT NULL ,
 [date]    datetime2(7) NOT NULL ,
 [note]    varchar(256) NOT NULL ,


 CONSTRAINT [PK_StoreStockReport] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_156] FOREIGN KEY ([storeID])  REFERENCES [Store]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO
CREATE NONCLUSTERED INDEX [fkIdx_156] ON StoreStockReport
 (
  [storeID] ASC
 );
GO

CREATE TABLE [StoreStockReportItem]
(
 [storeStockReportID] int NOT NULL ,
 [quantity]      int NOT NULL ,
 [productID]     int NOT NULL ,


 CONSTRAINT [PK_StoreStockReportItem] PRIMARY KEY CLUSTERED ([storeStockReportID] ASC, [productID] ASC),
 CONSTRAINT [FK_162] FOREIGN KEY ([storeStockReportID])  REFERENCES StoreStockReport([id]) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT [FK_166] FOREIGN KEY ([productID])  REFERENCES [Product]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [WarehouseOrder]
(
 [id]          int NOT NULL IDENTITY (1,1),
 [date]        datetime2(7) NOT NULL ,
 [status]      varchar NOT NULL ,
 [providerID]  int NOT NULL ,
 [warehouseID] int NOT NULL ,


 CONSTRAINT [PK_WarehouseOrder] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_59] FOREIGN KEY ([providerID])  REFERENCES [Provider]([id]) ON UPDATE CASCADE,
 CONSTRAINT [FK_62] FOREIGN KEY ([warehouseID])  REFERENCES [Warehouse]([id]) ON UPDATE NO ACTION
);
GO

CREATE TABLE [WarehouseOrderItem]
(
 [orderID]   int NOT NULL ,
 [productID] int NOT NULL ,
 [quantity]  int NOT NULL ,
 [unitPrice] money NOT NULL ,


 CONSTRAINT [PK_WarehouseOrderItem] PRIMARY KEY CLUSTERED ([orderID] ASC, [productID] ASC),
 CONSTRAINT [FK_92] FOREIGN KEY ([orderID])  REFERENCES [WarehouseOrder]([id]) ON DELETE CASCADE ON UPDATE CASCADE,
 CONSTRAINT [FK_96] FOREIGN KEY ([productID])  REFERENCES [Product]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO

CREATE TABLE [WarehouseOrderStatus]
(
 [id]      int NOT NULL IDENTITY (1,1),
 [orderID] int NOT NULL ,
 [date]    datetime2(7) NOT NULL ,
 [note]    varchar(32) NOT NULL ,


 CONSTRAINT [PK_WarehouseOrderStatus] PRIMARY KEY CLUSTERED ([id] ASC),
 CONSTRAINT [FK_68] FOREIGN KEY ([orderID])  REFERENCES [WarehouseOrder]([id]) ON DELETE CASCADE ON UPDATE CASCADE
);
GO