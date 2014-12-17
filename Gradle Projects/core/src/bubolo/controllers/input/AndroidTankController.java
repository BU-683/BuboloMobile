package bubolo.controllers.input;

import java.awt.Point;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;

import bubolo.controllers.Controller;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateBullet;
import bubolo.net.command.CreateEntity;
import bubolo.net.command.MoveTank;
import bubolo.world.World;
import bubolo.world.entity.concrete.Bullet;
import bubolo.world.entity.concrete.Mine;
import bubolo.world.entity.concrete.Tank;

/**
 * A controller for the local tank. This controller maps android touch inputs to tank commands.
 * 
 * @author BU CS673 - Clone Productions
 */
public class AndroidTankController implements Controller
{
	private Tank tank;
	private int touch1X;
	private int touch1Y;
	private int touch2X;
	private int touch2Y;
	private boolean isTouch1;
	private boolean isTouch2;

	/**
	 * Constructs an android tank controller.
	 * 
	 * @param tank
	 *            reference to the local tank.
	 */
	public AndroidTankController(Tank tank)
	{
		this.tank = tank;
	}

	@Override
	public void update(World world)
	{
		getTouchPoints();
		
		if (isTouch1)
		{
			processMovement(tank, touch1X, touch1Y);
			processCannon(tank, world, touch1X, touch1Y);
		}
		if (isTouch2)
		{
			processMovement(tank, touch2X, touch2Y);
			processCannon(tank, world, touch2X, touch2Y);	
		}
	}
	private void getTouchPoints()
	{
		if(Gdx.input.isTouched(0))
		{
			isTouch1 = true;
			touch1X = Gdx.input.getX(0);
			touch1Y = Gdx.input.getY(0);
		}else
		{
			isTouch1 = false;
		}
		
		if(Gdx.input.isTouched(1))
		{
			isTouch2 = true;
			touch2X = Gdx.input.getX(1);
			touch2Y = Gdx.input.getY(1);
		}else
		{
			isTouch2 = false;
		}
	}

	private static void processMovement(Tank tank, int touchX, int touchY)
	{
		// TODO (cdc - 3/14/2014): allow the key mappings to be changed.
		if(touchY > Gdx.graphics.getHeight()-100)
		{
			if(touchX < 100)
			{
				tank.rotateRight();
				sendMove(tank);
			}else if (touchX < 200)
			{
				tank.accelerate();
				sendMove(tank);
			}else if (touchX < 300)
			{
				tank.rotateLeft();
				sendMove(tank);
			}
			
		}
	}

	private static void processCannon(Tank tank, World world, int touchX, int touchY)
	{
		if (touchY > Gdx.graphics.getHeight()-100)
		{
			if(touchX > (Gdx.graphics.getWidth() - 100))
			{
				float tankCenterX = tank.getX();
				float tankCenterY = tank.getY();

				Bullet bullet = tank.fireCannon(world,
					tankCenterX + 18 * (float)Math.cos(tank.getRotation()),
					tankCenterY + 18 * (float)Math.sin(tank.getRotation()));
				if(bullet != null)
				{
					Network net = NetworkSystem.getInstance();
					net.send(new CreateBullet(Bullet.class, bullet.getId(), bullet.getX(), bullet.getY(),
						bullet.getRotation(), tank.getId()));
				}
			}
		}
	}

	private static void sendMove(Tank tank)
	{
		Network net = NetworkSystem.getInstance();
		net.send(new MoveTank(tank));
	}
}
