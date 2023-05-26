import React, { Component } from 'react';
import axios from "axios";
import { Form, Button, Col } from 'react-bootstrap';
const apiUrl = 'http://localhost:8085/training/createGoods';
class GoodsCreate extends Component {

    state = {
        fileName: '',
        Data: '',
        goodsName: '',
        goodsPrice: '',
        goodsQuantity: '',
        status: '1',
        goodsID: '',
        validated: false,
        errorMessage: ''  // 清除錯誤訊息
    };
    onChangeImg = (e) => {
        const changFile = e.target.files;
        //避免取消照片找不到檔名報錯
        const changFileName = changFile && changFile[0] ? changFile[0].name : '';
        this.setState({ fileName: changFileName });
    };

    handleChange = (event) => {
        this.setState({ status: event.target.value });
    }
    handleSubmit = async (event) => {
        const form = event.currentTarget;
        event.preventDefault();
        this.setState({ validated: true });
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        const { fileName,
            goodsName,
            goodsPrice,
            goodsQuantity,
            status,
            description } = this.state;



        const uploadFile = form.uploadFile.files[0];
        // multipart
        const formData = new FormData();
        formData.append('imageName', fileName);
        formData.append('file', uploadFile);
        formData.append('goodsName', goodsName);
        formData.append('price', goodsPrice);
        formData.append('quantity', goodsQuantity);
        formData.append('status', status);
        formData.append('description', description);

        // call 後端API上傳檔案
        try {
            const response = await axios.post(apiUrl, formData);
            const gooddata = response.data;
            console.log(gooddata);
            this.setState({
                goodsName: '',
                goodsPrice: '',
                goodsQuantity: '',
                status: '1',
                fileName: '',
                Data: gooddata,
                goodsID: gooddata.goodsID,
                validated: false,
                errorMessage: ''  // 清除錯誤訊息
            });
        } catch (error) {
            console.log(error);
            this.setState({ errorMessage: '新增商品失敗' });
        }
    };
    render() {
        const {
            goodsName,
            goodsPrice,
            goodsQuantity,
            status,
            description,
            goodsID,
            validated,
            fileName
        } = this.state;

        return (
            <div>
                {goodsID && (
                    <p style={{ color: 'blue' }}>新增商品成功 商品編號{goodsID}</p>
                )}
                {this.state.errorMessage && (
                    <p style={{ color: 'red' }}>{this.state.errorMessage}</p>
                )}
                <Form noValidate validated={validated} onSubmit={this.handleSubmit}>
                    <Form.Group controlId="goodsName" as={Col} md={3}>
                        <Form.Label>商品名稱：</Form.Label>
                        <Form.Control
                            type="text"
                            name="goodsName"
                            value={goodsName}
                            onChange={e => this.setState({ goodsName: e.target.value })}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            請填寫商品名稱。
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group controlId="goodsPrice" as={Col} md={3}>
                        <Form.Label>設定價格：</Form.Label>
                        <Form.Control
                            type="number"
                            name="goodsPrice"
                            value={goodsPrice}
                            onChange={e => this.setState({ goodsPrice: e.target.value })}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            請填寫設定價格。
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group controlId="goodsQuantity" as={Col} md={3}>
                        <Form.Label>初始數量：</Form.Label>
                        <Form.Control
                            type="number"
                            name="goodsQuantity"
                            value={goodsQuantity}
                            onChange={e => this.setState({ goodsQuantity: e.target.value })}
                            required
                        />
                        <Form.Control.Feedback type="invalid">
                            請填寫初始數量。
                        </Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group as={Col} md={4}>
                        <Form.Label>商品狀態：</Form.Label>
                        <div>
                            <Form.Check
                                type="radio"
                                label="上架"
                                name="status"
                                value="1"
                                checked={status === "1"}
                                onChange={this.handleChange}
                            />
                            <Form.Check
                                type="radio"
                                label="下架"
                                name="status"
                                value="0"
                                checked={status === "0"}
                                onChange={this.handleChange}
                            />
                        </div>

                        <Form.Control.Feedback type="invalid">
                            請選擇商品狀態。
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group as={Col} md={4} className="d-flex align-items-center">
                        <Form.File id="formcheck-api-custom" custom className="mr-2">
                            <Form.File.Input name="uploadFile" onChange={this.onChangeImg} />
                            <Form.File.Label data-browse="Upload Button">
                                {fileName ? fileName : '選擇要上傳的檔案...'}
                            </Form.File.Label>

                        </Form.File>
                    </Form.Group>
                    <Form.Group as={Col} md={6}>
                        <Form.Label>商品描述：</Form.Label>
                        <br />
                        <Form.Control
                            as="textarea"
                            name="description"
                            value={description}
                            onChange={(e) => this.setState({ description: e.target.value })}
                        />
                    </Form.Group>

                    <Button variant="outline-primary" type="submit">上傳</Button>
                </Form>
            </div>
        );
    }
}

export default GoodsCreate;