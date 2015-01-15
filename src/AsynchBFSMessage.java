import daj.*;

/**
 *	@author Shraddha
 */
class AsynchBFSMessage extends DijkstraScholtenMessage
{
	int value;	//Distance to root

	/**
	 *	@param value 
	 */
	public AsynchBFSMessage(int value)
	{
		this.value = value;
	}

	public int getValue()
	{
		return value;
	}

	public String getText()		// return Description of the message
	{
		return "AsynchBFS message:\nvalue: " + value + "\n" + super.getText();
	}
}
