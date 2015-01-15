import daj.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
/**
 *	Dijkstra-Scholten Algorithm
 *
 *	@author Shraddha
 */
abstract class DijkstraScholtenProgram extends Program
{
	
	protected static final int NOPARENT = -1;	// No parent

	
	protected static final int IDLE = 0;	//Idle State

	
	protected static final int SOURCE = 1;		//Source State

	
	protected static final int ACTIVE = 2;	// Active state

	
	protected static final String[] STATUSDESCRIPTION = {		//State description 
		new String("idle"),
		new String("source"),
		new String("active")
	};

	protected int[] channelMatrix;		// mapping of channel index

	protected int[] programMatrix;		//mapping of program index

	protected boolean isSource;	// Process P1 that initiates

	protected int number;	//index in array

	protected String nodeId;	//node id

	protected String parentId;	//parent id

	protected int parent;		//parent node index

	protected int status;		//node status

	protected int[] sendAcknowledgement;		//Holds Dijkstra-Scholten acknowledgement message FIFO send queues for each channel								//message counter

	protected int[] deficit;	//Holds acknowledgment deficit counter for each channel

	/**
	 *	@param number Index of program in program array.
	 */
	public DijkstraScholtenProgram(int number)
	{
		this.number = number;
		nodeId = new String("(unknown)");
		isSource = false;
	}

	/**
	 *	@param nodeId 
	 */
	public void setNodeId(String nodeId)
	{
		this.nodeId = nodeId;
	}

	/**
	 *	@param channelCnt Number of unidirectional channels of this node
	 *	@param nodeCnt Number of nodes in network.
	 */
	public void initConnectionInfo(int channelCnt, int nodeCnt)
	{
		int i;

		programMatrix = new int[channelCnt];
		channelMatrix = new int[nodeCnt];

		for (i = 0; i < programMatrix.length; i++)
		{
			programMatrix[i] = -1;
		}
		for (i = 0; i < channelMatrix.length; i++)
		{
			channelMatrix[i] = -1;
		}
	}
	/**
	 *	@param min
	 *	@param max
	 */
	public static int randInt(int min, int max)
	{
		Random rand = new Random();
    		int randomNum = rand.nextInt((max - min) + 1) + min;	
    		return randomNum;
	}	

	/**
	 *	@param channel 
	 *	@param program Index 
	 */
	public void setConnectionInfo(int channel, int program)
	{
		programMatrix[channel] = program;
		channelMatrix[program] = channel;
	}

	/**
	 *	@param program 
	 */
	public int getChannel(int program)
	{
		return channelMatrix[program];
	}

	/**
	 *	@param channel 
	 */
	public int getProgram(int channel)
	{
		return programMatrix[channel];
	}

	
	protected void init()
	{
		int channelCnt = in().getSize();
		int i;

		if (channelCnt != out().getSize())
		{
			System.err.println("Invalid network");
			exit(-1);
		}
		parentId = new String("(unknown)");
		isSource = false;

		status = IDLE;
		parent = NOPARENT;
		sendAcknowledgement = new int[channelCnt];
		deficit = new int[channelCnt];
		for (i = 0; i < sendAcknowledgement.length; i++)
		{
			sendAcknowledgement[i] = 0;
			deficit[i] = 0;
		}
	}

	/**
	 *	Sets the node to be the source node which received the initial input.
	 */
	public void setSource()
	{
		status = SOURCE;
		isSource = true;
	}

	/**
	 *	@param channel 
	 *	@param channelIndex 
	 */
	protected DijkstraScholtenMessage receive(InChannel channel, int channelIndex)
	{
		DijkstraScholtenMessage msg = (DijkstraScholtenMessage)channel.receive();

		if (msg.isAcknowledgement(channelIndex))
		{
			deficit[channelIndex]--;
			return null;
		}
		else
		{
			if (status == IDLE)
			{
				status = ACTIVE;
				parent = channelIndex;

				parentId = msg.getSenderId();
			}
			else
			{
				sendAcknowledgement[channelIndex]++;
			}
                        String text  = getText();
                        System.out.println("Text:"+text);
			return msg;
		}
	}
	int v = randInt(0,1);
	int messageCount = 0;
	/**
	 *	@param channel 
	 *	@param msg Message to be sent.
	 */
	
