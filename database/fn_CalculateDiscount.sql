-- ================================================
-- Template generated from Template Explorer using:
-- Create Scalar Function (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
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
-- Create date: <Create Date, ,>
-- Description:	<Description, ,>
-- =============================================
CREATE FUNCTION fn_CalculateDiscount
(
	-- Add the parameters for the function here
	@OrderId INT,
	@CurrentTime DATETIME
)
RETURNS DECIMAL(10, 3)
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Discount INT

	-- Add the T-SQL statements to compute the return value here
	DECLARE @ItemsCursor CURSOR 
	DECLARE @ItemId INT
	DECLARE @ArticleId INT
	DECLARE @ArticleCount INT
	DECLARE @Price INT
	DECLARE @ShopId INT
	DECLARE @ShopDiscount INT

	SET @ItemsCursor = CURSOR FOR
		SELECT Id, ArticleId, [Count]
		FROM Item 
		WHERE OrderId = @OrderId

	OPEN @ItemsCursor

	FETCH FROM @ItemsCursor
	INTO @ItemId, @ArticleId, @ArticleCount

	SET @Discount = 0

	WHILE @@FETCH_STATUS = 0
	BEGIN
		SELECT @Price = Price, @ShopId = ShopId 
		FROM Article 
		WHERE Id = @ArticleId 

		SELECT @ShopDiscount = Discount
		FROM Shop 
		WHERE Id = @ShopId 

		SET @Discount = @Discount + @ArticleCount * @Price * @ShopDiscount / 100 

		FETCH FROM @ItemsCursor
		INTO @ItemId, @ArticleId, @ArticleCount
	END

	CLOSE @ItemsCursor
	DEALLOCATE @ItemsCursor

	DECLARE @FullPrice DECIMAL(10, 3)

	SELECT @FullPrice = SUM(Article.Price * Item.[Count])
	FROM Item Item, Article Article
	WHERE Item.OrderId = @OrderId AND Item.ArticleId = Article.Id

	DECLARE @BuyerId INT 
	
	SELECT @BuyerId = BuyerId
	FROM [Order]
	WHERE Id = @OrderId

	DECLARE @TotalSpent DECIMAL(10, 3)

	SELECT @TotalSpent = SUM([Transaction].Amount) 
	FROM [Transaction], [Order]
	WHERE [Transaction].OrderId = [Order].Id AND [Order].BuyerId = @BuyerId AND [Transaction].[Type] = 0
		AND DATEDIFF(day, [Transaction].ExecutionTime, @CurrentTime) BETWEEN 0 AND 30

	IF @TotalSpent > 10000
	BEGIN
		SET @Discount = @Discount + 0.02 * @FullPrice
	END

	-- Return the result of the function
	RETURN @Discount

END
GO

DROP FUNCTION fn_CalculateDiscount
GO
