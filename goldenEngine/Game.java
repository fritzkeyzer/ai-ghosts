package GoldenEngine;

import java.util.ArrayList;

public class Game
{
	
	private static GoldenWindow window = new GoldenWindow("Game");
	
	private static long curTime = System.nanoTime();
	private static long lastTime = curTime;
	
	public static void update()
	{
		curTime = System.nanoTime();
		double dt = (curTime-lastTime)*0.000000001;
		lastTime = curTime;
		
		ArrayList<GameObject> objList = GameObject.getObjectList();
		
		for (GameObject obj : objList)
		{
			obj.update(dt);
		}
		
		window.clear();
		for (GameObject obj : objList)
		{
			obj.draw(window);
		}
		window.show();
	}
	
	public static GoldenWindow window()
	{
		return window;
	}
}