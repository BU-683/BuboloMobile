package bubolo;


import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

// import bubolo.audio.Audio;
import bubolo.graphics.Graphics;
import org.json.simple.parser.ParseException;

import bubolo.util.Parser;
import bubolo.net.Network;
import bubolo.net.NetworkSystem;
import bubolo.world.World;
import bubolo.world.entity.concrete.Tank;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class BuboloMobile extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	

	//private Network network;
	private int windowWidth;
	private int windowHeight;

	private Graphics graphics;
	private World world;
	private long lastUpdate = 0;

	private boolean ready;
	private static Map<String, Texture> textures = new HashMap<String, Texture>();

	/**
	 * The number of game ticks (calls to <code>update</code>) per second.
	 */
	public static final int TICKS_PER_SECOND = 300;

	/**
	 * The number of milliseconds per game tick.
	 */
	public static final float MILLIS_PER_TICK = 1000 / TICKS_PER_SECOND;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		
		network = NetworkSystem.getInstance();
		network.startDebug();

		graphics = new Graphics(windowWidth, windowHeight, textures);
		Parser fileParser = Parser.getInstance();
		Path path = FileSystems.getDefault().getPath("res", "maps/Everard Island.json");
		try
		{
			world = fileParser.parseMap(path);
		}
		catch (ParseException | IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		Tank tank = world.addEntity(Tank.class);
		tank.setParams(100, 100, 0);
		tank.setLocalPlayer(true);
		Audio.startMusic();
		ready = true;
		
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(1, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.begin();
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	public void SetTextures(Map<String, Texture> textures){
		this.textures = textures;
	}
}