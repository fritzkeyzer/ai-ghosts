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
	
	public static void main(String args[]) throws Exception
	{
		running = true;
		
		//todo app init
		
		Player player = new Player();
		
		Zombie zombie = new Zombie(0.3, 0.5);
		
		
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
					Game.window().error("game over");
				break;
				
				default:
					throw new Exception("Unhandled game state");
			}
			GUI.update();
			//try
			//{
			//	Thread.sleep(100);
			//}
			//catch (Exception e)
			//{
			//	
			//}
		}
	}
	
	
}