package bubolo.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;

import bubolo.world.World;
import bubolo.world.entity.concrete.Tank;
import static com.google.common.base.Preconditions.*;

/**
 * Controller that moves the camera based on the tank's position.
 * 
 * @author BU CS673 - Clone Productions
 */
class TankCameraController implements CameraController
{
	private Tank tank;
	private OrthographicCamera camera;

	/**
	 * Constructs a TankCameraController. Package-private because TankCameraControllers are internal
	 * to the Graphics system.
	 * 
	 * @param tank
	 */
	TankCameraController(Tank tank)
	{
		this.tank = checkNotNull(tank);
	}

	@Override
	public void setCamera(Camera camera)
	{
		this.camera = (OrthographicCamera) checkNotNull(camera);
	}

	@Override
	public boolean hasCamera()
	{
		return (camera != null);
	}

	@Override
	public void update(World world)
	{
		if (camera == null)
		{
			throw new IllegalStateException("No camera has been set for this TankCameraController.");
		}

		float tankX = calculateCameraX(camera, tank, world);
		float tankY = calculateCameraY(camera, tank, world);

		// The libgdx camera's position is from the bottom left corner:
		// https://github.com/libgdx/libgdx/wiki/Orthographic-camera
		camera.position.set(Math.round(tankX), Math.round(tankY), 0.001f);
		camera.update();
	}

	private static float calculateCameraX(Camera camera, Tank tank, World world)
	{
		//float screenWidth = Gdx.graphics.getWidth()/2;
		float screenWidth = camera.viewportWidth;
		
		float tankX = tank.getX();
		
		float cameraX = tankX - screenWidth / 2.f;
		if (cameraX < 0)
		{
			cameraX = 0;
		}
		else if (cameraX > world.getMapWidth() - screenWidth)
		{
			// Ensure that screen doesn't go negative if the world is smaller than the camera.
			float newCameraX = world.getMapWidth() - screenWidth;
			cameraX = (newCameraX >= 0) ? newCameraX : 0;
		}

		return cameraX;
	}

	private static float calculateCameraY(Camera camera, Tank tank, World world)
	{
		//float screenHeight = Gdx.graphics.getHeight()/2;
		float screenHeight = camera.viewportHeight;
		float tankY = tank.getY();

		float cameraY = tankY - screenHeight / 2.f;
		if (cameraY < 0)
		{
			cameraY = 0;
		}
		else if (cameraY > world.getMapHeight() - screenHeight)
		{
			// Ensure that screen doesn't go negative if the world is smaller than the camera.
			float newCameraY = world.getMapHeight() - screenHeight;
			cameraY = (newCameraY >= 0) ? newCameraY : 0;
		}

		return cameraY;
	}
}
