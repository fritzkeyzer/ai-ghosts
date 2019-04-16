import java.util.ArrayList;

public abstract class GameObject
{
	private static ArrayList<GameObject> objectList = new ArrayList<>();
	
	public GameObject()
	{
		
	}
	
	public void draw()
	{
		
	}
	
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