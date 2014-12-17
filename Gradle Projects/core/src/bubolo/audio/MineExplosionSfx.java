package bubolo.audio;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * A mine explosion sound effect.
 * 
 * @author BU673 - Clone Industries
 */
public class MineExplosionSfx extends SoundEffect
{
	/**
	 * Constructs a mine explosion sound effect. External systems should not 
	 * construct <code>SoundEffect</code>s directly.
	 */
	MineExplosionSfx()
	{
		try
		{
			FileHandle soundFile = Gdx.files.internal("sfx/mine_explosion.wav");
			Sound sound = Gdx.audio.newSound(soundFile);
			setSound(sound);
		}
		catch (Exception e)
		{
			System.out.println(e);
			e.printStackTrace();
			throw e;
		}
	}
}
