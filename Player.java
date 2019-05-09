import java.awt.Color;

import GoldenEngine.GameObject;
import GoldenEngine.GoldenWindow;
import GoldenEngine.PlayerInput;
import GoldenEngine.Vector2D;

public class Player extends GameObject
{

	private String texture = "Resources/Textures/player.png";
	
	private Vector2D position;
	private double movementSpeed = 0.4;
	
	public Player()
	{
		position = Vector2D.zero();
	}
	
	@Override
	public void update(double _dt)
	{
		handleMovement(_dt);
		
		
	}
	
	@Override
	public void draw(GoldenWindow _w)
	{
		//todo
		_w.picture(position.x(), position.y(), texture, 0.2, 0.2);
	}
	
	
	
	private void handleMovement(double _dt)
	{
		Vector2D move = PlayerInput.getMovementVector();
		
		move.multiply(movementSpeed*_dt);
		
		position.add(move);
	}
}