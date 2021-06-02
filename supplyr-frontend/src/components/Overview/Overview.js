import React, {Component} from 'react';
import {Orders} from './Orders';
import {UserContext} from '../../context/AppContext';
import {AssetInfo} from './AssetInfo';
import './Overview.css';
import Cookie from 'js-cookie';
import Navbar from '../Navbar/Navbar';
import '../Navbar/Navbar.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import 'react-toastify/dist/ReactToastify.css';
import {toast} from 'react-toastify';
import {AssetDropdown} from '../UI/AssetDropdown';
import {Button, Col, Container, Dropdown, DropdownButton, Row, Spinner, Table,} from 'react-bootstrap';
import jwt_decode from 'jwt-decode';

toast.configure();

const countOrders = (orderList, type) => {
    let numOrders = 0;

    orderList.forEach((order) => {
        if (order.type === type) {
            numOrders++;
        }
    });
    return numOrders;
};

const renderAsset = (asset, index) => {
    return (
        <tr key={index}>
            <td className="table-body">{asset.asset.name}</td>
            <td className="table-body">{asset.quantity}</td>
        </tr>
    );
};

const renderBlank = () => {
    return <p>no assets</p>;
};

const submitOrder = async (unit, asset, quantity, price, type) => {
    const orderUrl = 'http://localhost:8080/api/v1/offers/';
    let token = Cookie.get('token');

    try {
        const config = {
            method: 'POST',
            headers: {
                Authorization: 'Bearer ' + token,
                Accept: 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                organisationalUnit: unit,
                asset: asset,
                quantity: quantity,
                price: price,
            }),
        };
        const response = await fetch(orderUrl + type.toLowerCase(), config);

        if (response.ok) {
            //return json
            toast.success(type + ' order successfully placed ', {
                position: toast.POSITION.BOTTOM_RIGHT,
            });
            return response.json();
        } else {
            //
            return response.json().then((error) => {
                toast.error(error.message, {
                    position: toast.POSITION.BOTTOM_RIGHT,
                });
            });
        }
    } catch (error) {
    }
};

const fetchUserData = async () => {
    let token = Cookie.get('token');
    let decoded = jwt_decode(token);
    let username = decoded.sub;

    const userUrl = `http://localhost:8080/api/v1/users/`;
    const userResponse = await fetch(userUrl + username, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
            'Content-Type': 'application/json',
        },
    });
    const userData = await userResponse.json();

    let asset;

    try {
        typeof userData.organisationalUnit.organisationalUnitAssets !== 'undefined'
            ? (asset = userData.organisationalUnit.organisationalUnitAssets)
            : (asset = null);

        return {
            userData: userData,
            asset: asset,
        };
    } catch (error) {
    }
};

class Overview extends Component {
    static contextType = UserContext;

    constructor(props) {
        super();
        this.state = {
            loading: true,
            user: null,
            assets: null,
            selectedAsset: 'Select asset',
            orderType: 'BUY',
            orderQuantity: null,
            orderPrice: null,
            orderResponse: null,
            buyOrdersClicked: false,
            sellOrdersClicked: false,
            assetInfoClicked: false,
            assetInfo: null,
            numBuyOrders: null,
            numSellOrders: null,
            id: null,
        };
        this.updateSelectedAsset = this.updateSelectedAsset.bind(this);
        this.updateClickedState = this.updateClickedState.bind(this);
    }

    async componentDidMount() {
        const dataObject = await fetchUserData();

        try {
            this.setState({
                id: '123',
                loading: false,
                user: dataObject.userData,
                assets: dataObject.asset,
                numBuyOrders: countOrders(
                    dataObject.userData.organisationalUnit.offers,
                    'BUY'
                ),
                numSellOrders: countOrders(
                    dataObject.userData.organisationalUnit.offers,
                    'SELL'
                ),
            });
        } catch (error) {
            this.props.history.push('/');
        }
    }

    updateSelectedAsset(newAsset) {
        this.setState({selectedAsset: newAsset});
    }

    updateClickedState(type) {
        if (type === 'BUY') {
            this.setState({buyOrdersClicked: false});
        } else if (type === 'SELL') {
            this.setState({sellOrdersClicked: false});
        } else if (type === 'ASSET_TRUE') {
            this.setState({assetInfoClicked: true});
        } else if (type === 'ASSET_FALSE') {
            this.setState({assetInfoClicked: false});
        }
    }

    handleRefresh = () => {
        // by calling this method react re-renders the component
        this.setState({});
    };

