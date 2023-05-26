import React, { useEffect, useState } from 'react'
import { Routes, Route, useNavigate } from "react-router-dom";
import axios from "axios";
import VendingMachine from './VendingMachine';
import QueryGoods from './QueryGoods';
import SalesReportLifeCycle from './SalesReportLifeCycle';
import GoodsReplenishment from './GoodsReplenishment';
import GoodsCreate from './GoodsCreate';
import MemberLogin from './MemberLogin';
import Cargood from './Cargood';
import Detail from './Detail';
import LoginContext from './LoginContext';
const Home = () => {

    
    const [loginData, setLoginData] = useState(false);
  const navigate = useNavigate();

    //後臺表選單
    const handleSelectChange = (e) => {
        const selectedValue = e.target.value;
        if (selectedValue) {
            navigate(selectedValue);
        }
    };

    useEffect( () => {
    //首頁判斷是否登入
    console.log("是否登入",loginData);
        checkLogin();
    },[loginData,handleSelectChange]);


    const checkLogin = async () => {
        const loginData = await axios.get('http://localhost:8085/training/ecommerce/MemberController/checkLogin', { withCredentials: true })
          .then(rs => rs.data)
          .catch(error => { console.log(error); });
          console.log("是否登入",loginData);
          setLoginData(loginData);
        return loginData;
      };

    return (
        <LoginContext.Provider value={loginData}>
        <div>
            <div style={{ display: 'flex', alignItems: 'center' }}>
                <button onClick={() => navigate('/')}>前台頁面</button>
                {loginData}
                <select
                    style={{ marginLeft: '10px' }}
                    onChange={handleSelectChange}
                >
                    <option value="">後臺列表</option>
                    <option value="/QueryGoods">商品查詢</option>
                    <option value="/SalesReportLifeCycle">銷售報表</option>
                    <option value="/GoodsReplenishment">商品更新</option>
                    <option value="/GoodsCreate">商品新增</option>
                </select>
            </div>
            <hr />
      
            <br />
          
            <Routes>
                <Route path="/" element={loginData?<VendingMachine />:<MemberLogin />} />
                {/* <Route path="/" element={<VendingMachine />} /> */}
                <Route path="/QueryGoods" element={loginData?<QueryGoods />:<MemberLogin />} />
                <Route path="/SalesReportLifeCycle" element={loginData?<SalesReportLifeCycle />:<MemberLogin />} />
                <Route path="/GoodsReplenishment" element={loginData?<GoodsReplenishment />:<MemberLogin />}/>
                <Route path="/GoodsCreate" element={loginData?<GoodsCreate />:<MemberLogin />} />
                <Route path="/Cargood" element={loginData?<Cargood />:<MemberLogin />}/>
                <Route path="/detail" element={loginData?<Detail />:<MemberLogin />}/>
                <Route path="/login" element={<MemberLogin />} />
                
            </Routes>
        </div>
        </LoginContext.Provider>
    )
}

export default Home
