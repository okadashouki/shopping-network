import React from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import { Button } from 'react-bootstrap'

const Detail = () => {
  const location = useLocation();
  const inboxInfo = location.state;

  const goodsArray = Object.values(inboxInfo.goodsMap);
  const MsgArray = Object.values(inboxInfo.buyGoodsMsg);

  const navigate = useNavigate();

  return (
    <div style={{ margin: '0 auto', textAlign: 'center' }}>
      <div style={{  margin: '0 auto', textAlign: 'center' ,borderWidth: '3px', borderStyle: 'dashed', borderColor: '#FFAC55', padding: '5px', width: '300px' }}>
        <p style={{ color: 'blue' }}>~~~~~~~ 消費明細 ~~~~~~~</p>
        <p style={{ color: 'red' }}>
          {MsgArray.map((Msg) => (
            <React.Fragment key={Msg}>
              {Msg}
              <br /><br />
            </React.Fragment>
          ))}
        </p>
        <p style={{ color: 'red' }}>
          購買商品:
          {goodsArray.map((goods) => (
            <div key={goods.goodsID}>
              <p>{goods.goodsName} {goods.buygoodquantity} 件</p>
            </div>
          ))}
        </p>
        <p style={{ color: 'blue' }}>購買人: {inboxInfo.memberName}</p>
        {inboxInfo.creditCardNumber && (
          <p style={{ color: 'blue' }}>信用卡卡號: {inboxInfo.creditCardNumber}-{inboxInfo.creditCardNumber2}-{inboxInfo.creditCardNumber3}-{inboxInfo.creditCardNumber4}</p>
        )}
        {inboxInfo.expiryDate && (
          <p style={{ color: 'blue' }}>信用卡有效期限: {inboxInfo.expiryDate}</p>
        )}
        {inboxInfo.cvv && (
          <p style={{ color: 'blue' }}>CVV: {inboxInfo.cvv}</p>
        )}
        <p style={{ color: 'blue' }}>收件人: {inboxInfo.name}</p>
        <p style={{ color: 'blue' }}>電話: {inboxInfo.phone}</p>
        <p style={{ color: 'blue' }}>地址: {inboxInfo.address}</p>
      </div>
      <br />
      <Button variant="primary" onClick={() => navigate('/')}>繼續購買</Button>
    </div>
  );
};

export default Detail;

