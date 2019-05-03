package GoldenEngine;

import java.util.ArrayList;

public abstract class GameObject
{
	private static ArrayList<GameObject> objectList = new ArrayList<>();
	
	
	//INSTANCE
	public GameObject()
	{
		addToList(this);
	}
	
	public abstract void draw(GoldenWindow _window);
	
	public abstract void update(double _dt);
	
	
	//STATIC
	public static void addToList(GameObject _obj)
	{
		objectList.add(_obj);
	}
	
	public static void removeFromList(GameObject _obj)
	{
		objectList.remove(_obj);
	}
	
	public static ArrayList<GameObject> getObjectList()
	{
		return objectList;
	}
	
}