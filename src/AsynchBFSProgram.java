import daj.*;
import java.util.Vector;

/**	
 *	Dijkstra-Scholten algorithm for termination detection.
 *
 *	@author Shraddha
 */
class AsynchBFSProgram extends DijkstraScholtenProgram
{
	protected static final int NOPARENT = -1;   	//no parent value

	protected static final int INFINITE = -1;

	protected int parent;		//parent channel index 

	protected int dist;	//dist to root

	protected boolean idle;		//idle node

	protected Vector[] sendQueue;	// AsynchBFS message value FIFO send queues for each channel

	protected String parentId;	//Parent Id

	/**
	 *	@param number 
	 */
	public AsynchBFSProgram(int number)
	{
		super(number);
	}

	public void init()	//Initialization of process P1
	{
		int i;

		super.init();

		if (in().getSize() != out().getSize())
		{
			System.err.println("Invalid network");
			exit(-1);
		}
		parentId = new String("(unknown)");

		parent = NOPARENT;
		dist = -1;
		sendQueue = new Vector[in().getSize()];
		for (i = 0; i < sendQueue.length; i++)
		{
			sendQueue[i] = new Vector();
		}

		idle = true;
	}

	
	public void main()
	{
		InChannelSet msgIn = in();
		OutChannelSet msgOut = out();
		AsynchBFSMessage msg;
		boolean terminationDetected;
		int channelCnt = msgIn.getSize();
		int channel;
		int msgDist;
		int i;

		init();

		if (number == 0)
		{
			dist = 0;
			for (i = 0; i < sendQueue.length; i++)
			{
				sendQueue[i].addElement(new Integer(0));
			}
			setSource();
			idle = false;
		}

		while (true)
		{
			if (super.isDone()) // termination detected
			{
				break;
			}


			channel = msgIn.select(1);
			if (channel != -1)
			{
				msg = (AsynchBFSMessage)super.receive(msgIn.getChannel(channel), channel);
				if (msg != null)
				{
					idle = false;
					msgDist = msg.getValue();
					if ((dist == INFINITE) || ((msgDist != INFINITE) && (msgDist + 1 < dist)))
					{
						dist = msgDist + 1;
						parent = channel;
						parentId = msg.getSenderId();
						for (i = 0; i < sendQueue.length; i++)
						{
							if (i != channel)
							{
								sendQueue[i].addElement(new Integer(dist));
							}
						}
					}
				}
				continue;
			}

			if (super.cleanUp())
			{
				continue;
			}

			channel = Math.abs(Main.random.nextInt()) % sendQueue.length;
			for (i = 0; (i < sendQueue.length) && sendQueue[channel].isEmpty(); i++)
			{
				channel = (channel + 1) % channelCnt;
			}
			if (!sendQueue[channel].isEmpty())
			{
				idle = false;
				super.send(
					msgOut.getChannel(channel),
					new AsynchBFSMessage(((Integer)sendQueue[channel].firstElement()).intValue())
				);
				sendQueue[channel].removeElementAt(0);
				super.postprocessSend(channel);
				continue;
			}
			if (super.sendAcknowledgement())
			{
				continue;
			}
			idle = true;
		}
	}

	/**
	 *	@return True if node is idle
	 */
	protected boolean isIdle()
	{
		return idle;
	}

	public String getText()
	{
		StringBuffer text = new StringBuffer("AsynchBFS program:\nparent channel: ");

		text.append(parent);
		text.append("\nparent id: ");
		text.append(parentId);
		text.append("\ncurrent distance to root: ");
		if (dist == INFINITE)
		{
			text.append("infinite");
		}
		else
		{
			text.append(dist);
		}
		text.append("\n");
		text.append(super.getText());
		text.append("\nidle: ");
		text.append(idle);
		return text.toString();
	}
}
