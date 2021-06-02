import React, {useState} from 'react';
import './Orders.css';
import {Button, Col, Container, Row, Table} from 'react-bootstrap';
import Cookies from 'js-cookie';
import jwt_decode from 'jwt-decode';
import {useHistory} from 'react-router-dom';
import config from '../../config.json';

const deleteOffer = async (orderId) => {
    const url = config.API_URL + '/offers/delete/';

    const token = Cookies.get('token');

    try {
        const response = await fetch(url + orderId, {
            method: 'POST',
            headers: {
                Authorization: 'Bearer ' + token,
                accept: 'application/json',
                'Content-Type': 'application/json',
            },
        });

        if (response.ok) {
            const decode = jwt_decode(token);
            const newDataResponse = await fetch(
                config.API_URL + '/users/' + decode.sub()
            );
            const data = newDataResponse.json();
            return data.organisationalUnit.offers;

            //return json
        } else {
            console.log(response.json());
            //
        }
    } catch (error) {
        //
    }
};

export const Orders = (props) => {
    const [orders, setOrders] = useState(props.orderList);

    let history = useHistory();

    return (
        <Container className="orders-container">
            <Row className="column">
                <Col className="column">
                    <i
                        className="fas fa-times-circle icon"
                        onClick={() => {
                            props.updateClickedState(props.type);
                            history.push('/home');
                        }}
                    ></i>
                    <h3 className="title">{props.type} Orders</h3>

                    <Table striped hover size="sm">
                        <thead>
                        <tr className="table-row table-header">
                            <th className="table-header ">Asset</th>
                            <th className="table-header">Quantity</th>
                            <th className="table-header">Price</th>
                            <th className="table-header">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                        {orders.map((order, index) => {
                            if (order.type === props.type) {
                                return (
                                    <tr key={index}>
                                        <td className="table-body">{order.asset.name}</td>
                                        <td className="table-body">{order.quantity}</td>
                                        <td className="table-body">{order.price}</td>
                                        <td className="table-body">
                                            <Button
                                                onClick={async () => {
                                                    await deleteOffer(order.id);
                                                    const updatedData = await props.fetchUserData();
                                                    setOrders(
                                                        updatedData.userData.organisationalUnit.offers
                                                    );
                                                }}
                                            >
                                                delete
                                            </Button>
                                        </td>
                                    </tr>
                                );
                            }
                        })}
                        </tbody>
                    </Table>
                </Col>
            </Row>
        </Container>
    );
};
