import daj.*;
import java.util.Random;

/**
 *	Dijkstra-Scholten Algorithm for termination detection
 *	
 *	@author Shraddha
 */
public class Main extends Application
{
	public static final int PROCESS = 15;	//Number of processes

	public static Random random = new Random();	//Randomm Generator

	/**
	 *	@param args Command line arguments.
	 */
	public static void main(String[] args)
	{
		int seed;

		if (args.length >= 1)
		{
			try
			{
				seed = Integer.parseInt(args[0]);
				random = new Random(seed);
			}
			catch(NumberFormatException e)
			{
                                System.out.println("Exception::" + e);
			}
		}
		new Main().run();
	}

	public Main()
	{
		super("Dijkstra-Scholten termination detection algorithm", 400, 400);
	}
	
        @Override
	public void construct()
	{
		Node[] nodes = new Node[PROCESS];
		DijkstraScholtenProgram program;
	/*	int[][] pos = {
			{ 50, 200 },
			{ 100, 150 },
			{ 150, 100 },
			{ 200, 50 },
			{ 300, 50 },
			{ 350, 100 },
                        { 400, 150 },
			{ 450, 200 },
			{ 400, 250 },
			{ 350, 300 },
			{ 300, 350 },
			{ 250, 400 },
                        { 200, 350 },
			{ 150, 300 },
			{ 100, 250 },
                };  */
                int[][] pos = {
			{ 400, 50 },
			{ 200, 150 },
			{ 600, 150 },
			{ 100, 250 },
			{ 300, 250 },
			{ 500, 250 },
                        { 700, 250 },
			{ 50, 350 },
			{ 150, 350 },
			{ 250, 350 },
			{ 350, 350 },
			{ 450, 350 },
                        { 550, 350 },
			{ 650, 350 },
			{ 750, 350 },
                };
		int i, j,v;

		for (i = 0; i < nodes.length; i++)
		{
			program = new AsynchBFSProgram(i);
			program.setNodeId(Integer.toString(i));
			program.initConnectionInfo(nodes.length - 1, nodes.length);	//each node must store info about adjacent nodes 
                 /*       try {
                      //      System.out.println("Hi");
                            nodes[i].wait(program.randInt(250,1000));
                       //     System.out.println("Hello");
                        } 
                        catch (InterruptedException ex) {
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }   */
			nodes[i] = node(program, Integer.toString(i), pos[i][0], pos[i][1]);
		}
		for (i = 0; i < nodes.length - 1; i++)
		{
			for (j = i + 1; j < nodes.length; j++)		// store channel index and node number 
			{
				((DijkstraScholtenProgram)nodes[i].getProgram()).setConnectionInfo(nodes[i].getOut().getSize(), j);
				((DijkstraScholtenProgram)nodes[j].getProgram()).setConnectionInfo(nodes[j].getOut().getSize(), i);
				link(nodes[i], nodes[j]);
				link(nodes[j], nodes[i]);
			}
		}	
		for (i = 0; i < nodes.length; i++)
		{
			((DijkstraScholtenProgram)nodes[i].getProgram()).init();	//Initialization of nodes
		}
	}

	public String getText()
	{
		return "Dijkstra-Scholten termination detection algorithm";
	}

     	
}


