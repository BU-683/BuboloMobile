package bubolo.mobile.android;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import bubolo.BuboloMobile;
import bubolo.GameApplication.State;


/**
 * Main activity for BuboloMobile
 * 
 * @author BU CS673 - Clone Productions
 */
public class AndroidLauncher extends AndroidApplication {
	
    @Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		//System.out.print("Server IP Address: ");
		//String addressString = br.readLine();
		//InetAddress address;
	    //network = NetworkSystem.getInstance();
		//new networkTask().execute(net);
//		try {
//			address = Inet4Address.getByName("10.0.0.4");
//			net.connect(address, "Client");
//		} catch (UnknownHostException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
	
		
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;
		
		//initialize(new BuboloMobile(width, height,false,State.GAME), config);
		initialize(new BuboloMobile(width, height,true,State.PLAYER_INFO), config);
    }
}
