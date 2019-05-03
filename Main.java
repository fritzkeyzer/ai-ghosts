import GoldenEngine.Game;

public class Main
{
	
	private static enum gameState
	{
		MAIN_MENU,
		RUNNING,
		GAMEOVER
	}
	
	private static gameState state = gameState.MAIN_MENU;
	
	private static boolean running = false;
	
	public static void main(String args[])
	{
		running = true;
		
		//todo app init
		
		while (running)
		{
			switch (state)
			{
				case MAIN_MENU:
					//todo
					Game.window().message("skip main menu");
					state = gameState.RUNNING;
				break;
				
				case RUNNING:
					Game.update();
				break;
				
				case GAMEOVER:
				break;
				
				default:
				
				break;
			}
		}
	}
	
	
	
	
}