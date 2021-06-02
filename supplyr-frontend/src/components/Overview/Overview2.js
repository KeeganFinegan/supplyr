import React, {useEffect, useState} from 'react';
import {Orders} from './Orders';
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
import {Redirect} from 'react-router';
import config from '../../config.json';

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
    const orderUrl = config.API_URL + '/offers/';
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

    const userUrl = config.API_URL + '/users/';
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
        console.log('ERROR');
    }
};

export const Overview2 = () => {
    const [loading, setLoading] = useState(true);
    const [user, setUser] = useState(null);
    const [asset, setAsset] = useState(null);
    const [assets, setAssets] = useState(null);
    const [selectedAsset, setSelectedAsset] = useState('Select asset');
    const [orderType, setOrderType] = useState('BUY');
    const [orderQuantity, setOrderQuantity] = useState(null);
    const [orderPrice, setOrderPrice] = useState(null);
    const [orderResponse, setOrderResponse] = useState(null);
    const [buyOrdersClicked, setBuyOrdersClicked] = useState(false);
    const [sellOrdersClicked, setSellOrdersClicked] = useState(false);
    const [assetInfoClicked, setAssetInfoClicked] = useState(false);
    const [assetInfo, setAssetInfo] = useState(null);
    const [numBuyOrders, setNumBuyOrders] = useState(null);
    const [numSellOrders, setNumSellOrders] = useState(null);
    const [id, setId] = useState(null);

    useEffect(async () => {
        const dataObject = await fetchUserData();

        try {
            setId('123');
            setLoading(false);
            setUser(dataObject.userData);
            setAssets(dataObject.asset);
            setNumBuyOrders(
                countOrders(dataObject.userData.organisationalUnit.offers, 'BUY')
            );
            setNumSellOrders(
                countOrders(dataObject.userData.organisationalUnit.offers, 'SELL')
            );
        } catch (error) {
            console.log(error);
        }
    }, []);

    const updateSelectedAsset = (newAsset) => {
        setSelectedAsset(newAsset);
    };

    const updateClickedState = (type) => {
        if (type === 'BUY') {
            setBuyOrdersClicked(false);
        } else if (type === 'SELL') {
            setSellOrdersClicked(false);
        } else if (type === 'ASSET_TRUE') {
            this.setState({assetInfoClicked: true});
            setAssetInfoClicked(true);
        } else if (type === 'ASSET_FALSE') {
            setAssetInfoClicked(false);
        }
    };

    const handleRefresh = () => {
        // by calling this method react re-renders the component
        return (
            <Redirect
                to={{
                    pathname: '/login',
                }}
            ></Redirect>
        );
    };

    if (loading || !user) {
        return (
            <div className="loading-container">
                <Spinner animation="border" variant="secondary"/>
            </div>
        );
    } else {
        return (
            <Container className="overview-container">
                <Navbar/>
                {buyOrdersClicked ? (
                    <Orders
                        updateClickedState={updateClickedState}
                        orderList={user.organisationalUnit.offers}
                        type={'BUY'}
                        fetchUserData={fetchUserData}
                    />
                ) : null}
                {sellOrdersClicked ? (
                    <Orders
                        updateClickedState={updateClickedState}
                        orderList={user.organisationalUnit.offers}
                        type={'SELL'}
                        fetchUserData={fetchUserData}
                    />
                ) : null}
                {assetInfoClicked ? (
                    <AssetInfo
                        asset={selectedAsset}
                        updateClickedState={updateClickedState}
                    />
                ) : null}
                <Row className="card-row">
                    <Col>
                        <div className="card">
                            <i className="fas fa-users card-icon"></i>
                            <div className="card-inner">
                                <h2 className="card-title">Organisational Unit</h2>
                                <p className="card-text">{user.organisationalUnit.name}</p>
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
                                <p className="card-text">{user.organisationalUnit.credits}</p>
                            </div>
                        </div>
                    </Col>
                    <Col>
                        <div
                            className="card clickable"
                            onClick={() => setBuyOrdersClicked(true)}
                        >
                            <i className="fas fa-clipboard-list card-icon green"></i>
                            <div className="card-inner">
                                <h2 className="card-title">BUY Orders</h2>
                                <p className="card-text">{numBuyOrders}</p>
                            </div>
                        </div>
                    </Col>
                    <Col>
                        {' '}
                        <div
                            className="card clickable"
                            onClick={() => setSellOrdersClicked(true)}
                        >
                            <i className="fas fa-clipboard-list card-icon red"></i>
                            <div className="card-inner">
                                <h2 className="card-title">SELL Orders</h2>
                                <p className="card-text">{numSellOrders}</p>
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
                                <tbody>{assets ? assets.map(renderAsset) : renderBlank}</tbody>
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
                                    selectedAsset={selectedAsset}
                                    setAsset={updateSelectedAsset}
                                />
                                {selectedAsset != 'Select asset' ? (
                                    <Button
                                        className="more-info"
                                        onClick={() => {
                                            setAssetInfoClicked(true);
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
                                onChange={(e) => setOrderQuantity(e.target.value)}
                            ></input>
                            <input
                                type="number"
                                className="quantity-input"
                                placeholder="Price"
                                onChange={(e) => setOrderPrice(e.target.value)}
                            ></input>
                            <DropdownButton title={orderType} variant="secondary">
                                <Dropdown.Item onClick={() => setOrderType('BUY')}>
                                    BUY
                                </Dropdown.Item>
                                <Dropdown.Item onClick={() => setOrderType('SELL')}>
                                    SELL
                                </Dropdown.Item>
                            </DropdownButton>
                            {orderPrice > 0 &&
                            orderQuantity > 0 &&
                            selectedAsset !== 'Select asset' ? (
                                <Button
                                    type="button"
                                    variant="secondary"
                                    className="submit-button"
                                    onClick={async () => {
                                        setLoading(true);
                                        await submitOrder(
                                            user.organisationalUnit.name,
                                            selectedAsset,
                                            orderQuantity,
                                            orderPrice,
                                            orderType
                                        ).then(() => {
                                            fetchUserData().then((newData) => {
                                                handleRefresh();

                                                setLoading(false);
                                                setOrderQuantity(null);
                                                setOrderPrice(null);
                                                setUser(newData.userData);
                                                setId(Math.random().toString());
                                                setNumBuyOrders(
                                                    countOrders(
                                                        newData.userData.organisationalUnit.offers,
                                                        'BUY'
                                                    )
                                                );
                                                setNumSellOrders(
                                                    countOrders(
                                                        newData.userData.organisationalUnit.offers,
                                                        'SELL'
                                                    )
                                                );
                                                return newData;
                                            });
                                        });
                                    }}
                                >
                                    Submit
                                </Button>
                            ) : (
                                <Button className="submit-button" disabled variant="secondary">
                                    Submit
                                </Button>
                            )}
                        </div>
                    </Col>
                </Row>
            </Container>
        );
    }
};
