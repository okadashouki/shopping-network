import React, { Component } from 'react';
import axios from "axios";
import 'bootstrap/dist/css/bootstrap.css';
import Container from 'react-bootstrap/Container';
import Button from 'react-bootstrap/Button';
import Table from 'react-bootstrap/Table';
import Pagination from 'react-bootstrap/Pagination';
import { Form, Col, Row } from 'react-bootstrap';
import FormControl from 'react-bootstrap/FormControl';
import Navbar from 'react-bootstrap/Navbar';
import Nav from 'react-bootstrap/Nav';
import NavDropdown from 'react-bootstrap/NavDropdown';
import Modal from 'react-bootstrap/Modal';
import Card from 'react-bootstrap/Card'
import CardDeck from 'react-bootstrap/CardDeck';
const apiUrl = 'http://localhost:8085//training/queryGoods';
class QueryGoods extends Component {
    state = {
        goodsID: '',
        goodsName: '',
        minPrice: '',
        maxPrice: '',
        sort: '',
        goodsQuantity: '',
        currentPageNo: 1,
        status: 'ON_SALE',
        data: [],
        genericPageable: [],
    }


    onClickSearch = async (event) => {
        event.preventDefault();
        const form = event.currentTarget;

        if (form.checkValidity() === false) {
            this.setState({ validated: true });
            return;
        }
        const { goodsID, goodsName, minPrice, maxPrice, sort, goodsQuantity, status, currentPageNo } = this.state;

        const params = { currentPageNo, "pageDataSize": 5, "pagesIconSize": 5, goodsID, goodsName, minPrice, maxPrice, sort, goodsQuantity, status };


        try {
            const response = await axios.get(apiUrl, { params });

            console.log("goodsList:", response.data);

            this.setState({
                data: response.data.data,
                genericPageable: response.data.pageable
            });
        } catch (error) {
            console.log(error);
        }

    }

    onClickPage = async (page) => {
        const { goodsID, goodsName, minPrice, maxPrice, sort, goodsQuantity, status } = this.state;
        const params = { "currentPageNo": page, "pageDataSize": 5, "pagesIconSize": 5, goodsID, goodsName, minPrice, maxPrice, sort, goodsQuantity, status };
        try {
            const response = await axios.get(apiUrl, { params })
            this.setState({
                data: response.data.data,
                genericPageable: response.data.pageable
            });
        } catch (error) {
            console.log(error);
        }

    }


    render() {
        const { goodsID, goodsName, minPrice, maxPrice, sort, goodsQuantity, status, data, genericPageable } = this.state
        const validated = false;
        console.log(data);
        console.log(genericPageable);


        return (
            <>
                <Form noValidate validated={validated} >
                    <Row>
                        <Col>
                            <Form.Group controlId="formGridGoodsID">
                                <Form.Label>商品編號</Form.Label>
                                <Form.Control
                                    required
                                    type="number"
                                    placeholder="輸入商品編號"
                                    value={goodsID}
                                    onChange={(e) => this.setState({ goodsID: e.target.value })}
                                />
                                <Form.Control.Feedback>欄位正確!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">欄位錯誤!</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group controlId="formGridGoodsName">
                                <Form.Label>商品名稱</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="輸入商品名稱"
                                    value={goodsName}
                                    onChange={(e) => this.setState({ goodsName: e.target.value })}
                                />
                                <Form.Control.Feedback>欄位正確!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">欄位錯誤!</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col>
                            <Form.Group controlId="formGridGoodsQuantity">
                                <Form.Label>商品數量</Form.Label>
                                <Form.Control
                                    required
                                    type="number"
                                    placeholder="輸入商品數量"
                                    value={goodsQuantity}
                                    onChange={(e) => this.setState({ goodsQuantity: e.target.value })}
                                />
                                <Form.Control.Feedback>欄位正確!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">欄位錯誤!</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group controlId="formGridMinPrice">
                                <Form.Label>最小價格</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="輸入最小價格"
                                    value={minPrice}
                                    onChange={(e) => this.setState({ minPrice: e.target.value })}
                                />
                                <Form.Control.Feedback>欄位正確!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">欄位錯誤!</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group controlId="formGridMaxPrice">
                                <Form.Label>最大價格</Form.Label>
                                <Form.Control
                                    required
                                    type="text"
                                    placeholder="輸入最大價格"
                                    value={maxPrice}
                                    onChange={(e) => this.setState({ maxPrice: e.target.value })}
                                />
                                <Form.Control.Feedback>欄位正確!</Form.Control.Feedback>
                                <Form.Control.Feedback type="invalid">欄位錯誤!</Form.Control.Feedback>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Row>
                        <Col>
                            <Form.Group controlId="formGridSort">
                                <Form.Label>排序</Form.Label>
                                <Form.Control
                                    as="select"
                                    value={sort}
                                    onChange={(e) => this.setState({ sort: e.target.value })}
                                >
                                    <option value={null}>--</option>
                                    <option value="ASC">價格低到高</option>
                                    <option value="DESC">價格高到低</option>
                                </Form.Control>
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group controlId="formGridStatus">
                                <Form.Label>上架狀態</Form.Label>
                                <Form.Control
                                    as="select"
                                    value={status}
                                    onChange={(e) => this.setState({ status: e.target.value })}
                                >
                                    <option value="ON_SALE">上架</option>
                                    <option value="OFF_SALE">未上架</option>
                                </Form.Control>
                            </Form.Group>
                        </Col>
                    </Row>

                    <Button variant="outline-primary" onClick={this.onClickSearch}>查詢</Button>
                </Form>
                <br />
                {data && data.length > 0 && (
                    <Table striped bordered hover >
                        <thead>
                            <tr>
                                <th>商品編號</th>
                                <th>商品名稱</th>
                                <th>商品價格</th>
                                <th>現有庫存</th>
                                <th>商品狀態</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data.map(good =>
                                <tr>
                                    <td>{good.goodsID}</td>
                                    <td>{good.goodsName}</td>
                                    <td>{good.price}</td>
                                    <td>{good.quantity}</td>
                                    <td>{good.status == 1 ? "已上架" : "未上架"}</td>
                                </tr>
                            )}
                        </tbody>
                    </Table>
                )}
                <hr />
                {data && data.length > 0 && (
                    <Pagination>
                        <Pagination.First disabled={genericPageable.currentPageNo === 1 ? true : false} onClick={() => this.onClickPage(1)} />
                        <Pagination.Prev disabled={genericPageable.currentPageNo === 1 ? true : false} onClick={() => this.onClickPage(genericPageable.currentPageNo - 1)} />
                        {genericPageable.pages && genericPageable.pages.map(page => (
                            <Pagination.Item active={page === genericPageable.currentPageNo} key={page} onClick={() => this.onClickPage(page)}>{page}</Pagination.Item>
                        ))}
                        <Pagination.Next disabled={genericPageable.currentPageNo === genericPageable.totalPages ? true : false} onClick={() => this.onClickPage(genericPageable.currentPageNo + 1)} />
                        <Pagination.Last disabled={genericPageable.currentPageNo === genericPageable.totalPages ? true : false} onClick={() => this.onClickPage(genericPageable.totalPages)} />
                    </Pagination>
                )}
            </>
        );
    }
}

export default QueryGoods;