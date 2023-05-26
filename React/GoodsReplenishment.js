import React, { Component } from 'react';
import axios from "axios";
import { Form, Button, Col } from 'react-bootstrap';
const apiUrl = 'http://localhost:8085/training/updateGoods';
const allGoodUrl = 'http://localhost:8085/training/ecommerce/FrontendController/queryAllGoodsData';
class GoodsReplenishment extends Component {
    constructor(props) {
        console.log("1.constructor 建構函式(Mounting:掛載)");
        super(props);
        this.state = {
            fileName: '',
            Data: [],
            goodsName: '',
            goodsPrice: '',
            goodsQuantity: '',
            status: '1',
            goodsID: '',
            selectedGoods: {},
            validated: false
        };
    }

    componentDidMount() {
        console.log("3.componentDidMount 組件掛載(Mounting:掛載)");
        this.onClickSearch();
    }

    componentDidUpdate(prevProps, prevState) {
        console.log("4.componentDidUpdate 組件更新(Updating :更新)");

        const { goodsID, Data } = this.state;
        console.log("prevState goodsID:", prevState.goodsID);
        console.log("state goodsID:", goodsID);
        if (goodsID !== prevState.goodsID && goodsID !== "") {
            const selectedGoods = Data.find(g => g.goodsID == goodsID);
            console.log(selectedGoods);
            if (selectedGoods) {
                this.setState({
                    goodsName: selectedGoods.goodsName,
                    goodsPrice: selectedGoods.price,
                    goodsQuantity: selectedGoods.quantity,
                    fileName: selectedGoods.imageName,
                    description: selectedGoods.description,
                    status: selectedGoods.status,
                    selectedGoods,
                    successful: ''

                });
            }
        }
    }
    onChangeImg = (e) => {
        const changFile = e.target.files;
         //避免取消照片找不到檔名報錯
         const changFileName = changFile && changFile[0] ? changFile[0].name : '';
        this.setState({ fileName: changFileName });
    };
    handleChange = (event) => {
        this.setState({
            status: event.target.value,

        });
    }
    handleGoodsIdChange = (event) => {
        this.setState({
            goodsID: event.target.value,

        });
    }
    onClickSearch = async () => {

        const goodData = await axios.get(allGoodUrl)
            .then(rs => rs.data)
            .catch(error => { console.log(error); });
        console.log(goodData);
        this.setState({
            Data: goodData,
        });
    };
    handleSubmit = async (event) => {
        const form = event.currentTarget;
        this.setState({ validated: true });
        if (form.checkValidity() === false) {
            event.preventDefault();
            event.stopPropagation();
            return;
        }
        const {
            goodsName,
            goodsPrice,
            goodsQuantity,
            fileName,
            status, description, successful, goodsID } = this.state;
        event.preventDefault();

        // 檢查必填欄位是否有填寫
        if (!goodsID) {

            alert('請填寫必填欄位！');
            return;
        }
        const uploadFile = form.uploadFile.files[0];
        // 組成要提交的表單資料
        const formData = new FormData();
        formData.append('goodsID', goodsID);
        formData.append('goodsName', goodsName.trim());
        formData.append('price', goodsPrice);
        formData.append('quantity', goodsQuantity);
        formData.append('description', description);
        formData.append('status', status);
        if (uploadFile) {
            formData.append('imageName', fileName);
            formData.append('file', uploadFile);
        }
        // 提交表單
        const gooddata = await axios.post(apiUrl, formData)
            .then(rs => rs.data)
            .catch(error => { console.log(error); });
        console.log(gooddata);
        this.setState({
            fileName: gooddata.imageName,
            successful: gooddata
        });
        form.uploadFile.value = null;
    };

    render() {
        console.log("2.render 渲染函式(Mounting :掛載、Updating:更新)");
        const {
            goodsName,
            goodsPrice,
            goodsQuantity,
            fileName,
            status, description, Data, goodsID, successful,validated } = this.state;

        return (
            <div>
                {successful && <p style={{ color: 'blue' }}>商品編號:{goodsID} {goodsName} 更新成功</p>}

                <Form noValidate validated={validated} onSubmit={this.handleSubmit}>
                    <Form.Group as={Col} md={4}>
                        <Form.Label>飲料名稱：</Form.Label>
                        <Form.Control
                            as="select"
                            name="goodsID"
                            value={goodsID}
                            onChange={this.handleGoodsIdChange}
                            required
                        >
                            <option value="">----- 請選擇 -----</option>
                            {Data.map((g) => (
                                <option key={g.goodsID} value={g.goodsID}>
                                    編號:{g.goodsID} {g.goodsName}
                                </option>
                            ))}
                        </Form.Control>
                    </Form.Group>
                    <Form.Group as={Col} md={4}>
                        <Form.Label>商品名稱：</Form.Label>
                        <Form.Control
                            type="text"
                            name="goodsName"
                            value={goodsName}
                            onChange={(e) => this.setState({ goodsName: e.target.value })}
                            required
                        />
                    </Form.Group>
                    <Form.Group as={Col} md={4}>
                        <Form.Label>商品價格：</Form.Label>
                        <Form.Control
                            type="number"
                            name="goodsPrice"
                            min="0"
                            max="1000"
                            value={goodsPrice}
                            required
                            onChange={(e) => this.setState({ goodsPrice: e.target.value })}
                        />
                    </Form.Group>
                    <Form.Group as={Col} md={4}>
                        <Form.Label>商品庫存：</Form.Label>
                        <Form.Control
                            type="number"
                            name="goodsQuantity"
                            min="0"
                            max="1000"
                            value={goodsQuantity}
                            required
                            onChange={(e) => this.setState({ goodsQuantity: e.target.value })}
                        />
                    </Form.Group>
                    <Form.Group as={Col} md={8}>
                        <Form.Label>商品描述：</Form.Label>
                        <br />
                        <Form.Control
                            as="textarea"
                            name="description"
                            value={description}
                            onChange={(e) => this.setState({ description: e.target.value })}
                        />
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
                    </Form.Group>
                    <Form.Group as={Col} md={4} className="d-flex align-items-center">
                        <Form.File id="formcheck-api-custom" custom className="mr-2">
                            <Form.File.Input name="uploadFile" onChange={this.onChangeImg} />
                            <Form.File.Label data-browse="Upload Button">
                                {fileName ? fileName : '選擇要上傳的檔案...'}
                            </Form.File.Label>
                        </Form.File>
                        <br/>
                        <Button variant="outline-primary" type="submit">上傳</Button>
                    </Form.Group>
                </Form>
            </div>
        );
    }
}

export default GoodsReplenishment;