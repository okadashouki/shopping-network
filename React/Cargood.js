import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { Row, Col, Form, Button } from 'react-bootstrap';
import Table from 'react-bootstrap/Table';


const CashUrl = 'http://localhost:8085/training/ecommerce/FrontendController/checkoutGoods';
const CreditCardUrl = 'http://localhost:8085/training/ecommerce/FrontendController/checkoutCreditCardGoods';
const Cargood = () => {
    const [carGoods, setCarGoods] = useState([]);
    const [total, setTotal] = useState(0);
    const [paymentMethod, setPaymentMethod] = useState('cash');
    const [creditCardNumber, setCreditCardNumber] = useState('');
    const [creditCardNumber2, setCreditCardNumber2] = useState('');
    const [creditCardNumber3, setCreditCardNumber3] = useState('');
    const [creditCardNumber4, setCreditCardNumber4] = useState('');
    const [expiryDate, setExpiryDate] = useState('');
    const [cvv, setCVV] = useState('');
    const [inputMoney, setInputMoney] = useState(0);
    const [purchaseFailed, setPurchaseFailed] = useState(false);
    const [name, setName] = useState('');
    const [phone, setPhone] = useState('');
    const [address, setAddress] = useState('');
    const cardNumber1Ref = useRef(null);
    const cardNumber2Ref = useRef(null);
    const cardNumber3Ref = useRef(null);
    const imageUrl = 'http://localhost:8085/training/goodsImg/';


    const handleKeyDown = (event, ref) => {
        if (event.key === 'Backspace') {
            if (event.target.value.length === 0) {
                ref.current.focus();
            }
        } else if (event.key === 'Delete') {
            event.target.value = '';
        } else if (event.target.value.length >= 4) {
            ref.current.focus();
        }
    };
    useEffect(() => {
        // 當組件載入時，從後端獲取購物車商品資料
        fetchCarGoods();

    }, []);


    const handleCVVChange = (event) => {
        const { value } = event.target;
        // 移除非數字字元
        const formattedValue = value.replace(/\D/g, '');
        setCVV(formattedValue);
    };
    const fetchCarGoods = async () => {
        try {
            const response = await axios.get('http://localhost:8085/training/ecommerce/MemberController/queryCartGoods');
            const newCarGoods = response.data;

            // 計算總金額
            const newTotal = calculateTotal(newCarGoods);

            setCarGoods(newCarGoods);
            setTotal(newTotal);
        } catch (error) {
            console.error('獲取購物車商品失敗:', error);
        }
    };


    const calculateTotal = (goodsList) => {
        return goodsList.reduce((acc, goods) => {
            const quantity = goods.buygoodquantity;
            const subTotal = goods.price * quantity;
            return acc + subTotal;
        }, 0);
    };



    const removeItem = async (goodsId) => {
        if (window.confirm('確定要刪除此商品嗎？')) {
            try {
                await axios.delete(`http://localhost:8085/training/ecommerce/MemberController/removeItem?goodsID=${goodsId}`);
                // 刪除成功後重新獲取購物車商品資料
                fetchCarGoods();
            } catch (error) {
                console.error('刪除商品失敗:', error);
            }
        }
    };

    const updateQuantity = async (goodsId, newQuantity) => {
        try {
            const response = await axios.post('http://localhost:8085/training/ecommerce/MemberController/updateItem', {
                goodsId,
                newQuantity
            });

            console.log(response.data);
            // 更新成功後重新獲取購物車商品資料
            fetchCarGoods();
        } catch (error) {
            console.error('更新數量失敗:', error);
        }
    };

    const navigate = useNavigate();
    const goToVendingMachine = () => {
        navigate('/');
    };
    const CashSubmit = (event) => {
        event.preventDefault();
        const formData = new FormData();
        formData.append('inputMoney', inputMoney);
        axios
            .post(CashUrl, formData, { withCredentials: true })
            .then((response) => {
                // 處理 API 回應的邏輯
                const data = response.data;
                console.log(data);

                const inboxInfo = {
                    buyGoodsMsg: data.buyGoodsMsg,
                    goodsMap: data.goodsMap,
                    memberName: data.memberName,
                    name: name,
                    phone: phone,
                    address: address

                };
                if (data.success) {
                    navigate("/detail", { state: inboxInfo });

                    // 清空購物車
                    axios
                        .delete('http://localhost:8085/training/ecommerce/MemberController/clearCartGoods', null, { withCredentials: true })
                        .then((response) => {
                            console.log('購物車已清空');
                        })
                        .catch((error) => {
                            console.error('清空購物車時發生錯誤', error);
                        });
                } else {
                    setPurchaseFailed(true);
                }
            })
            .catch((error) => {
                // 處理錯誤
                console.error(error);
            });
    };

    const creditCardSubmit = (event) => {
        event.preventDefault();
        console.log(event.target);
        // 收集表單資料

        axios
            .post(CreditCardUrl, {}, { withCredentials: true })
            .then((response) => {
                // 處理 API 回應的邏輯
                const data = response.data;
                console.log(data);

                const inboxInfo = {
                    buyGoodsMsg: data.buyGoodsMsg,
                    goodsMap: data.goodsMap,
                    memberName: data.memberName,
                    name: name,
                    phone: phone,
                    address: address,
                    creditCardNumber: creditCardNumber,
                    creditCardNumber2: creditCardNumber2,
                    creditCardNumber3: creditCardNumber3,
                    creditCardNumber4: creditCardNumber4,
                    expiryDate: expiryDate,
                    cvv: cvv

                };
                if (data.success) {
                    navigate("/detail", { state: inboxInfo });

                    // 清空購物車
                    axios
                        .delete('http://localhost:8085/training/ecommerce/MemberController/clearCartGoods', null, { withCredentials: true })
                        .then((response) => {
                            console.log('購物車已清空');
                        })
                        .catch((error) => {
                            console.error('清空購物車時發生錯誤', error);
                        });
                } else {
                    setPurchaseFailed(true);
                }
            })
            .catch((error) => {
                // 處理錯誤
                console.error(error);
            });
    };

    return (

        <div>
            <Button onClick={goToVendingMachine} variant="outline-success">前往商品頁</Button>
            <h1>購物車商品</h1>
            <hr />

            {carGoods.length > 0 ? (
                <Table striped bordered hover>
                    <thead>
                        <tr>
                            <th>商品照片</th>
                            <th>商品名稱</th>
                            <th>商品價格</th>
                            <th>購買數量</th>
                            <th>商品總金額</th>
                            <th>刪除商品</th>
                        </tr>
                    </thead>
                    <tbody>
                        {carGoods.map((goods) => {
                            const quantity = goods.buygoodquantity;
                            const subTotal = goods.price * quantity;

                            return (
                                <tr key={goods.goodsID}>
                                    <td>
                                        <img src={`${imageUrl}${goods.imageName}`} width="50" height="50" />
                                    </td>
                                    <td>{goods.goodsName}</td>
                                    <td>{goods.price}</td>
                                    <td>
                                        <input
                                            className="quantity-input"
                                            type="number"
                                            name="quantity"
                                            min="1"
                                            max={goods.quantity}
                                            value={quantity}
                                            onChange={(e) => updateQuantity(goods.goodsID, parseInt(e.target.value))}
                                        />
                                        <br />
                                        <span style={{ color: 'red' }}>庫存: {goods.quantity}</span>
                                    </td>
                                    <td className="sub-total">{subTotal}</td>
                                    <td>
                                        <button onClick={() => removeItem(goods.goodsID)}>刪除</button>
                                    </td>
                                </tr>
                            );
                        })}
                    </tbody>
                    <tfoot>
                        <tr>
                            <td id="total" colSpan="4" style={{ color: 'red' }}>
                                總金額：{total}
                            </td>
                        </tr>
                    </tfoot>
                </Table>

            ) : (
                <p>購物車沒商品</p>
            )}
            <br />
          
            {carGoods.length > 0 && (
                <div style={{ fontFamily: '微軟正黑體', fontSize: '4px', margin: '30px' }}>
                    <Row>
                        <Col md={4}>
                            <Form>
                                <Form.Group>
                                    <Form.Label>收件人:</Form.Label>
                                    <Form.Control type="text" name="name" value={name} onChange={(e) => setName(e.target.value)} />
                                </Form.Group>

                                <Form.Group>
                                    <Form.Label>電話:</Form.Label>
                                    <Form.Control type="text" name="phone" value={phone} onChange={(e) => setPhone(e.target.value)} />
                                </Form.Group>

                                <Form.Group>
                                    <Form.Label>地址:</Form.Label>
                                    <Form.Control as="textarea" name="address" rows="4" cols="50" value={address} onChange={(e) => setAddress(e.target.value)} />
                                </Form.Group>

                                <Form.Group>
                                    <Form.Label>付款方式:</Form.Label>
                                    <Form.Control as="select" value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}>
                                        <option value="cash">現金支付</option>
                                        <option value="creditCard">信用卡支付</option>
                                    </Form.Control>
                                </Form.Group>
                            </Form>

                            {paymentMethod === 'cash' && (
                                <Form onSubmit={CashSubmit}>
                                    <Form.Group>
                                        <Form.Label>投入:</Form.Label>
                                        <Form.Control
                                            type="number"
                                            name="inputMoney"
                                            max="100000"
                                            min="0"
                                            size="5"
                                            value={inputMoney}
                                            onChange={(event) => setInputMoney(event.target.value)}
                                        />
                                        <Form.Text className="text-muted">(元)</Form.Text>
                                    </Form.Group>
                                    <Button variant="primary" type="submit">
                                        送出
                                    </Button>
                                </Form>
                            )}

                            {paymentMethod === 'creditCard' && (
                                <Form onSubmit={creditCardSubmit}>


                                    <Form.Group>
                                        <Form.Label column sm={2}>
                                            信用卡號碼:
                                        </Form.Label>
                                        <Col sm={12}>
                                            <Row>
                                                <Col sm={3}>
                                                    <Form.Control
                                                        type="text"
                                                        name="cardNumber1"
                                                        maxLength="4"
                                                        value={creditCardNumber}
                                                        onChange={(event) => {
                                                            const { value } = event.target;
                                                            const input = value.replace(/\D/g, ''); // 移除非數字字符
                                                            setCreditCardNumber(input);
                                                        }}
                                                        onKeyDown={(event) => handleKeyDown(event, cardNumber2Ref)}
                                                        ref={cardNumber1Ref}
                                                    />
                                                </Col>
                                                <Col sm={3}>
                                                    <Form.Control
                                                        type="password"
                                                        name="cardNumber2"
                                                        maxLength="4"
                                                        value={creditCardNumber2}
                                                        onChange={(event) => {
                                                            const { value } = event.target;
                                                            const input = value.replace(/\D/g, ''); // 移除非數字字符
                                                            setCreditCardNumber2(input);
                                                        }}
                                                        onKeyDown={(event) => handleKeyDown(event, cardNumber3Ref)}
                                                        ref={cardNumber2Ref}
                                                    />
                                                </Col>
                                                <Col sm={3}>
                                                    <Form.Control
                                                        type="password"
                                                        name="cardNumber3"
                                                        maxLength="4"
                                                        value={creditCardNumber3}
                                                        onChange={(event) => {
                                                            const { value } = event.target;
                                                            const input = value.replace(/\D/g, ''); // 移除非數字字符
                                                            setCreditCardNumber3(input);
                                                        }}
                                                        onKeyDown={(event) => handleKeyDown(event, cardNumber1Ref)}
                                                        ref={cardNumber3Ref}
                                                    />
                                                </Col>
                                                <Col sm={3}>
                                                    <Form.Control
                                                        type="text"
                                                        name="cardNumber4"
                                                        maxLength="4"
                                                        value={creditCardNumber4}
                                                        onChange={(event) => {
                                                            const { value } = event.target;
                                                            const input = value.replace(/\D/g, ''); // 移除非數字字符
                                                            setCreditCardNumber4(input);
                                                        }}
                                                        ref={cardNumber1Ref}
                                                    />
                                                </Col>
                                            </Row>
                                        </Col>
                                    </Form.Group>

                                    <Form.Group>
                                        <Form.Label>有效期限:</Form.Label>
                                        <Form.Control
                                            type="date"
                                            name="expiryDate"
                                            maxLength="4"
                                            value={expiryDate}
                                            onChange={(e) => setExpiryDate(e.target.value)}
                                        />
                                    </Form.Group>

                                    <Form.Group>
                                        <Form.Label>CVV:</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="securityCode"
                                            maxLength="3"
                                            value={cvv}
                                            onChange={handleCVVChange}
                                        />
                                    </Form.Group>

                                    <Button variant="primary" type="submit">
                                        送出
                                    </Button>
                                </Form>
                            )}
                        </Col>
                    </Row>
                </div>
            )}
            <br />

            {purchaseFailed && carGoods.length > 0 && (
                <div style={{ margin: '30px', borderWidth: '3px', borderStyle: 'dashed', borderColor: '#FFAC55', padding: '5px', width: '300px' }}>
                    <p style={{ color: 'blue' }}>~~~~~~~ 消費明細 ~~~~~~~</p>
                    <p style={{ color: 'red' }}>餘額不足!購買失敗!</p>
                </div>
            )}


        </div>
    );
};

export default Cargood;
