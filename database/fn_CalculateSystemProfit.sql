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
CREATE FUNCTION dbo.fn_CalculateSystemProfit 
(
)
RETURNS DECIMAL(10, 3)
AS
BEGIN
	-- Declare the return variable here
	DECLARE @Profit DECIMAL(10, 3)

	-- Add the T-SQL statements to compute the return value here
	DECLARE @Revenue DECIMAL(10, 3)
	DECLARE @Expenses DECIMAL(10, 3)

	SELECT @Revenue = SUM([Transaction].Amount)
	FROM [Transaction] [Transaction] 
	WHERE [Transaction].Type = 0
	AND (SELECT State FROM [Order] [Order] WHERE [Order].Id = [Transaction].[OrderId]) = 'arrived'

	SELECT @Expenses = SUM(Amount)
	FROM [Transaction]
	WHERE Type = 1

	SET @Profit = @Revenue - @Expenses 

	-- Return the result of the function
	RETURN @Profit

END
GO

DROP FUNCTION fn_CalculateSystemProfit