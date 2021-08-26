package scenario;

public class TSRAScenario {

	public static final int numberOfOrders = 3;

	public static final int numberOfTasks = 19;

	public static final int numberOfHumanResources = 7;

	public static final int numberOfMachineResources = 5;

	public static final int numberOfSkills = 5;

	public static final double[] orderDeadlines = {200, 180, 300};

	public static final double[] orderPenaltyRate = {30, 60, 50};

	public static final double[] durations = {1, 1, 2, 3, 2, 3, 2, 2, 1, 3, 2, 2, 2, 1, 3, 2, 3, 2, 3};

	public static final double[] scheduledTimes = {0, 1, 2, 4, 7, 9, 9, 11, 13, 12, 15, 17, 19, 22, 23, 26, 28, 28, 31};

	public static final double[] weights = {1.0, 0.5, 1.3, 5, 2, 3, 2.2, 1.6, 1.1, 0.5, 0.5, 3.2};

	public static final double[] humanCosts = {39, 42, 25, 56, 59, 28, 21, 16.5, 19.5, 23, 22};

	public static final double[] machineCosts = {0.43, 0.48, 0.58, 0.47, 0.49};

	public static final int numberOfObjectives = 3;

	public static final int numberOfFitness = 2;

	public static final double maxDuration = 3;

	public static final double maxWeight = 2.6;

	public static final int[][] treq = {
			{ 1,   1,	0,	 0,	  0 },
			{ 1,   1,	0,	 0,	  0 },
			{ 1,   0,	0,	 0,	  0 },
			{ 0,   0,	1,	 0,	  0 },
			{ 0,   0,	0,	 1,	  0 },
			{ 0,   0,	0,	 0,	  1 },
			{ 1,   0,	0,	 0,	  0 },
			{ 1,   0,	0,	 0,	  0 },
			{ 1,   0,	0,	 0,	  0 },
			{ 0,   0,	1,	 0,	  0 },
			{ 0,   0,	0,	 0,	  1 },
			{ 1,   0,	0,	 0,	  0 },
			{ 1,   0,	0,	 0,	  0 },
			{ 1,   1,	0,	 0,	  0 },
			{ 0,   0,	1,	 0,	  0 },
			{ 0,   0,	1,	 0,	  0 },
			{ 0,   0,	0,	 0,	  1 },
			{ 1,   0,	0,	 0,	  0 },
			{ 0,   0,	0,	 0,	  1 }
	};


	public static final double[][] lexp = {
			{ 0.97	,0.86	,0	    ,0		,0	   	},
			{ 0.86	,0.97	,0	    ,0		,0		},
			{ 0	    ,0	    ,0.1	,0.52	,0.9	},
			{ 0	    ,0		,0.5	,0.72	,0.9	},
			{ 0		,0	    ,0.91	,0.91	,0.95	},
			{ 0	    ,0	    ,0.8	,0.52	,0.9	},
			{ 0	    ,0		,0.91	,0.91	,0.95	},
	};

	public static final double[][] mreq = {
			{ 1,   1,	0,  0,	0 },
			{ 1,   1,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 0,   0,	1,  0,	0 },
			{ 0,   0,	0,  1,	0 },
			{ 0,   0,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 0,   0,	1,  0,	0 },
			{ 0,   0,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 1,   1,	0,  0,	0 },
			{ 0,   0,	1,  0,	0 },
			{ 0,   0,	1,  0,	0 },
			{ 0,   0,	0,  0,	0 },
			{ 1,   0,	0,  0,	0 },
			{ 0,   0,	0,  0,	1 }
	};

	/**
	 * task[i][j] = 1 -> task i is precedence of task j
	 * */
	public static final int[][] tasks = {
			{0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0},
	     	{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
	        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
        	{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
	};

}