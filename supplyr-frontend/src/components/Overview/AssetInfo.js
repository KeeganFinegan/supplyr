import React, {useEffect, useState} from 'react';
import './Orders.css';
import {Col, Container, Row, Spinner} from 'react-bootstrap';
import Cookies from 'js-cookie';
import jwtDecode from 'jwt-decode';
import {Chart} from './Chart';
import {useHistory} from 'react-router-dom';
import config from '../../config.json';

const getAssetPriceData = async (asset, setResponse) => {
    const url = config.API_URL + '/assets/' + asset + '/trades';
    const token = Cookies.get('token');
    const decoded = jwtDecode(token);

    return fetch(url, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
            'Content-Type': 'application/json',
        },
    })
        .then((res) => res.json())
        .then((data) => {
            setResponse(data);
            return data;
        })
        .catch((error) => {
            console.log(error.message);
            setResponse(error);
        });
};

const getAssetStats = async (asset) => {
    const url = config.API_URL + '/assets/' + asset + '/offer-info';
    const token = Cookies.get('token');
    const decoded = jwtDecode(token);

    console.log(url);
    return fetch(url, {
        method: 'GET',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
        },
    })
        .then((res) => res.json())
        .then((data) => {
            return data;
        })
        .catch((error) => {
            console.log(error.message);
        });
};

export const AssetInfo = (props) => {
    const [tradeData, setTradeData] = useState(null);
    const [loading, setLoading] = useState(true);
    const [response, setResponse] = useState(null);
    const [assetStatsResponse, setAssetStatsResponse] = useState(null);
    const [dataset, setDataset] = useState(null);
    const [lables, setlabels] = useState(null);
    const [lowestAsk, setLowestAsk] = useState(null);
    const [highestBid, setHighestBid] = useState(null);

    let history = useHistory();

    useEffect(() => {
        getAssetPriceData(props.asset, setResponse).then((data) => {
            setTradeData(data);
        });

        getAssetStats(props.asset).then((data) => {
            console.log(data);
            setLowestAsk(data.lowestAsk);
            setHighestBid(data.highestBid);
            setLoading(false);
        });
    }, []);

    if (loading) {
        return (
            <div className="loading-container">
                <Spinner animation="border" variant="secondary"/>
            </div>
        );
    } else {
        return (
            <Container className="orders-container">
                {' '}
                <i
                    className="fas fa-times-circle icon"
                    onClick={() => {
                        props.updateClickedState('ASSET_FALSE');
                        history.push('/home');
                    }}
                ></i>
                <h3 className="title">Asset Information {props.asset}</h3>
                <Row className="column">
                    <Col className="column">
                        {tradeData !== null && tradeData.length > 1 ? (
                            <Chart tradeData={tradeData} asset={props.asset}/>
                        ) : (
                            <div className="chart-container">
                                <p className="no-info">No Asset Pricing Data</p>
                            </div>
                        )}
                    </Col>
                    <Col className="column">
                        <div className="card-narrow">
                            <p className="items-text">
                                Lowest Ask: {lowestAsk > 0 ? lowestAsk : 'No Asks'}
                            </p>
                            <p className="items-text">
                                Highest Bid: {highestBid > 0 ? highestBid : 'No Bids'}
                            </p>
                            {tradeData !== null && tradeData.length > 1 ? (
                                <div className="items-container">
                                    <p className="items-text">
                                        Last Sale Price: {tradeData[tradeData.length - 1].price}
                                    </p>
                                    <p className="items-text">
                                        Number of Sales: {tradeData.length / 2}
                                    </p>
                                </div>
                            ) : null}
                        </div>
                    </Col>
                </Row>
            </Container>
        );
    }
};
