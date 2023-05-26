import React, { Component } from 'react';
import axios from "axios";

const apiUrl = 'http://localhost:8085//training/querySalesReport';

class SalesReport extends Component {

    state = {
        startDate: '',
        endDate: '',
        orderList: [],
        genericPageable: {},
        currentPageNo: 1,
    
    };


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
        const { startDate, endDate } = this.state;
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
        const { startDate, endDate, orderList, genericPageable ,visible} = this.state;
        

        return (
            <div>
                <label>查詢日期起：</label>{" "}
                <input
                    type="date"
                    value={startDate}
                    onChange={(e) =>
                        this.setState({ startDate: e.target.value })
                    }
                />
                <label style={{ marginLeft: "20px" }} />
                <label>查詢日期迄：</label>{" "}
                <input
                    type="date"
                    value={endDate}
                    onChange={(e) => this.setState({ endDate: e.target.value })}
                />
                <label style={{ marginLeft: "20px" }} />
                <button onClick={this.onClickSearch}>查詢</button>
                <hr />
                {orderList && orderList.length > 0 && (
                <table border={'2'}>
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
                </table>
                  )}
                <hr />
                {orderList && orderList.length > 0 && (
                <div>
                    <button disabled={genericPageable.currentPageNo === 1 ? true : false} onClick={() => this.onClickPage(1)}>{'<<'}</button>
                    <button disabled={genericPageable.currentPageNo === 1 ? true : false} onClick={() => this.onClickPage(genericPageable.currentPageNo-1)}>{'<'}</button>

                    {genericPageable.pages && genericPageable.pages.map(page => (
                        <button key={page} onClick={() => this.onClickPage(page)}>
                           {page === genericPageable.currentPageNo ? <b>{page}</b> : page}
                        </button>
                    ))}
                    <button disabled={genericPageable.currentPageNo === genericPageable.totalPages ? true : false} onClick={() => this.onClickPage(genericPageable.currentPageNo+1)}>{'>'}</button>
                    <button disabled={genericPageable.currentPageNo === genericPageable.totalPages ? true : false} onClick={() => this.onClickPage(genericPageable.totalPages)}>{'>>'}</button>
                </div>
                  )}
            </div>
        );
    }
}

export default SalesReport;