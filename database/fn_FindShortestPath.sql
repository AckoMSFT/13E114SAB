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
CREATE PROCEDURE SP_FIND_SHORTEST_PATH
(	
	-- Add the parameters for the stored procedure here
	@StartNode INT,
	@EndNode INT,
	@OrderId INT,
	@ShouldUpdateItem BIT 
)
AS
BEGIN 
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
	WHERE Id = @StartNode

	DECLARE @CurrentNode INT, @CurrentMinimum INT

	WHILE 1 = 1
	BEGIN
		SET @CurrentNode = NULL
		
		SELECT TOP 1 @CurrentNode = Id, @CurrentMinimum = Distance 
		FROM #Nodes 
		WHERE Visited = 0 AND Distance < @INT_MAX
		ORDER BY Distance ASC 
		
		IF @CurrentNode IS NULL OR @CurrentNode = @EndNode
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

	DECLARE @Path VARCHAR(8000);
	DECLARE @Distance INT;

	WITH FindPathCTE(Id, Name, Distance, Path, NamePath) 
	AS
	(
		SELECT Node.Id, City.Name, Node.Distance, CAST(Node.Id AS VARCHAR(8000)), CAST(City.Name AS VARCHAR(8000))
		FROM #Nodes Node 
		JOIN [dbo].[City] City ON 
		Node.Id = City.Id 

		UNION ALL

		SELECT Node.Id, 
			City.Name, 
			Node.Distance, 
			CAST(FindPathCTE.Path + ',' + CAST(Node.Id AS VARCHAR(100)) AS VARCHAR(8000)), 
			CAST(FindPathCTE.NamePath + ',' + CAST(City.Name AS VARCHAR(100)) AS VARCHAR(8000))
		FROM #Nodes Node 
		JOIN FindPathCTE FindPathCTE ON Node.Previous = FindPathCTE.Id
		JOIN [dbo].[City] City ON Node.Id = City.Id 
	)


	SELECT @Path = Path, @Distance = Distance 
	FROM FindPathCTE
	WHERE Id = @EndNode

	DROP TABLE #Nodes 

	IF @ShouldUpdateItem = 1 
		BEGIN
			UPDATE [dbo].[Item] 
			SET Path = @Path 
			WHERE OrderId = @OrderId AND
				ArticleId IN (
					SELECT Article.Id
					FROM Article Article, Shop Shop
					WHERE Article.ShopId = Shop.Id and Shop.CityId = @StartNode
				)
		END
	ELSE
		BEGIN
			UPDATE [dbo].[Order] 
			SET Path = @Path 
			WHERE Id = @OrderId 
		END

	RETURN 0
END
GO

DROP PROCEDURE SP_FIND_SHORTEST_PATH