import GoldenEngine.Game;
import GoldenEngine.GoldenWindow;

public class GUI
{
	
	private static int printLines = 0;
	
	
	
	
	public static void update()
	{
		printLines = 1;
	}
	
	
	
	
	public static void print(String _s)
	{
		Game.window().consoleText(_s, printLines++);
	}
	
	public static String sci(double _in)
	{
		if (_in < 0)
		{
			return String.format("%5.2e", _in);
		}
		return String.format("%6.3e", _in);
	}
	
}