import React, {useState} from 'react';
import {Button, Container, Dropdown, DropdownButton, Form,} from 'react-bootstrap';
import './Admin.css';
import 'bootstrap/dist/css/bootstrap.min.css';
import Cookies from 'js-cookie';
import {UnitDropdown} from '../UI/UnitDropdown';
import 'react-toastify/dist/ReactToastify.css';
import {toast} from 'react-toastify';
import config from '../../config.json';

toast.configure();

const createUser = async (
    organisationalUnit,
    username,
    password,
    role,
    setResponse
) => {
    const url = config.API_URL + '/users/';
    const token = Cookies.get('token');
    organisationalUnit = role === 'ADMIN' ? 'admin' : organisationalUnit;

    return fetch(url + organisationalUnit, {
        method: 'POST',
        headers: {
            Authorization: 'Bearer ' + token,
            accept: 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify({username: username, password: password}),
    })
        .then((res) => res.json())
        .then((data) => {
            console.log(data.status);
            setResponse(data);
        })
        .catch((error) => {
            console.log(error.message);
            setResponse(error);
        });
};

const success = (setResponse) => {
    toast.success('New user created! ', {
        position: toast.POSITION.BOTTOM_RIGHT,
    });
    setResponse(null);
};

const error = (error, setResponse) => {
    toast.error(error, {
        position: toast.POSITION.BOTTOM_RIGHT,
    });
    setResponse(null);
};

export const User = () => {
    const [role, setRole] = useState('ROLE');
    const [organisationalUnit, setOrganisaationalUnit] = useState('Select Unit');
    const [username, setUsername] = useState(null);
    const [password, setPassword] = useState(null);
    const [response, setResponse] = useState(null);

    return (
        <Container>
            <div className="admin-card">
                <h2 className="admin-card-title">Create User</h2>
                <Form className="admin-form">
                    <p className="admin-input-title">User details</p>
                    <input
                        className="admin-input"
                        placeholder="Username..."
                        onChange={(e) => setUsername(e.target.value)}
                    ></input>
                    <input
                        className="admin-input"
                        type="password"
                        placeholder="Password..."
                        onChange={(e) => setPassword(e.target.value)}
                    ></input>
                    {role === 'USER' ? (
                        <UnitDropdown
                            setUnit={setOrganisaationalUnit}
                            selectedUnit={organisationalUnit}
                        />
                    ) : null}
                </Form>
                {response
                    ? response.status === 'CONFLICT'
                        ? error(response.message, setResponse)
                        : success(setResponse)
                    : null}

                <DropdownButton
                    title={role}
                    className="user-dropdown"
                    variant="secondary"
                >
                    <Dropdown.Item onClick={() => setRole('USER')}>USER</Dropdown.Item>
                    <Dropdown.Item onClick={() => setRole('ADMIN')}>ADMIN</Dropdown.Item>
                </DropdownButton>
                {username && password && role === 'ADMIN' ? (
                    <Button
                        variant="secondary"
                        className="user-button "
                        onClick={() => {
                            createUser(
                                organisationalUnit,
                                username,
                                password,
                                role,
                                setResponse
                            );
                        }}
                    >
                        Submit
                    </Button>
                ) : username &&
                password &&
                role === 'USER' &&
                organisationalUnit != 'Select Unit' ? (
                    <Button
                        variant="secondary"
                        className="user-button "
                        onClick={() => {
                            createUser(
                                organisationalUnit,
                                username,
                                password,
                                role,
                                setResponse
                            );
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
