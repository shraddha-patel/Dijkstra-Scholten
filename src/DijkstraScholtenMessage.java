import daj.*;

/**
 *	Dijkstra-Scholten algorithm 
 *
 *	@author Shraddha
 */
class DijkstraScholtenMessage extends Message
{
	protected static final int ACKNOWLEDGEMENT = 0;		// acknowledgement message type

	protected static final int COMPUTATION = 1;		// computation message type

	protected static final String[] TYPEDESCRIPTION = {
								new String("Acknowledgement"),
								new String("Computation message")
	};

	String senderId;		// Sender process's id

	int type;	// type of message

	public DijkstraScholtenMessage()		// Default values
	{
		type = COMPUTATION;
		senderId = new String("(unknown)");
	}

	/**
	 *	@param type Type of message 
	 *	@param senderId	
	 */
	public DijkstraScholtenMessage(int type, String senderId)
	{
		this.type = type;
		this.senderId = senderId;
	}

	/**
	 *	@param senderId 
	 */
	public void setSenderId(String senderId)
	{
		this.senderId = senderId;
	}

	/**
	 *	@return True if message is an acknowledgement.
	 */
	public boolean isAcknowledgement(int type)
	{
		return (type == ACKNOWLEDGEMENT);
	}

	/**
	 *	@return True if message is a computation message.
	 */
	public boolean isComputationMessage(int type)
	{
		return (type == COMPUTATION);
	}

	/**
	 *	@return The sender identification
	 */
	public String getSenderId()
	{
		return senderId;
	}

	/**
	 *	@return Description of the message.
	 */
	public String getText()
	{
		return "Dijkstra-Scholten message:\n" + TYPEDESCRIPTION[type] + "\nSender: " + senderId;
	}
}
