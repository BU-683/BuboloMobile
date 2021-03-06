package bubolo.audio;

import java.io.File;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

/**
 * A tree gathered sound effect.
 * 
 * @author BU673 - Clone Industries
 */
public class TreeGatheredSfx extends SoundEffect
{
	/**
	 * Constructs a tree gathered sound effect. External systems should not 
	 * construct <code>SoundEffect</code>s directly.
	 */
	TreeGatheredSfx()
	{
		try
		{
			FileHandle soundFile = new FileHandle(new File(Audio.SFX_PATH + "cannon_fired.ogg"));
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
