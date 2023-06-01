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
CREATE FUNCTION fn_CalculateFullPrice
(
	-- Add the parameters for the function here
	@OrderId int
)
RETURNS DECIMAL(10, 3)
AS
BEGIN
	-- Declare the return variable here
	DECLARE @FullPrice DECIMAL(10, 3)

	-- Add the T-SQL statements to compute the return value here
	SELECT @FullPrice = SUM(Article.Price * Item.[Count])
	FROM Item Item, Article Article
	WHERE Item.OrderId = @OrderId AND Item.ArticleId = Article.Id

	-- Return the result of the function
	RETURN @FullPrice

END
GO

DROP FUNCTION fn_CalculateFullPrice
GO