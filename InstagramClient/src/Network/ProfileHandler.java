package Network;

import java.rmi.RemoteException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Common.ProfileService;

public class ProfileHandler {

	// xem profile:
	public void requestProfile(ProfileService profileService) throws RemoteException, ParseException {
		String profileJSONString = profileService.detailProfile(LoginAction.userID.intValue());
		
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(profileJSONString);
		
	}

	// chỉnh sửa profile:

}