    render() {
        if (this.state.loading || !this.state.user) {
            return (
                <div className="loading-container">
                    <Spinner animation="border" variant="secondary"/>
                </div>
            );
        } else {
            return (
                <Container className="overview-container">
                    <Navbar/>
                    {this.state.buyOrdersClicked ? (
                        <Orders
                            updateClickedState={this.updateClickedState}
                            orderList={this.state.user.organisationalUnit.offers}
                            type={'BUY'}
                            fetchUserData={fetchUserData}
                        />
                    ) : null}
                    {this.state.sellOrdersClicked ? (
                        <Orders
                            updateClickedState={this.updateClickedState}
                            orderList={this.state.user.organisationalUnit.offers}
                            type={'SELL'}
                            fetchUserData={fetchUserData}
                        />
                    ) : null}
                    {this.state.assetInfoClicked ? (
                        <AssetInfo
                            asset={this.state.selectedAsset}
                            updateClickedState={this.updateClickedState}
                        />
                    ) : null}
                    <Row className="card-row">
                        <Col>
                            <div className="card">
                                <i className="fas fa-users card-icon"></i>
                                <div className="card-inner">
                                    <h2 className="card-title">Organisational Unit</h2>
                                    <p className="card-text">
                                        {this.state.user.organisationalUnit.name}
                                    </p>
                                </div>
                            </div>
                        </Col>
                    </Row>
                    <Row className="card-row">
                        {' '}
                        <Col>
                            <div className="card">
                                <i className="fas fa-coins card-icon"></i>
                                <div className="card-inner">
                                    <h2 className="card-title">Credits</h2>
                                    <p className="card-text">
                                        {this.state.user.organisationalUnit.credits}
                                    </p>
                                </div>
                            </div>
                        </Col>
                        <Col>
                            <div
                                className="card clickable"
                                onClick={() => this.setState({buyOrdersClicked: true})}
                            >
                                <i className="fas fa-clipboard-list card-icon green"></i>
                                <div className="card-inner">
                                    <h2 className="card-title">BUY Orders</h2>
                                    <p className="card-text">{this.state.numBuyOrders}</p>
                                </div>
                            </div>
                        </Col>
                        <Col>
                            {' '}
                            <div
                                className="card clickable "
                                onClick={() => this.setState({sellOrdersClicked: true})}
                            >
                                <i className="fas fa-clipboard-list card-icon red"></i>
                                <div className="card-inner">
                                    <h2 className="card-title">SELL Orders</h2>
                                    <p className="card-text">{this.state.numSellOrders}</p>
                                </div>
                            </div>
                        </Col>
                    </Row>
                    <Row className="card-row">
                        {' '}
                        <Col>
                            {' '}
                            <div className="card-tall-table">
                                <i className="fas fa-boxes card-icon"></i>
                                <h3 className="card-title ">Assets</h3>
                                <Table striped hover>
                                    <thead>
                                    <tr className="table-row table-header">
                                        <th className="table-header">Name</th>
                                        <th className="table-header">Quantity</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    {this.state.assets
                                        ? this.state.assets.map(renderAsset)
                                        : renderBlank}
                                    </tbody>
                                </Table>
                            </div>
                        </Col>
                        <Col>
                            {' '}
                            <div className="card-tall-table">
                                <i className="fas fa-store card-icon"></i>
                                <h3 className="card-title ">Trade</h3>
                                <form className="form-trade">
                                    {' '}
                                    <AssetDropdown
                                        selectedAsset={this.state.selectedAsset}
                                        setAsset={this.updateSelectedAsset}
                                    />
                                    {this.state.selectedAsset != 'Select asset' ? (
                                        <Button
                                            className="more-info"
                                            onClick={() => {
                                                this.setState({assetInfoClicked: true});
                                            }}
                                        >
                                            More Info
                                        </Button>
                                    ) : null}
                                </form>
                                <input
                                    type="number"
                                    className="quantity-input"
                                    placeholder="Quantity"
                                    onChange={(e) =>
                                        this.setState({orderQuantity: e.target.value})
                                    }
                                ></input>
                                <input
                                    type="number"
                                    className="quantity-input"
                                    placeholder="Price"
                                    onChange={(e) =>
                                        this.setState({orderPrice: e.target.value})
                                    }
                                ></input>
                                <DropdownButton
                                    title={this.state.orderType}
                                    variant="secondary"
                                >
                                    <Dropdown.Item
                                        onClick={() => this.setState({orderType: 'BUY'})}
                                    >
                                        BUY
                                    </Dropdown.Item>
                                    <Dropdown.Item
                                        onClick={() => this.setState({orderType: 'SELL'})}
                                    >
                                        SELL
                                    </Dropdown.Item>
                                </DropdownButton>
                                {this.state.orderPrice > 0 &&
                                this.state.orderQuantity > 0 &&
                                this.state.selectedAsset !== 'Select asset' ? (
                                    <Button
                                        type="button"
                                        variant="secondary"
                                        className="submit-button"
                                        onClick={async () => {
                                            this.setState({loading: true});
                                            await submitOrder(
                                                this.state.user.organisationalUnit.name,
                                                this.state.selectedAsset,
                                                this.state.orderQuantity,
                                                this.state.orderPrice,
                                                this.state.orderType
                                            ).then(() => {
                                                fetchUserData().then((newData) => {
                                                    this.handleRefresh();
                                                    this.setState({
                                                        loading: false,
                                                        orderQuantity: null,
                                                        orderPrice: null,
                                                        user: newData.userData,
                                                        id: Math.random().toString(),
                                                        numBuyOrders: countOrders(
                                                            newData.userData.organisationalUnit.offers,
                                                            'BUY'
                                                        ),
                                                        numSellOrders: countOrders(
                                                            newData.userData.organisationalUnit.offers,
                                                            'SELL'
                                                        ),
                                                    });
                                                    return newData;
                                                });
                                            });
                                        }}
                                    >
                                        Submit
                                    </Button>
                                ) : (
                                    <Button
                                        className="submit-button"
                                        disabled
                                        variant="secondary"
                                    >
                                        Submit
                                    </Button>
                                )}
                            </div>
                        </Col>
                    </Row>
                </Container>
            );
        }
    }
}

export default Overview;
