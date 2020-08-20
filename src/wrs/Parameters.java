package wrs;


public class Parameters {
	public boolean bTheory; // Calculate Theoretical Probabilities
	public boolean bD; // Calculate For Algorithm D
	public boolean bAcRe; // Calculate For Algorithm AcRe
	public boolean bAcRe2; // Calculate For Algorithm AcRe2
	public boolean bES; // Calculate For Algorithm ES (algorithm A of IPL) with a Reservoir
	public boolean bESJ; // Calculate For Algorithm ESJ (algorithm A with J) with Exponential Jumps
	public boolean bChao; // Calculate for Algorithm Chao (prob prop to weight)
	public boolean bVerbose; // Print Info ...
	public boolean bStats; // Keeps Stats
	public boolean bComparison; // Compare Statistics ...
	public boolean bBatch; // Output number in a tabular form

	public int nLoops; // Num of Loops
	public int nSize; // Population Size;
	public int nSample; // Sample Size
	public EnumWeightsType nWType; // Weight Type : Random, Decreasing, ....

	public long seed; // seed for random number generator
	
	public Parameters() {
		bTheory = false;
		bD = false;
		bAcRe = true;
		bAcRe2 = false;
		bES = true;
		bESJ = true;
		bChao = false;
		bVerbose = false;
		bStats = false;
		nLoops = 1;
		nSize = 3;
		nSample = 2;
		// nWType = 0; // Random
		// nWType = 1; // UNIFORM
		// nWType = 2; // Decreasing weights
		// nWType = 3; // User Input
		nWType = EnumWeightsType.RANDOM;
		bComparison = false;
		bBatch = false;
		seed = 103; 
	}

	public String WTypeName() {
		String str;
		switch (this.nWType) {
		case USER:
			str = "UserInput";
			break;
		case INCREASING:
			str = "Increasing: 1,2,...";
			break;
		case DECREASING:
			str = "Decreasing: n,n-1,n-2,...";
			break;
		case UNIFORM:
			str = "Uniform: weight=1";
			break;
		case RANDOM:
		default:
			str = "Uniform-Random: 1 <= weight <= n+1";
			break;
		}
		return str;
	}

	public void Process(String[] args) throws Exception {
		/*
		 * switch (args.length) { case 9: bD = (args[8].equals("-D")); case 8:
		 * bVerbose = (args[7].equals("-v")); case 7: bTheory =
		 * (args[6].equals("-t")); case 6: bStats = bComparison =
		 * (args[5].equals("-c")); case 5: bBatch = (args[4].equals("-batch"));
		 * case 4: nLoops = Integer.parseInt(args[3]); case 3: nSample =
		 * Integer.parseInt(args[2]); case 2: nSize = Integer.parseInt(args[1]);
		 * case 1: nWType = Integer.parseInt(args[0]); break; }
		 */
		int len = args.length;
		if (len <= 0) {
			System.out.println(Usage());
			System.exit(0);
		}

		int cur = 0;
		while (cur < len) {
			String arg = args[cur];
			if (arg.compareTo("-h") == 0) {
				System.out.println(Help());
				System.exit(0);
			} else if (arg.compareTo("-version") == 0) {
				System.out.println("Version: " + Version());
				System.exit(0);
			} else if (arg.compareTo("-batch") == 0) {
				bBatch = true;
				cur++;
			} else if (arg.compareTo("-theory") == 0) {
				bTheory = true;
				cur++;
			} else if (arg.compareTo("-compare") == 0) {
				bComparison = true;
				cur++;
			} else if (arg.compareTo("-verbose") == 0) {
				bVerbose = true;
				cur++;
			} else if (arg.compareTo("-stats") == 0) {
				bStats = true;
				cur++;
			} else if (arg.compareTo("-W") == 0) {
				nWType = ProcessEnumWeightsType(args, cur);
				cur += 2;
			} else if (arg.compareTo("-n") == 0) {
				nSize = ProcessInt(args, cur);
				cur += 2;
			} else if (arg.compareTo("-m") == 0) {
				nSample = ProcessInt(args, cur);
				cur += 2;
			} else if (arg.compareTo("-L") == 0) {
				nLoops = ProcessInt(args, cur);
				cur += 2;
			} else if (arg.compareTo("-D") == 0) {
				bD = (ProcessInt(args, cur) > 0);
				cur += 2;
			} else if (arg.compareTo("-AcRe") == 0) {
				bAcRe = (ProcessInt(args, cur) > 0);
				cur += 2;
			} else if (arg.compareTo("-AcRe2Beta") == 0) {
				bAcRe2 = (ProcessInt(args, cur) > 0);
				cur += 2;
			} else if (arg.compareTo("-A") == 0 || arg.compareTo("-AES") == 0) {
				bES = (ProcessInt(args, cur) > 0);
				cur += 2;
			} else if (arg.compareTo("-AJ") == 0 || arg.compareTo("-AESJ") == 0) {
				bESJ = (ProcessInt(args, cur) > 0);
				cur += 2;
			} else if (arg.compareTo("-Chao") == 0) {
				bChao = (ProcessInt(args, cur) > 0);
				cur += 2;
			} else if (arg.compareTo("-seed") == 0) {
				seed = ProcessLong(args, cur);
				cur += 2;
			} else if (arg.compareTo("-compare") == 0) {
				bComparison = true;
				cur++;
			} else {
				throw new Exception("Invalid Argument: " + args[cur]);
			}
		}
	}

