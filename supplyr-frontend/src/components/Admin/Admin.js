import React, {useState} from 'react';
import {Col, Container, Nav, Row, Tab} from 'react-bootstrap';
import './Admin.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Navbar from '../Navbar/Navbar';
import {OrganisationalUnit} from './OrganisationalUnit';
import {Asset} from './Asset';
import {User} from './User';
import Cookies from 'js-cookie';
import jwt_decode from 'jwt-decode';

export const Admin = () => {
    const token = Cookies.get('token');
    let decoded = jwt_decode(token);
    const [id, setId] = useState('123');

    const role = decoded.authorities[0].authority;

    if (role != 'ROLE_ADMIN') {
        return (
            <div>
                <Navbar/>
                <div className="cont">
                    <p className="access-denied">ACCESS DENIED</p>
                </div>
            </div>
        );
    }
    return (
        <div>
            <Navbar/>
            <Container className="admin-container">
                <Tab.Container
                    variant="secondary"
                    id="left-tabs-example"
                    defaultActiveKey="first"
                    className="side-nav"
                    unmountOnExit="true"
                >
                    <Row className="side-nav">
                        <Col sm={3}>
                            <Nav variant="pills" className="flex-column">
                                <Nav.Item>
                                    <Nav.Link eventKey="first">Organisational Units</Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey="second">Assets</Nav.Link>
                                </Nav.Item>
                                <Nav.Item>
                                    <Nav.Link eventKey="third">Users</Nav.Link>
                                </Nav.Item>
                            </Nav>
                        </Col>
                        <Col sm={9}>
                            <Tab.Content>
                                <Tab.Pane eventKey="first">
                                    <OrganisationalUnit key={id}/>
                                </Tab.Pane>
                                <Tab.Pane eventKey="second">
                                    <Asset key={id}/>
                                </Tab.Pane>
                                <Tab.Pane eventKey="third">
                                    <User key={id}/>
                                </Tab.Pane>
                            </Tab.Content>
                        </Col>
                    </Row>
                </Tab.Container>
            </Container>
        </div>
    );
};
