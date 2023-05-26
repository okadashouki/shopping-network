package com.training.jpa.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.training.jpa.dao.BeverageMemberDao;
import com.training.jpa.vo.MemberInfo;

@Service
public class AuthenticationService {
	 	@Autowired
	    private BeverageMemberDao memberDao;

	    public boolean checkLogin(String memberId, String password) {
	        // 依據 memberId 取得對應的會員資訊
	    	MemberInfo member = memberDao.queryAccountById(memberId);
	        
	        // 若找不到對應的會員，則登入失敗
	        if (member == null) {
	            return false;
	        }
	        
	        // 比對密碼是否正確
	        return member.getCusPassword().equals(password);
	    }
}
