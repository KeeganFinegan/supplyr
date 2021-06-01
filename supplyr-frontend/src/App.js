import React, { useState, useEffect, useRef } from 'react';
import './App.css';
import { Login } from './components/Login/Login';

import { Overview2 } from './components/Overview/Overview2';
import { Admin } from './components/Admin/Admin';
import { Account } from './components/Account/Account';
import { BrowserRouter as Router, Route, withRouter } from 'react-router-dom';

function App() {
  return (
    <div className="App">
      <Router>
        <Route path="/login" component={withRouter(Login)} />
        <Route path="/admin" component={withRouter(Admin)} />
        <Route path="/account" component={withRouter(Account)} />
        <Route path="/home" component={withRouter(Overview2)} />
      </Router>
    </div>
  );
}

export default App;
