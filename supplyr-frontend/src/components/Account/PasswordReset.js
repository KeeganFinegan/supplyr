import React, { useState } from 'react';
import { Container, Form, Button, Alert } from 'react-bootstrap';
import './Account.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookies from 'js-cookie';
import 'react-toastify/dist/ReactToastify.css';
import jwtDecode from 'jwt-decode';
import { toast } from 'react-toastify';

toast.configure();

const notify = (setResponse) => {
  toast.success('Password successfully changed', {
    position: toast.POSITION.BOTTOM_RIGHT,
  });
  setResponse(null);
};
const updatePassword = async (password, setResponse) => {
  const url = 'http://localhost:8080/api/v1/users/';
  const token = Cookies.get('token');
  const decoded = jwtDecode(token);

  return fetch(url + decoded.sub, {
    method: 'PUT',
    headers: {
      Authorization: 'Bearer ' + token,
      accept: 'application/json',
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ password: password }),
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

const handleSubmit = (password, confirm, setResponse) => {
  if (password !== confirm) {
    setResponse({
      status: 'NO_MATCH',
      message: 'passwords do not match',
    });
  } else {
    updatePassword(password, setResponse);
  }
};

export const PasswordReset = () => {
  const [password, setPassword] = useState(null);
  const [confirm, setConfirm] = useState(null);
  const [response, setResponse] = useState(null);

  return (
    <Container>
      <div className="admin-card">
        <h2 className="admin-card-title">Password Reset</h2>
        <Form className="admin-form">
          <p className="admin-input-title">New password</p>
          <input
            type="password"
            className="admin-input"
            placeholder="new password..."
            onChange={(e) => setPassword(e.target.value)}
          ></input>
          <input
            type="password"
            className="admin-input"
            placeholder="confirm password..."
            onChange={(e) => {
              setConfirm(e.target.value);
            }}
          ></input>
        </Form>
        {response ? (
          response.status === 'NOT_FOUND' || response.status === 'NO_MATCH' ? (
            <p className="response-status-error">{response.message}</p>
          ) : (
            notify(setResponse)
          )
        ) : null}

        {password && confirm ? (
          <Button
            variant="secondary"
            className="user-button "
            onClick={() => {
              handleSubmit(password, confirm, setResponse);
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
    </Container>
  );
};
