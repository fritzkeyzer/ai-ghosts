import GoldenEngine.GameObject;
import GoldenEngine.GoldenWindow;
import GoldenEngine.Vector2D;

public class Zombie extends GameObject
{

	private String texture = "Resources/Textures/zombie.png";
	private Vector2D position;
	private double moveSpeed = 0.2;
	
	public Zombie(double _x, double _y)
	{
		position = new Vector2D(_x, _y);
	}
	
	@Override
	public void update(double _dt)
	{
		//todo
		double x = Math.random()*2 -1;
		double y = Math.random()*2 -1;
		
		Vector2D rand = new Vector2D(x, y);
		rand.normalise();
		rand.multiply(_dt * moveSpeed);
		
		GUI.print("x = " + GUI.sci(x));
		GUI.print("y = " + GUI.sci(y));
		
		move(rand);
	}
	
	@Override
	public void draw(GoldenWindow _w)
	{
		//todo
		_w.picture(position.x(), position.y(), texture, 0.2, 0.2);
	}
	
	private void move(Vector2D _mov)
	{
		position.add(_mov);
	}
}