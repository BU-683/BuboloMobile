package bubolo;

import java.io.IOException;
import java.util.List;
import java.util.Random;

import bubolo.GameApplication.State;
import bubolo.audio.Audio;
import bubolo.graphics.Graphics;
import org.json.simple.parser.ParseException;
import bubolo.ui.LobbyScreen;
import bubolo.ui.PlayerInfoScreen;
import bubolo.ui.Screen;
import bubolo.util.GameRuntimeException;
import bubolo.util.Parser;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.net.command.CreateTank;
import bubolo.world.GameWorld;
import bubolo.world.World;
import bubolo.world.entity.Entity;
import bubolo.world.entity.concrete.Tank;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Main BuboloMobile application
 * 
 * 
 * @author BU CS673 - Clone Productions
 */
public class BuboloMobile extends AbstractGameApplication {
	private Network network;
	private int windowWidth;
	private int windowHeight;

	private Graphics graphics;
	private long lastUpdate = 0;
	private boolean isClient;
	private State initialState;
    private Stage stage;
    private Skin skin;
    private TextureAtlas buttonAtlas;
    private Screen screen;

	/**
	 * The number of game ticks (calls to <code>update</code>) per second.
	 */
	public static final int TICKS_PER_SECOND = 300;

	/**
	 * The number of milliseconds per game tick.
	 */
	public static final float MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;

	/**
	 * start a mobile game 
	 * 
	 * @param width
	 * @param height
	 * @param isClient
	 * @param initialState
	 */
	public BuboloMobile(int width, int height, boolean isClient,
			State initialState)
	{
		this.windowHeight = height;
		this.windowWidth = width;
		this.isClient = isClient;
		this.initialState = initialState;
	}
	
	@Override
	public void create () {
		
		network = NetworkSystem.getInstance();
		graphics = new Graphics(windowWidth, windowHeight);
		
		if (!isClient)
		{
			Parser fileParser = Parser.getInstance();
			
			try
			{
				world = fileParser.androidParseMap();
			}
			catch (ParseException | IOException e)
			{
				e.printStackTrace();
				throw new GameRuntimeException(e);
			}
		}
		else
		{
			world = new GameWorld();
		}

		setState(initialState);
	}

	@Override
	public void render () {

		
		final State state = getState();
		if (state == State.NET_GAME)
		{
			graphics.draw(world,stage);
			world.update();
			network.update(world);
		}
		else if (state == State.GAME)
		{
			if (this.isReady())
			{
				graphics.draw(world,stage);
				world.update();
			}
		}
		else if (state == State.GAME_LOBBY ||
				state == State.GAME_STARTING)
		{
			graphics.draw(screen);
			network.update(world);
		}
		else if (state == State.PLAYER_INFO)
		{
			graphics.draw(screen);
		}
		
//		network.update(world);
//		graphics.draw(world, stage	);
//		world.update();
//		
//		long currentMillis = System.currentTimeMillis();
//		if (currentMillis > (lastUpdate + (long)MILLIS_PER_TICK))
//		{
////			network.update(world);
////			graphics.draw(world, stage	);
////			world.update();
//			lastUpdate = currentMillis;
//		}
	}
	
	@Override
	public boolean isGameStarted()
	{
		return world.getMapTiles() != null;
	}
	@Override
	public void onStateChanged()
	{
		if (getState() == State.NET_GAME)
		{
			screen.dispose();

			Tank tank = world.addEntity(Tank.class);
			if (!isClient)
			{
				Vector2 spawnLocation = getRandomSpawn(world);
				tank.setParams(spawnLocation.x, spawnLocation.y, 0);
			}
			else
			{
				tank.setParams(getRandomX(), 200, 0);
			}
			tank.setLocalPlayer(true);

			network.send(new CreateTank(tank));

			setGameControls();
			
			
			setReady(true);
		}
		else if (getState() == State.GAME)
		{
			this.isClient = false;
			Parser fileParser = Parser.getInstance();
			
			try
			{
				world = fileParser.androidParseMap();
			}
			catch (ParseException | IOException e)
			{
				e.printStackTrace();
				throw new GameRuntimeException(e);
			}
			if (screen != null)
			{
				screen.dispose();
			}

			Tank tank = world.addEntity(Tank.class);
			//Vector2 spawnLocation = getRandomSpawn(world);
			tank.setParams(200, 200, 0);
			//tank.setParams(spawnLocation.x, spawnLocation.y, 0);
			tank.setLocalPlayer(true);

			network.startDebug();
			setGameControls();
			
			setReady(true);
		}
		else if (getState() == State.GAME_LOBBY)
		{
			screen = new LobbyScreen(this, world);
		}
		else if (getState() == State.PLAYER_INFO)
		{
			screen = new PlayerInfoScreen(this, isClient);
		}
	}

	/**
	 * Returns a random spawn point.
	 * 
	 * @return the location of a random spawn point.
	 */
	private static Vector2 getRandomSpawn(World world)
	{
		List<Entity> spawns = world.getSpawns();
		if (spawns.size() > 0)
		{
			Random randomGenerator = new Random();
			Entity spawn = spawns.get(randomGenerator.nextInt(spawns.size()));
			return new Vector2(spawn.getX(), spawn.getY());
		}
		return null;
	}
	
	private static int getRandomX()
	{
		int val = (new Random()).nextInt(10);
		return (1250 + (100 * val));
	}

	/**
	 * Called when the application is destroyed.
	 * 
	 * @see <a
	 *      href="http://libgdx.badlogicgames.com/nightlies/docs/api/com/badlogic/gdx/ApplicationListener.html">ApplicationListener</a>
	 */
	@Override
	public void dispose()
	{
		Audio.dispose();
	}	
	
	private void setGameControls()
	{
		Audio.startMusic();
		
        stage = new Stage();

        Gdx.input.setInputProcessor(stage);

        buttonAtlas = new TextureAtlas(Gdx.files.internal("skins/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("skins/uiskin.json"),buttonAtlas);

        TextButton fireButton = new TextButton("Fire", skin);
        TextButton rightButton = new TextButton("Right", skin);
        TextButton forwardButton = new TextButton("Forward", skin);       
        TextButton leftButton = new TextButton("Left", skin);
        final CheckBox musicCheck = new CheckBox("Music", skin);
        musicCheck.setChecked(true);
        musicCheck.addListener(new ClickListener(){
        	@Override
        	public void clicked(InputEvent event, float x, float y)
        	{
        		if (musicCheck.isChecked())
        		{
        			Audio.startMusic();
        		}else
        		{
        			Audio.stopMusic();
        		}	
        	}
        });
        final CheckBox sfxCheck = new CheckBox("Sound FX", skin);
        sfxCheck.setChecked(true);
        sfxCheck.addListener(new ClickListener(){
        	@Override
        	public void clicked (InputEvent event, float x, float y)
        	{
        		if (sfxCheck.isChecked())
        		{
        			Audio.setSoundEffectVolume(100);
        		}else
        		{
        			Audio.setSoundEffectVolume(0);
        		}
        	}
        });

        Table table = new Table();

        table.row().colspan(5).size(windowWidth, 100);
        table.align(Align.left + Align.bottom);
        table.add(leftButton).expandX().width(100.f);
        table.add(forwardButton).expandX().width(100.f);
        table.add(rightButton).expandX().width(100.f);
        table.add(musicCheck).expandX().width(100.f);
        table.add(sfxCheck).expandX().width(100.f);
        table.add().width(windowWidth - 600);
        table.add(fireButton).expandX().width(100.f);
        stage.addActor(table);
	}
}