package com.fdahpstudydesigner.dao;

import com.fdahpstudydesigner.bo.MasterDataBO;
import com.fdahpstudydesigner.bo.UserBO;

public interface DashBoardAndProfileDAO {

	public MasterDataBO getMasterData(String type);

	public String isEmailValid(String email);

	public String updateProfileDetails(UserBO userBO, int userId);
}
