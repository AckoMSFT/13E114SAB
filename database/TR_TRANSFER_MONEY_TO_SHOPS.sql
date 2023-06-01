-- ================================================
-- Template generated from Template Explorer using:
-- Create Trigger (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- See additional Create Trigger templates for more
-- examples of different Trigger statements.
--
-- This block of comments will not be included in
-- the definition of the function.
-- ================================================
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
-- =============================================
-- Author:		<Author,,Name>
-- Create date: <Create Date,,>
-- Description:	<Description,,>
-- =============================================
CREATE TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
   ON [Order]
   AFTER UPDATE
AS 
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for trigger here
	DECLARE @OrderCursor CURSOR
	DECLARE @OrderId INT
	DECLARE @OrderState VARCHAR(100)
	DECLARE @ShopCursor CURSOR
	DECLARE @ShopId INT
	DECLARE @Amount DECIMAL(10, 3)
	DECLARE @ExecutionTime DATETIME

	SET @OrderCursor = CURSOR FOR 
		SELECT Id, State 
		FROM inserted

	OPEN @OrderCursor

	FETCH FROM @OrderCursor
	INTO @OrderId, @OrderState 

	WHILE @@FETCH_STATUS = 0 
	BEGIN
		IF @OrderState = 'arrived'
		BEGIN
			SET @ShopCursor = CURSOR FOR
				SELECT DISTINCT (Article.ShopId)
				FROM Item Item, Article Article 
				WHERE Item.OrderId = @OrderId AND Article.Id = Item.ArticleId

			OPEN @ShopCursor
			
			FETCH FROM @ShopCursor
			INTO @ShopId

			SELECT @ExecutionTime = ReceivedTime 
			FROM [Order]
			WHERE Id = @OrderId

			WHILE @@FETCH_STATUS = 0
			BEGIN
				SELECT @Amount = SUM((Item.[Count] * Article.Price * (100 - Shop.Discount) / 100) * 0.95)
				FROM Item Item, Article Article, Shop Shop
				WHERE Item.OrderId = @OrderId AND Article.Id = Item.ArticleId AND Shop.Id = Article.ShopId AND Shop.Id = @ShopId

				INSERT INTO [dbo].[Transaction] (ExecutionTime, OrderId, ShopId, Amount, Type)
				VALUES (@ExecutionTime, @OrderId, @ShopId, @Amount, 1)

				FETCH FROM @ShopCursor
				INTO @ShopId
			END

			CLOSE @ShopCursor
			DEALLOCATE @ShopCursor
		END

		FETCH FROM @OrderCursor
		INTO @OrderId, @OrderState 
	END

	CLOSE @OrderCursor
	DEALLOCATE @OrderCursor
END
GO

DROP TRIGGER TR_TRANSFER_MONEY_TO_SHOPS
GO

