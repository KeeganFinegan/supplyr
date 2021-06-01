import React, { useState } from 'react';
import {
  Tab,
  Container,
  Nav,
  Row,
  Col,
  Form,
  Dropdown,
  DropdownButton,
  Button,
} from 'react-bootstrap';
import './Admin.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookies, { set } from 'js-cookie';
import styles from '../BootstrapStyles/styles';
import 'react-toastify/dist/ReactToastify.css';
import { toast } from 'react-toastify';

toast.configure();

const createUnit = async (unitName, unitCredits, setResponse) => {
  const url = 'http://localhost:8080/api/v1/organisational-unit';
  const token = Cookies.get('token');

  return fetch(url, {
    method: 'POST',
    headers: {
      Authorization: 'Bearer ' + token,
      accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ name: unitName, credits: unitCredits }),
  })
    .then((res) => res.json())
    .then((data) => {
      console.log(data);
      setResponse(data);
    })
    .catch((error) => {
      console.log(error.message);
      setResponse(error);
    });
};

const success = (setResponse) => {
  toast.success('New unit created! ', {
    position: toast.POSITION.BOTTOM_RIGHT,
  });
  setResponse(null);
};

const error = (setResponse, error) => {
  toast.error(error, {
    position: toast.POSITION.BOTTOM_RIGHT,
  });
  setResponse(null);
};

export const OrganisationalUnit = () => {
  const [unitName, setUnitName] = useState(null);
  const [unitCredits, setUnitCredits] = useState(null);
  const [response, setResponse] = useState(null);

  return (
    <Container>
      <div className="admin-card">
        <h2 className="admin-card-title">Create Organisational Unit</h2>
        <Form className="admin-form">
          <p className="admin-input-title">Unit details</p>
          <input
            className="admin-input"
            placeholder="Unit name..."
            onChange={(e) => {
              e.preventDefault();
              setUnitName(e.target.value);
            }}
          ></input>
          <input
            type="number"
            className="admin-input"
            placeholder="Credits..."
            onChange={(e) => {
              e.preventDefault();
              setUnitCredits(e.target.value);
            }}
          ></input>
        </Form>
        {unitName && unitCredits ? (
          <Button
            variant="secondary"
            className="user-button "
            onClick={() => {
              createUnit(unitName, unitCredits, setResponse);
              setUnitName('');
              setUnitCredits('');
            }}
          >
            Submit
          </Button>
        ) : (
          <Button disabled className="user-button " variant="secondary">
            Submit
          </Button>
        )}
      </div>
      {response
        ? response.status
          ? error(setResponse, response.message)
          : success(setResponse)
        : null}
    </Container>
  );
};
