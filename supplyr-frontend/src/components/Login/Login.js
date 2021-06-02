import React, {useState} from 'react';
import {Redirect} from 'react-router-dom';
import Cookie from 'js-cookie';
import './Login.css';
import config from '../../config.json'

function LoginAction(props) {
    if (props.response.error) {
        return <p>Invalid Login Details</p>;
    } else if (props.response.token) {
        Cookie.set('token', props.response.token);

        return (
            <Redirect
                to={{
                    pathname: '/home',
                }}
            ></Redirect>
        );
    }
    return <div></div>;
}

export const Login = (props) => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [response, setResponse] = useState('');

    const handleLogin = async (e) => {
        e.preventDefault();
        const url = config.API_URL + '/login';

        return fetch(url, {
            method: 'POST',
            headers: {
                accept: 'application/json',
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({username: username, password: password}),
        })
            .then((res) => res.json())
            .then((data) => {
                setResponse(data);
            })
            .catch((error) => {
                console.log('errir');
                setResponse(error);
            });
    };

    return (
        <div className="wrapper">
            <div className="login-outer-container">
                <h1 className="logo">supplyr</h1>
                <div className="login-container">
                    <div className="login-inner-container">
                        <h2>User Login</h2>
                        <form className="form">
                            <input
                                placeholder="username..."
                                onChange={(e) => setUsername(e.target.value)}
                            ></input>
                            <input
                                type="password"
                                placeholder="password..."
                                onChange={(e) => setPassword(e.target.value)}
                            ></input>
                            <button
                                className="login-button"
                                type="button"
                                onClick={handleLogin}
                            >
                                Login
                            </button>
                        </form>
                    </div>
                </div>
                <div className="error">
                    <LoginAction response={response} setUser={props.setUser}/>
                </div>
            </div>
        </div>
    );
};
