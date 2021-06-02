import React from 'react';
import {MenuItems} from './MenuItems';
import './Navbar.css';
import jwt_decode from 'jwt-decode';
import Cookie from 'js-cookie';
import {useHistory} from 'react-router-dom';

export default function Navbar() {
    let history = useHistory();

    let token = Cookie.get('token');
    let username = '';
    let role = '';

    if (token) {
        let decoded = jwt_decode(token);

        username = decoded.sub;
        role = decoded.authorities[0].authority;
    } else {
        username = ' ';
    }

    function logout(e) {
        e.preventDefault();
        Cookie.remove('token');
        history.push('/login');
    }

    return (
        <nav className="NavbarItems">
            <h1 className="navbar-logo" onClick={() => history.push('/home')}>
                supplyr
            </h1>
            <ul className="nav-menu">
                {role === 'ROLE_ADMIN'
                    ? MenuItems.map((item, index) => {
                        return (
                            <li key={index}>
                                <a className={item.cName} href={item.url}>
                                    {item.title}
                                </a>
                            </li>
                        );
                    })
                    : null}
            </ul>

            <div className="menu-icon">
                <div className="dropdown">
          <span className="username" onClick={logout}>
            {username}
          </span>
                    <div className="dropdown-content" onClick={logout}>
                        <button className="logout">LOGOUT</button>
                    </div>
                </div>
            </div>
        </nav>
    );
}
