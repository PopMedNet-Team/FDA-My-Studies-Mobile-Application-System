package com.fdahpstudydesigner.service;

import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;
import com.fdahpstudydesigner.util.SessionObject;

public interface DashBoardAndProfileService {

	public MasterDataBO getMasterData(String type);

	public String isEmailValid(String email);

	public String updateProfileDetails(UserBO userBO, int userId,
			SessionObject userSession);

}
