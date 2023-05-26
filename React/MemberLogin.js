import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const apiUrl = 'http://localhost:8085/training/ecommerce/MemberController/login';

const MemberLogin = () => {
  const [id, setId] = useState('');
  const [pwd, setPwd] = useState('');
  const [loginError, setLoginError] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (event) => {
    event.preventDefault();

    if (!id || !pwd) {
      alert('請填寫必填欄位！');
      return;
    }

    const requestData = {
      identificationNo: id,
      cusPassword: pwd,
    };

    try {
      const response = await axios.post(apiUrl, requestData, { withCredentials: true });
      const logindata = response.data;
      console.log(logindata.memberInfo);
      console.log(logindata.memberInfo.name);

      localStorage.setItem('name', logindata.memberInfo.name)

      navigate('/', { state: { name: logindata.memberInfo.name } });
      
    } catch (error) {
      console.log(error);
      setLoginError(true)
    }
  };

 
  return (
    <div>
    <form onSubmit={handleSubmit}>
      帳號：<input type="text" name="id" value={id} onChange={(e) => setId(e.target.value)} />
      <br />
      密碼：<input type="password" name="pwd" value={pwd} onChange={(e) => setPwd(e.target.value)} />
      <br />
      <button type="submit">登入</button>
    </form>
    {loginError && <p style={{ color: 'blue' }}>帳號或密碼錯誤</p>}
    </div>
  );
};

export default MemberLogin;
