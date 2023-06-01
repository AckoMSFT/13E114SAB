-- ================================================
-- Template generated from Template Explorer using:
-- Create Procedure (New Menu).SQL
--
-- Use the Specify Values for Template Parameters 
-- command (Ctrl-Shift-M) to fill in the parameter 
-- values below.
--
-- This block of comments will not be included in
-- the definition of the procedure.
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
CREATE PROCEDURE SP_FINAL_PRICE 
	-- Add the parameters for the stored procedure here
	@OrderId INT,
	@FinalPrice DECIMAL(10, 3) OUTPUT,
	@CurrentTime DATETIME
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;
	
	-- Insert statements for procedure here
	DECLARE @FullPrice DECIMAL(10, 3)
	DECLARE @Discount DECIMAL(10, 3)

	SET @FullPrice = dbo.fn_CalculateFullPrice(@OrderId)
	SET @Discount = dbo.fn_CalculateDiscount(@OrderId, @CurrentTime)
	SELECT @FinalPrice = @FullPrice - @Discount

	RETURN
END
GO

DROP PROCEDURE SP_FINAL_PRICE
GO