package common;

public class TSRAParams {

	public static final int numberOfTasks = 12;

	public static final int numberOfResources = 11;

	public static final int numberOfSkills = 10;

	public static final double[] durations = {24, 26, 20, 12, 15, 18, 15, 15, 20, 27, 10, 18};

	public static final double[] scheduledTimes = {26, 0, 50, 70, 82, 52, 107, 97, 60, 23, 112, 32};

	public static final int numberOfObjectives = 3;

	public static final int numberOfFitness = 2;

	public static final int maxDuration = 27;

	public static final int[][] treq = {
			{ 0,   0,	1,	 0,	  0,	0,	  0,	0,	 0,	  0 },
			{ 0,   1,	1,	 0,	  0,	1,	  1,	0,	 1,	  0 },
			{ 0,   0,	1,	 1,	  0,	0,	  0,	0,	 0,	  0 },
			{ 1,   0,	0,	 0,	  1,	0,	  1,	0,	 0,	  0 },
			{ 0,   0,	1,	 0,	  0,	0,	  0,	0,	 0,	  0 },
			{ 0,   1,	0,	 0,	  0,	0,	  0,	0,	 0,	  1 },
			{ 0,   1,	1,	 1,	  0,	0,	  0,	1,	 0,	  0 },
			{ 1,   0,	1,	 1,	  0,	1,	  0,	1,	 1,	  0 },
			{ 0,   1,	1,	 0,	  0,	1,	  0,	0,	 0,	  0 },
			{ 0,   0,	1,	 0,	  0,	0,	  0,	0,	 0,	  1 },
			{ 1,   0,	1,	 1,	  0,	0,	  1,	0,	 0,	  1 },
			{ 0,   0,	1,	 0,	  0,	1,	  0,	1,	 1,	  0 }
	};

	public static final double[][] lexp = {
			{ 0.15	,0.86	,0	    ,0.26	,0	    ,0.07	,0	    ,0	    ,0       ,0.03 },
			{ 0.97	,0	    ,0	    ,0	    ,0.15	,0	    ,0	    ,0.01	,0       ,0.05 },
			{ 0	    ,0	    ,0	    ,0.11	,0	    ,0.17	,0	    ,0	    ,0.62	 ,0.65 },
			{ 0	    ,0.53	,0.73	,0	    ,0	    ,0	    ,0.6	,0.4	,0.63	 ,0.9  },
			{ 0.28	,0	    ,0	    ,0	    ,0	    ,0	    ,0	    ,0.83	,0       ,0    },
			{ 0	    ,0	    ,0.77	,0.27	,0	    ,0	    ,0	    ,0	    ,0       ,0    },
			{ 0	    ,0.66	,0.19	,0.58	,0	    ,0.91	,0	    ,0.85	,0.63	 ,0    },
			{ 0	    ,0	    ,0	    ,0	    ,0.79	,0	    ,0.43	,0	    ,0.29	 ,0.22 },
			{ 0.53	,0.73	,0	    ,0.81	,0.18	,0.01	,0	    ,0.31	,0	     ,0    },
			{ 0	    ,0	    ,0	    ,0	    ,0	    ,0.97	,0	    ,0 	    ,0.96	 ,0.75 },
			{ 0	    ,0	    ,0.36	,0.35	,0	    ,0.09	,0.49	,0 	    ,0.15	 ,0.5  }
	};

	public static final int[][] tasks = {
			{0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0},
			{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 1, 0, 1, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0},
			{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0},
			{0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
			{0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0}
	};

	public static final double[] baseSalary = {20, 25, 20, 25, 25, 25, 35, 20, 20, 20, 25};
}
