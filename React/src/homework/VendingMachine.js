import React, { useState, useEffect, Fragment, useContext } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';
import './styles.css';
import Button from 'react-bootstrap/Button';
import Pagination from 'react-bootstrap/Pagination';
import { Form, Col, Row } from 'react-bootstrap';
import FormControl from 'react-bootstrap/FormControl';
import Modal from 'react-bootstrap/Modal';
import Card from 'react-bootstrap/Card'
import CardDeck from 'react-bootstrap/CardDeck';
import LoginContext from './LoginContext';

const VendingMachine = () => {
  const [goodsData, setGoodsData] = useState({ goodsList: [], genericPageable: {} });
  const [currentPageNo, setCurrentPageNo] = useState(1);
  const [pageDataSize, setPageDataSize] = useState(6);
  const [pagesIconSize, setPagesIconSize] = useState(5);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [name, setname] = useState('');
  const navigate = useNavigate();
  const loginData = useContext(LoginContext);
  const [show, setShow] = useState(false);
  const handleClose = () => setShow(false);
  const handleShow = () => setShow(true);
  const [modalMessage, setModalMessage] = useState(''); // 用於顯示燈箱訊息
  const imageUrl = 'http://localhost:8085/training/goodsImg/';
  useEffect(() => {
    if (!loginData) {
      navigate('/login');
    }
    if (localStorage.getItem('name')) {
      setname(localStorage.getItem('name'));
    }

    fetchGoodsData();
  }, []); // 一開始進入頁面就執行查詢商品資料

  const fetchGoodsData = async () => {
    try {
      const response = await axios.get('http://localhost:8085/training/ecommerce/FrontendController/queryGoodsData', {
        params: {
          currentPageNo,
          pageDataSize,
          pagesIconSize,
          searchKeyword
        }
      });
      const data = response.data;
      setGoodsData(data);
      console.log(name);
      console.log(localStorage.getItem('name'));
    } catch (error) {
      console.error('Error fetching goods data:', error);
    }
  };
  console.log(goodsData.goodsList);
  console.log(goodsData.genericPageable);


  const goToCart = () => {
    navigate('/cargood');
  };
  const goToClearCart = () => {
    axios
      .delete('http://localhost:8085/training/ecommerce/MemberController/clearCartGoods', null, { withCredentials: true })
      .then((response) => {
        setModalMessage('購物車已清空');
        setShow(true);
      })
      .catch((error) => {
        console.error('清空購物車時發生錯誤', error);
      });
  };


  // 在按下查詢按鈕後觸發商品查詢
  const handleSearch = async (event) => {
    event.preventDefault();
    await fetchGoodsData();
  };

  const addCartGoods = (goodsID, goodsName, description, imageName, price, quantity, buygoodquantity) => {
    // 確定要傳遞的資料
    console.log(buygoodquantity)
    if (!buygoodquantity || buygoodquantity <= 0) {
      // 數量無效，不執行添加購物車的操作
      setModalMessage('請輸入有效的商品數量');
      setShow(true);
      return;
    } else if (!buygoodquantity || buygoodquantity > quantity) {
      setModalMessage('超過商品庫存');
      setShow(true);
      return;
    };
    const data = {
      goodsID,
      goodsName,
      description,
      imageName,
      price,
      quantity,
      buygoodquantity
    };

    // 發送 POST 請求
    axios.post('http://localhost:8085/training/ecommerce/MemberController/addCartGoods', data)
      .then(response => {
        // 請求成功的處理邏輯

        setModalMessage(goodsName + buygoodquantity + '個成功加入購物車');
        setShow(true);
      })
      .catch(error => {
        // 請求失敗的處理邏輯
        console.error('加入購物車失敗:', error);
      });
  };

  const onClickPage = async (page) => {
    const params = { "currentPageNo": page, "pageDataSize": 6, "pagesIconSize": 5, searchKeyword };
    try {
      const response = await axios.get('http://localhost:8085/training/ecommerce/FrontendController/queryGoodsData', { params });
      const data = response.data;
      setGoodsData({
        goodsList: data.goodsList,
        genericPageable: data.genericPageable
      });

    } catch (error) {
      console.log(error);
    }
  };


  const handleChange = (event) => {
    setSearchKeyword(event.target.value);
  };
  const handleLogout = async () => {
    try {
      await axios.get('http://localhost:8085/training/ecommerce/MemberController/logout', { withCredentials: true });
      navigate('/login');
      axios
        .delete('http://localhost:8085/training/ecommerce/MemberController/clearCartGoods', null, { withCredentials: true })
        .then((response) => {
          console.log('購物車已清空');
          localStorage.clear()
        })
        .catch((error) => {
          console.error('清空購物車時發生錯誤', error);
        });
    } catch (error) {
      console.log(error);
    }
  };
  const goods = goodsData.goodsList.slice(0, 3);
  const goods2 = goodsData.goodsList.slice(3, 6);
  const genericPageable = goodsData.genericPageable;

  return (
    <Fragment>
      <div>
        <>
          <Modal show={show} onHide={handleClose}>
            <Modal.Header closeButton>
              <Modal.Title>購物車訊息</Modal.Title>
            </Modal.Header>
            <Modal.Body>
              <p>{modalMessage}</p>
            </Modal.Body>
            <Modal.Footer>
              <Button variant="secondary" onClick={handleClose}>
                Close
              </Button>
            </Modal.Footer>
          </Modal>
        </>

        <table width="1000" height="400" align="center">
          <tbody>
            <tr>
              <td width="400" height="200" align="center">
                <img border="0" src={`${imageUrl}coffee.jpg`} width="200" height="200" />
                <h1>歡迎光臨，{name}</h1>

                <Button variant="outline-primary" onClick={handleLogout}>登出</Button>
                <br /><br />


              </td>
              <td width="800" height="400">

                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Button variant="outline-primary" onClick={goToCart}>前往購物車</Button>
                  <Button variant="outline-primary" onClick={goToClearCart}>清空購物車</Button>
                  <Form inline onSubmit={handleSearch}>
                    <FormControl type="text" placeholder="Search" className="mr-sm-2" name="searchKeyword" value={searchKeyword} onChange={handleChange} />
                    <Button variant="outline-success" type="submit">商品查詢</Button>
                  </Form>
                </div>
              </td>

            </tr>

          </tbody>
        </table>
        <table style={{ width: '800px', margin: '0 auto' }}>
          <tbody>
            <tr>
              <tr>
                <td>

                  <CardDeck>
                    {goods.map((good, index) => (
                      <Card key={`goods_${index}`}>
                        <Card.Img variant="top" src={`${imageUrl}${good.imageName}`} />
                        <Card.Body>
                          <Card.Title>{good.goodsName}</Card.Title>
                          <Card.Text>{good.price} 元/罐</Card.Text>
                          <input type="hidden" name="goodsID" value={good.goodsID} />
                          <Form>
                            <Form.Group controlId={`buyQuantity_${index}`}>
                              <Form.Label>購買數量：</Form.Label>
                              <Form.Control type="number" name="buyQuantity" min="0" max={good.quantity} size="5" />
                              <Form.Text className="text-muted" >(庫存 {good.quantity} 罐)</Form.Text>
                            </Form.Group>
                            <Button variant="primary" onClick={() => {
                              addCartGoods(
                                good.goodsID,
                                good.goodsName,
                                good.description,
                                good.imageName,
                                good.price,
                                good.quantity,
                                document.getElementsByName("buyQuantity")[index].value
                              );
                              handleShow(); // 顯示燈箱
                            }}>
                              加入購物車
                            </Button>
                          </Form>
                        </Card.Body>
                      </Card>
                    ))}
                  </CardDeck>
                </td>
              </tr>
              <tr>
                <td>
                  <CardDeck>
                    {goods2.map((good, index) => (
                      <Card key={`goods2_${index}`}>
                        <Card.Img variant="top" src={`${imageUrl}${good.imageName}`} />
                        <Card.Body>
                          <Card.Title>{good.goodsName}</Card.Title>
                          <Card.Text>{good.price} 元/罐</Card.Text>
                          <input type="hidden" name="goodsID" value={good.goodsID} />
                          <Form>
                            <Form.Group controlId={`buyQuantity_goods2_${index}`}>
                              <Form.Label>購買數量：</Form.Label>
                              <Form.Control type="number" name={`buyQuantity_goods2_${index}`} min="0" max={good.quantity} size="5" />
                              <Form.Text className="text-muted">(庫存 {good.quantity} 罐)</Form.Text>
                            </Form.Group>
                            <Button variant="primary" onClick={() => {


                              addCartGoods(
                                good.goodsID,
                                good.goodsName,
                                good.description,
                                good.imageName,
                                good.price,
                                good.quantity,
                                document.getElementsByName(`buyQuantity_goods2_${index}`)[0].value
                              );
                              handleShow(); // 顯示燈箱

                            }}>
                              加入購物車
                            </Button>
                          </Form>
                        </Card.Body>
                      </Card>
                    ))}
                  </CardDeck>
                </td>
              </tr>
              <br /> <br />
              <tr>
                {genericPageable.totalDataSize > pageDataSize && (
                  <Pagination>
                    <Pagination.First disabled={genericPageable.currentPageNo === 1 ? true : false} onClick={() => onClickPage(1)} />
                    <Pagination.Prev disabled={genericPageable.currentPageNo === 1 ? true : false} onClick={() => onClickPage(genericPageable.currentPageNo - 1)} />
                    {genericPageable.pages && genericPageable.pages.map(page => (
                      <Pagination.Item active={page === genericPageable.currentPageNo} key={page} onClick={() => onClickPage(page)}>{page}</Pagination.Item>
                    ))}
                    <Pagination.Next disabled={genericPageable.currentPageNo === genericPageable.totalPages ? true : false} onClick={() => onClickPage(genericPageable.currentPageNo + 1)} />
                    <Pagination.Last disabled={genericPageable.currentPageNo === genericPageable.totalPages ? true : false} onClick={() => onClickPage(genericPageable.totalPages)} />
                  </Pagination>
                )}

              </tr>
            </tr>
          </tbody>
        </table>


      </div>
    </Fragment>
  );
}

export default VendingMachine;