	int ProcessInt(String[] args, int cur) throws Exception {
		int len = args.length;
		int value = 0;
		if ((cur + 1) >= len) {
			// Arg Value Missing
			throw new Exception("Arg Value Missing for parameter: " + args[cur]);
		}
		value = (new Integer(args[cur + 1])).intValue();
		return value;
	}

	long ProcessLong(String[] args, int cur) throws Exception {
		int len = args.length;
		long value = 0;
		if ((cur + 1) >= len) {
			// Arg Value Missing
			throw new Exception("Arg Value Missing for parameter: " + args[cur]);
		}
		value = (new Long(args[cur + 1])).longValue();
		return value;
	}
	
	EnumWeightsType ProcessEnumWeightsType(String[] args, int cur) throws Exception {
		int len = args.length;
		EnumWeightsType value = EnumWeightsType.RANDOM;
		if ((cur + 1) >= len) {
			// Arg Value Missing
			throw new Exception("Arg Value Missing for parameter: " + args[cur]);
		}
		value = EnumWeightsType.valueOf(args[cur + 1]);
		return value;
	}
	
	public String Usage() {
		String usage = "Usage: <application> [-h] [-W weight-type] [-n Population]"
				+ " [-m sample] [-L loops] [-batch] [-theory] [-D 0=No/1=Yes] [-AcRe 0/1] [-A 0/1] [-AJ 0/1]"
				+ "\n\nFOR MORE INFORMATION: <application> -h";
		return usage;
	}

	public String Version() {
		String strVersion = "0.5 (alpha status - preliminary release)";
		return strVersion;
	}

	public String Help() {
		String strHelp = "Usage: <application> [-h] [-version] [-W weight-type] [-n Population]"
				+ " [-m sample] [-L loops] [-batch] [-theory] [-D 0=No/1=Yes] [-AcRe 0/1] [-A 0/1] [-AJ 0/1]"
				+ "\nARGUMENTS:"
				+ "\n\t[-h] : This Information"
				+ "\n\t[-version] : Version Information"
				+ "\n\t[-W weight-type] : RANDOM, UNIFORM, DECREASING, INCREASING, USER "
				+ "\n\t\t -W RANDOM  : Random Weights uniformly distributed in [1,n+1]"
				+ "\n\t\t -W UNIFORM  : Uniform Random Sampling: All weights are set to 1"
				+ "\n\t\t -W DECREASING  : Decreasing Weights: n, n-1, n-2, .., 1"
				+ "\n\t\t -W INCREASING  : Increasing Weights: 1, 2, .., n-1, n"
				+ "\n\t\t -W USER  : Weights given from Console User Input"
				+ "\n\t[-n Population] : number of Items in the population"
				+ "\n\t[-m sample] : the size of the sample"
				+ "\n\t[-L loops] : number of loops for the algorithm"
				+ "\n\t[-batch] : compact output for batch execution of the program"
				+ "\n\t\tThe columns of the batch output are: "
				+ "\n\t\t<hostname> <WeightType> <Population Size> <Sample Size> <Loops> "
				+ "\n\t\t<Time for Algorithm D> <Time for Alg Ac/Re> <Time for Alg A-Reservoir> <Time for Alg A-Jumps> "
				+ "\n\t\t<Number of Reservoir Insertions for A-Reservoir> <Number of Reservoir Insertions for A-Jumps>"
				+ "\n\t[-theory] : calculate theoretical probabilities (only for small problem instances)"
				+ "\n\t[-D 0=no/1=yes] : calculate with algorithm D (definition of WRS)"
				+ "\n\t[-AcRe 0=no/1=yes] : calculate with algorithm AcRe (In this version the Population Order is preserved)"
				+ "\n\t[-ES 0=no/1=yes] : calculate with algorithm ES with a Reservoir"
				+ "\n\t[-ESJ 0=no/1=yes] : calculate with algorithm ES with Jumps"
				+ "\n\t[-seed <long>] : the seed for the random number generator";
		return strHelp;
	}
	
	public String toString() {

		String str = DateUtils.now(DateUtils.DATE_FORMAT_NOW)
			+ "\nParameter Values ----------" 
			+ "\n\tweight-type: " + nWType
			+ "\n\tseed: " + seed
			+ "\n\tpopulation: " + nSize
			+ "\n\tsample: " + nSample
			+ "\n\tloops: " + nLoops
			+ "\n\tbatch]: " + bBatch
			+ "\n\ttheory: " + bTheory
			+ "\n\tD: " + bD
			+ "\n\tAcRe: " + bAcRe
			+ "\n\tES: " + bES
			+ "\n\tESJ: " + bESJ;
		return str;
	}
}