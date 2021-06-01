import React, { useState } from 'react';
import { Tab, Container, Nav, Row, Col } from 'react-bootstrap';
import './Account.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Navbar from '../Navbar/Navbar';
import Cookies from 'js-cookie';
import jwt_decode from 'jwt-decode';
import { PasswordReset } from './PasswordReset';
import styles from '../BootstrapStyles/styles';

export const Account = () => {
  const token = Cookies.get('token');
  let decoded = jwt_decode(token);

  return (
    <div>
      <Navbar />
      <Container className="admin-container">
        <Tab.Container
          variant="secondary"
          id="left-tabs-example"
          defaultActiveKey="first"
          className="side-nav"
        >
          <Row className="side-nav">
            <Col sm={3}>
              <Nav variant="pills" className="flex-column">
                <Nav.Item>
                  <Nav.Link eventKey="first">Password Reset</Nav.Link>
                </Nav.Item>
              </Nav>
            </Col>
            <Col sm={9}>
              <Tab.Content>
                <Tab.Pane eventKey="first">
                  <PasswordReset />
                </Tab.Pane>
              </Tab.Content>
            </Col>
          </Row>
        </Tab.Container>
      </Container>
    </div>
  );
};
