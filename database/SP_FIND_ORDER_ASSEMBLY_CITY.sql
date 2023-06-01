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
CREATE PROCEDURE SP_FIND_ORDER_ASSEMBLY_CITY 
	-- Add the parameters for the stored procedure here
	@OrderId INT,
	@OrderDestinationCity INT
AS
BEGIN
	-- SET NOCOUNT ON added to prevent extra result sets from
	-- interfering with SELECT statements.
	SET NOCOUNT ON;

    -- Insert statements for procedure here
	SET XACT_ABORT ON
	BEGIN TRAN
		DECLARE @INT_MAX INT 
		SELECT @INT_MAX = POWER(10, 9)

		PRINT 'Creating temp table for Dijkstra algorithm'
		CREATE TABLE #Nodes
		(
			Id INT NOT NULL PRIMARY KEY,
			Distance INT NOT NULL,
			Previous INT NULL,
			Visited BIT NOT NULL
		)

		INSERT INTO #Nodes (Id, Distance, Previous, Visited)
		SELECT Id, @INT_MAX, NULL, 0 FROM [dbo].[City]

		UPDATE #Nodes 
		SET Distance = 0
		WHERE Id = @OrderDestinationCity

		CREATE TABLE #EligibleCities
		(
			Id INT NOT NULL PRIMARY KEY
		)

		INSERT INTO #EligibleCities (Id)
		SELECT DISTINCT CityId 
		FROM [dbo].Shop 

		DECLARE @CurrentNode INT, @CurrentMinimum INT

		WHILE 1 = 1
		BEGIN
			SET @CurrentNode = NULL
			
			SELECT TOP 1 @CurrentNode = Id, @CurrentMinimum = Distance 
			FROM #Nodes 
			WHERE Visited = 0 AND Distance < @INT_MAX
			ORDER BY Distance ASC 

			IF @CurrentNode IS NULL
			BEGIN
				PRINT 'No more nodes to traverse, stopping the algorithm'
				BREAK
			END 

			UPDATE #Nodes 
			SET Visited = 1
			WHERE Id = @CurrentNode 

			UPDATE #Nodes 
			SET Distance = @CurrentMinimum + Edge.Distance, Previous = @CurrentNode
			FROM #Nodes Node
				INNER JOIN [dbo].ConnectedCities Edge
				ON Node.Id = Edge.CityId2 
				WHERE Visited = 0 AND Edge.CityId1 = @CurrentNode 
					AND (@CurrentMinimum + Edge.Distance) < Node.Distance 
		END 

		DECLARE @OrderAssemblyCityId INT
		DECLARE @MinimumDistance INT 

		SELECT @MinimumDistance = MIN(Distance)
		FROM #Nodes Node INNER JOIN #EligibleCities EligibleCity ON Node.Id = EligibleCity.Id

		SELECT @OrderAssemblyCityId = Node.Id 
		FROM #Nodes Node INNER JOIN #EligibleCities EligibleCity ON Node.Id = EligibleCity.Id 
		WHERE Distance = @MinimumDistance

		UPDATE [dbo].[Order]
		SET AssemblyCityId = @OrderAssemblyCityId
		WHERE Id = @OrderId 
		
		DROP TABLE #EligibleCities
		DROP TABLE #Nodes 

	COMMIT TRAN

	RETURN 0
END
GO

DROP PROCEDURE SP_FIND_ORDER_ASSEMBLY_CITY
GO