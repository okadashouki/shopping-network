  const checkLogin = async () => {
    const longinData = await axios.get('http://localhost:8085/training/ecommerce/MemberController/checkLogin', { withCredentials: true }, { timeout: 3000 })
      .then(rs => rs.data)
      .catch(error => { console.log(error); });

    setLonginData(longinData);


    return longinData;
  };
  
  
