import React, { Component } from 'react';
import axios from "axios";
import 'bootstrap/dist/css/bootstrap.css';
import Button from 'react-bootstrap/Button';
import Table from 'react-bootstrap/Table';
import Pagination from 'react-bootstrap/Pagination';
import { Form, Col, Row } from 'react-bootstrap';
const apiUrl = 'http://localhost:8085//training/querySalesReport';
class SalesReportLifeCycle extends Component {

    constructor(props) {
        console.log("1.constructor 建構函式(Mounting:掛載)");
        super(props);
        this.state = {
            startDate: '2023-01-01',
            endDate: '2023-12-31',
            orderList: [],
            genericPageable: {},
            currentPageNo: 1,

        };
    }

    componentDidMount() {
        console.log("3.componentDidMount 組件掛載(Mounting:掛載)");

        this.onClickSearch();
    }

    componentDidUpdate(prevProps, prevState) {
        console.log("4.componentDidUpdate 組件更新(Updating :更新)");

        const { startDate, endDate } = this.state;
        console.log("prevState startDate:", prevState.startDate);
        console.log("state startDate:", startDate);
        console.log("prevState endDate:", prevState.endDate);
        console.log("state endDate:", endDate);
        // 當 startDate、endDate 其中一個有變化時，執行 onClickSearch() 更新資料
        if (startDate !== prevState.startDate || endDate !== prevState.endDate) {
            this.onClickSearch();
        }
    }
    onClickSearch = async () => {
        const { startDate, endDate, currentPageNo } = this.state;

        const params = { currentPageNo, "pageDataSize": 5, "pagesIconSize": 5, startDate, endDate };
        const reportData = await axios.get(apiUrl, { params })
            .then(rs => rs.data)
            .catch(error => { console.log(error); });

        console.log("goodsReportSalesList:", reportData.orderList);
        console.log("genericPageable:", reportData.genericPageable);
        this.setState({
            orderList: reportData.orderList,
            genericPageable: reportData.genericPageable
        });
    };

    onClickPage = async (page) => {
        const { startDate, endDate, } = this.state;
        const params = { "currentPageNo": page, "pageDataSize": 5, "pagesIconSize": 5, startDate, endDate };
        const reportData = await axios.get(apiUrl, { params })
            .then(rs => rs.data)
            .catch(error => { console.log(error); });

        this.setState({
            orderList: reportData.orderList,
            genericPageable: reportData.genericPageable,

        });

    }


    render() {
        console.log("2.render 渲染函式(Mounting :掛載、Updating:更新)");
        const { startDate, endDate, orderList, genericPageable, visible } = this.state;


        return (
            <>
                <Form>
                    <Row>
                        <Col>
                            <Form.Group>
                                <Form.Label>查詢日期起：</Form.Label>
                                <Form.Control
                                    type="date"
                                    value={startDate}
                                    onChange={(e) => this.setState({ startDate: e.target.value })}
                                />
                            </Form.Group>
                        </Col>
                        <Col>
                            <Form.Group>
                                <Form.Label>查詢日期迄：</Form.Label>
                                <Form.Control
                                    type="date"
                                    value={endDate}
                                    onChange={(e) => this.setState({ endDate: e.target.value })}
                                />
                            </Form.Group>
                        </Col>
                        <Col>
                            <Button variant="outline-primary" style={{ marginLeft: '20px', marginTop: '32px' }} onClick={this.onClickSearch}>查詢</Button>
                        </Col>
                    </Row>
                </Form>
                
                {orderList && orderList.length > 0 && (
                <Table striped bordered hover >
                    <thead>
                        <tr>
                            <th>訂單編號</th>
                            <th>購買日期</th>
                            <th>顧客姓名</th>
                            <th>商品名稱</th>
                            <th>商品價格</th>
                            <th>購買數量</th>
                            <th>總金額</th>
                        </tr>
                    </thead>
                    <tbody>
                        {orderList.map(order =>
                            <tr>
                                <td>{order.orderID}</td>
                                <td>{order.orderDate}</td>
                                <td>{order.customerName}</td>
                                <td>{order.goodsName}</td>
                                <td>{order.goodsBuyPrice}</td>
                                <td>{order.buyQuantity}</td>
                                <td>{order.buyAmount}</td>
                            </tr>
                        )}
                    </tbody>
                </Table>
                )}
                <hr />

                 {orderList && orderList.length > 0 && (
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

export default SalesReportLifeCycle;