	protected void send(OutChannel channel, DijkstraScholtenMessage msg)
	{
		boolean isCompMessage = msg.isComputationMessage(Integer.parseInt(msg.toString()));
		if(isCompMessage == true)
			messageCount++; 
		if(v>=0.1 && v<=1 && messageCount<25)
		{
			msg.setSenderId(nodeId);
			channel.send(msg);
                        String text  = getText();
                        System.out.println("Text:"+text);
		}
	}

	/**
	 *	@param channelIndex 
	 */
	protected void postprocessSend(int channelIndex)
	{
		deficit[channelIndex]++;
	}

	protected boolean sendAcknowledgement()
	{
		int channel = Math.abs(Main.random.nextInt()) % sendAcknowledgement.length;
		int i;

		for (i = 0; (i < sendAcknowledgement.length) && (sendAcknowledgement[channel] <= 0); i++)
		{
			channel = (channel + 1) % sendAcknowledgement.length;
		}
		if (sendAcknowledgement[channel] > 0)
		{
			out(channel).send(new DijkstraScholtenMessage(DijkstraScholtenMessage.ACKNOWLEDGEMENT, nodeId));
			sendAcknowledgement[channel]--;
			return true;
		}
		return false;
	}

	public boolean isDone()
	{
		if ((status == SOURCE) && isIdle() && noDeficit())
		{
			status = IDLE;
			return true;
		}
		return false;
	}

	
	protected boolean cleanUp()
	{
		if ((status == ACTIVE) && isIdle() && noDeficit() && v>=0 && v<1)
		{
			sendAcknowledgement[parent]++;
			
			status = IDLE;
			parent = NOPARENT;

			parentId = new String("(unknown)");
			return true;
		}
		return false;
	}

	
	protected boolean noDeficit()
	{
		boolean noDeficit = true;
		int i = 0;

		while ((i < deficit.length) && noDeficit)
		{
			noDeficit = (deficit[i] == 0);
			i++;
		}
		return noDeficit;
	}

	protected abstract boolean isIdle();

        @Override
	public String getText()
	{
		StringBuffer text = new StringBuffer("Dijkstra-Scholten program:\nnode number: ");
		int i;

		text.append(number);
		text.append("\nnode id: ");
		text.append(nodeId);
		text.append("\nstatus: ");
		text.append(STATUSDESCRIPTION[status]);
		text.append("\nparent channel: ");
		text.append(parent);
		text.append("\nparent id: ");
		text.append(parentId);
		if (isSource)
		{
			text.append("\nnode is input node");
		}
		if(status == 0)
		{
			text.append("\nFrom Active to Idle");
		}	
		text.append("\nchannel partner nodes: ");
		if ((programMatrix != null) && (programMatrix.length > 0))
		{
			text.append(programMatrix[0]);
			for (i = 1; i < programMatrix.length; i++)
			{
				text.append(", ");
				text.append(programMatrix[i]);
			}
		}
		text.append("\ndeficit: ");
		if ((deficit != null) && (deficit.length > 0))
		{
			text.append(deficit[0]);
			for (i = 1; i < deficit.length; i++)
			{
				text.append(", ");
				text.append(deficit[i]);
			}
		}
		else
		{
			text.append("\nSending Ack to parent and detaching from tree ");
		}
		text.append("\nsend-acknowledgment buffer sizes: ");
		if ((sendAcknowledgement != null) && (sendAcknowledgement.length > 0))
		{
			text.append(sendAcknowledgement[0]);
			for (i = 1; i < sendAcknowledgement.length; i++)
			{
				text.append(", ");
				text.append(sendAcknowledgement[i]);
			}
		}
		Calendar cal = Calendar.getInstance();
                String file_path = "C:\\Users\\Shraddha\\Documents\\NetBeansProjects\\Project 1 AOS\\files\\File_Name"+number+".txt";
		text.append("\nLocal Clock Value: ");
    		text.append(cal.getTime());
                try {
                    writeToFile(file_path,text);
                }
                catch (IOException ex) {
                    Logger.getLogger(DijkstraScholtenProgram.class.getName()).log(Level.SEVERE, null, ex);
                }
		return text.toString();
	}
	/**
	 * @param pFilename
	 * @param pData
	 */
	public static void writeToFile(String pFilename, StringBuffer pData) throws IOException
	{  
        	BufferedWriter out = new BufferedWriter(new FileWriter(pFilename));  
        	out.write(pData.toString());  
        	out.flush();  
        	out.close();  
    	}  
}
